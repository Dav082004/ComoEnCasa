package com.comoencasa_backend.controller;

import com.comoencasa_backend.dto.CategoriaConConteoDTO;
import com.comoencasa_backend.model.Categoria;
import com.comoencasa_backend.repository.CategoriaRepository;
import com.comoencasa_backend.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador REST para la gestión de categorías de productos
 * Implementa el patrón MVC y buenas prácticas de arquitectura
 * 
 * @author Como En Casa Team
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class CategoriaController {
     private final CategoriaRepository categoriaRepository;
     private final ProductoRepository productoRepository;

     /**
      * Obtiene todas las categorías disponibles
      * Endpoint público para uso en la interfaz de administración
      * 
      * @return ResponseEntity con lista de categorías
      */
     @GetMapping
     public ResponseEntity<List<Categoria>> getAllCategorias() {
          try {
               log.debug("Solicitando todas las categorías");
               List<Categoria> categorias = categoriaRepository.findAllOrderByNombre();

               log.info("Se encontraron {} categorías", categorias.size());
               return ResponseEntity.ok(categorias);

          } catch (Exception e) {
               log.error("Error al obtener categorías: {}", e.getMessage(), e);
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
          }
     }

     /**
      * Obtiene todas las categorías con el conteo de productos asociados
      * Endpoint específico para el panel de administración
      * 
      * @return ResponseEntity con lista de categorías con conteo
      */
     @GetMapping("/con-conteo")
     public ResponseEntity<List<CategoriaConConteoDTO>> getCategoriasConConteo() {
          try {
               log.debug("Solicitando categorías con conteo de productos");
               List<Categoria> categorias = categoriaRepository.findAllOrderByNombre();
               List<CategoriaConConteoDTO> categoriasConConteo = categorias.stream()
                         .map(categoria -> {
                              Long conteo = productoRepository.countByCategoriaIdAndDisponibleTrue(categoria.getId());
                              return new CategoriaConConteoDTO(
                                        categoria.getId(),
                                        categoria.getNombre(),
                                        categoria.getDescripcion(),
                                        conteo);
                         })
                         .collect(Collectors.toList());

               log.info("Se encontraron {} categorías con conteo", categoriasConConteo.size());
               return ResponseEntity.ok(categoriasConConteo);

          } catch (Exception e) {
               log.error("Error al obtener categorías con conteo: {}", e.getMessage(), e);
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
          }
     }

     /**
      * Obtiene una categoría específica por su ID
      * 
      * @param id el ID de la categoría
      * @return ResponseEntity con la categoría encontrada
      */
     @GetMapping("/{id}")
     public ResponseEntity<Categoria> getCategoriaById(@PathVariable Long id) {
          try {
               log.debug("Solicitando categoría con ID: {}", id);
               Optional<Categoria> categoria = categoriaRepository.findById(id);

               if (categoria.isPresent()) {
                    log.info("Categoría encontrada: {}", categoria.get().getNombre());
                    return ResponseEntity.ok(categoria.get());
               } else {
                    log.warn("Categoría con ID {} no encontrada", id);
                    return ResponseEntity.notFound().build();
               }

          } catch (Exception e) {
               log.error("Error al obtener categoría por ID {}: {}", id, e.getMessage(), e);
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
          }
     }

     /**
      * Busca una categoría por su nombre
      * Útil para validaciones desde el frontend
      * 
      * @param nombre el nombre de la categoría a buscar
      * @return ResponseEntity con la categoría encontrada
      */
     @GetMapping("/buscar")
     public ResponseEntity<Categoria> getCategoriaByNombre(@RequestParam String nombre) {
          try {
               log.debug("Buscando categoría por nombre: {}", nombre);
               Optional<Categoria> categoria = categoriaRepository.findByNombreIgnoreCase(nombre);

               if (categoria.isPresent()) {
                    log.info("Categoría encontrada por nombre: {}", categoria.get().getNombre());
                    return ResponseEntity.ok(categoria.get());
               } else {
                    log.warn("Categoría con nombre '{}' no encontrada", nombre);
                    return ResponseEntity.notFound().build();
               }

          } catch (Exception e) {
               log.error("Error al buscar categoría por nombre '{}': {}", nombre, e.getMessage(), e);
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
          }
     }

     /**
      * Verifica si una categoría existe por su nombre
      * Endpoint útil para validaciones del frontend
      * 
      * @param nombre el nombre de la categoría
      * @return ResponseEntity con booleano indicando existencia
      */
     @GetMapping("/existe")
     public ResponseEntity<Boolean> existeCategoriaByNombre(@RequestParam String nombre) {
          try {
               log.debug("Verificando existencia de categoría: {}", nombre);
               boolean existe = categoriaRepository.findByNombreIgnoreCase(nombre).isPresent();

               log.info("Categoría '{}' existe: {}", nombre, existe);
               return ResponseEntity.ok(existe);

          } catch (Exception e) {
               log.error("Error al verificar existencia de categoría '{}': {}", nombre, e.getMessage(), e);
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
          }
     }

     /**
      * Crea una nueva categoría
      * 
      * @param categoria datos de la nueva categoría
      * @return ResponseEntity con la categoría creada
      */
     @PostMapping
     public ResponseEntity<Categoria> createCategoria(@RequestBody Categoria categoria) {
          try {
               log.info("Creando nueva categoría: {}", categoria.getNombre());

               // Validar que el nombre no esté vacío
               if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
                    log.warn("Intento de crear categoría sin nombre");
                    return ResponseEntity.badRequest().build();
               }

               // Verificar que no exista una categoría con el mismo nombre
               if (categoriaRepository.findByNombreIgnoreCase(categoria.getNombre().trim()).isPresent()) {
                    log.warn("Intento de crear categoría con nombre duplicado: {}", categoria.getNombre());
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
               }

               // Limpiar y establecer datos
               categoria.setNombre(categoria.getNombre().trim());
               if (categoria.getDescripcion() != null) {
                    categoria.setDescripcion(categoria.getDescripcion().trim());
               }

               Categoria categoriaCreada = categoriaRepository.save(categoria);
               log.info("Categoría creada exitosamente con ID: {}", categoriaCreada.getId());

               return ResponseEntity.status(HttpStatus.CREATED).body(categoriaCreada);

          } catch (Exception e) {
               log.error("Error al crear categoría: {}", e.getMessage(), e);
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
          }
     }

     /**
      * Actualiza una categoría existente
      * 
      * @param id                   ID de la categoría a actualizar
      * @param categoriaActualizada datos actualizados de la categoría
      * @return ResponseEntity con la categoría actualizada
      */
     @PutMapping("/{id}")
     public ResponseEntity<Categoria> updateCategoria(@PathVariable Long id,
               @RequestBody Categoria categoriaActualizada) {
          try {
               log.info("Actualizando categoría con ID: {}", id);

               Optional<Categoria> categoriaExistente = categoriaRepository.findById(id);
               if (categoriaExistente.isEmpty()) {
                    log.warn("Categoría con ID {} no encontrada para actualizar", id);
                    return ResponseEntity.notFound().build();
               }

               // Validar que el nombre no esté vacío
               if (categoriaActualizada.getNombre() == null || categoriaActualizada.getNombre().trim().isEmpty()) {
                    log.warn("Intento de actualizar categoría {} sin nombre", id);
                    return ResponseEntity.badRequest().build();
               }

               // Verificar que no exista otra categoría con el mismo nombre
               Optional<Categoria> categoriaConMismoNombre = categoriaRepository
                         .findByNombreIgnoreCase(categoriaActualizada.getNombre().trim());
               if (categoriaConMismoNombre.isPresent() && !categoriaConMismoNombre.get().getId().equals(id)) {
                    log.warn("Intento de actualizar categoría {} con nombre duplicado: {}", id,
                              categoriaActualizada.getNombre());
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
               }

               Categoria categoria = categoriaExistente.get();
               categoria.setNombre(categoriaActualizada.getNombre().trim());
               if (categoriaActualizada.getDescripcion() != null) {
                    categoria.setDescripcion(categoriaActualizada.getDescripcion().trim());
               }

               Categoria categoriaGuardada = categoriaRepository.save(categoria);
               log.info("Categoría {} actualizada exitosamente", id);

               return ResponseEntity.ok(categoriaGuardada);

          } catch (Exception e) {
               log.error("Error al actualizar categoría {}: {}", id, e.getMessage(), e);
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
          }
     }

     /**
      * Elimina una categoría
      * 
      * @param id ID de la categoría a eliminar
      * @return ResponseEntity indicando el resultado
      */
     @DeleteMapping("/{id}")
     public ResponseEntity<Void> deleteCategoria(@PathVariable Long id) {
          try {
               log.info("Eliminando categoría con ID: {}", id);

               Optional<Categoria> categoria = categoriaRepository.findById(id);
               if (categoria.isEmpty()) {
                    log.warn("Categoría con ID {} no encontrada para eliminar", id);
                    return ResponseEntity.notFound().build();
               }

               // TODO: Verificar si hay productos asociados antes de eliminar
               // Esta validación se puede implementar cuando se tenga la relación con
               // productos

               categoriaRepository.deleteById(id);
               log.info("Categoría {} eliminada exitosamente", id);

               return ResponseEntity.noContent().build();

          } catch (Exception e) {
               log.error("Error al eliminar categoría {}: {}", id, e.getMessage(), e);
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
          }
     }
}
