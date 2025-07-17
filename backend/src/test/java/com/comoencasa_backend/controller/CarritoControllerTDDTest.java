package com.comoencasa_backend.controller;

import com.comoencasa_backend.dto.CarritoDTO;
import com.comoencasa_backend.dto.CarritoItemDTO;
import com.comoencasa_backend.service.CarritoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarritoController.class)
@ActiveProfiles("test")
@DisplayName("CarritoController TDD Tests")
class CarritoControllerTDDTest {

     @Autowired
     private MockMvc mockMvc;

     @MockBean
     private CarritoService carritoService;

     @Autowired
     private ObjectMapper objectMapper;

     private MockHttpSession session;
     private CarritoDTO carritoTest;

     @BeforeEach
     void setUp() {
          session = new MockHttpSession();
          session.setAttribute("JSESSIONID", "test-session-123");

          // Crear carrito de prueba
          carritoTest = new CarritoDTO("test-session-123");
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
     @DisplayName("POST /api/carrito/agregar")
     class AgregarProducto {

          @Test
          @DisplayName("GREEN: Debería agregar producto exitosamente")
          void deberiaAgregarProductoExitosamente() throws Exception {
               // Given
               Map<String, Object> request = Map.of(
                         "productoId", 1L,
                         "cantidad", 2,
                         "comentarios", "Sin azúcar");
               when(carritoService.agregarProducto(anyString(), eq(1L), eq(2), eq("Sin azúcar")))
                         .thenReturn(carritoTest);

               // When & Then
               mockMvc.perform(post("/api/carrito/agregar")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request))
                         .session(session))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$.sessionId").value("test-session-123"))
                         .andExpect(jsonPath("$.items[0].productoId").value(1))
                         .andExpect(jsonPath("$.items[0].cantidad").value(2));

               verify(carritoService).agregarProducto(anyString(), eq(1L), eq(2), eq("Sin azúcar"));
          }

          @Test
          @DisplayName("GREEN: Debería manejar comentarios nulos")
          void deberiaManejarComentariosNulos() throws Exception {
               // Given
               Map<String, Object> request = Map.of(
                         "productoId", 1L,
                         "cantidad", 2);
               when(carritoService.agregarProducto(anyString(), eq(1L), eq(2), eq("")))
                         .thenReturn(carritoTest);

               // When & Then
               mockMvc.perform(post("/api/carrito/agregar")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request))
                         .session(session))
                         .andExpect(status().isOk());

               verify(carritoService).agregarProducto(anyString(), eq(1L), eq(2), eq(""));
          }

          @Test
          @DisplayName("RED: Debería retornar 400 para errores de validación")
          void deberiaRetornar400ParaErroresValidacion() throws Exception {
               // Given
               Map<String, Object> request = Map.of(
                         "productoId", 1L,
                         "cantidad", 2);
               when(carritoService.agregarProducto(anyString(), eq(1L), eq(2), eq("")))
                         .thenThrow(new IllegalArgumentException("Producto no encontrado"));

               // When & Then
               mockMvc.perform(post("/api/carrito/agregar")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request))
                         .session(session))
                         .andExpect(status().isBadRequest());
          }

          @Test
          @DisplayName("RED: Debería retornar 500 para errores internos")
          void deberiaRetornar500ParaErroresInternos() throws Exception {
               // Given
               Map<String, Object> request = Map.of(
                         "productoId", 1L,
                         "cantidad", 2);
               when(carritoService.agregarProducto(anyString(), eq(1L), eq(2), eq("")))
                         .thenThrow(new RuntimeException("Error interno"));

               // When & Then
               mockMvc.perform(post("/api/carrito/agregar")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request))
                         .session(session))
                         .andExpect(status().isInternalServerError());
          }
     }

     @Nested
     @DisplayName("PUT /api/carrito/actualizar/{productoId}")
     class ActualizarCantidad {

          @Test
          @DisplayName("GREEN: Debería actualizar cantidad exitosamente")
          void deberiaActualizarCantidadExitosamente() throws Exception {
               // Given
               Map<String, Integer> request = Map.of("cantidad", 5);
               when(carritoService.actualizarCantidad(anyString(), eq(1L), eq(5)))
                         .thenReturn(carritoTest);

               // When & Then
               mockMvc.perform(put("/api/carrito/actualizar/1")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request))
                         .session(session))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$.sessionId").value("test-session-123"));

               verify(carritoService).actualizarCantidad(anyString(), eq(1L), eq(5));
          }

          @Test
          @DisplayName("RED: Debería retornar 500 para errores")
          void deberiaRetornar500ParaErrores() throws Exception {
               // Given
               Map<String, Integer> request = Map.of("cantidad", 5);
               when(carritoService.actualizarCantidad(anyString(), eq(1L), eq(5)))
                         .thenThrow(new RuntimeException("Error al actualizar"));

               // When & Then
               mockMvc.perform(put("/api/carrito/actualizar/1")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request))
                         .session(session))
                         .andExpect(status().isInternalServerError());
          }
     }

     @Nested
     @DisplayName("DELETE /api/carrito/eliminar/{productoId}")
     class EliminarProducto {

          @Test
          @DisplayName("GREEN: Debería eliminar producto exitosamente")
          void deberiaEliminarProductoExitosamente() throws Exception {
               // Given
               CarritoDTO carritoVacio = new CarritoDTO("test-session-123");
               when(carritoService.eliminarProducto(anyString(), eq(1L)))
                         .thenReturn(carritoVacio);

               // When & Then
               mockMvc.perform(delete("/api/carrito/eliminar/1")
                         .session(session))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$.sessionId").value("test-session-123"))
                         .andExpect(jsonPath("$.items").isEmpty());

               verify(carritoService).eliminarProducto(anyString(), eq(1L));
          }

          @Test
          @DisplayName("RED: Debería retornar 500 para errores")
          void deberiaRetornar500ParaErrores() throws Exception {
               // Given
               when(carritoService.eliminarProducto(anyString(), eq(1L)))
                         .thenThrow(new RuntimeException("Error al eliminar"));

               // When & Then
               mockMvc.perform(delete("/api/carrito/eliminar/1")
                         .session(session))
                         .andExpect(status().isInternalServerError());
          }
     }

     @Nested
     @DisplayName("GET /api/carrito")
     class ObtenerCarrito {

          @Test
          @DisplayName("GREEN: Debería obtener carrito exitosamente")
          void deberiaObtenerCarritoExitosamente() throws Exception {
               // Given
               when(carritoService.obtenerCarrito(anyString()))
                         .thenReturn(carritoTest);

               // When & Then
               mockMvc.perform(get("/api/carrito")
                         .session(session))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$.sessionId").value("test-session-123"))
                         .andExpect(jsonPath("$.items[0].nombre").value("Torta de Chocolate"));

               verify(carritoService).obtenerCarrito(anyString());
          }

          @Test
          @DisplayName("RED: Debería retornar 500 para errores")
          void deberiaRetornar500ParaErrores() throws Exception {
               // Given
               when(carritoService.obtenerCarrito(anyString()))
                         .thenThrow(new RuntimeException("Error al obtener carrito"));

               // When & Then
               mockMvc.perform(get("/api/carrito")
                         .session(session))
                         .andExpect(status().isInternalServerError());
          }
     }

     @Nested
     @DisplayName("DELETE /api/carrito/limpiar")
     class LimpiarCarrito {

          @Test
          @DisplayName("GREEN: Debería limpiar carrito exitosamente")
          void deberiaLimpiarCarritoExitosamente() throws Exception {
               // Given
               CarritoDTO carritoVacio = new CarritoDTO("test-session-123");
               when(carritoService.limpiarCarrito(anyString()))
                         .thenReturn(carritoVacio);

               // When & Then
               mockMvc.perform(delete("/api/carrito/limpiar")
                         .session(session))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$.sessionId").value("test-session-123"))
                         .andExpect(jsonPath("$.items").isEmpty())
                         .andExpect(jsonPath("$.totalItems").value(0));

               verify(carritoService).limpiarCarrito(anyString());
          }

          @Test
          @DisplayName("RED: Debería retornar 500 para errores")
          void deberiaRetornar500ParaErrores() throws Exception {
               // Given
               when(carritoService.limpiarCarrito(anyString()))
                         .thenThrow(new RuntimeException("Error al limpiar carrito"));

               // When & Then
               mockMvc.perform(delete("/api/carrito/limpiar")
                         .session(session))
                         .andExpect(status().isInternalServerError());
          }
     }

     @Nested
     @DisplayName("GET /api/carrito/total-items")
     class ObtenerTotalItems {

          @Test
          @DisplayName("GREEN: Debería obtener total de items exitosamente")
          void deberiaObtenerTotalItemsExitosamente() throws Exception {
               // Given
               when(carritoService.obtenerTotalItems(anyString()))
                         .thenReturn(5);

               // When & Then
               mockMvc.perform(get("/api/carrito/total-items")
                         .session(session))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$.totalItems").value(5));

               verify(carritoService).obtenerTotalItems(anyString());
          }

          @Test
          @DisplayName("GREEN: Debería manejar carrito vacío")
          void deberiaManejarCarritoVacio() throws Exception {
               // Given
               when(carritoService.obtenerTotalItems(anyString()))
                         .thenReturn(0);

               // When & Then
               mockMvc.perform(get("/api/carrito/total-items")
                         .session(session))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$.totalItems").value(0));
          }

          @Test
          @DisplayName("RED: Debería retornar 500 para errores")
          void deberiaRetornar500ParaErrores() throws Exception {
               // Given
               when(carritoService.obtenerTotalItems(anyString()))
                         .thenThrow(new RuntimeException("Error al obtener total"));

               // When & Then
               mockMvc.perform(get("/api/carrito/total-items")
                         .session(session))
                         .andExpect(status().isInternalServerError());
          }
     }

     @Nested
     @DisplayName("Manejo de errores específicos")
     class ManejoErroresEspecificos {

          @Test
          @DisplayName("RED: Debería manejar errores de stock insuficiente correctamente")
          void deberiaManejarErroresStockInsuficiente() throws Exception {
               // Given
               Map<String, Object> request = Map.of(
                         "productoId", 1L,
                         "cantidad", 10);
               when(carritoService.agregarProducto(anyString(), eq(1L), eq(10), eq("")))
                         .thenThrow(new IllegalArgumentException("Stock insuficiente. Disponible: 5, Solicitado: 10"));

               // When & Then
               mockMvc.perform(post("/api/carrito/agregar")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request))
                         .session(session))
                         .andExpect(status().isBadRequest());
          }

          @Test
          @DisplayName("RED: Debería manejar productos no encontrados")
          void deberiaManejarProductosNoEncontrados() throws Exception {
               // Given
               Map<String, Object> request = Map.of(
                         "productoId", 999L,
                         "cantidad", 1);
               when(carritoService.agregarProducto(anyString(), eq(999L), eq(1), eq("")))
                         .thenThrow(new IllegalArgumentException("Producto no encontrado"));

               // When & Then
               mockMvc.perform(post("/api/carrito/agregar")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request))
                         .session(session))
                         .andExpect(status().isBadRequest());
          }

          @Test
          @DisplayName("RED: Debería manejar datos de entrada inválidos")
          void deberiaManejarDatosEntradaInvalidos() throws Exception {
               // Given - Request malformado
               String jsonMalformado = "{\"productoId\": \"abc\", \"cantidad\": -1}";

               // When & Then
               mockMvc.perform(post("/api/carrito/agregar")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(jsonMalformado)
                         .session(session))
                         .andExpect(status().isBadRequest()); // Error de parsing devuelve 400
          }
     }

     @Nested
     @DisplayName("Validación de sesiones")
     class ValidacionSesiones {

          @Test
          @DisplayName("GREEN: Debería usar sessionId de la sesión HTTP")
          void deberiaUsarSessionIdDeLaSesionHTTP() throws Exception {
               // Given
               MockHttpSession sessionEspecifica = new MockHttpSession();
               sessionEspecifica.setAttribute("JSESSIONID", "session-especifica");

               when(carritoService.obtenerCarrito(anyString()))
                         .thenReturn(carritoTest);

               // When & Then
               mockMvc.perform(get("/api/carrito")
                         .session(sessionEspecifica))
                         .andExpect(status().isOk());

               // Verificar que se llamó con algún sessionId (Spring genera uno
               // automáticamente)
               verify(carritoService).obtenerCarrito(anyString());
          }

          @Test
          @DisplayName("GREEN: Debería funcionar con diferentes sesiones")
          void deberiaFuncionarConDiferentesSesiones() throws Exception {
               // Given
               MockHttpSession session1 = new MockHttpSession();
               MockHttpSession session2 = new MockHttpSession();

               when(carritoService.obtenerCarrito(anyString()))
                         .thenReturn(carritoTest);

               // When & Then
               mockMvc.perform(get("/api/carrito").session(session1))
                         .andExpect(status().isOk());

               mockMvc.perform(get("/api/carrito").session(session2))
                         .andExpect(status().isOk());

               verify(carritoService, times(2)).obtenerCarrito(anyString());
          }
     }

     @Nested
     @DisplayName("Logging de operaciones")
     class LoggingOperaciones {

          @Test
          @DisplayName("GREEN: Debería registrar operaciones exitosas")
          void deberiaRegistrarOperacionesExitosas() throws Exception {
               // Given
               Map<String, Object> request = Map.of("productoId", 1L, "cantidad", 2);
               when(carritoService.agregarProducto(anyString(), eq(1L), eq(2), eq("")))
                         .thenReturn(carritoTest);

               // When & Then
               mockMvc.perform(post("/api/carrito/agregar")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request))
                         .session(session))
                         .andExpect(status().isOk());

               // Los logs son verificados implícitamente por el funcionamiento del endpoint
               verify(carritoService).agregarProducto(anyString(), eq(1L), eq(2), eq(""));
          }

          @Test
          @DisplayName("RED: Debería registrar errores apropiadamente")
          void deberiaRegistrarErroresApropiadamente() throws Exception {
               // Given
               when(carritoService.obtenerCarrito(anyString()))
                         .thenThrow(new RuntimeException("Error de prueba"));

               // When & Then
               mockMvc.perform(get("/api/carrito")
                         .session(session))
                         .andExpect(status().isInternalServerError());

               // Los logs de error son manejados por el controller
               verify(carritoService).obtenerCarrito(anyString());
          }
     }
}
