package com.comoencasa_backend.repository;

import com.comoencasa_backend.model.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static com.comoencasa_backend.testutil.TestDataFactory.*;

/**
 * Tests de integración TDD para ProductoRepository
 * Probando la persistencia real con base de datos H2
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ProductoRepository Integration Tests")
class ProductoRepositoryTDDIT {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductoRepository productoRepository;

    @BeforeEach
    void setUp() {
        // Limpiar datos antes de cada test
        entityManager.clear();
    }

    @Nested
    @DisplayName("Operaciones CRUD básicas")
    class OperacionesCrud {

        @Test
        @DisplayName("RED: Debería guardar un producto correctamente")
        void deberiaGuardarProductoCorrectamente() {
            // Given
            Producto producto = unProducto()
                    .conNombre("Torta de Chocolate")
                    .conPrecio(25.50)
                    .conCosto(15.00)
                    .build();

            // When
            Producto productoGuardado = productoRepository.save(producto);

            // Then
            assertThat(productoGuardado)
                    .satisfies(p -> {
                        assertThat(p.getId()).isNotNull();
                        assertThat(p.getNombre()).isEqualTo("Torta de Chocolate");
                        assertThat(p.getPrecioVenta()).isEqualTo(25.50);
                        assertThat(p.getCostoProduccion()).isEqualTo(15.00);
                        assertThat(p.getDisponible()).isTrue();
                    });
        }

        @Test
        @DisplayName("GREEN: Debería encontrar producto por ID")
        void deberiaEncontrarProductoPorId() {
            // Given
            Producto producto = unProducto()
                    .conNombre("Torta de Vainilla")
                    .build();
            
            Producto productoGuardado = entityManager.persistAndFlush(producto);

            // When
            Optional<Producto> resultado = productoRepository.findById(productoGuardado.getId());

            // Then
            assertThat(resultado)
                    .isPresent()
                    .get()
                    .satisfies(p -> {
                        assertThat(p.getNombre()).isEqualTo("Torta de Vainilla");
                    });
        }

        @Test
        @DisplayName("REFACTOR: Debería eliminar producto correctamente")
        void deberiaEliminarProductoCorrectamente() {
            // Given
            Producto producto = unProducto().build();
            Producto productoGuardado = entityManager.persistAndFlush(producto);
            Long id = productoGuardado.getId();

            // When
            productoRepository.deleteById(id);
            entityManager.flush();

            // Then
            Optional<Producto> resultado = productoRepository.findById(id);
            assertThat(resultado).isEmpty();
        }
    }

    @Nested
    @DisplayName("Consultas personalizadas")
    class ConsultasPersonalizadas {

        @Test
        @DisplayName("RED: Debería encontrar solo productos disponibles")
        void deberiaEncontrarSoloProductosDisponibles() {
            // Given
            Producto disponible = unProducto()
                    .conNombre("Producto Disponible")
                    .build();
            
            Producto noDisponible = unProducto()
                    .conNombre("Producto No Disponible")
                    .noDisponible()
                    .build();

            entityManager.persist(disponible);
            entityManager.persist(noDisponible);
            entityManager.flush();

            // When
            List<Producto> resultado = productoRepository.findByDisponibleTrue();

            // Then
            assertThat(resultado)
                    .hasSize(1)
                    .extracting(Producto::getNombre)
                    .containsExactly("Producto Disponible");
        }

        @Test
        @DisplayName("GREEN: Debería encontrar productos por categoría disponibles")
        void deberiaEncontrarProductosPorCategoriaDisponibles() {
            // Given
            Long categoriaPostres = 1L;
            Long categoriaComidas = 2L;

            Producto tortaDisponible = unProducto()
                    .conNombre("Torta")
                    .conCategoria(categoriaPostres)
                    .build();

            Producto cupcakeDisponible = unProducto()
                    .conNombre("Cupcake")
                    .conCategoria(categoriaPostres)
                    .build();

            Producto pizzaDisponible = unProducto()
                    .conNombre("Pizza")
                    .conCategoria(categoriaComidas)
                    .build();

            Producto brownieNoDisponible = unProducto()
                    .conNombre("Brownie")
                    .conCategoria(categoriaPostres)
                    .noDisponible()
                    .build();

            entityManager.persist(tortaDisponible);
            entityManager.persist(cupcakeDisponible);
            entityManager.persist(pizzaDisponible);
            entityManager.persist(brownieNoDisponible);
            entityManager.flush();

            // When
            List<Producto> postresDisponibles = productoRepository
                    .findByCategoriaIdAndDisponibleTrue(categoriaPostres);

            // Then
            assertThat(postresDisponibles)
                    .hasSize(2)
                    .extracting(Producto::getNombre)
                    .containsExactlyInAnyOrder("Torta", "Cupcake");
        }

        @Test
        @DisplayName("REFACTOR: Debería manejar búsquedas con categorías sin productos")
        void deberiaManejarBusquedasConCategoriasSinProductos() {
            // Given
            Long categoriaSinProductos = 999L;

            // When
            List<Producto> resultado = productoRepository
                    .findByCategoriaIdAndDisponibleTrue(categoriaSinProductos);

            // Then
            assertThat(resultado).isEmpty();
        }
    }

    @Nested
    @DisplayName("Validaciones de integridad")
    class ValidacionesIntegridad {

        @Test
        @DisplayName("Debería validar que el nombre es requerido")
        void deberiaValidarQueElNombreEsRequerido() {
            // Given
            Producto producto = unProducto()
                    .conNombre(null)
                    .build();

            // When & Then
            assertThatThrownBy(() -> {
                entityManager.persistAndFlush(producto);
            }).hasMessageContaining("nombre");
        }

        @Test
        @DisplayName("Debería validar que el precio de venta es requerido")
        void deberiaValidarQueElPrecioVentaEsRequerido() {
            // Given
            Producto producto = unProducto()
                    .conPrecio(null)
                    .build();

            // When & Then
            assertThatThrownBy(() -> {
                entityManager.persistAndFlush(producto);
            }).hasMessageContaining("precio");
        }

        @Test
        @DisplayName("Debería validar que la categoría es requerida")
        void deberiaValidarQueLaCategoriaEsRequerida() {
            // Given
            Producto producto = unProducto()
                    .conCategoria(null)
                    .build();

            // When & Then
            assertThatThrownBy(() -> {
                entityManager.persistAndFlush(producto);
            }).hasMessageContaining("categoria");
        }
    }

    @Nested
    @DisplayName("Performance y escalabilidad")
    class PerformanceYEscalabilidad {

        @Test
        @DisplayName("Debería manejar múltiples productos eficientemente")
        void deberiaManejarMultiplesProductosEficientemente() {
            // Given - Crear 100 productos
            for (int i = 1; i <= 100; i++) {
                Producto producto = unProducto()
                        .conNombre("Producto " + i)
                        .conCategoria((long) (i % 5 + 1)) // 5 categorías diferentes
                        .build();
                entityManager.persist(producto);
                
                if (i % 20 == 0) {
                    entityManager.flush();
                    entityManager.clear();
                }
            }
            entityManager.flush();

            // When
            List<Producto> todosDisponibles = productoRepository.findByDisponibleTrue();
            List<Producto> categoria1 = productoRepository.findByCategoriaIdAndDisponibleTrue(1L);

            // Then
            assertThat(todosDisponibles).hasSize(100);
            assertThat(categoria1).hasSize(20);
        }
    }
}
