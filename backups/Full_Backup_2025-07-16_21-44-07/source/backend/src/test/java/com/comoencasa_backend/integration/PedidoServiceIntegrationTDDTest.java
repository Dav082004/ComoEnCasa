package com.comoencasa_backend.integration;

import com.comoencasa_backend.dto.PedidoDTO;
import com.comoencasa_backend.model.Pedido;
import com.comoencasa_backend.model.Usuario;
import com.comoencasa_backend.repository.PedidoRepository;
import com.comoencasa_backend.repository.UsuarioRepository;
import com.comoencasa_backend.service.PedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("PedidoService Integration TDD Tests")
class PedidoServiceIntegrationTDDTest {

     @Autowired
     private WebApplicationContext webApplicationContext;

     @Autowired
     private PedidoService pedidoService;

     @Autowired
     private PedidoRepository pedidoRepository;

     @Autowired
     private UsuarioRepository usuarioRepository;

     private MockMvc mockMvc;
     private ObjectMapper objectMapper;
     private Usuario usuarioTest;
     private Pedido pedidoTest;

     @BeforeEach
     void setUp() {
          mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
          objectMapper = new ObjectMapper();
          objectMapper.findAndRegisterModules();

          // Limpiar datos de prueba
          pedidoRepository.deleteAll();
          usuarioRepository.deleteAll();

          // Crear usuario de prueba
          usuarioTest = new Usuario();
          usuarioTest.setNombre("Juan");
          usuarioTest.setApellido("Pérez");
          usuarioTest.setEmail("juan.test@example.com");
          usuarioTest.setPassword("password123");
          usuarioTest.setActivado(true);
          usuarioTest = usuarioRepository.save(usuarioTest);

          // Crear pedido de prueba
          pedidoTest = new Pedido();
          pedidoTest.setUsuario(usuarioTest);
          pedidoTest.setFechaCreacion(LocalDateTime.now());
          pedidoTest.setEstado("Pendiente");
          pedidoTest.setSubtotal(new BigDecimal("50.00"));
          pedidoTest.setCostoTotal(new BigDecimal("59.00"));
          pedidoTest.setDireccionEntrega("Av. Test 123");
          pedidoTest = pedidoRepository.save(pedidoTest);
     }

     @Nested
     @DisplayName("Flujo completo de estados - Integration Tests")
     class FlujoCompletoEstados {

          @Test
          @DisplayName("GREEN: Debería permitir flujo normal de estados")
          void deberiaPermitirFlujoNormalDeEstados() {
               // Given - Pedido en estado "Pendiente"
               assertThat(pedidoTest.getEstado()).isEqualTo("Pendiente");

               // When - Cambiar a "En preparación"
               PedidoDTO resultado1 = pedidoService.actualizarEstadoPedido(pedidoTest.getId(), "En preparación");

               // Then
               assertThat(resultado1.getEstado()).isEqualTo("En preparación");

               // Verificar en BD
               Pedido pedidoBD = pedidoRepository.findById(pedidoTest.getId()).orElseThrow();
               assertThat(pedidoBD.getEstado()).isEqualTo("En preparación");

               // When - Cambiar a "Entregado"
               PedidoDTO resultado2 = pedidoService.actualizarEstadoPedido(pedidoTest.getId(), "Entregado");

               // Then
               assertThat(resultado2.getEstado()).isEqualTo("Entregado");

               // Verificar en BD
               pedidoBD = pedidoRepository.findById(pedidoTest.getId()).orElseThrow();
               assertThat(pedidoBD.getEstado()).isEqualTo("Entregado");
          }

          @Test
          @DisplayName("GREEN: Debería permitir cancelación desde cualquier estado")
          void deberiaPermitirCancelacionDesdeEstadoCualquiera() {
               // Given - Cambiar a "En preparación" primero
               pedidoService.actualizarEstadoPedido(pedidoTest.getId(), "En preparación");

               // When - Cancelar desde "En preparación"
               PedidoDTO resultado = pedidoService.actualizarEstadoPedido(pedidoTest.getId(), "Cancelado");

               // Then
               assertThat(resultado.getEstado()).isEqualTo("Cancelado");

               // Verificar en BD
               Pedido pedidoBD = pedidoRepository.findById(pedidoTest.getId()).orElseThrow();
               assertThat(pedidoBD.getEstado()).isEqualTo("Cancelado");
          }

          @Test
          @DisplayName("RED: Debería fallar al intentar cambio desde estado final sin forzar")
          void deberiaFallarAlIntentarCambioDesdeEstadoFinalSinForzar() {
               // Given - Cambiar a estado final "Entregado"
               pedidoService.actualizarEstadoPedido(pedidoTest.getId(), "En preparación");
               pedidoService.actualizarEstadoPedido(pedidoTest.getId(), "Entregado");

               // When & Then - Intentar cambiar desde "Entregado"
               assertThatThrownBy(() -> pedidoService.actualizarEstadoPedido(pedidoTest.getId(), "En preparación"))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("Transición no válida desde estado Entregado");
          }

          @Test
          @DisplayName("GREEN: Debería permitir cambio forzado desde estado final")
          void deberiaPermitirCambioForzadoDesdeEstadoFinal() {
               // Given - Cambiar a estado final "Entregado"
               pedidoService.actualizarEstadoPedido(pedidoTest.getId(), "En preparación");
               pedidoService.actualizarEstadoPedido(pedidoTest.getId(), "Entregado");

               // When - Cambio forzado con password correcto
               PedidoDTO resultado = pedidoService.actualizarEstadoPedidoForzado(
                         pedidoTest.getId(), "Pendiente", "123");

               // Then
               assertThat(resultado.getEstado()).isEqualTo("Pendiente");

               // Verificar en BD
               Pedido pedidoBD = pedidoRepository.findById(pedidoTest.getId()).orElseThrow();
               assertThat(pedidoBD.getEstado()).isEqualTo("Pendiente");
          }
     }

     @Nested
     @DisplayName("API Endpoints Integration Tests")
     class ApiEndpointsIntegrationTests {

          @Test
          @DisplayName("GREEN: PUT /api/pedidos/{id}/estado - Actualización exitosa")
          void deberiaActualizarEstadoViaAPI() throws Exception {
               // Given
               Map<String, String> request = new HashMap<>();
               request.put("estado", "En preparación");

               // When & Then
               mockMvc.perform(put("/api/pedidos/" + pedidoTest.getId() + "/estado")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request)))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$.id").value(pedidoTest.getId()))
                         .andExpect(jsonPath("$.estado").value("En preparación"))
                         .andExpect(jsonPath("$.usuarioNombre").value("Juan"));

               // Verificar en BD
               Pedido pedidoBD = pedidoRepository.findById(pedidoTest.getId()).orElseThrow();
               assertThat(pedidoBD.getEstado()).isEqualTo("En preparación");
          }

          @Test
          @DisplayName("RED: PUT /api/pedidos/{id}/estado - Transición inválida")
          void deberiaDevolverErrorParaTransicionInvalida() throws Exception {
               // Given - Cambiar a estado final primero
               pedidoService.actualizarEstadoPedido(pedidoTest.getId(), "En preparación");
               pedidoService.actualizarEstadoPedido(pedidoTest.getId(), "Entregado");

               Map<String, String> request = new HashMap<>();
               request.put("estado", "Pendiente");

               // When & Then
               mockMvc.perform(put("/api/pedidos/" + pedidoTest.getId() + "/estado")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request)))
                         .andExpect(status().isBadRequest());

               // Verificar que el estado no cambió en BD
               Pedido pedidoBD = pedidoRepository.findById(pedidoTest.getId()).orElseThrow();
               assertThat(pedidoBD.getEstado()).isEqualTo("Entregado");
          }

          @Test
          @DisplayName("GREEN: PUT /api/pedidos/{id}/estado/forzado - Actualización forzada exitosa")
          void deberiaActualizarEstadoForzadoViaAPI() throws Exception {
               // Given - Cambiar a estado final primero
               pedidoService.actualizarEstadoPedido(pedidoTest.getId(), "En preparación");
               pedidoService.actualizarEstadoPedido(pedidoTest.getId(), "Entregado");

               Map<String, String> request = new HashMap<>();
               request.put("estado", "Pendiente");
               request.put("password", "123");

               // When & Then
               mockMvc.perform(put("/api/pedidos/" + pedidoTest.getId() + "/estado/forzado")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request)))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$.id").value(pedidoTest.getId()))
                         .andExpect(jsonPath("$.estado").value("Pendiente"));

               // Verificar en BD
               Pedido pedidoBD = pedidoRepository.findById(pedidoTest.getId()).orElseThrow();
               assertThat(pedidoBD.getEstado()).isEqualTo("Pendiente");
          }

          @Test
          @DisplayName("RED: PUT /api/pedidos/{id}/estado/forzado - Password incorrecto")
          void deberiaDevolverErrorParaPasswordIncorrecto() throws Exception {
               // Given
               Map<String, String> request = new HashMap<>();
               request.put("estado", "Pendiente");
               request.put("password", "password_incorrecto");

               // When & Then
               mockMvc.perform(put("/api/pedidos/" + pedidoTest.getId() + "/estado/forzado")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request)))
                         .andExpect(status().isBadRequest());
          }

          @Test
          @DisplayName("GREEN: GET /api/pedidos/estados - Obtener estados disponibles")
          void deberiaObtenerEstadosDisponibles() throws Exception {
               mockMvc.perform(get("/api/pedidos/estados"))
                         .andExpect(status().isOk())
                         .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                         .andExpect(jsonPath("$").isArray())
                         .andExpect(jsonPath("$.length()").value(4))
                         .andExpect(jsonPath("$[0]").value("Pendiente"))
                         .andExpect(jsonPath("$[1]").value("En preparación"))
                         .andExpect(jsonPath("$[2]").value("Entregado"))
                         .andExpect(jsonPath("$[3]").value("Cancelado"));
          }

          @Test
          @DisplayName("GREEN: GET /api/pedidos/transiciones/{estado} - Obtener transiciones")
          void deberiaObtenerTransicionesDisponibles() throws Exception {
               mockMvc.perform(get("/api/pedidos/transiciones/Pendiente"))
                         .andExpect(status().isOk())
                         .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                         .andExpect(jsonPath("$").isArray())
                         .andExpect(jsonPath("$.length()").value(2))
                         .andExpect(jsonPath("$[0]").value("En preparación"))
                         .andExpect(jsonPath("$[1]").value("Cancelado"));
          }
     }

     @Nested
     @DisplayName("Persistencia y Transacciones")
     class PersistenciaYTransacciones {

          @Test
          @DisplayName("GREEN: Debería mantener consistencia tras múltiples cambios")
          void deberiaMantenerConsistenciaTrasMúltiplesCambios() {
               // Given - Estado inicial
               Long pedidoId = pedidoTest.getId();
               assertThat(pedidoRepository.findById(pedidoId).orElseThrow().getEstado())
                         .isEqualTo("Pendiente");

               // When - Serie de cambios válidos
               pedidoService.actualizarEstadoPedido(pedidoId, "En preparación");
               pedidoService.actualizarEstadoPedido(pedidoId, "Entregado");
               pedidoService.actualizarEstadoPedidoForzado(pedidoId, "Cancelado", "123");

               // Then - Verificar estado final en BD
               Pedido pedidoFinal = pedidoRepository.findById(pedidoId).orElseThrow();
               assertThat(pedidoFinal.getEstado()).isEqualTo("Cancelado");
               assertThat(pedidoFinal.getId()).isEqualTo(pedidoId);
               assertThat(pedidoFinal.getUsuario().getId()).isEqualTo(usuarioTest.getId());
          }

          @Test
          @DisplayName("RED: Debería rollback en caso de error durante transacción")
          void deberiaRollbackEnCasoDeErrorDuranteTransaccion() {
               // Given - Estado inicial
               String estadoInicial = pedidoTest.getEstado();

               // When & Then - Intentar transacción que falla
               assertThatThrownBy(
                         () -> pedidoService.actualizarEstadoPedidoForzado(pedidoTest.getId(), "EstadoInválido", "123"))
                         .isInstanceOf(IllegalArgumentException.class);

               // Verificar que el estado no cambió (rollback)
               Pedido pedidoBD = pedidoRepository.findById(pedidoTest.getId()).orElseThrow();
               assertThat(pedidoBD.getEstado()).isEqualTo(estadoInicial);
          }
     }
}
