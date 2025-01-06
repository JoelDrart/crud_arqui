package net.osgg.crudthymeleaf.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import net.osgg.crudthymeleaf.entities.Receta;
import net.osgg.crudthymeleaf.repository.RecetaRepo;
import net.osgg.crudthymeleaf.service.PictureService;

@Controller
@RequestMapping("/recetas")
public class RecetaControlador {

	 @Autowired
	 private RecetaRepo repo;
	 
	 @Autowired
	    PictureService picService;
	 
	 
	 @RequestMapping("")
	 public String index(Model model) {
		model.addAttribute("recipes", repo.findAll());
		return "index";
	 }

	@GetMapping("/about")
	public String showAboutPage() {
		return "about";
	}
	 
	 @GetMapping("/add_recipe")
	 public String showSignUpForm(Receta receta) {
	     return "add_recipe";
	 }
 
	 @GetMapping("/list")
	 public String showRecipes(Model model) {
		 model.addAttribute("recipes", repo.findAll());
	     return "list_recipes";
	 }

	@GetMapping("/details/{id}")
	@ResponseBody
	public ResponseEntity<?> getRecipeDetails(@PathVariable("id") Long id) {
		try {
			java.util.Optional<Receta> receta = repo.findById(id);

			if (receta.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Receta no encontrada");
			}

			var recetaDetalles = new RecetaDetallesDTO(
					receta.get().getId(),
					receta.get().getNombre(),
					receta.get().getFoto() != null ? receta.get().getFoto().toString() : "",
					receta.get().getPreparacion(),
					receta.get().getDificultad(),
					receta.get().getAutor(),
					receta.get().getTelefono(),
					receta.get().getCorreo()
			);

			return ResponseEntity.ok(recetaDetalles);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
		}
	}


	@RequestMapping("/login")
	 public String showLogin() {
	     return "login";
	 }
	 
	 @GetMapping("/sc")
	 public String showRecipes() {
	     return "soundcloud.html";
	 }	 
	 
	 @PreAuthorize("hasAuthority('admin')")
	 @RequestMapping("/private")
	 public String showPrivate(Model model) {
		 model.addAttribute("recipes", repo.findAll());
	     return "list_recipes";
	 }
	 
	 @PreAuthorize("hasAuthority('admin')")
	 @PostMapping("/add")
	 public String addRecipe(Receta receta, BindingResult result, Model model, @RequestParam("file") MultipartFile file) {
	     if (result.hasErrors()) {
	        return "add_recipe";
	     }
	     UUID idPic = UUID.randomUUID();
	     picService.uploadPicture(file, idPic);
	     receta.setFoto(idPic);
	     repo.save(receta);   
	     return "redirect:list";
	 }

	 @PreAuthorize("hasAuthority('admin')")
	 @GetMapping("/edit/{id}")
	 public String showUpdateForm(@PathVariable("id") Long id, Model model) {
	     Receta receta = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid recipe Id:" + id));
	     model.addAttribute("recipe", receta);
	     return "update_recipe";
	 }
	 
	 @PreAuthorize("hasAuthority('admin')")
	 @PostMapping("/update/{id}")
	 public String updateRecipe(@PathVariable("id") Long id, Receta receta, BindingResult result, Model model, @RequestParam("file") MultipartFile file) {
	     if (result.hasErrors()) {
	          receta.setId(id);
	          return "update_recipe";
	     }
	     
	     if (!file.isEmpty()) {
	    	 picService.deletePicture(receta.getFoto());
		     UUID idPic = UUID.randomUUID();
		     picService.uploadPicture(file, idPic);
		     receta.setFoto(idPic);
	     }
	     repo.save(receta);
	     return "redirect:/recetas/list";
	 }

	 @PreAuthorize("hasAuthority('admin')")
	 @GetMapping("/delete/{id}")
	 public String deleteRecipe(@PathVariable("id") Long id, Model model) {
	     Receta receta = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid recipe Id:" + id));
	     picService.deletePicture(receta.getFoto());
	     repo.delete(receta);	     
	     model.addAttribute("recipes", repo.findAll());
	     return "list_recipes";
	 }


}
