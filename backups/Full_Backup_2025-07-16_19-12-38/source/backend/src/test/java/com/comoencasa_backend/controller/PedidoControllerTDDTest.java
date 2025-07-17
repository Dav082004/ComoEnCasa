package com.comoencasa_backend.controller;

import com.comoencasa_backend.dto.PedidoDTO;
import com.comoencasa_backend.service.PedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PedidoController TDD Tests")
class PedidoControllerTDDTest {

     @Mock
     private PedidoService pedidoService;

     @InjectMocks
     private PedidoController pedidoController;

     private MockMvc mockMvc;
     private ObjectMapper objectMapper;
     private PedidoDTO pedidoTestDTO;

     @BeforeEach
     void setUp() {
          mockMvc = MockMvcBuilders.standaloneSetup(pedidoController).build();
          objectMapper = new ObjectMapper();
          objectMapper.findAndRegisterModules(); // Para manejar LocalDateTime

          // Configurar datos de prueba
          pedidoTestDTO = new PedidoDTO();
          pedidoTestDTO.setId(1L);
          pedidoTestDTO.setUsuarioId(1L);
          pedidoTestDTO.setUsuarioNombre("Juan Pérez");
          pedidoTestDTO.setFechaCreacion(LocalDateTime.now());
          pedidoTestDTO.setEstado("Pendiente");
          pedidoTestDTO.setSubtotal(new BigDecimal("50.00"));
          pedidoTestDTO.setCostoTotal(new BigDecimal("59.00"));
          pedidoTestDTO.setDireccionEntrega("Av. Test 123");
     }

     @Nested
     @DisplayName("GET /api/pedidos - Listar todos los pedidos")
     class ListarTodosLosPedidos {

          @Test
          @DisplayName("RED: Debería retornar 200 con lista vacía cuando no hay pedidos")
          void deberiaRetornar200ConListaVaciaCuandoNoHayPedidos() throws Exception { // Given
               when(pedidoService.findAll()).thenReturn(Collections.emptyList());

               // When & Then
               mockMvc.perform(get("/api/pedidos"))
                         .andExpect(status().isOk())
                         .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                         .andExpect(jsonPath("$").isArray())
                         .andExpect(jsonPath("$.length()").value(0));

               verify(pedidoService).findAll();
          }

          @Test
          @DisplayName("GREEN: Debería retornar 200 con lista de pedidos")
          void deberiaRetornar200ConListaDePedidos() throws Exception {
               // Given
               PedidoDTO pedido2 = new PedidoDTO();
               pedido2.setId(2L);
               pedido2.setUsuarioNombre("María García");
               pedido2.setEstado("En preparación");

               List<PedidoDTO> pedidos = Arrays.asList(pedidoTestDTO, pedido2);
               when(pedidoService.findAll()).thenReturn(pedidos);

               // When & Then
               mockMvc.perform(get("/api/pedidos"))
                         .andExpect(status().isOk())
                         .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                         .andExpect(jsonPath("$").isArray())
                         .andExpect(jsonPath("$.length()").value(2))
                         .andExpect(jsonPath("$[0].id").value(1))
                         .andExpect(jsonPath("$[0].usuarioNombre").value("Juan Pérez"))
                         .andExpect(jsonPath("$[0].estado").value("Pendiente"))
                         .andExpect(jsonPath("$[1].id").value(2))
                         .andExpect(jsonPath("$[1].usuarioNombre").value("María García"))
                         .andExpect(jsonPath("$[1].estado").value("En preparación"));

               verify(pedidoService).findAll();
          }
     }

     @Nested
     @DisplayName("GET /api/pedidos/usuario/{id} - Obtener pedidos por usuario")
     class ObtenerPedidosPorUsuario {

          @Test
          @DisplayName("RED: Debería retornar 400 cuando el ID de usuario es inválido")
          void deberiaRetornar400CuandoIdUsuarioEsInvalido() throws Exception {
               // Given
               when(pedidoService.obtenerPedidosPorUsuario(0L))
                         .thenThrow(new IllegalArgumentException("El ID de usuario debe ser mayor a 0"));

               // When & Then
               mockMvc.perform(get("/api/pedidos/usuario/0"))
                         .andExpect(status().isBadRequest());

               verify(pedidoService).obtenerPedidosPorUsuario(0L);
          }

          @Test
          @DisplayName("GREEN: Debería retornar 200 con pedidos del usuario")
          void deberiaRetornar200ConPedidosDelUsuario() throws Exception {
               // Given
               when(pedidoService.obtenerPedidosPorUsuario(1L))
                         .thenReturn(Arrays.asList(pedidoTestDTO));

               // When & Then
               mockMvc.perform(get("/api/pedidos/usuario/1"))
                         .andExpect(status().isOk())
                         .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                         .andExpect(jsonPath("$").isArray())
                         .andExpect(jsonPath("$.length()").value(1))
                         .andExpect(jsonPath("$[0].id").value(1))
                         .andExpect(jsonPath("$[0].usuarioId").value(1));

               verify(pedidoService).obtenerPedidosPorUsuario(1L);
          }
     }

     @Nested
     @DisplayName("PUT /api/pedidos/{id}/estado - Actualizar estado de pedido")
     class ActualizarEstadoPedido {

          @Test
          @DisplayName("RED: Debería retornar 400 cuando la transición no es válida")
          void deberiaRetornar400CuandoTransicionNoEsValida() throws Exception {
               // Given
               Map<String, String> request = new HashMap<>();
               request.put("estado", "En preparación");

               when(pedidoService.actualizarEstadoPedido(1L, "En preparación"))
                         .thenThrow(new IllegalArgumentException(
                                   "Transición no válida desde estado Entregado a En preparación"));

               // When & Then
               mockMvc.perform(put("/api/pedidos/1/estado")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request)))
                         .andExpect(status().isBadRequest());

               verify(pedidoService).actualizarEstadoPedido(1L, "En preparación");
          }

          @Test
          @DisplayName("RED: Debería retornar 404 cuando el pedido no existe")
          void deberiaRetornar404CuandoPedidoNoExiste() throws Exception {
               // Given
               Map<String, String> request = new HashMap<>();
               request.put("estado", "En preparación");

               when(pedidoService.actualizarEstadoPedido(999L, "En preparación"))
                         .thenThrow(new IllegalArgumentException("Pedido no encontrado con ID=999"));

               // When & Then
               mockMvc.perform(put("/api/pedidos/999/estado")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request)))
                         .andExpect(status().isBadRequest());

               verify(pedidoService).actualizarEstadoPedido(999L, "En preparación");
          }

          @Test
          @DisplayName("GREEN: Debería retornar 200 con pedido actualizado")
          void deberiaRetornar200ConPedidoActualizado() throws Exception {
               // Given
               Map<String, String> request = new HashMap<>();
               request.put("estado", "En preparación");

               PedidoDTO pedidoActualizado = new PedidoDTO();
               pedidoActualizado.setId(1L);
               pedidoActualizado.setEstado("En preparación");
               pedidoActualizado.setUsuarioNombre("Juan Pérez");

               when(pedidoService.actualizarEstadoPedido(1L, "En preparación"))
                         .thenReturn(pedidoActualizado);

               // When & Then
               mockMvc.perform(put("/api/pedidos/1/estado")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request)))
                         .andExpect(status().isOk())
                         .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                         .andExpect(jsonPath("$.id").value(1))
                         .andExpect(jsonPath("$.estado").value("En preparación"))
                         .andExpect(jsonPath("$.usuarioNombre").value("Juan Pérez"));

               verify(pedidoService).actualizarEstadoPedido(1L, "En preparación");
          }
     }

     @Nested
     @DisplayName("PUT /api/pedidos/{id}/estado/forzado - Actualizar estado forzado")
     class ActualizarEstadoForzado {

          @Test
          @DisplayName("RED: Debería retornar 400 cuando la contraseña es incorrecta")
          void deberiaRetornar400CuandoPasswordEsIncorrecto() throws Exception {
               // Given
               Map<String, String> request = new HashMap<>();
               request.put("estado", "Pendiente");
               request.put("password", "password_incorrecto");

               when(pedidoService.actualizarEstadoPedidoForzado(1L, "Pendiente", "password_incorrecto"))
                         .thenThrow(new IllegalArgumentException("Contraseña de confirmación incorrecta"));

               // When & Then
               mockMvc.perform(put("/api/pedidos/1/estado/forzado")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request)))
                         .andExpect(status().isBadRequest());

               verify(pedidoService).actualizarEstadoPedidoForzado(1L, "Pendiente", "password_incorrecto");
          }

          @Test
          @DisplayName("GREEN: Debería retornar 200 con pedido actualizado forzadamente")
          void deberiaRetornar200ConPedidoActualizadoForzadamente() throws Exception {
               // Given
               Map<String, String> request = new HashMap<>();
               request.put("estado", "Pendiente");
               request.put("password", "123");

               PedidoDTO pedidoForzado = new PedidoDTO();
               pedidoForzado.setId(1L);
               pedidoForzado.setEstado("Pendiente");
               pedidoForzado.setUsuarioNombre("Juan Pérez");

               when(pedidoService.actualizarEstadoPedidoForzado(1L, "Pendiente", "123"))
                         .thenReturn(pedidoForzado);

               // When & Then
               mockMvc.perform(put("/api/pedidos/1/estado/forzado")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request)))
                         .andExpect(status().isOk())
                         .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                         .andExpect(jsonPath("$.id").value(1))
                         .andExpect(jsonPath("$.estado").value("Pendiente"));

               verify(pedidoService).actualizarEstadoPedidoForzado(1L, "Pendiente", "123");
          }
     }

     @Nested
     @DisplayName("GET /api/pedidos/estados - Obtener estados disponibles")
     class ObtenerEstadosDisponibles {

          @Test
          @DisplayName("GREEN: Debería retornar lista de estados disponibles")
          void deberiaRetornarListaDeEstadosDisponibles() throws Exception {
               // When & Then
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
     }

     @Nested
     @DisplayName("GET /api/pedidos/transiciones/{estado} - Obtener transiciones disponibles")
     class ObtenerTransicionesDisponibles {

          @Test
          @DisplayName("GREEN: Debería retornar transiciones para estado Pendiente")
          void deberiaRetornarTransicionesParaEstadoPendiente() throws Exception {
               // Given
               List<String> transiciones = Arrays.asList("En preparación", "Cancelado");
               when(pedidoService.getTransicionesDisponibles("Pendiente")).thenReturn(transiciones);

               // When & Then
               mockMvc.perform(get("/api/pedidos/transiciones/Pendiente"))
                         .andExpect(status().isOk())
                         .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                         .andExpect(jsonPath("$").isArray())
                         .andExpect(jsonPath("$.length()").value(2))
                         .andExpect(jsonPath("$[0]").value("En preparación"))
                         .andExpect(jsonPath("$[1]").value("Cancelado"));

               verify(pedidoService).getTransicionesDisponibles("Pendiente");
          }

          @Test
          @DisplayName("GREEN: Debería retornar lista vacía para estados finales")
          void deberiaRetornarListaVaciaParaEstadosFinales() throws Exception {
               // Given
               when(pedidoService.getTransicionesDisponibles("Entregado")).thenReturn(Collections.emptyList());

               // When & Then
               mockMvc.perform(get("/api/pedidos/transiciones/Entregado"))
                         .andExpect(status().isOk())
                         .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                         .andExpect(jsonPath("$").isArray())
                         .andExpect(jsonPath("$.length()").value(0));

               verify(pedidoService).getTransicionesDisponibles("Entregado");
          }
     }
}
