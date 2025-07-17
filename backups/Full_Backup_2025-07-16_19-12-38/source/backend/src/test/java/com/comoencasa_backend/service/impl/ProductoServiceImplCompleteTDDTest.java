package com.comoencasa_backend.service.impl;

import com.comoencasa_backend.model.Producto;
import com.comoencasa_backend.repository.ProductoRepository;
import com.comoencasa_backend.repository.CategoriaRepository;
import com.comoencasa_backend.service.ProductoService;
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
class ProductoServiceImplCompleteTDDTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    private Producto productoEjemplo;

    @BeforeEach
    void setUp() {
        // Producto de ejemplo para las pruebas
        productoEjemplo = new Producto();
        productoEjemplo.setId(1L);
        productoEjemplo.setNombre("Torta de Chocolate");
        productoEjemplo.setDescripcion("Deliciosa torta de chocolate casera");
        productoEjemplo.setPrecioVenta(new BigDecimal("25.50"));
        productoEjemplo.setStock(10);
        productoEjemplo.setCategoria("Tortas");
        productoEjemplo.setImagenUrl("torta-chocolate.jpg");
        productoEjemplo.setActivo(true);
        productoEjemplo.setFechaCreacion(LocalDateTime.now());

        // DTO de ejemplo
        productoDTOEjemplo = new ProductoDTO();
        productoDTOEjemplo.setId(1L);
        productoDTOEjemplo.setNombre("Torta de Chocolate");
        productoDTOEjemplo.setDescripcion("Deliciosa torta de chocolate casera");
        productoDTOEjemplo.setPrecioVenta(new BigDecimal("25.50"));
        productoDTOEjemplo.setStock(10);
        productoDTOEjemplo.setCategoria("Tortas");
        productoDTOEjemplo.setImagenUrl("torta-chocolate.jpg");
        productoDTOEjemplo.setActivo(true);
    }

    @Nested
    @DisplayName("Tests de Creación de Productos")
    class CreacionProductosTests {

        @Test
        @DisplayName("Crear producto exitoso con datos válidos")
        void testCrearProductoExitoso() {
            // Given
            when(productoRepository.save(any(Producto.class)))
                    .thenReturn(productoEjemplo);

            // When
            ProductoDTO resultado = productoService.crearProducto(productoDTOEjemplo);

            // Then
            assertNotNull(resultado);
            assertEquals(productoDTOEjemplo.getNombre(), resultado.getNombre());
            assertEquals(productoDTOEjemplo.getPrecioVenta(), resultado.getPrecioVenta());
            verify(productoRepository).save(any(Producto.class));
        }

        @Test
        @DisplayName("Crear producto fallido - datos null")
        void testCrearProductoDatosNull() {
            // Given
            ProductoDTO productoNull = null;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                productoService.crearProducto(productoNull);
            });

            verify(productoRepository, never()).save(any(Producto.class));
        }

        @Test
        @DisplayName("Crear producto fallido - nombre vacío")
        void testCrearProductoNombreVacio() {
            // Given
            productoDTOEjemplo.setNombre("");

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                productoService.crearProducto(productoDTOEjemplo);
            });

            verify(productoRepository, never()).save(any(Producto.class));
        }

        @Test
        @DisplayName("Crear producto fallido - precio negativo")
        void testCrearProductoPrecioNegativo() {
            // Given
            productoDTOEjemplo.setPrecioVenta(new BigDecimal("-10.00"));

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                productoService.crearProducto(productoDTOEjemplo);
            });

            verify(productoRepository, never()).save(any(Producto.class));
        }

        @Test
        @DisplayName("Crear producto fallido - stock negativo")
        void testCrearProductoStockNegativo() {
            // Given
            productoDTOEjemplo.setStock(-5);

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                productoService.crearProducto(productoDTOEjemplo);
            });

            verify(productoRepository, never()).save(any(Producto.class));
        }

        @Test
        @DisplayName("Crear producto con valores límite")
        void testCrearProductoValoresLimite() {
            // Given
            productoDTOEjemplo.setPrecioVenta(new BigDecimal("0.01")); // Precio mínimo
            productoDTOEjemplo.setStock(0); // Stock mínimo

            when(productoRepository.save(any(Producto.class)))
                    .thenReturn(productoEjemplo);

            // When
            ProductoDTO resultado = productoService.crearProducto(productoDTOEjemplo);

            // Then
            assertNotNull(resultado);
            verify(productoRepository).save(any(Producto.class));
        }
    }

    @Nested
    @DisplayName("Tests de Obtención de Productos")
    class ObtenerProductosTests {

        @Test
        @DisplayName("Obtener todos los productos exitoso")
        void testObtenerTodosLosProductos() {
            // Given
            List<Producto> productos = Arrays.asList(productoEjemplo);
            when(productoRepository.findAll()).thenReturn(productos);

            // When
            List<ProductoDTO> resultado = productoService.obtenerTodosLosProductos();

            // Then
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertEquals(productoEjemplo.getNombre(), resultado.get(0).getNombre());
            verify(productoRepository).findAll();
        }

        @Test
        @DisplayName("Obtener producto por ID exitoso")
        void testObtenerProductoPorIdExitoso() {
            // Given
            Long productId = 1L;
            when(productoRepository.findById(productId))
                    .thenReturn(Optional.of(productoEjemplo));

            // When
            Optional<ProductoDTO> resultado = productoService.obtenerProductoPorId(productId);

            // Then
            assertTrue(resultado.isPresent());
            assertEquals(productoEjemplo.getNombre(), resultado.get().getNombre());
            verify(productoRepository).findById(productId);
        }

        @Test
        @DisplayName("Obtener producto por ID - no encontrado")
        void testObtenerProductoPorIdNoEncontrado() {
            // Given
            Long productId = 999L;
            when(productoRepository.findById(productId))
                    .thenReturn(Optional.empty());

            // When
            Optional<ProductoDTO> resultado = productoService.obtenerProductoPorId(productId);

            // Then
            assertFalse(resultado.isPresent());
            verify(productoRepository).findById(productId);
        }

        @Test
        @DisplayName("Obtener productos por categoría")
        void testObtenerProductosPorCategoria() {
            // Given
            String categoria = "Tortas";
            List<Producto> productos = Arrays.asList(productoEjemplo);
            when(productoRepository.findByCategoria(categoria))
                    .thenReturn(productos);

            // When
            List<ProductoDTO> resultado = productoService.obtenerProductosPorCategoria(categoria);

            // Then
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertEquals(categoria, resultado.get(0).getCategoria());
            verify(productoRepository).findByCategoria(categoria);
        }

        @Test
        @DisplayName("Buscar productos por nombre")
        void testBuscarProductosPorNombre() {
            // Given
            String termino = "chocolate";
            List<Producto> productos = Arrays.asList(productoEjemplo);
            when(productoRepository.findByNombreContainingIgnoreCase(termino))
                    .thenReturn(productos);

            // When
            List<ProductoDTO> resultado = productoService.buscarProductosPorNombre(termino);

            // Then
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(productoRepository).findByNombreContainingIgnoreCase(termino);
        }

        @Test
        @DisplayName("Obtener productos activos")
        void testObtenerProductosActivos() {
            // Given
            List<Producto> productos = Arrays.asList(productoEjemplo);
            when(productoRepository.findByActivoTrue())
                    .thenReturn(productos);

            // When
            List<ProductoDTO> resultado = productoService.obtenerProductosActivos();

            // Then
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertTrue(resultado.get(0).getActivo());
            verify(productoRepository).findByActivoTrue();
        }
    }

    @Nested
    @DisplayName("Tests de Actualización de Productos")
    class ActualizacionProductosTests {

        @Test
        @DisplayName("Actualizar producto exitoso")
        void testActualizarProductoExitoso() {
            // Given
            Long productId = 1L;
            ProductoDTO actualizacion = new ProductoDTO();
            actualizacion.setNombre("Torta de Vainilla");
            actualizacion.setPrecioVenta(new BigDecimal("30.00"));

            when(productoRepository.findById(productId))
                    .thenReturn(Optional.of(productoEjemplo));
            when(productoRepository.save(any(Producto.class)))
                    .thenReturn(productoEjemplo);

            // When
            ProductoDTO resultado = productoService.actualizarProducto(productId, actualizacion);

            // Then
            assertNotNull(resultado);
            verify(productoRepository).findById(productId);
            verify(productoRepository).save(any(Producto.class));
        }

        @Test
        @DisplayName("Actualizar producto - no encontrado")
        void testActualizarProductoNoEncontrado() {
            // Given
            Long productId = 999L;
            ProductoDTO actualizacion = new ProductoDTO();
            actualizacion.setNombre("Nuevo Nombre");

            when(productoRepository.findById(productId))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThrows(RuntimeException.class, () -> {
                productoService.actualizarProducto(productId, actualizacion);
            });

            verify(productoRepository).findById(productId);
            verify(productoRepository, never()).save(any(Producto.class));
        }

        @Test
        @DisplayName("Actualizar stock de producto")
        void testActualizarStockProducto() {
            // Given
            Long productId = 1L;
            Integer nuevoStock = 20;

            when(productoRepository.findById(productId))
                    .thenReturn(Optional.of(productoEjemplo));
            when(productoRepository.save(any(Producto.class)))
                    .thenReturn(productoEjemplo);

            // When
            ProductoDTO resultado = productoService.actualizarStock(productId, nuevoStock);

            // Then
            assertNotNull(resultado);
            verify(productoRepository).findById(productId);
            verify(productoRepository).save(any(Producto.class));
        }

        @Test
        @DisplayName("Actualizar stock - stock negativo")
        void testActualizarStockNegativo() {
            // Given
            Long productId = 1L;
            Integer stockNegativo = -5;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                productoService.actualizarStock(productId, stockNegativo);
            });

            verify(productoRepository, never()).findById(any());
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
            Long productId = 1L;

            when(productoRepository.existsById(productId))
                    .thenReturn(true);
            doNothing().when(productoRepository).deleteById(productId);

            // When
            boolean resultado = productoService.eliminarProducto(productId);

            // Then
            assertTrue(resultado);
            verify(productoRepository).existsById(productId);
            verify(productoRepository).deleteById(productId);
        }

        @Test
        @DisplayName("Eliminar producto - no encontrado")
        void testEliminarProductoNoEncontrado() {
            // Given
            Long productId = 999L;

            when(productoRepository.existsById(productId))
                    .thenReturn(false);

            // When
            boolean resultado = productoService.eliminarProducto(productId);

            // Then
            assertFalse(resultado);
            verify(productoRepository).existsById(productId);
            verify(productoRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("Desactivar producto exitoso")
        void testDesactivarProductoExitoso() {
            // Given
            Long productId = 1L;

            when(productoRepository.findById(productId))
                    .thenReturn(Optional.of(productoEjemplo));
            when(productoRepository.save(any(Producto.class)))
                    .thenReturn(productoEjemplo);

            // When
            ProductoDTO resultado = productoService.desactivarProducto(productId);

            // Then
            assertNotNull(resultado);
            verify(productoRepository).findById(productId);
            verify(productoRepository).save(any(Producto.class));
        }
    }

    @Nested
    @DisplayName("Tests de Validaciones de Negocio")
    class ValidacionesNegocioTests {

        @Test
        @DisplayName("Validar disponibilidad de stock")
        void testValidarDisponibilidadStock() {
            // Given
            Long productId = 1L;
            Integer cantidadSolicitada = 5;

            when(productoRepository.findById(productId))
                    .thenReturn(Optional.of(productoEjemplo));

            // When
            boolean resultado = productoService.validarDisponibilidadStock(productId, cantidadSolicitada);

            // Then
            assertTrue(resultado);
            verify(productoRepository).findById(productId);
        }

        @Test
        @DisplayName("Validar disponibilidad de stock - stock insuficiente")
        void testValidarDisponibilidadStockInsuficiente() {
            // Given
            Long productId = 1L;
            Integer cantidadSolicitada = 15; // Mayor al stock disponible

            when(productoRepository.findById(productId))
                    .thenReturn(Optional.of(productoEjemplo));

            // When
            boolean resultado = productoService.validarDisponibilidadStock(productId, cantidadSolicitada);

            // Then
            assertFalse(resultado);
            verify(productoRepository).findById(productId);
        }

        @Test
        @DisplayName("Reducir stock tras venta")
        void testReducirStockTrasVenta() {
            // Given
            Long productId = 1L;
            Integer cantidadVendida = 3;

            when(productoRepository.findById(productId))
                    .thenReturn(Optional.of(productoEjemplo));
            when(productoRepository.save(any(Producto.class)))
                    .thenReturn(productoEjemplo);

            // When
            ProductoDTO resultado = productoService.reducirStock(productId, cantidadVendida);

            // Then
            assertNotNull(resultado);
            verify(productoRepository).findById(productId);
            verify(productoRepository).save(any(Producto.class));
        }

        @Test
        @DisplayName("Reducir stock - cantidad excede stock disponible")
        void testReducirStockCantidadExcede() {
            // Given
            Long productId = 1L;
            Integer cantidadExcesiva = 15;

            when(productoRepository.findById(productId))
                    .thenReturn(Optional.of(productoEjemplo));

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                productoService.reducirStock(productId, cantidadExcesiva);
            });

            verify(productoRepository).findById(productId);
            verify(productoRepository, never()).save(any(Producto.class));
        }
    }

    @Nested
    @DisplayName("Tests de Edge Cases")
    class EdgeCasesTests {

        @Test
        @DisplayName("Manejar lista vacía de productos")
        void testManejarListaVaciaProductos() {
            // Given
            when(productoRepository.findAll())
                    .thenReturn(Arrays.asList());

            // When
            List<ProductoDTO> resultado = productoService.obtenerTodosLosProductos();

            // Then
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
            verify(productoRepository).findAll();
        }

        @Test
        @DisplayName("Manejar excepción en base de datos")
        void testManejarExcepcionBaseDatos() {
            // Given
            when(productoRepository.findAll())
                    .thenThrow(new RuntimeException("Error de conexión a BD"));

            // When & Then
            assertThrows(RuntimeException.class, () -> {
                productoService.obtenerTodosLosProductos();
            });

            verify(productoRepository).findAll();
        }

        @Test
        @DisplayName("Búsqueda con término null")
        void testBusquedaTerminoNull() {
            // Given
            String termino = null;

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                productoService.buscarProductosPorNombre(termino);
            });

            verify(productoRepository, never()).findByNombreContainingIgnoreCase(any());
        }

        @Test
        @DisplayName("Búsqueda con término vacío")
        void testBusquedaTerminoVacio() {
            // Given
            String termino = "";

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                productoService.buscarProductosPorNombre(termino);
            });

            verify(productoRepository, never()).findByNombreContainingIgnoreCase(any());
        }

        @Test
        @DisplayName("Precio con muchos decimales")
        void testPrecioConMuchosDecimales() {
            // Given
            productoDTOEjemplo.setPrecioVenta(new BigDecimal("25.123456789"));

            when(productoRepository.save(any(Producto.class)))
                    .thenReturn(productoEjemplo);

            // When
            ProductoDTO resultado = productoService.crearProducto(productoDTOEjemplo);

            // Then
            assertNotNull(resultado);
            verify(productoRepository).save(any(Producto.class));
        }
    }

    @Nested
    @DisplayName("Tests de Performance")
    class PerformanceTests {

        @Test
        @DisplayName("Búsqueda con muchos resultados")
        void testBusquedaConMuchosResultados() {
            // Given
            String termino = "torta";
            List<Producto> muchosProductos = Arrays.asList(
                productoEjemplo, productoEjemplo, productoEjemplo
            );

            when(productoRepository.findByNombreContainingIgnoreCase(termino))
                    .thenReturn(muchosProductos);

            // When
            List<ProductoDTO> resultado = productoService.buscarProductosPorNombre(termino);

            // Then
            assertNotNull(resultado);
            assertEquals(3, resultado.size());
            verify(productoRepository).findByNombreContainingIgnoreCase(termino);
        }

        @Test
        @DisplayName("Procesamiento de producto con datos muy largos")
        void testProcesamientoProductoDatosLargos() {
            // Given
            productoDTOEjemplo.setNombre("A".repeat(255));
            productoDTOEjemplo.setDescripcion("B".repeat(1000));

            when(productoRepository.save(any(Producto.class)))
                    .thenReturn(productoEjemplo);

            // When
            ProductoDTO resultado = productoService.crearProducto(productoDTOEjemplo);

            // Then
            assertNotNull(resultado);
            verify(productoRepository).save(any(Producto.class));
        }
    }
}
