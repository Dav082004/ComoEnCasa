package com.comoencasa_backend.service;

import com.comoencasa_backend.model.Producto;
import com.comoencasa_backend.repository.ProductoRepository;
import com.comoencasa_backend.service.impl.ProductoServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.comoencasa_backend.testutil.TestDataFactory.*;

/**
 * Tests TDD para ProductoService
 * Siguiendo la metodología Red-Green-Refactor
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductoService TDD Tests")
class ProductoServiceTDDTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    @Nested
    @DisplayName("Buscar todos los productos disponibles")
    class BuscarTodosDisponibles {

        @Test
        @DisplayName("RED: Debería retornar lista vacía cuando no hay productos disponibles")
        void deberiaRetornarListaVaciaCuandoNoHayProductosDisponibles() {
            // Given
            when(productoRepository.findByDisponibleTrue()).thenReturn(Collections.emptyList());

            // When
            List<Producto> resultado = productoService.findAllAvailable();

            // Then
            assertThat(resultado).isEmpty();
            verify(productoRepository).findByDisponibleTrue();
        }

        @Test
        @DisplayName("GREEN: Debería retornar lista de productos disponibles")
        void deberiaRetornarListaDeProductosDisponibles() {
            // Given
            Producto producto1 = unProducto()
                    .conNombre("Torta de Chocolate")
                    .conPrecio(25.50)
                    .build();
            
            Producto producto2 = unProducto()
                    .conNombre("Torta de Vainilla")
                    .conPrecio(23.00)
                    .build();

            when(productoRepository.findByDisponibleTrue())
                    .thenReturn(Arrays.asList(producto1, producto2));

            // When
            List<Producto> resultado = productoService.findAllAvailable();

            // Then
            assertThat(resultado)
                    .hasSize(2)
                    .extracting(Producto::getNombre)
                    .containsExactly("Torta de Chocolate", "Torta de Vainilla");
            
            verify(productoRepository).findByDisponibleTrue();
        }

        @Test
        @DisplayName("REFACTOR: Debería manejar productos con diferentes estados")
        void deberiaManejarProductosConDiferentesEstados() {
            // Given
            Producto productoDisponible = unProducto()
                    .conNombre("Producto Disponible")
                    .build();

            when(productoRepository.findByDisponibleTrue())
                    .thenReturn(Arrays.asList(productoDisponible));

            // When
            List<Producto> resultado = productoService.findAllAvailable();

            // Then
            assertThat(resultado)
                    .hasSize(1)
                    .allMatch(Producto::getDisponible);
        }
    }

    @Nested
    @DisplayName("Buscar producto por ID")
    class BuscarPorId {

        @Test
        @DisplayName("RED: Debería retornar Optional.empty() cuando el producto no existe")
        void deberiaRetornarOptionalVacioCuandoProductoNoExiste() {
            // Given
            Long idInexistente = 999L;
            when(productoRepository.findById(idInexistente)).thenReturn(Optional.empty());

            // When
            Optional<Producto> resultado = productoService.findById(idInexistente);

            // Then
            assertThat(resultado).isEmpty();
            verify(productoRepository).findById(idInexistente);
        }

        @Test
        @DisplayName("GREEN: Debería retornar el producto cuando existe")
        void deberiaRetornarProductoCuandoExiste() {
            // Given
            Long idExistente = 1L;
            Producto producto = unProducto()
                    .conId(idExistente)
                    .conNombre("Torta Existente")
                    .build();

            when(productoRepository.findById(idExistente)).thenReturn(Optional.of(producto));

            // When
            Optional<Producto> resultado = productoService.findById(idExistente);

            // Then
            assertThat(resultado)
                    .isPresent()
                    .get()
                    .satisfies(p -> {
                        assertThat(p.getId()).isEqualTo(idExistente);
                        assertThat(p.getNombre()).isEqualTo("Torta Existente");
                    });
            
            verify(productoRepository).findById(idExistente);
        }
    }

    @Nested
    @DisplayName("Buscar productos por categoría")
    class BuscarPorCategoria {

        @Test
        @DisplayName("RED: Debería retornar lista vacía cuando no hay productos en la categoría")
        void deberiaRetornarListaVaciaCuandoNoHayProductosEnCategoria() {
            // Given
            Long categoriaId = 1L;
            when(productoRepository.findByCategoriaIdAndDisponibleTrue(categoriaId))
                    .thenReturn(Collections.emptyList());

            // When
            List<Producto> resultado = productoService.findByCategoriaId(categoriaId);

            // Then
            assertThat(resultado).isEmpty();
            verify(productoRepository).findByCategoriaIdAndDisponibleTrue(categoriaId);
        }

        @Test
        @DisplayName("GREEN: Debería retornar productos de la categoría especificada")
        void deberiaRetornarProductosDeLaCategoriaEspecificada() {
            // Given
            Long categoriaId = 1L;
            Producto producto1 = unProducto()
                    .conNombre("Torta de Chocolate")
                    .conCategoria(categoriaId)
                    .build();
            
            Producto producto2 = unProducto()
                    .conNombre("Torta de Fresa")
                    .conCategoria(categoriaId)
                    .build();

            when(productoRepository.findByCategoriaIdAndDisponibleTrue(categoriaId))
                    .thenReturn(Arrays.asList(producto1, producto2));

            // When
            List<Producto> resultado = productoService.findByCategoriaId(categoriaId);

            // Then
            assertThat(resultado)
                    .hasSize(2)
                    .allMatch(p -> p.getCategoriaId().equals(categoriaId))
                    .allMatch(Producto::getDisponible);
            
            verify(productoRepository).findByCategoriaIdAndDisponibleTrue(categoriaId);
        }

        @Test
        @DisplayName("REFACTOR: Debería manejar categorías con diferentes tipos de productos")
        void deberiaManejarCategoriasConDiferentesTiposDeProductos() {
            // Given
            Long categoriaPostres = 1L;
            Producto torta = unProducto()
                    .conNombre("Torta")
                    .conCategoria(categoriaPostres)
                    .conPrecio(25.0)
                    .build();
            
            Producto cupcake = unProducto()
                    .conNombre("Cupcake")
                    .conCategoria(categoriaPostres)
                    .conPrecio(5.0)
                    .build();

            when(productoRepository.findByCategoriaIdAndDisponibleTrue(categoriaPostres))
                    .thenReturn(Arrays.asList(torta, cupcake));

            // When
            List<Producto> resultado = productoService.findByCategoriaId(categoriaPostres);

            // Then
            assertThat(resultado)
                    .hasSize(2)
                    .extracting(Producto::getNombre)
                    .containsExactlyInAnyOrder("Torta", "Cupcake");
        }
    }

    @Nested
    @DisplayName("Casos Edge y Validaciones")
    class CasosEdgeYValidaciones {

        @Test
        @DisplayName("Debería manejar IDs nulos correctamente")
        void deberiaManejarIdsNulosCorrectamente() {
            // When & Then
            assertThatThrownBy(() -> productoService.findById(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Debería manejar categoría ID nulo correctamente")
        void deberiaManejarCategoriaIdNuloCorrectamente() {
            // When & Then
            assertThatThrownBy(() -> productoService.findByCategoriaId(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
