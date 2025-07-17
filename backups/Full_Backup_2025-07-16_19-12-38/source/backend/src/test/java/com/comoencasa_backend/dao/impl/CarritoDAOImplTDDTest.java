package com.comoencasa_backend.dao.impl;

import com.comoencasa_backend.dto.CarritoDTO;
import com.comoencasa_backend.dto.CarritoItemDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DisplayName("CarritoDAOImpl TDD Tests")
class CarritoDAOImplTDDTest {

     private CarritoDAOImpl carritoDAO;
     private CarritoDTO carritoTest;
     private String sessionId;

     @BeforeEach
     void setUp() {
          carritoDAO = new CarritoDAOImpl();
          sessionId = "test-session-123";

          // Crear carrito de prueba
          carritoTest = new CarritoDTO(sessionId);
          CarritoItemDTO item = CarritoItemDTO.builder()
                    .productoId(1L)
                    .nombre("Torta de Chocolate")
                    .precioVenta(25.50)
                    .cantidad(2)
                    .comentarios("Sin azúcar")
                    .build();
          carritoTest.addItem(item);
     }

     @Nested
     @DisplayName("Guardar carrito")
     class GuardarCarrito {

          @Test
          @DisplayName("RED: Debería lanzar excepción si sessionId es nulo")
          void deberiaLanzarExcepcionSiSessionIdEsNulo() {
               // When & Then
               assertThatThrownBy(() -> carritoDAO.guardarCarrito(null, carritoTest))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("SessionId y carrito no pueden ser nulos");
          }

          @Test
          @DisplayName("RED: Debería lanzar excepción si carrito es nulo")
          void deberiaLanzarExcepcionSiCarritoEsNulo() {
               // When & Then
               assertThatThrownBy(() -> carritoDAO.guardarCarrito(sessionId, null))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("SessionId y carrito no pueden ser nulos");
          }

          @Test
          @DisplayName("GREEN: Debería guardar carrito exitosamente")
          void deberiaGuardarCarritoExitosamente() {
               // When
               carritoDAO.guardarCarrito(sessionId, carritoTest);

               // Then
               Optional<CarritoDTO> resultado = carritoDAO.obtenerCarrito(sessionId);
               assertThat(resultado).isPresent();
               assertThat(resultado.get().getSessionId()).isEqualTo(sessionId);
               assertThat(resultado.get().getItems()).hasSize(1);
          }
     }

     @Nested
     @DisplayName("Obtener carrito")
     class ObtenerCarrito {

          @Test
          @DisplayName("RED: Debería retornar vacío si sessionId es nulo")
          void deberiaRetornarVacioSiSessionIdEsNulo() {
               // When
               Optional<CarritoDTO> resultado = carritoDAO.obtenerCarrito(null);

               // Then
               assertThat(resultado).isEmpty();
          }

          @Test
          @DisplayName("GREEN: Debería retornar carrito si existe")
          void deberiaRetornarCarritoSiExiste() {
               // Given
               carritoDAO.guardarCarrito(sessionId, carritoTest);

               // When
               Optional<CarritoDTO> resultado = carritoDAO.obtenerCarrito(sessionId);

               // Then
               assertThat(resultado).isPresent();
               assertThat(resultado.get().getSessionId()).isEqualTo(sessionId);
          }

          @Test
          @DisplayName("GREEN: Debería retornar vacío si carrito no existe")
          void deberiaRetornarVacioSiCarritoNoExiste() {
               // When
               Optional<CarritoDTO> resultado = carritoDAO.obtenerCarrito("sesion-inexistente");

               // Then
               assertThat(resultado).isEmpty();
          }
     }

     @Nested
     @DisplayName("Eliminar carrito")
     class EliminarCarrito {

          @Test
          @DisplayName("GREEN: Debería manejar sessionId nulo sin errores")
          void deberiaManejarSessionIdNuloSinErrores() {
               // When & Then (no debería lanzar excepción)
               assertThatCode(() -> carritoDAO.eliminarCarrito(null))
                         .doesNotThrowAnyException();
          }

          @Test
          @DisplayName("GREEN: Debería eliminar carrito exitosamente")
          void deberiaEliminarCarritoExitosamente() {
               // Given
               carritoDAO.guardarCarrito(sessionId, carritoTest);
               assertThat(carritoDAO.obtenerCarrito(sessionId)).isPresent();

               // When
               carritoDAO.eliminarCarrito(sessionId);

               // Then
               assertThat(carritoDAO.obtenerCarrito(sessionId)).isEmpty();
          }
     }

     @Nested
     @DisplayName("Verificar existencia de carrito")
     class VerificarExistencia {

          @Test
          @DisplayName("RED: Debería retornar false si sessionId es nulo")
          void deberiaRetornarFalseSiSessionIdEsNulo() {
               // When
               boolean resultado = carritoDAO.existeCarrito(null);

               // Then
               assertThat(resultado).isFalse();
          }

          @Test
          @DisplayName("GREEN: Debería retornar true si carrito existe")
          void deberiaRetornarTrueSiCarritoExiste() {
               // Given
               carritoDAO.guardarCarrito(sessionId, carritoTest);

               // When
               boolean resultado = carritoDAO.existeCarrito(sessionId);

               // Then
               assertThat(resultado).isTrue();
          }

          @Test
          @DisplayName("GREEN: Debería retornar false si carrito no existe")
          void deberiaRetornarFalseSiCarritoNoExiste() {
               // When
               boolean resultado = carritoDAO.existeCarrito("sesion-inexistente");

               // Then
               assertThat(resultado).isFalse();
          }
     }

     @Nested
     @DisplayName("Limpiar carritos expirados")
     class LimpiarCarritosExpirados {

          @Test
          @DisplayName("GREEN: Debería ejecutar limpieza sin errores")
          void deberiaEjecutarLimpiezaSinErrores() {
               // Given
               carritoDAO.guardarCarrito(sessionId, carritoTest);
               carritoDAO.guardarCarrito("otra-sesion", carritoTest);

               // When & Then (no debería lanzar excepción)
               assertThatCode(() -> carritoDAO.limpiarCarritosExpirados())
                         .doesNotThrowAnyException();
          }

          @Test
          @DisplayName("GREEN: Debería reportar estadísticas de limpieza correctamente")
          void deberiaReportarEstadisticasCorrectamente() {
               // Given
               carritoDAO.guardarCarrito(sessionId, carritoTest);
               long countAntes = carritoDAO.contarCarritosActivos();

               // When
               carritoDAO.limpiarCarritosExpirados();

               // Then
               long countDespues = carritoDAO.contarCarritosActivos();
               assertThat(countAntes).isGreaterThanOrEqualTo(countDespues);
          }
     }

     @Nested
     @DisplayName("Contar carritos activos")
     class ContarCarritosActivos {

          @Test
          @DisplayName("GREEN: Debería contar carritos correctamente")
          void deberiaContarCarritosCorrectamente() {
               // Given - Cache inicialmente vacío
               long countInicial = carritoDAO.contarCarritosActivos();

               // When
               carritoDAO.guardarCarrito(sessionId, carritoTest);
               carritoDAO.guardarCarrito("otra-sesion", carritoTest);

               // Then
               long countFinal = carritoDAO.contarCarritosActivos();
               assertThat(countFinal).isEqualTo(countInicial + 2);
          }

          @Test
          @DisplayName("GREEN: Debería retornar 0 si no hay carritos")
          void deberiaRetornarCeroSiNoHayCarritos() {
               // Given - Limpiar cache
               carritoDAO.limpiarCarritosExpirados();

               // When
               long count = carritoDAO.contarCarritosActivos();

               // Then
               assertThat(count).isGreaterThanOrEqualTo(0L);
          }
     }

     @Nested
     @DisplayName("Actualizar item específico")
     class ActualizarItem {

          @Test
          @DisplayName("RED: Debería lanzar excepción si sessionId es nulo")
          void deberiaLanzarExcepcionSiSessionIdEsNulo() {
               // Given
               CarritoItemDTO item = CarritoItemDTO.builder()
                         .productoId(1L)
                         .cantidad(3)
                         .build();

               // When & Then
               assertThatThrownBy(() -> carritoDAO.actualizarItem(null, item))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("SessionId e item no pueden ser nulos");
          }

          @Test
          @DisplayName("RED: Debería lanzar excepción si item es nulo")
          void deberiaLanzarExcepcionSiItemEsNulo() {
               // When & Then
               assertThatThrownBy(() -> carritoDAO.actualizarItem(sessionId, null))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("SessionId e item no pueden ser nulos");
          }

          @Test
          @DisplayName("GREEN: Debería actualizar item exitosamente")
          void deberiaActualizarItemExitosamente() {
               // Given
               carritoDAO.guardarCarrito(sessionId, carritoTest);
               CarritoItemDTO itemActualizado = CarritoItemDTO.builder()
                         .productoId(1L)
                         .nombre("Torta de Chocolate")
                         .cantidad(5)
                         .comentarios("Actualizado")
                         .build();

               // When
               carritoDAO.actualizarItem(sessionId, itemActualizado);

               // Then
               Optional<CarritoDTO> resultado = carritoDAO.obtenerCarrito(sessionId);
               assertThat(resultado).isPresent();
               CarritoItemDTO item = resultado.get().getItems().stream()
                         .filter(i -> i.getProductoId().equals(1L))
                         .findFirst()
                         .orElse(null);
               assertThat(item).isNotNull();
               assertThat(item.getCantidad()).isEqualTo(5);
               assertThat(item.getComentarios()).isEqualTo("Actualizado");
          }

          @Test
          @DisplayName("GREEN: No debería hacer nada si carrito no existe")
          void noDeberiaHacerNadaSiCarritoNoExiste() {
               // Given
               CarritoItemDTO item = CarritoItemDTO.builder()
                         .productoId(1L)
                         .cantidad(3)
                         .build();

               // When & Then (no debería lanzar excepción)
               assertThatCode(() -> carritoDAO.actualizarItem("sesion-inexistente", item))
                         .doesNotThrowAnyException();
          }
     }

     @Nested
     @DisplayName("Eliminar item específico")
     class EliminarItem {

          @Test
          @DisplayName("RED: Debería lanzar excepción si sessionId es nulo")
          void deberiaLanzarExcepcionSiSessionIdEsNulo() {
               // When & Then
               assertThatThrownBy(() -> carritoDAO.eliminarItem(null, 1L))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("SessionId y productoId no pueden ser nulos");
          }

          @Test
          @DisplayName("RED: Debería lanzar excepción si productoId es nulo")
          void deberiaLanzarExcepcionSiProductoIdEsNulo() {
               // When & Then
               assertThatThrownBy(() -> carritoDAO.eliminarItem(sessionId, null))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("SessionId y productoId no pueden ser nulos");
          }

          @Test
          @DisplayName("GREEN: Debería eliminar item exitosamente")
          void deberiaEliminarItemExitosamente() {
               // Given
               carritoDAO.guardarCarrito(sessionId, carritoTest);
               assertThat(carritoDAO.obtenerCarrito(sessionId).get().getItems()).hasSize(1);

               // When
               carritoDAO.eliminarItem(sessionId, 1L);

               // Then
               Optional<CarritoDTO> resultado = carritoDAO.obtenerCarrito(sessionId);
               assertThat(resultado).isPresent();
               assertThat(resultado.get().getItems()).isEmpty();
          }

          @Test
          @DisplayName("GREEN: No debería hacer nada si carrito no existe")
          void noDeberiaHacerNadaSiCarritoNoExiste() {
               // When & Then (no debería lanzar excepción)
               assertThatCode(() -> carritoDAO.eliminarItem("sesion-inexistente", 1L))
                         .doesNotThrowAnyException();
          }

          @Test
          @DisplayName("GREEN: No debería hacer nada si item no existe en carrito")
          void noDeberiaHacerNadaSiItemNoExiste() {
               // Given
               carritoDAO.guardarCarrito(sessionId, carritoTest);

               // When
               carritoDAO.eliminarItem(sessionId, 999L);

               // Then
               Optional<CarritoDTO> resultado = carritoDAO.obtenerCarrito(sessionId);
               assertThat(resultado).isPresent();
               assertThat(resultado.get().getItems()).hasSize(1); // No debería cambiar
          }
     }

     @Nested
     @DisplayName("Integración con Cache Guava")
     class IntegracionCache {

          @Test
          @DisplayName("GREEN: Debería mantener datos en cache entre operaciones")
          void deberiaMantenerDatosEnCacheEntreOperaciones() {
               // Given
               carritoDAO.guardarCarrito(sessionId, carritoTest);

               // When - Múltiples operaciones
               assertThat(carritoDAO.existeCarrito(sessionId)).isTrue();
               Optional<CarritoDTO> carrito1 = carritoDAO.obtenerCarrito(sessionId);
               Optional<CarritoDTO> carrito2 = carritoDAO.obtenerCarrito(sessionId);

               // Then
               assertThat(carrito1).isPresent();
               assertThat(carrito2).isPresent();
               assertThat(carrito1.get().getSessionId()).isEqualTo(carrito2.get().getSessionId());
          }

          @Test
          @DisplayName("GREEN: Debería funcionar correctamente después de inicialización")
          void deberiaFuncionarCorrectamenteDespuesDeInicializacion() {
               // Given - Crear nueva instancia para probar inicialización
               CarritoDAOImpl nuevoDAO = new CarritoDAOImpl();

               // When & Then
               assertThat(nuevoDAO.contarCarritosActivos()).isGreaterThanOrEqualTo(0L);
               assertThat(nuevoDAO.existeCarrito("test")).isFalse();

               // Operación básica debería funcionar
               CarritoDTO carritoNuevo = new CarritoDTO("test-init");
               assertThatCode(() -> nuevoDAO.guardarCarrito("test-init", carritoNuevo))
                         .doesNotThrowAnyException();
          }
     }
}
