package net.osgg.crudthymeleaf.controller;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import net.osgg.crudthymeleaf.entities.Categoria;
import net.osgg.crudthymeleaf.repository.CategoriaRepo;
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

	@Autowired
	private CategoriaRepo categoriaRepo;
	 
	 
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
	 public String showSignUpForm(Receta receta, Model model) {
		 List<Categoria> categorias = categoriaRepo.findAll();
		 model.addAttribute("categorias", categorias);
		 model.addAttribute("receta", new Receta());
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
					receta.get().getCorreo(),
					receta.get().getIngredientes(),
					receta.get().getEnlace(),
					receta.get().getIdVideo()
			);

			return ResponseEntity.ok(recetaDetalles);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
		}
	}

	@GetMapping("/detalle/{id}")
	public String verDetalleReceta(@PathVariable("id") Long id, Model model) {
		// Buscar la receta por ID
		Receta receta = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Receta no encontrada: " + id));

		// Verificar si la receta tiene una foto asociada
		if (receta.getFoto() == null) {
			receta.setFoto(UUID.randomUUID()); // Esto asegura que siempre habrá un ID válido (puedes reemplazar con un placeholder).
		}

		// Agregar la receta al modelo
		model.addAttribute("receta", receta);

		// Retornar la vista para los detalles
		return "detalle_receta";
	}



//	@Autowired
//	private RecetaService recetaService;

//	@GetMapping("/list")
//	public String listarRecetas(
//			@RequestParam(required = false) String search,
//			@RequestParam(required = false) String difficulty,
//			@RequestParam(required = false) String sort,
//			Model model) {
//
//		// Obtener todas las recetas
//		List<Receta> recetas = (List<Receta>) repo.findAll();
//
//		// Filtrar por búsqueda
//		if (search != null && !search.isEmpty()) {
//			recetas = recetas.stream()
//					.filter(r -> r.getNombre().toLowerCase().contains(search.toLowerCase()))
//					.collect(Collectors.toList());
//		}
//
//		// Filtrar por dificultad
//		if (difficulty != null && !difficulty.isEmpty()) {
//			recetas = recetas.stream()
//					.filter(r -> r.getDificultad().equalsIgnoreCase(difficulty))
//					.collect(Collectors.toList());
//		}
//
//		// Ordenar
//		if (sort != null && !sort.isEmpty()) {
//			switch (sort) {
//				case "nombre":
//					recetas.sort(Comparator.comparing(Receta::getNombre));
//					break;
//				case "autor":
//					recetas.sort(Comparator.comparing(Receta::getAutor));
//					break;
//				case "correo":
//					recetas.sort(Comparator.comparing(Receta::getCorreo));
//					break;
//				default:
//					break;
//			}
//		}
//
//		// Agregar recetas filtradas al modelo
//		model.addAttribute("recipes", recetas);
//		return "list_recipes";
//	}


	@GetMapping("/buscar")
	public String buscarRecetas(@RequestParam("nombre") String nombre, Model model) {
//		// Buscar recetas por nombre usando el repositorio
//		List<Receta> recetasEncontradas = repo.findByNombre(nombre);

		// Buscar recetas ignorando mayúsculas y tildes
		List<Receta> recetasEncontradas = repo.buscarPorNombreIgnorandoCasingYTildes(nombre);


		// Agregar las recetas encontradas al modelo
		model.addAttribute("recipes", recetasEncontradas);

		// Retornar una nueva vista con los resultados
		return "resultados_busqueda"; // Nombre de la plantilla nueva (resultados_busqueda.html)
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
		 List<Categoria> categorias = categoriaRepo.findAll();
		 model.addAttribute("recipe", receta);
		 model.addAttribute("categoriasX", categorias);
	     return "update_recipe";
	 }
	@PreAuthorize("hasAuthority('admin')")
	@PostMapping("/update/{id}")
	public String updateRecipe(@PathVariable("id") Long id, Receta receta, BindingResult result, Model model, @RequestParam("file") MultipartFile file) {
		if (result.hasErrors()) {
			receta.setId(id);

			// Inicializar la categoría si es nula
			if (receta.getCategoria() == null || receta.getCategoria().getId() == null) {
				receta.setCategoria(new Categoria());
			}

			// Cargar categorías para el desplegable
			List<Categoria> categorias = categoriaRepo.findAll();
			model.addAttribute("categorias", categorias);

			return "update_recipe";
		}

		if (!file.isEmpty()) {
			picService.deletePicture(receta.getFoto());
			UUID idPic = UUID.randomUUID();
			picService.uploadPicture(file, idPic);
			receta.setFoto(idPic);
		}

		// Validar y guardar la categoría seleccionada
		Categoria categoria = categoriaRepo.findById(receta.getCategoria().getId())
				.orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + receta.getCategoria().getId()));
		receta.setCategoria(categoria);

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
