package com.comoencasa_backend.service.impl;

import com.comoencasa_backend.model.Producto;
import com.comoencasa_backend.repository.ProductoRepository;
import com.comoencasa_backend.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests TDD para ProductoServiceImpl
 * 
 * Cubre todas las operaciones CRUD de productos:
 * - Crear producto
 * - Obtener productos
 * - Actualizar producto
 * - Eliminar producto
 * - Búsquedas y filtros
 * - Validaciones de negocio
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductoServiceImpl - Tests TDD")
class ProductoServiceImplTDDCorregido {

     @Mock
     private ProductoRepository productoRepository;

     @Mock
     private CategoriaRepository categoriaRepository;

     @InjectMocks
     private ProductoServiceImpl productoService;

     private Producto productoEjemplo;
     private Producto productoEjemplo2;

     @BeforeEach
     void setUp() {
          // Producto de ejemplo para las pruebas
          productoEjemplo = new Producto();
          productoEjemplo.setId(1L);
          productoEjemplo.setCategoriaId(1L);
          productoEjemplo.setNombre("Torta de Chocolate");
          productoEjemplo.setDescripcion("Deliciosa torta de chocolate casera");
          productoEjemplo.setPrecioVenta(25.50);
          productoEjemplo.setCostoProduccion(15.00);
          productoEjemplo.setCantidad(10);
          productoEjemplo.setImagenUrl("torta-chocolate.jpg");
          productoEjemplo.setDisponible(true);

          // Segundo producto de ejemplo
          productoEjemplo2 = new Producto();
          productoEjemplo2.setId(2L);
          productoEjemplo2.setCategoriaId(1L);
          productoEjemplo2.setNombre("Torta de Vainilla");
          productoEjemplo2.setDescripcion("Deliciosa torta de vainilla casera");
          productoEjemplo2.setPrecioVenta(23.00);
          productoEjemplo2.setCostoProduccion(13.00);
          productoEjemplo2.setCantidad(5);
          productoEjemplo2.setImagenUrl("torta-vainilla.jpg");
          productoEjemplo2.setDisponible(true);
     }

     @Nested
     @DisplayName("Tests de Creación de Productos")
     class CreacionProductosTests {

          @Test
          @DisplayName("Crear producto exitoso con datos válidos")
          void testCrearProductoExitoso() {
               // Given
               when(categoriaRepository.existsById(1L)).thenReturn(true);
               when(productoRepository.save(any(Producto.class)))
                         .thenReturn(productoEjemplo);

               // When
               Producto resultado = productoService.create(productoEjemplo);

               // Then
               assertNotNull(resultado);
               assertEquals(productoEjemplo.getNombre(), resultado.getNombre());
               assertEquals(productoEjemplo.getPrecioVenta(), resultado.getPrecioVenta());
               verify(categoriaRepository).existsById(1L);
               verify(productoRepository).save(any(Producto.class));
          }

          @Test
          @DisplayName("Crear producto fallido - categoría no existe")
          void testCrearProductoCategoriaNoExiste() {
               // Given
               when(categoriaRepository.existsById(1L)).thenReturn(false);

               // When & Then
               IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    productoService.create(productoEjemplo);
               });

               assertEquals("No existe la categoría con ID: 1", exception.getMessage());
               verify(categoriaRepository).existsById(1L);
               verify(productoRepository, never()).save(any(Producto.class));
          }

          @Test
          @DisplayName("Crear producto exitoso - sin categoría")
          void testCrearProductoSinCategoria() {
               // Given
               productoEjemplo.setCategoriaId(null);
               when(productoRepository.save(any(Producto.class)))
                         .thenReturn(productoEjemplo);

               // When
               Producto resultado = productoService.create(productoEjemplo);

               // Then
               assertNotNull(resultado);
               assertEquals(productoEjemplo.getNombre(), resultado.getNombre());
               verify(categoriaRepository, never()).existsById(any());
               verify(productoRepository).save(any(Producto.class));
          }
     }

     @Nested
     @DisplayName("Tests de Consulta de Productos")
     class ConsultaProductosTests {

          @Test
          @DisplayName("Obtener todos los productos disponibles")
          void testFindAllAvailable() {
               // Given
               List<Producto> productosDisponibles = Arrays.asList(productoEjemplo, productoEjemplo2);
               when(productoRepository.findByDisponibleTrue()).thenReturn(productosDisponibles);

               // When
               List<Producto> resultado = productoService.findAllAvailable();

               // Then
               assertNotNull(resultado);
               assertEquals(2, resultado.size());
               assertTrue(resultado.contains(productoEjemplo));
               assertTrue(resultado.contains(productoEjemplo2));
               verify(productoRepository).findByDisponibleTrue();
          }

          @Test
          @DisplayName("Obtener todos los productos")
          void testFindAll() {
               // Given
               List<Producto> todosProductos = Arrays.asList(productoEjemplo, productoEjemplo2);
               when(productoRepository.findAll()).thenReturn(todosProductos);

               // When
               List<Producto> resultado = productoService.findAll();

               // Then
               assertNotNull(resultado);
               assertEquals(2, resultado.size());
               verify(productoRepository).findAll();
          }

          @Test
          @DisplayName("Obtener producto por ID - existente")
          void testFindByIdExistente() {
               // Given
               when(productoRepository.findById(1L)).thenReturn(Optional.of(productoEjemplo));

               // When
               Optional<Producto> resultado = productoService.findById(1L);

               // Then
               assertTrue(resultado.isPresent());
               assertEquals(productoEjemplo, resultado.get());
               verify(productoRepository).findById(1L);
          }

          @Test
          @DisplayName("Obtener producto por ID - no existente")
          void testFindByIdNoExistente() {
               // Given
               when(productoRepository.findById(999L)).thenReturn(Optional.empty());

               // When
               Optional<Producto> resultado = productoService.findById(999L);

               // Then
               assertFalse(resultado.isPresent());
               verify(productoRepository).findById(999L);
          }

          @Test
          @DisplayName("Obtener producto por ID - ID nulo")
          void testFindByIdNulo() {
               // When & Then
               IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    productoService.findById(null);
               });

               assertEquals("El ID del producto no puede ser nulo", exception.getMessage());
               verify(productoRepository, never()).findById(any());
          }

          @Test
          @DisplayName("Obtener productos por categoría")
          void testFindByCategoriaId() {
               // Given
               List<Producto> productosCategoria = Arrays.asList(productoEjemplo, productoEjemplo2);
               when(productoRepository.findByCategoriaIdAndDisponibleTrue(1L)).thenReturn(productosCategoria);

               // When
               List<Producto> resultado = productoService.findByCategoriaId(1L);

               // Then
               assertNotNull(resultado);
               assertEquals(2, resultado.size());
               verify(productoRepository).findByCategoriaIdAndDisponibleTrue(1L);
          }

          @Test
          @DisplayName("Obtener productos por categoría - ID nulo")
          void testFindByCategoriaIdNulo() {
               // When & Then
               IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    productoService.findByCategoriaId(null);
               });

               assertEquals("El ID de la categoría no puede ser nulo", exception.getMessage());
               verify(productoRepository, never()).findByCategoriaIdAndDisponibleTrue(any());
          }
     }

     @Nested
     @DisplayName("Tests de Actualización de Stock")
     class ActualizacionStockTests {

          @Test
          @DisplayName("Actualizar stock exitoso")
          void testActualizarStockExitoso() {
               // Given
               when(productoRepository.findById(1L)).thenReturn(Optional.of(productoEjemplo));
               when(productoRepository.save(any(Producto.class))).thenReturn(productoEjemplo);

               // When
               Producto resultado = productoService.actualizarStock(1L, 15);

               // Then
               assertNotNull(resultado);
               assertEquals(15, resultado.getCantidad());
               assertTrue(resultado.getDisponible());
               verify(productoRepository).findById(1L);
               verify(productoRepository).save(any(Producto.class));
          }

          @Test
          @DisplayName("Actualizar stock a cero - producto no disponible")
          void testActualizarStockACero() {
               // Given
               when(productoRepository.findById(1L)).thenReturn(Optional.of(productoEjemplo));
               when(productoRepository.save(any(Producto.class))).thenReturn(productoEjemplo);

               // When
               Producto resultado = productoService.actualizarStock(1L, 0);

               // Then
               assertNotNull(resultado);
               assertEquals(0, resultado.getCantidad());
               assertFalse(resultado.getDisponible());
               verify(productoRepository).findById(1L);
               verify(productoRepository).save(any(Producto.class));
          }

          @Test
          @DisplayName("Actualizar stock - producto no encontrado")
          void testActualizarStockProductoNoEncontrado() {
               // Given
               when(productoRepository.findById(999L)).thenReturn(Optional.empty());

               // When & Then
               IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    productoService.actualizarStock(999L, 10);
               });

               assertEquals("Producto no encontrado con ID: 999", exception.getMessage());
               verify(productoRepository).findById(999L);
               verify(productoRepository, never()).save(any(Producto.class));
          }

          @Test
          @DisplayName("Actualizar stock - ID nulo")
          void testActualizarStockIdNulo() {
               // When & Then
               IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    productoService.actualizarStock(null, 10);
               });

               assertEquals("El ID del producto no puede ser nulo", exception.getMessage());
               verify(productoRepository, never()).findById(any());
          }

          @Test
          @DisplayName("Actualizar stock - cantidad negativa")
          void testActualizarStockCantidadNegativa() {
               // When & Then
               IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    productoService.actualizarStock(1L, -5);
               });

               assertEquals("La cantidad no puede ser nula o negativa", exception.getMessage());
               verify(productoRepository, never()).findById(any());
          }

          @Test
          @DisplayName("Actualizar stock - cantidad nula")
          void testActualizarStockCantidadNula() {
               // When & Then
               IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    productoService.actualizarStock(1L, null);
               });

               assertEquals("La cantidad no puede ser nula o negativa", exception.getMessage());
               verify(productoRepository, never()).findById(any());
          }
     }

     @Nested
     @DisplayName("Tests de Cambio de Disponibilidad")
     class CambioDisponibilidadTests {

          @Test
          @DisplayName("Cambiar disponibilidad exitoso")
          void testCambiarDisponibilidadExitoso() {
               // Given
               when(productoRepository.findById(1L)).thenReturn(Optional.of(productoEjemplo));
               when(productoRepository.save(any(Producto.class))).thenReturn(productoEjemplo);

               // When
               Producto resultado = productoService.cambiarDisponibilidad(1L, false);

               // Then
               assertNotNull(resultado);
               assertFalse(resultado.getDisponible());
               verify(productoRepository).findById(1L);
               verify(productoRepository).save(any(Producto.class));
          }

          @Test
          @DisplayName("Cambiar disponibilidad - producto no encontrado")
          void testCambiarDisponibilidadProductoNoEncontrado() {
               // Given
               when(productoRepository.findById(999L)).thenReturn(Optional.empty());

               // When & Then
               IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    productoService.cambiarDisponibilidad(999L, false);
               });

               assertEquals("Producto no encontrado con ID: 999", exception.getMessage());
               verify(productoRepository).findById(999L);
               verify(productoRepository, never()).save(any(Producto.class));
          }

          @Test
          @DisplayName("Cambiar disponibilidad - ID nulo")
          void testCambiarDisponibilidadIdNulo() {
               // When & Then
               IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    productoService.cambiarDisponibilidad(null, false);
               });

               assertEquals("El ID del producto no puede ser nulo", exception.getMessage());
               verify(productoRepository, never()).findById(any());
          }

          @Test
          @DisplayName("Cambiar disponibilidad - estado nulo")
          void testCambiarDisponibilidadEstadoNulo() {
               // When & Then
               IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    productoService.cambiarDisponibilidad(1L, null);
               });

               assertEquals("El estado de disponibilidad no puede ser nulo", exception.getMessage());
               verify(productoRepository, never()).findById(any());
          }
     }

     @Nested
     @DisplayName("Tests de Reducción de Stock")
     class ReduccionStockTests {

          @Test
          @DisplayName("Reducir stock exitoso")
          void testReducirStockExitoso() {
               // Given
               productoEjemplo.setCantidad(10);
               when(productoRepository.findById(1L)).thenReturn(Optional.of(productoEjemplo));

               // When
               productoService.reducirStock(1L, 3);

               // Then
               assertEquals(7, productoEjemplo.getCantidad());
               assertTrue(productoEjemplo.getDisponible());
               verify(productoRepository).findById(1L);
               verify(productoRepository).save(productoEjemplo);
          }

          @Test
          @DisplayName("Reducir stock a cero - producto no disponible")
          void testReducirStockACero() {
               // Given
               productoEjemplo.setCantidad(5);
               when(productoRepository.findById(1L)).thenReturn(Optional.of(productoEjemplo));

               // When
               productoService.reducirStock(1L, 5);

               // Then
               assertEquals(0, productoEjemplo.getCantidad());
               assertFalse(productoEjemplo.getDisponible());
               verify(productoRepository).findById(1L);
               verify(productoRepository).save(productoEjemplo);
          }

          @Test
          @DisplayName("Reducir stock - stock insuficiente")
          void testReducirStockInsuficiente() {
               // Given
               productoEjemplo.setCantidad(3);
               when(productoRepository.findById(1L)).thenReturn(Optional.of(productoEjemplo));

               // When & Then
               IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    productoService.reducirStock(1L, 5);
               });

               assertEquals("Stock insuficiente. Disponible: 3, Solicitado: 5", exception.getMessage());
               verify(productoRepository).findById(1L);
               verify(productoRepository, never()).save(any(Producto.class));
          }

          @Test
          @DisplayName("Reducir stock - producto no encontrado")
          void testReducirStockProductoNoEncontrado() {
               // Given
               when(productoRepository.findById(999L)).thenReturn(Optional.empty());

               // When & Then
               IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    productoService.reducirStock(999L, 1);
               });

               assertEquals("Producto no encontrado con ID: 999", exception.getMessage());
               verify(productoRepository).findById(999L);
               verify(productoRepository, never()).save(any(Producto.class));
          }

          @Test
          @DisplayName("Reducir stock - ID nulo")
          void testReducirStockIdNulo() {
               // When & Then
               IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    productoService.reducirStock(null, 1);
               });

               assertEquals("El ID del producto no puede ser nulo", exception.getMessage());
               verify(productoRepository, never()).findById(any());
          }

          @Test
          @DisplayName("Reducir stock - cantidad nula")
          void testReducirStockCantidadNula() {
               // When & Then
               IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    productoService.reducirStock(1L, null);
               });

               assertEquals("La cantidad vendida debe ser mayor a 0", exception.getMessage());
               verify(productoRepository, never()).findById(any());
          }

          @Test
          @DisplayName("Reducir stock - cantidad cero")
          void testReducirStockCantidadCero() {
               // When & Then
               IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    productoService.reducirStock(1L, 0);
               });

               assertEquals("La cantidad vendida debe ser mayor a 0", exception.getMessage());
               verify(productoRepository, never()).findById(any());
          }

          @Test
          @DisplayName("Reducir stock - cantidad negativa")
          void testReducirStockCantidadNegativa() {
               // When & Then
               IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    productoService.reducirStock(1L, -1);
               });

               assertEquals("La cantidad vendida debe ser mayor a 0", exception.getMessage());
               verify(productoRepository, never()).findById(any());
          }
     }

     @Nested
     @DisplayName("Tests de Actualización de Productos")
     class ActualizacionProductosTests {

          @Test
          @DisplayName("Actualizar producto exitoso")
          void testActualizarProductoExitoso() {
               // Given
               Producto productoActualizado = new Producto();
               productoActualizado.setCategoriaId(2L);
               productoActualizado.setNombre("Torta de Fresa");
               productoActualizado.setDescripcion("Deliciosa torta de fresa");
               productoActualizado.setPrecioVenta(28.00);
               productoActualizado.setCostoProduccion(18.00);
               productoActualizado.setImagenUrl("torta-fresa.jpg");
               productoActualizado.setCantidad(8);

               when(productoRepository.findById(1L)).thenReturn(Optional.of(productoEjemplo));
               when(categoriaRepository.existsById(2L)).thenReturn(true);
               when(productoRepository.save(any(Producto.class))).thenReturn(productoEjemplo);

               // When
               Producto resultado = productoService.update(1L, productoActualizado);

               // Then
               assertNotNull(resultado);
               verify(productoRepository).findById(1L);
               verify(categoriaRepository).existsById(2L);
               verify(productoRepository).save(any(Producto.class));
          }

          @Test
          @DisplayName("Actualizar producto - no encontrado")
          void testActualizarProductoNoEncontrado() {
               // Given
               Producto productoActualizado = new Producto();
               when(productoRepository.findById(999L)).thenReturn(Optional.empty());

               // When & Then
               IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    productoService.update(999L, productoActualizado);
               });

               assertEquals("No existe producto 999", exception.getMessage());
               verify(productoRepository).findById(999L);
               verify(productoRepository, never()).save(any(Producto.class));
          }

          @Test
          @DisplayName("Actualizar producto - categoría no existe")
          void testActualizarProductoCategoriaNoExiste() {
               // Given
               Producto productoActualizado = new Producto();
               productoActualizado.setCategoriaId(999L);
               productoActualizado.setNombre("Torta Test");
               productoActualizado.setDescripcion("Test");
               productoActualizado.setPrecioVenta(20.00);
               productoActualizado.setCostoProduccion(10.00);
               productoActualizado.setCantidad(5);

               when(productoRepository.findById(1L)).thenReturn(Optional.of(productoEjemplo));
               when(categoriaRepository.existsById(999L)).thenReturn(false);

               // When & Then
               IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    productoService.update(1L, productoActualizado);
               });

               assertEquals("No existe la categoría con ID: 999", exception.getMessage());
               verify(productoRepository).findById(1L);
               verify(categoriaRepository).existsById(999L);
               verify(productoRepository, never()).save(any(Producto.class));
          }
     }

     @Nested
     @DisplayName("Tests de Eliminación de Productos")
     class EliminacionProductosTests {

          @Test
          @DisplayName("Eliminar producto exitoso")
          void testEliminarProductoExitoso() {
               // Given
               when(productoRepository.existsById(1L)).thenReturn(true);

               // When
               productoService.delete(1L);

               // Then
               verify(productoRepository).existsById(1L);
               verify(productoRepository).deleteById(1L);
          }

          @Test
          @DisplayName("Eliminar producto - no encontrado")
          void testEliminarProductoNoEncontrado() {
               // Given
               when(productoRepository.existsById(999L)).thenReturn(false);

               // When & Then
               IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    productoService.delete(999L);
               });

               assertEquals("No existe producto 999", exception.getMessage());
               verify(productoRepository).existsById(999L);
               verify(productoRepository, never()).deleteById(any());
          }
     }
}
