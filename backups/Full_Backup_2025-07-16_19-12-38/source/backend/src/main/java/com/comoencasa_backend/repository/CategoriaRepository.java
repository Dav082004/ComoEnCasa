package com.comoencasa_backend.repository;

import com.comoencasa_backend.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de categorías de productos
 * Implementa el patrón DAO usando Spring Data JPA
 * 
 * @author Como En Casa Team
 * @version 1.0
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

     /**
      * Busca una categoría por su nombre
      * Útil para validaciones de unicidad
      * 
      * @param nombre el nombre de la categoría
      * @return Optional con la categoría si existe
      */
     Optional<Categoria> findByNombre(String nombre);

     /**
      * Busca una categoría por nombre ignorando mayúsculas/minúsculas
      * 
      * @param nombre el nombre de la categoría
      * @return Optional con la categoría si existe
      */
     Optional<Categoria> findByNombreIgnoreCase(String nombre);

     /**
      * Obtiene todas las categorías ordenadas por nombre
      * Mejora la experiencia del usuario mostrando categorías ordenadas
      * 
      * @return Lista de categorías ordenadas alfabéticamente
      */
     @Query("SELECT c FROM Categoria c ORDER BY c.nombre ASC")
     List<Categoria> findAllOrderByNombre();

     /**
      * Verifica si existe una categoría con el nombre dado
      * 
      * @param nombre el nombre a verificar
      * @return true si existe, false caso contrario
      */
     boolean existsByNombre(String nombre);
}
