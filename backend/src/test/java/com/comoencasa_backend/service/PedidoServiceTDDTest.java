package com.comoencasa_backend.service;

import com.comoencasa_backend.dto.PedidoDTO;
import com.comoencasa_backend.model.Pedido;
import com.comoencasa_backend.model.Usuario;
import com.comoencasa_backend.repository.PedidoRepository;
import com.comoencasa_backend.repository.UsuarioRepository;
import com.comoencasa_backend.service.impl.PedidoServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PedidoService TDD Tests")
class PedidoServiceTDDTest {

     @Mock
     private PedidoRepository pedidoRepository;

     @Mock
     private UsuarioRepository usuarioRepository;

     @InjectMocks
     private PedidoServiceImpl pedidoService;

     private Usuario usuarioTest;
     private Pedido pedidoTest;
     private PedidoDTO pedidoDTOTest;

     @BeforeEach
     void setUp() {
          // Configurar datos de prueba
          usuarioTest = new Usuario();
          usuarioTest.setId(1L);
          usuarioTest.setNombre("Juan");
          usuarioTest.setApellido("Pérez");
          usuarioTest.setEmail("juan@test.com");

          pedidoTest = new Pedido();
          pedidoTest.setId(1L);
          pedidoTest.setUsuario(usuarioTest);
          pedidoTest.setFechaCreacion(LocalDateTime.now());
          pedidoTest.setEstado("Pendiente");
          pedidoTest.setSubtotal(new BigDecimal("50.00"));
          pedidoTest.setCostoTotal(new BigDecimal("59.00"));
          pedidoTest.setDireccionEntrega("Av. Test 123");

          pedidoDTOTest = new PedidoDTO();
          pedidoDTOTest.setUsuarioId(1L);
          pedidoDTOTest.setFechaCreacion(LocalDateTime.now());
          pedidoDTOTest.setEstado("Pendiente");
          pedidoDTOTest.setSubtotal(new BigDecimal("50.00"));
          pedidoDTOTest.setCostoTotal(new BigDecimal("59.00"));
          pedidoDTOTest.setDireccionEntrega("Av. Test 123");
     }

     @Nested
     @DisplayName("Listar todos los pedidos")
     class ListarTodosLosPedidos {

          @Test
          @DisplayName("RED: Debería retornar lista vacía cuando no hay pedidos")
          void deberiaRetornarListaVaciaCuandoNoHayPedidos() {
               // Given
               when(pedidoRepository.findAll()).thenReturn(Collections.emptyList());

               // When
               List<PedidoDTO> resultado = pedidoService.findAll();

               // Then
               assertThat(resultado).isEmpty();
               verify(pedidoRepository).findAll();
          }

          @Test
          @DisplayName("GREEN: Debería retornar lista de pedidos cuando existen")
          void deberiaRetornarListaDePedidosCuandoExisten() {
               // Given
               Pedido pedido2 = new Pedido();
               pedido2.setId(2L);
               pedido2.setUsuario(usuarioTest);
               pedido2.setEstado("En preparación");

               when(pedidoRepository.findAll()).thenReturn(Arrays.asList(pedidoTest, pedido2));

               // When
               List<PedidoDTO> resultado = pedidoService.findAll();

               // Then
               assertThat(resultado)
                         .hasSize(2)
                         .extracting(PedidoDTO::getId)
                         .containsExactly(1L, 2L);

               assertThat(resultado.get(0).getUsuarioNombre()).isEqualTo("Juan");
               assertThat(resultado.get(0).getEstado()).isEqualTo("Pendiente");
               assertThat(resultado.get(1).getEstado()).isEqualTo("En preparación");
          }
     }

     @Nested
     @DisplayName("Obtener pedidos por usuario")
     class ObtenerPedidosPorUsuario {

          @Test
          @DisplayName("RED: Debería lanzar excepción si usuarioId es nulo")
          void deberiaLanzarExcepcionSiUsuarioIdEsNulo() {
               // When & Then
               assertThatThrownBy(() -> pedidoService.obtenerPedidosPorUsuario(null))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("El ID de usuario no puede ser nulo");
          }

          @Test
          @DisplayName("RED: Debería lanzar excepción si usuarioId es menor o igual a cero")
          void deberiaLanzarExcepcionSiUsuarioIdEsInvalido() {
               // When & Then
               assertThatThrownBy(() -> pedidoService.obtenerPedidosPorUsuario(0L))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("El ID de usuario debe ser mayor a 0");

               assertThatThrownBy(() -> pedidoService.obtenerPedidosPorUsuario(-1L))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("El ID de usuario debe ser mayor a 0");
          }

          @Test
          @DisplayName("GREEN: Debería retornar lista vacía cuando usuario no tiene pedidos")
          void deberiaRetornarListaVaciaCuandoUsuarioNoTienePedidos() {
               // Given
               when(pedidoRepository.findByUsuarioId(1L)).thenReturn(Collections.emptyList());

               // When
               List<PedidoDTO> resultado = pedidoService.obtenerPedidosPorUsuario(1L);

               // Then
               assertThat(resultado).isEmpty();
               verify(pedidoRepository).findByUsuarioId(1L);
          }

          @Test
          @DisplayName("GREEN: Debería retornar pedidos del usuario específico")
          void deberiaRetornarPedidosDelUsuarioEspecifico() {
               // Given
               when(pedidoRepository.findByUsuarioId(1L)).thenReturn(Arrays.asList(pedidoTest));

               // When
               List<PedidoDTO> resultado = pedidoService.obtenerPedidosPorUsuario(1L);

               // Then
               assertThat(resultado)
                         .hasSize(1)
                         .extracting(PedidoDTO::getId)
                         .containsExactly(1L);

               assertThat(resultado.get(0).getUsuarioId()).isEqualTo(1L);
               assertThat(resultado.get(0).getUsuarioNombre()).isEqualTo("Juan");
          }
     }

     @Nested
     @DisplayName("Crear pedido")
     class CrearPedido {

          @Test
          @DisplayName("RED: Debería lanzar excepción si pedidoDTO es nulo")
          void deberiaLanzarExcepcionSiPedidoDTOEsNulo() {
               // When & Then
               assertThatThrownBy(() -> pedidoService.crearPedido(null))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("El pedido no puede ser nulo");
          }

          @Test
          @DisplayName("RED: Debería lanzar excepción si usuarioId es nulo")
          void deberiaLanzarExcepcionSiUsuarioIdEsNulo() {
               // Given
               pedidoDTOTest.setUsuarioId(null);

               // When & Then
               assertThatThrownBy(() -> pedidoService.crearPedido(pedidoDTOTest))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("El ID de usuario no puede ser nulo");
          }

          @Test
          @DisplayName("RED: Debería lanzar excepción si usuario no existe")
          void deberiaLanzarExcepcionSiUsuarioNoExiste() {
               // Given
               when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

               // When & Then
               assertThatThrownBy(() -> pedidoService.crearPedido(pedidoDTOTest))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("Usuario no encontrado con ID=1");
          }

          @Test
          @DisplayName("GREEN: Debería crear pedido exitosamente")
          void deberiaCrearPedidoExitosamente() {
               // Given
               when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTest));
               when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoTest);

               // When
               PedidoDTO resultado = pedidoService.crearPedido(pedidoDTOTest);

               // Then
               assertThat(resultado).isNotNull();
               assertThat(resultado.getId()).isEqualTo(1L);
               assertThat(resultado.getUsuarioId()).isEqualTo(1L);
               assertThat(resultado.getUsuarioNombre()).isEqualTo("Juan");
               assertThat(resultado.getEstado()).isEqualTo("Pendiente");
               assertThat(resultado.getCostoTotal()).isEqualTo(new BigDecimal("59.00"));

               verify(usuarioRepository).findById(1L);
               verify(pedidoRepository).save(any(Pedido.class));
          }
     }

     @Nested
     @DisplayName("Actualizar estado de pedido - NUEVAS FUNCIONALIDADES")
     class ActualizarEstadoPedido {

          @Test
          @DisplayName("RED: Debería lanzar excepción si pedidoId es nulo")
          void deberiaLanzarExcepcionSiPedidoIdEsNulo() {
               // When & Then
               assertThatThrownBy(() -> pedidoService.actualizarEstadoPedido(null, "En preparación"))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("El ID del pedido no puede ser nulo");
          }

          @Test
          @DisplayName("RED: Debería lanzar excepción si estado es nulo o vacío")
          void deberiaLanzarExcepcionSiEstadoEsNuloOVacio() {
               // When & Then
               assertThatThrownBy(() -> pedidoService.actualizarEstadoPedido(1L, null))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("El estado no puede ser nulo o vacío");

               assertThatThrownBy(() -> pedidoService.actualizarEstadoPedido(1L, ""))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("El estado no puede ser nulo o vacío");

               assertThatThrownBy(() -> pedidoService.actualizarEstadoPedido(1L, "   "))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("El estado no puede ser nulo o vacío");
          }

          @Test
          @DisplayName("RED: Debería lanzar excepción si pedido no existe")
          void deberiaLanzarExcepcionSiPedidoNoExiste() {
               // Given
               when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

               // When & Then
               assertThatThrownBy(() -> pedidoService.actualizarEstadoPedido(999L, "En preparación"))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("Pedido no encontrado con ID=999");
          }

          @Test
          @DisplayName("RED: Debería lanzar excepción para transiciones no válidas desde estados finales")
          void deberiaLanzarExcepcionParaTransicionesNoValidasDesdeEstadosFinales() {
               // Given - Pedido en estado "Entregado"
               pedidoTest.setEstado("Entregado");
               when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoTest));

               // When & Then
               assertThatThrownBy(() -> pedidoService.actualizarEstadoPedido(1L, "En preparación"))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("Transición no válida desde estado Entregado a En preparación");
          }

          @Test
          @DisplayName("RED: Debería lanzar excepción para transición de retroceso sin confirmación especial")
          void deberiaLanzarExcepcionParaRetrocesoSinConfirmacion() {
               // Given - Pedido en estado "En preparación"
               pedidoTest.setEstado("En preparación");
               when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoTest));

               // When & Then
               assertThatThrownBy(() -> pedidoService.actualizarEstadoPedido(1L, "Pendiente"))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("Transición de retroceso requiere confirmación especial");
          }

          @Test
          @DisplayName("GREEN: Debería actualizar estado exitosamente para transiciones válidas")
          void deberiaActualizarEstadoExitosamenteParaTransicionesValidas() {
               // Given - Transición normal: Pendiente -> En preparación
               when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoTest));

               Pedido pedidoActualizado = new Pedido();
               pedidoActualizado.setId(1L);
               pedidoActualizado.setUsuario(usuarioTest);
               pedidoActualizado.setEstado("En preparación");
               pedidoActualizado.setFechaCreacion(pedidoTest.getFechaCreacion());

               when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoActualizado);

               // When
               PedidoDTO resultado = pedidoService.actualizarEstadoPedido(1L, "En preparación");

               // Then
               assertThat(resultado).isNotNull();
               assertThat(resultado.getId()).isEqualTo(1L);
               assertThat(resultado.getEstado()).isEqualTo("En preparación");

               verify(pedidoRepository).findById(1L);
               verify(pedidoRepository).save(any(Pedido.class));
          }

          @Test
          @DisplayName("GREEN: Debería permitir cancelación desde cualquier estado")
          void deberiaPermitirCancelacionDesdeEstadoCualquiera() {
               // Given - Pedido en cualquier estado puede ser cancelado
               pedidoTest.setEstado("En preparación");
               when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoTest));

               Pedido pedidoCancelado = new Pedido();
               pedidoCancelado.setId(1L);
               pedidoCancelado.setUsuario(usuarioTest);
               pedidoCancelado.setEstado("Cancelado");

               when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoCancelado);

               // When
               PedidoDTO resultado = pedidoService.actualizarEstadoPedido(1L, "Cancelado");

               // Then
               assertThat(resultado.getEstado()).isEqualTo("Cancelado");
               verify(pedidoRepository).save(any(Pedido.class));
          }
     }

     @Nested
     @DisplayName("Actualizar estado forzado - NUEVAS FUNCIONALIDADES")
     class ActualizarEstadoForzado {
          @Test
          @DisplayName("RED: Debería lanzar excepción si password es incorrecto")
          void deberiaLanzarExcepcionSiPasswordEsIncorrecto() {
               // Given
               lenient().when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoTest));

               // When & Then
               assertThatThrownBy(
                         () -> pedidoService.actualizarEstadoPedidoForzado(1L, "En preparación", "password_incorrecto"))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("Contraseña de confirmación incorrecta");
          }

          @Test
          @DisplayName("GREEN: Debería permitir cualquier transición con password correcto")
          void deberiaPermitirCualquierTransicionConPasswordCorrecto() {
               // Given - Estado final que normalmente no permitiría cambios
               pedidoTest.setEstado("Entregado");
               when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoTest));

               Pedido pedidoForzado = new Pedido();
               pedidoForzado.setId(1L);
               pedidoForzado.setUsuario(usuarioTest);
               pedidoForzado.setEstado("Pendiente");

               when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoForzado);

               // When - Usando password de administrador correcto
               PedidoDTO resultado = pedidoService.actualizarEstadoPedidoForzado(1L, "Pendiente", "123");

               // Then
               assertThat(resultado.getEstado()).isEqualTo("Pendiente");
               verify(pedidoRepository).save(any(Pedido.class));
          }

          @Test
          @DisplayName("GREEN: Debería permitir retroceso con password correcto")
          void deberiaPermitirRetrocesoConPasswordCorrecto() {
               // Given - Retroceso que normalmente requiere confirmación
               pedidoTest.setEstado("En preparación");
               when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoTest));

               Pedido pedidoRetrocedido = new Pedido();
               pedidoRetrocedido.setId(1L);
               pedidoRetrocedido.setUsuario(usuarioTest);
               pedidoRetrocedido.setEstado("Pendiente");

               when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoRetrocedido);

               // When
               PedidoDTO resultado = pedidoService.actualizarEstadoPedidoForzado(1L, "Pendiente", "123");

               // Then
               assertThat(resultado.getEstado()).isEqualTo("Pendiente");
               verify(pedidoRepository).save(any(Pedido.class));
          }
     }

     @Nested
     @DisplayName("Validación de estados")
     class ValidacionEstados {

          @Test
          @DisplayName("GREEN: Debería validar estados disponibles correctamente")
          void deberiaValidarEstadosDisponiblesCorrectamente() {
               // Given
               List<String> estadosValidos = Arrays.asList("Pendiente", "En preparación", "Entregado", "Cancelado");

               // When & Then - Probar cada estado válido
               for (String estado : estadosValidos) {
                    assertThat(pedidoService.esEstadoValido(estado)).isTrue();
               }

               // Estados inválidos
               assertThat(pedidoService.esEstadoValido("Estado_Inexistente")).isFalse();
               assertThat(pedidoService.esEstadoValido(null)).isFalse();
               assertThat(pedidoService.esEstadoValido("")).isFalse();
          }

          @Test
          @DisplayName("GREEN: Debería obtener transiciones disponibles correctamente")
          void deberiaObtenerTransicionesDisponiblesCorrectamente() {
               // When & Then
               List<String> transicionesPendiente = pedidoService.getTransicionesDisponibles("Pendiente");
               assertThat(transicionesPendiente).containsExactlyInAnyOrder("En preparación", "Cancelado");

               List<String> transicionesEnPreparacion = pedidoService.getTransicionesDisponibles("En preparación");
               assertThat(transicionesEnPreparacion).containsExactlyInAnyOrder("Entregado", "Cancelado", "Pendiente"); // Incluye
                                                                                                                       // retroceso

               List<String> transicionesEntregado = pedidoService.getTransicionesDisponibles("Entregado");
               assertThat(transicionesEntregado).isEmpty(); // Estado final sin transiciones normales

               List<String> transicionesCancelado = pedidoService.getTransicionesDisponibles("Cancelado");
               assertThat(transicionesCancelado).isEmpty(); // Estado final sin transiciones normales
          }
     }
}
