package com.comoencasa_backend.controller;

import com.comoencasa_backend.controller.ProductoController.StockUpdateRequest;
import com.comoencasa_backend.controller.ProductoController.DisponibilidadUpdateRequest;
import com.comoencasa_backend.model.Producto;
import com.comoencasa_backend.service.ProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests TDD para clases internas de ProductoController
 * Enfocados en aumentar cobertura de StockUpdateRequest y
 * DisponibilidadUpdateRequest
 * Patrón Red-Green-Refactor aplicado estrictamente
 */
@WebMvcTest(ProductoController.class)
@DisplayName("ProductoController Clases Internas TDD Tests")
class ProductoControllerClasesInternasTDDTest {

     @Autowired
     private MockMvc mockMvc;

     @MockBean
     private ProductoService productoService;

     @Autowired
     private ObjectMapper objectMapper;

     @Nested
     @DisplayName("Tests TDD para StockUpdateRequest")
     class TestsStockUpdateRequest {

          @Test
          @DisplayName("RED: StockUpdateRequest debería funcionar con valores por defecto")
          void stockUpdateRequest_DeberiaFuncionarConValoresPorDefecto() {
               // When
               StockUpdateRequest request = new StockUpdateRequest();

               // Then
               assertThat(request.getCantidad()).isNull();
          }

          @Test
          @DisplayName("GREEN: StockUpdateRequest debería set/get cantidad correctamente")
          void stockUpdateRequest_DeberiaSetearyObtenerCantidad() {
               // Given
               StockUpdateRequest request = new StockUpdateRequest();
               Integer cantidad = 50;

               // When
               request.setCantidad(cantidad);

               // Then
               assertThat(request.getCantidad()).isEqualTo(cantidad);
          }

          @Test
          @DisplayName("GREEN: StockUpdateRequest debería manejar valores nulos")
          void stockUpdateRequest_DeberiaManejarValoresNulos() {
               // Given
               StockUpdateRequest request = new StockUpdateRequest();

               // When
               request.setCantidad(null);

               // Then
               assertThat(request.getCantidad()).isNull();
          }

          @Test
          @DisplayName("GREEN: StockUpdateRequest debería manejar valores negativos")
          void stockUpdateRequest_DeberiaManejarValoresNegativos() {
               // Given
               StockUpdateRequest request = new StockUpdateRequest();
               Integer cantidadNegativa = -10;

               // When
               request.setCantidad(cantidadNegativa);

               // Then
               assertThat(request.getCantidad()).isEqualTo(cantidadNegativa);
          }

          @Test
          @DisplayName("REFACTOR: StockUpdateRequest debería funcionar en endpoint real")
          void stockUpdateRequest_DeberiaFuncionarEnEndpointReal() throws Exception {
               // Given
               Long productoId = 1L;
               Integer nuevaCantidad = 100;

               StockUpdateRequest request = new StockUpdateRequest();
               request.setCantidad(nuevaCantidad);

               Producto productoActualizado = new Producto();
               productoActualizado.setId(productoId);
               productoActualizado.setCantidad(nuevaCantidad);
               productoActualizado.setDisponible(true);

               when(productoService.actualizarStock(productoId, nuevaCantidad))
                         .thenReturn(productoActualizado);

               // When & Then
               mockMvc.perform(put("/api/productos/{id}/stock", productoId)
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request)))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$.id").value(productoId))
                         .andExpect(jsonPath("$.cantidad").value(nuevaCantidad))
                         .andExpect(jsonPath("$.disponible").value(true));

               verify(productoService).actualizarStock(productoId, nuevaCantidad);
          }

          @Test
          @DisplayName("REFACTOR: StockUpdateRequest debería manejar valor cero")
          void stockUpdateRequest_DeberiaManejarValorCero() {
               // Given
               StockUpdateRequest request = new StockUpdateRequest();
               Integer cantidadCero = 0;

               // When
               request.setCantidad(cantidadCero);

               // Then
               assertThat(request.getCantidad()).isEqualTo(cantidadCero);
          }

          @Test
          @DisplayName("REFACTOR: StockUpdateRequest debería manejar valores máximos")
          void stockUpdateRequest_DeberiaManejarValoresMaximos() {
               // Given
               StockUpdateRequest request = new StockUpdateRequest();
               Integer cantidadMaxima = Integer.MAX_VALUE;

               // When
               request.setCantidad(cantidadMaxima);

               // Then
               assertThat(request.getCantidad()).isEqualTo(cantidadMaxima);
          }
     }

     @Nested
     @DisplayName("Tests TDD para DisponibilidadUpdateRequest")
     class TestsDisponibilidadUpdateRequest {

          @Test
          @DisplayName("RED: DisponibilidadUpdateRequest debería funcionar con valores por defecto")
          void disponibilidadUpdateRequest_DeberiaFuncionarConValoresPorDefecto() {
               // When
               DisponibilidadUpdateRequest request = new DisponibilidadUpdateRequest();

               // Then
               assertThat(request.getDisponible()).isNull();
          }

          @Test
          @DisplayName("GREEN: DisponibilidadUpdateRequest debería set/get disponible true")
          void disponibilidadUpdateRequest_DeberiaSetearyObtenerDisponibleTrue() {
               // Given
               DisponibilidadUpdateRequest request = new DisponibilidadUpdateRequest();
               Boolean disponible = true;

               // When
               request.setDisponible(disponible);

               // Then
               assertThat(request.getDisponible()).isTrue();
          }

          @Test
          @DisplayName("GREEN: DisponibilidadUpdateRequest debería set/get disponible false")
          void disponibilidadUpdateRequest_DeberiaSetearyObtenerDisponibleFalse() {
               // Given
               DisponibilidadUpdateRequest request = new DisponibilidadUpdateRequest();
               Boolean disponible = false;

               // When
               request.setDisponible(disponible);

               // Then
               assertThat(request.getDisponible()).isFalse();
          }

          @Test
          @DisplayName("GREEN: DisponibilidadUpdateRequest debería manejar valores nulos")
          void disponibilidadUpdateRequest_DeberiaManejarValoresNulos() {
               // Given
               DisponibilidadUpdateRequest request = new DisponibilidadUpdateRequest();

               // When
               request.setDisponible(null);

               // Then
               assertThat(request.getDisponible()).isNull();
          }

          @Test
          @DisplayName("REFACTOR: DisponibilidadUpdateRequest debería funcionar en endpoint real para activar")
          void disponibilidadUpdateRequest_DeberiaFuncionarEnEndpointRealParaActivar() throws Exception {
               // Given
               Long productoId = 1L;
               Boolean disponible = true;

               DisponibilidadUpdateRequest request = new DisponibilidadUpdateRequest();
               request.setDisponible(disponible);

               Producto productoActualizado = new Producto();
               productoActualizado.setId(productoId);
               productoActualizado.setDisponible(disponible);

               when(productoService.cambiarDisponibilidad(productoId, disponible))
                         .thenReturn(productoActualizado);

               // When & Then
               mockMvc.perform(put("/api/productos/{id}/disponibilidad", productoId)
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request)))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$.id").value(productoId))
                         .andExpect(jsonPath("$.disponible").value(true));

               verify(productoService).cambiarDisponibilidad(productoId, disponible);
          }

          @Test
          @DisplayName("REFACTOR: DisponibilidadUpdateRequest debería funcionar en endpoint real para desactivar")
          void disponibilidadUpdateRequest_DeberiaFuncionarEnEndpointRealParaDesactivar() throws Exception {
               // Given
               Long productoId = 2L;
               Boolean disponible = false;

               DisponibilidadUpdateRequest request = new DisponibilidadUpdateRequest();
               request.setDisponible(disponible);

               Producto productoActualizado = new Producto();
               productoActualizado.setId(productoId);
               productoActualizado.setDisponible(disponible);

               when(productoService.cambiarDisponibilidad(productoId, disponible))
                         .thenReturn(productoActualizado);

               // When & Then
               mockMvc.perform(put("/api/productos/{id}/disponibilidad", productoId)
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request)))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$.id").value(productoId))
                         .andExpect(jsonPath("$.disponible").value(false));

               verify(productoService).cambiarDisponibilidad(productoId, disponible);
          }

          @Test
          @DisplayName("REFACTOR: DisponibilidadUpdateRequest debería manejar múltiples cambios")
          void disponibilidadUpdateRequest_DeberiaManejarMultiplesCambios() {
               // Given
               DisponibilidadUpdateRequest request = new DisponibilidadUpdateRequest();

               // When - Múltiples cambios
               request.setDisponible(true);
               assertThat(request.getDisponible()).isTrue();

               request.setDisponible(false);
               assertThat(request.getDisponible()).isFalse();

               request.setDisponible(null);
               assertThat(request.getDisponible()).isNull();

               request.setDisponible(true);
               // Then
               assertThat(request.getDisponible()).isTrue();
          }
     }

     @Nested
     @DisplayName("Tests de Integración de Clases Internas")
     class TestsIntegracionClasesInternas {

          @Test
          @DisplayName("REFACTOR: Ambas clases deberían funcionar independientemente")
          void ambasClases_DeberianFuncionarIndependientemente() {
               // Given
               StockUpdateRequest stockRequest = new StockUpdateRequest();
               DisponibilidadUpdateRequest disponibilidadRequest = new DisponibilidadUpdateRequest();

               // When
               stockRequest.setCantidad(100);
               disponibilidadRequest.setDisponible(true);

               // Then
               assertThat(stockRequest.getCantidad()).isEqualTo(100);
               assertThat(disponibilidadRequest.getDisponible()).isTrue();
          }

          @Test
          @DisplayName("REFACTOR: Deberían serializar/deserializar correctamente con Jackson")
          void deberianSerializarDeserializarCorrectamenteConJackson() throws Exception {
               // Given
               StockUpdateRequest stockRequest = new StockUpdateRequest();
               stockRequest.setCantidad(75);

               DisponibilidadUpdateRequest disponibilidadRequest = new DisponibilidadUpdateRequest();
               disponibilidadRequest.setDisponible(false);

               // When - Serializar
               String stockJson = objectMapper.writeValueAsString(stockRequest);
               String disponibilidadJson = objectMapper.writeValueAsString(disponibilidadRequest);

               // Then - Deserializar
               StockUpdateRequest stockDeserialized = objectMapper.readValue(stockJson, StockUpdateRequest.class);
               DisponibilidadUpdateRequest disponibilidadDeserialized = objectMapper.readValue(disponibilidadJson,
                         DisponibilidadUpdateRequest.class);

               assertThat(stockDeserialized.getCantidad()).isEqualTo(75);
               assertThat(disponibilidadDeserialized.getDisponible()).isFalse();
          }

          @Test
          @DisplayName("REFACTOR: Deberían manejar errores del servicio en endpoints")
          void deberianManejarErroresDelServicioEnEndpoints() throws Exception {
               // Given
               Long productoIdInexistente = 999L;

               StockUpdateRequest stockRequest = new StockUpdateRequest();
               stockRequest.setCantidad(50);

               DisponibilidadUpdateRequest disponibilidadRequest = new DisponibilidadUpdateRequest();
               disponibilidadRequest.setDisponible(true);

               when(productoService.actualizarStock(productoIdInexistente, 50))
                         .thenThrow(new IllegalArgumentException("Producto no encontrado"));
               when(productoService.cambiarDisponibilidad(productoIdInexistente, true))
                         .thenThrow(new IllegalArgumentException("Producto no encontrado"));

               // When & Then - Stock endpoint
               mockMvc.perform(put("/api/productos/{id}/stock", productoIdInexistente)
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(stockRequest)))
                         .andExpect(status().isBadRequest());

               // When & Then - Disponibilidad endpoint
               mockMvc.perform(put("/api/productos/{id}/disponibilidad", productoIdInexistente)
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(disponibilidadRequest)))
                         .andExpect(status().isBadRequest());

               verify(productoService).actualizarStock(productoIdInexistente, 50);
               verify(productoService).cambiarDisponibilidad(productoIdInexistente, true);
          }
     }
}
