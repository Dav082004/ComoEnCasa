package com.comoencasa_backend.service;

import com.comoencasa_backend.dao.CarritoDAO;
import com.comoencasa_backend.dto.CarritoDTO;
import com.comoencasa_backend.dto.CarritoItemDTO;
import com.comoencasa_backend.model.Producto;
import com.comoencasa_backend.service.impl.CarritoServiceImpl;
import com.comoencasa_backend.service.ProductoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CarritoService TDD Tests")
class CarritoServiceTDDTest {

    @Mock
    private CarritoDAO carritoDAO;
    
    @Mock
    private ProductoService productoService;

    @InjectMocks
    private CarritoServiceImpl carritoService;    private Producto productoTest;
    private String sessionId;
    private Map<String, CarritoDTO> carritoStorage; // Simular almacenamiento del carrito

    @BeforeEach
    void setUp() {
        sessionId = "test-session-123";
        carritoStorage = new HashMap<>();
        
        productoTest = new Producto();
        productoTest.setId(1L);
        productoTest.setNombre("Torta de Chocolate");
        productoTest.setDescripcion("Deliciosa torta de chocolate");
        productoTest.setPrecioVenta(25.50);
        productoTest.setImagenUrl("/images/torta.jpg");
        productoTest.setDisponible(true);        // Configurar mocks para simular almacenamiento persistente del carrito
        lenient().when(carritoDAO.obtenerCarrito(anyString())).thenAnswer(invocation -> {
            String sessionId = invocation.getArgument(0);
            return Optional.ofNullable(carritoStorage.get(sessionId));
        });
        
        lenient().doAnswer(invocation -> {
            String sessionId = invocation.getArgument(0);
            CarritoDTO carrito = invocation.getArgument(1);
            carritoStorage.put(sessionId, carrito);
            return null;
        }).when(carritoDAO).guardarCarrito(anyString(), any(CarritoDTO.class));
    }

    @Nested
    @DisplayName("Agregar producto al carrito")
    class AgregarProducto {

        @Test
        @DisplayName("RED: Debería lanzar excepción si sessionId es nulo")
        void deberiaLanzarExcepcionSiSessionIdEsNulo() {
            // When & Then
            assertThatThrownBy(() -> carritoService.agregarProducto(null, 1L, 1, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Session ID no puede ser nulo");
        }

        @Test
        @DisplayName("RED: Debería lanzar excepción si productoId es nulo")
        void deberiaLanzarExcepcionSiProductoIdEsNulo() {
            // When & Then
            assertThatThrownBy(() -> carritoService.agregarProducto(sessionId, null, 1, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Producto ID no puede ser nulo");
        }

        @Test
        @DisplayName("RED: Debería lanzar excepción si cantidad es cero o negativa")
        void deberiaLanzarExcepcionSiCantidadEsInvalida() {
            // When & Then
            assertThatThrownBy(() -> carritoService.agregarProducto(sessionId, 1L, 0, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cantidad debe ser mayor a 0");

            assertThatThrownBy(() -> carritoService.agregarProducto(sessionId, 1L, -1, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cantidad debe ser mayor a 0");
        }

        @Test
        @DisplayName("RED: Debería lanzar excepción si producto no existe")
        void deberiaLanzarExcepcionSiProductoNoExiste() {
            // Given
            when(productoService.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> carritoService.agregarProducto(sessionId, 999L, 1, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Producto no encontrado");
        }

        @Test
        @DisplayName("RED: Debería lanzar excepción si producto no está disponible")
        void deberiaLanzarExcepcionSiProductoNoDisponible() {
            // Given
            productoTest.setDisponible(false);
            when(productoService.findById(1L)).thenReturn(Optional.of(productoTest));

            // When & Then
            assertThatThrownBy(() -> carritoService.agregarProducto(sessionId, 1L, 1, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Producto no disponible");
        }

        @Test
        @DisplayName("GREEN: Debería agregar producto nuevo al carrito exitosamente")
        void deberiaAgregarProductoNuevoExitosamente() {
            // Given
            when(productoService.findById(1L)).thenReturn(Optional.of(productoTest));

            // When
            CarritoDTO resultado = carritoService.agregarProducto(sessionId, 1L, 2, "Sin azúcar extra");

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getSessionId()).isEqualTo(sessionId);
            assertThat(resultado.getItems()).hasSize(1);
            assertThat(resultado.getTotalItems()).isEqualTo(2);
            assertThat(resultado.getSubtotal()).isEqualTo(51.0); // 25.50 * 2

            CarritoItemDTO item = resultado.getItems().get(0);
            assertThat(item.getProductoId()).isEqualTo(1L);
            assertThat(item.getNombre()).isEqualTo("Torta de Chocolate");
            assertThat(item.getCantidad()).isEqualTo(2);
            assertThat(item.getComentarios()).isEqualTo("Sin azúcar extra");
        }        @Test
        @DisplayName("GREEN: Debería establecer cantidad exacta si producto ya existe en carrito")
        void deberiaActualizarCantidadSiProductoYaExiste() {
            // Given
            when(productoService.findById(1L)).thenReturn(Optional.of(productoTest));

            // Agregar producto primera vez
            carritoService.agregarProducto(sessionId, 1L, 1, "Comentario inicial");

            // When - Agregar mismo producto segunda vez (establecer nueva cantidad)
            CarritoDTO resultado = carritoService.agregarProducto(sessionId, 1L, 2, "Comentario actualizado");

            // Then
            assertThat(resultado.getItems()).hasSize(1);
            CarritoItemDTO item = resultado.getItems().get(0);
            assertThat(item.getCantidad()).isEqualTo(2); // Cantidad establecida, no sumada
            assertThat(item.getComentarios()).isEqualTo("Comentario actualizado");
        }

        @Test
        @DisplayName("RED: Debería lanzar excepción si stock insuficiente")
        void deberiaLanzarExcepcionSiStockInsuficiente() {
            // Given
            productoTest.setCantidad(5); // Stock limitado
            when(productoService.findById(1L)).thenReturn(Optional.of(productoTest));

            // When & Then
            assertThatThrownBy(() -> carritoService.agregarProducto(sessionId, 1L, 10, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Stock insuficiente")
                .hasMessageContaining("Disponible: 5")
                .hasMessageContaining("Solicitado: 10");
        }

        @Test
        @DisplayName("GREEN: Debería manejar comentarios nulos correctamente")
        void deberiaManejarComentariosNulos() {
            // Given
            when(productoService.findById(1L)).thenReturn(Optional.of(productoTest));

            // When
            CarritoDTO resultado = carritoService.agregarProducto(sessionId, 1L, 1, null);

            // Then
            assertThat(resultado.getItems()).hasSize(1);
            assertThat(resultado.getItems().get(0).getComentarios()).isNull();
        }

        @Test
        @DisplayName("GREEN: Debería manejar comentarios vacíos correctamente")
        void deberiaManejarComentariosVacios() {
            // Given
            when(productoService.findById(1L)).thenReturn(Optional.of(productoTest));

            // When
            CarritoDTO resultado = carritoService.agregarProducto(sessionId, 1L, 1, "   ");

            // Then
            assertThat(resultado.getItems()).hasSize(1);
            assertThat(resultado.getItems().get(0).getComentarios()).isEqualTo("   ");
        }

        @Test
        @DisplayName("GREEN: No debería actualizar comentarios si son vacíos en producto existente")
        void noDeberiaActualizarComentariosSiSonVacios() {
            // Given
            when(productoService.findById(1L)).thenReturn(Optional.of(productoTest));
            carritoService.agregarProducto(sessionId, 1L, 1, "Comentario original");

            // When - Agregar mismo producto con comentario vacío
            CarritoDTO resultado = carritoService.agregarProducto(sessionId, 1L, 2, "   ");

            // Then
            assertThat(resultado.getItems()).hasSize(1);
            CarritoItemDTO item = resultado.getItems().get(0);
            assertThat(item.getCantidad()).isEqualTo(2);
            assertThat(item.getComentarios()).isEqualTo("Comentario original"); // No debería cambiar
        }
    }

    @Nested
    @DisplayName("Actualizar cantidad de producto")
    class ActualizarCantidad {

        @Test
        @DisplayName("RED: Debería lanzar excepción si sessionId es nulo")
        void deberiaLanzarExcepcionSiSessionIdEsNulo() {
            // When & Then
            assertThatThrownBy(() -> carritoService.actualizarCantidad(null, 1L, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("sessionId no puede ser nulo");
        }

        @Test
        @DisplayName("RED: Debería lanzar excepción si sessionId es vacío")
        void deberiaLanzarExcepcionSiSessionIdEsVacio() {
            // When & Then
            assertThatThrownBy(() -> carritoService.actualizarCantidad("   ", 1L, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("sessionId no puede ser nulo");
        }

        @Test
        @DisplayName("RED: Debería lanzar excepción si productoId es nulo")
        void deberiaLanzarExcepcionSiProductoIdEsNulo() {
            // When & Then
            assertThatThrownBy(() -> carritoService.actualizarCantidad(sessionId, null, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("producto no puede ser nulo");
        }

        @Test
        @DisplayName("RED: Debería lanzar excepción si cantidad es negativa")
        void deberiaLanzarExcepcionSiCantidadEsNegativa() {
            // When & Then
            assertThatThrownBy(() -> carritoService.actualizarCantidad(sessionId, 1L, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cantidad no puede ser nula o negativa");
        }

        @Test
        @DisplayName("RED: Debería lanzar excepción si cantidad es nula")
        void deberiaLanzarExcepcionSiCantidadEsNula() {
            // When & Then
            assertThatThrownBy(() -> carritoService.actualizarCantidad(sessionId, 1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cantidad no puede ser nula o negativa");
        }

        @Test
        @DisplayName("RED: Debería lanzar excepción si producto no está en carrito")
        void deberiaLanzarExcepcionSiProductoNoEstaEnCarrito() {
            // Given
            when(productoService.findById(1L)).thenReturn(Optional.of(productoTest));
            carritoService.agregarProducto(sessionId, 1L, 1, "");

            // When & Then
            assertThatThrownBy(() -> carritoService.actualizarCantidad(sessionId, 999L, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Producto no encontrado en el carrito");
        }

        @Test
        @DisplayName("RED: Debería lanzar excepción si stock insuficiente en actualización")
        void deberiaLanzarExcepcionSiStockInsuficienteEnActualizacion() {
            // Given
            productoTest.setCantidad(3); // Stock limitado
            when(productoService.findById(1L)).thenReturn(Optional.of(productoTest));
            carritoService.agregarProducto(sessionId, 1L, 1, "");

            // When & Then
            assertThatThrownBy(() -> carritoService.actualizarCantidad(sessionId, 1L, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Stock insuficiente")
                .hasMessageContaining("Disponible: 3")
                .hasMessageContaining("Solicitado: 5");
        }

        @Test
        @DisplayName("GREEN: Debería actualizar cantidad exitosamente")
        void deberiaActualizarCantidadExitosamente() {
            // Given
            when(productoService.findById(1L)).thenReturn(Optional.of(productoTest));
            carritoService.agregarProducto(sessionId, 1L, 1, "");

            // When
            CarritoDTO resultado = carritoService.actualizarCantidad(sessionId, 1L, 5);

            // Then
            assertThat(resultado.getItems()).hasSize(1);
            assertThat(resultado.getItems().get(0).getCantidad()).isEqualTo(5);
            assertThat(resultado.getTotalItems()).isEqualTo(5);
        }

        @Test
        @DisplayName("GREEN: Debería eliminar producto si nueva cantidad es 0")
        void deberiaEliminarProductoSiCantidadEsCero() {
            // Given
            when(productoService.findById(1L)).thenReturn(Optional.of(productoTest));
            carritoService.agregarProducto(sessionId, 1L, 1, "");

            // When
            CarritoDTO resultado = carritoService.actualizarCantidad(sessionId, 1L, 0);

            // Then
            assertThat(resultado.getItems()).isEmpty();
            assertThat(resultado.getTotalItems()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("Eliminar producto del carrito")
    class EliminarProducto {

        @Test
        @DisplayName("RED: Debería lanzar excepción si sessionId es nulo")
        void deberiaLanzarExcepcionSiSessionIdEsNulo() {
            // When & Then
            assertThatThrownBy(() -> carritoService.eliminarProducto(null, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("sessionId no puede ser nulo");
        }

        @Test
        @DisplayName("RED: Debería lanzar excepción si sessionId es vacío")
        void deberiaLanzarExcepcionSiSessionIdEsVacio() {
            // When & Then
            assertThatThrownBy(() -> carritoService.eliminarProducto("   ", 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("sessionId no puede ser nulo");
        }

        @Test
        @DisplayName("RED: Debería lanzar excepción si productoId es nulo")
        void deberiaLanzarExcepcionSiProductoIdEsNulo() {
            // When & Then
            assertThatThrownBy(() -> carritoService.eliminarProducto(sessionId, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("producto no puede ser nulo");
        }

        @Test
        @DisplayName("RED: Debería lanzar excepción si producto no está en carrito")
        void deberiaLanzarExcepcionSiProductoNoEstaEnCarrito() {
            // Given
            when(productoService.findById(1L)).thenReturn(Optional.of(productoTest));
            carritoService.agregarProducto(sessionId, 1L, 2, "");

            // When & Then
            assertThatThrownBy(() -> carritoService.eliminarProducto(sessionId, 999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Producto no encontrado en el carrito");
        }

        @Test
        @DisplayName("GREEN: Debería eliminar producto exitosamente")
        void deberiaEliminarProductoExitosamente() {
            // Given
            when(productoService.findById(1L)).thenReturn(Optional.of(productoTest));
            carritoService.agregarProducto(sessionId, 1L, 2, "");

            // When
            CarritoDTO resultado = carritoService.eliminarProducto(sessionId, 1L);

            // Then
            assertThat(resultado.getItems()).isEmpty();
            assertThat(resultado.getTotalItems()).isEqualTo(0);
            assertThat(resultado.getTotal()).isEqualTo(0.0);
        }
    }

    @Nested
    @DisplayName("Obtener carrito")
    class ObtenerCarrito {

        @Test
        @DisplayName("GREEN: Debería crear carrito nuevo si no existe")
        void deberiaCrearCarritoNuevoSiNoExiste() {
            // When
            CarritoDTO resultado = carritoService.obtenerCarrito("nueva-session");

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getSessionId()).isEqualTo("nueva-session");
            assertThat(resultado.getItems()).isEmpty();
            assertThat(resultado.getTotalItems()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("Limpiar carrito")
    class LimpiarCarrito {

        @Test
        @DisplayName("GREEN: Debería limpiar carrito exitosamente")
        void deberiaLimpiarCarritoExitosamente() {
            // Given
            when(productoService.findById(1L)).thenReturn(Optional.of(productoTest));
            carritoService.agregarProducto(sessionId, 1L, 2, "");

            // When
            CarritoDTO resultado = carritoService.limpiarCarrito(sessionId);

            // Then
            assertThat(resultado.getItems()).isEmpty();
            assertThat(resultado.getTotalItems()).isEqualTo(0);
            assertThat(resultado.getTotal()).isEqualTo(0.0);
        }
    }

    @Nested
    @DisplayName("Cálculos de totales")
    class CalculosTotales {

        @Test
        @DisplayName("GREEN: Debería calcular totales correctamente")
        void deberiaCalcularTotalesCorrectamente() {
            // Given
            when(productoService.findById(1L)).thenReturn(Optional.of(productoTest));

            // When
            CarritoDTO resultado = carritoService.agregarProducto(sessionId, 1L, 2, "");

            // Then
            double expectedSubtotal = 25.50 * 2; // 51.0
            double expectedIgv = expectedSubtotal * 0.18; // 9.18
            double expectedTotal = expectedSubtotal + expectedIgv; // 60.18

            assertThat(resultado.getSubtotal()).isEqualTo(expectedSubtotal);
            assertThat(resultado.getIgv()).isCloseTo(expectedIgv, within(0.01));
            assertThat(resultado.getTotal()).isCloseTo(expectedTotal, within(0.01));
        }
    }
}
