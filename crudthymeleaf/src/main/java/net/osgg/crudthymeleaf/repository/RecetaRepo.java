package net.osgg.crudthymeleaf.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import net.osgg.crudthymeleaf.entities.Receta;
import org.springframework.data.repository.query.Param;

public interface RecetaRepo extends CrudRepository <Receta, Long>{

	List<Receta> findByNombre(String nombre); 
	
	List<Receta> findByPreparacion(String preparacion);

	@Query("SELECT r FROM Receta r WHERE LOWER(UNACCENT(r.nombre)) LIKE LOWER(UNACCENT(CONCAT('%', :nombre, '%')))")
	List<Receta> buscarPorNombreIgnorandoCasingYTildes(@Param("nombre") String nombre);
}
