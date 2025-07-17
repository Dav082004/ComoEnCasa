package com.comoencasa_backend.service;

import com.comoencasa_backend.dto.PedidoDTO;
import com.comoencasa_backend.model.Pedido;
import com.comoencasa_backend.model.Usuario;
import com.comoencasa_backend.repository.PedidoRepository;
import com.comoencasa_backend.repository.UsuarioRepository;
import com.comoencasa_backend.repository.PagoRepository;
import com.comoencasa_backend.service.impl.PedidoServiceImpl;
import com.comoencasa_backend.service.EmailService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test TDD para PedidoService
 * Patrón: Red-Green-Refactor
 * 
 * El servicio PedidoService es responsable de:
 * - Gestionar pedidos de los usuarios
 * - Convertir entre modelos y DTOs
 * - Manejar operaciones CRUD
 * - Validar reglas de negocio
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PedidoService TDD Tests")
public class PedidoServiceTDDTest {

    @Mock
    private PedidoRepository pedidoRepository;
    
    @Mock
    private UsuarioRepository usuarioRepository;
    
    @Mock
    private PagoRepository pagoRepository;
    
    @Mock
    private EmailService emailService;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    private Pedido pedidoTest;
    private Usuario usuarioTest;

    @BeforeEach
    void setUp() {
        // Configuración común para todos los tests
        usuarioTest = new Usuario();
        usuarioTest.setId(1L);
        usuarioTest.setNombre("Juan");
        usuarioTest.setEmail("juan@test.com");

        pedidoTest = new Pedido();
        pedidoTest.setId(1L);
        pedidoTest.setUsuario(usuarioTest);
        pedidoTest.setFechaCreacion(LocalDateTime.now());
        pedidoTest.setEstado("Pendiente");
        pedidoTest.setSubtotal(new BigDecimal("50.00"));
        pedidoTest.setCostoTotal(new BigDecimal("59.00"));
        pedidoTest.setDireccionEntrega("Av. Test 123");
    }

    // ===========================================
    // TESTS para findAll()
    // ===========================================
    @Nested
    @DisplayName("Tests para findAll()")
    class TestsFindAll {

        @Test
        @DisplayName("GREEN: Debería retornar lista vacía cuando no hay pedidos")
        void deberiaRetornarListaVaciaCuandoNoHayPedidos() {
            // Given
            when(pedidoRepository.findAllWithDetails()).thenReturn(Collections.emptyList());

            // When
            List<PedidoDTO> resultado = pedidoService.findAll();

            // Then
            assertThat(resultado).isEmpty();
            verify(pedidoRepository).findAllWithDetails();
        }

        @Test
        @DisplayName("GREEN: Debería retornar lista de pedidos cuando existen")
        void deberiaRetornarListaDePedidosCuandoExisten() {
            // Given
            Pedido pedido2 = new Pedido();
            pedido2.setId(2L);
            pedido2.setUsuario(usuarioTest);
            pedido2.setEstado("En preparación");

            when(pedidoRepository.findAllWithDetails()).thenReturn(Arrays.asList(pedidoTest, pedido2));

            // When
            List<PedidoDTO> resultado = pedidoService.findAll();

            // Then
            assertThat(resultado)
                .hasSize(2)
                .extracting(PedidoDTO::getId)
                .containsExactly(1L, 2L);

            assertThat(resultado.get(0).getUsuarioNombre()).isEqualTo("Juan");
            assertThat(resultado.get(0).getEstado()).isEqualTo("Pendiente");
            verify(pedidoRepository).findAllWithDetails();
        }

        @Test
        @DisplayName("GREEN: Debería convertir correctamente modelo a DTO")
        void deberiaConvertirCorrectamenteModeloADTO() {
            // Given
            when(pedidoRepository.findAllWithDetails()).thenReturn(Arrays.asList(pedidoTest));

            // When
            List<PedidoDTO> resultado = pedidoService.findAll();

            // Then
            PedidoDTO pedidoDTO = resultado.get(0);
            assertThat(pedidoDTO.getId()).isEqualTo(1L);
            assertThat(pedidoDTO.getUsuarioNombre()).isEqualTo("Juan");
            assertThat(pedidoDTO.getEstado()).isEqualTo("Pendiente");
            assertThat(pedidoDTO.getSubtotal()).isEqualByComparingTo(new BigDecimal("50.00"));
            assertThat(pedidoDTO.getCostoTotal()).isEqualByComparingTo(new BigDecimal("59.00"));
            assertThat(pedidoDTO.getDireccionEntrega()).isEqualTo("Av. Test 123");
        }
    }

    // ===========================================
    // TESTS para obtenerPedidosPorUsuario()
    // ===========================================
    @Nested
    @DisplayName("Tests para obtenerPedidosPorUsuario()")
    class TestsObtenerPedidosPorUsuario {

        @Test
        @DisplayName("GREEN: Debería retornar lista vacía cuando usuario no tiene pedidos")
        void deberiaRetornarListaVaciaCuandoUsuarioNoTienePedidos() {
            // Given
            when(pedidoRepository.findByUsuarioIdWithDetails(1L)).thenReturn(Collections.emptyList());

            // When
            List<PedidoDTO> resultado = pedidoService.obtenerPedidosPorUsuario(1L);

            // Then
            assertThat(resultado).isEmpty();
            verify(pedidoRepository).findByUsuarioIdWithDetails(1L);
        }

        @Test
        @DisplayName("GREEN: Debería retornar pedidos del usuario específico")
        void deberiaRetornarPedidosDelUsuarioEspecifico() {
            // Given
            when(pedidoRepository.findByUsuarioIdWithDetails(1L)).thenReturn(Arrays.asList(pedidoTest));

            // When
            List<PedidoDTO> resultado = pedidoService.obtenerPedidosPorUsuario(1L);

            // Then
            assertThat(resultado)
                .hasSize(1)
                .extracting(PedidoDTO::getId)
                .containsExactly(1L);

            assertThat(resultado.get(0).getUsuarioNombre()).isEqualTo("Juan");
            verify(pedidoRepository).findByUsuarioIdWithDetails(1L);
        }

        @Test
        @DisplayName("GREEN: Debería filtrar pedidos por usuario correctamente")
        void deberiaFiltrarPedidosPorUsuarioCorrectamente() {
            // Given
            Usuario usuario2 = new Usuario();
            usuario2.setId(2L);
            usuario2.setNombre("María");

            Pedido pedidoUsuario2 = new Pedido();
            pedidoUsuario2.setId(2L);
            pedidoUsuario2.setUsuario(usuario2);
            pedidoUsuario2.setEstado("Completado");

            when(pedidoRepository.findByUsuarioIdWithDetails(2L)).thenReturn(Arrays.asList(pedidoUsuario2));

            // When
            List<PedidoDTO> resultado = pedidoService.obtenerPedidosPorUsuario(2L);

            // Then
            assertThat(resultado)
                .hasSize(1)
                .extracting(PedidoDTO::getUsuarioNombre)
                .containsExactly("María");

            verify(pedidoRepository).findByUsuarioIdWithDetails(2L);
        }
    }

    // ===========================================
    // TESTS para findById()
    // ===========================================
    @Nested
    @DisplayName("Tests para findById()")
    class TestsFindById {

        @Test
        @DisplayName("GREEN: Debería retornar pedido cuando existe")
        void deberiaRetornarPedidoCuandoExiste() {
            // Given
            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoTest));

            // When
            PedidoDTO resultado = pedidoService.findById(1L);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getUsuarioNombre()).isEqualTo("Juan");
            verify(pedidoRepository).findById(1L);
        }

        @Test
        @DisplayName("RED: Debería lanzar excepción cuando no existe")
        void deberiaLanzarExcepcionCuandoNoExiste() {
            // Given
            when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(RuntimeException.class, () -> {
                pedidoService.findById(999L);
            });
            
            verify(pedidoRepository).findById(999L);
        }
    }

    // ===========================================
    // TESTS para crearPedido()
    // ===========================================
    @Nested
    @DisplayName("Tests para crearPedido()")
    class TestsCrearPedido {

        @Test
        @DisplayName("GREEN: Debería crear pedido correctamente")
        void deberiaCrearPedidoCorrectamente() {
            // Given
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTest));
            when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoTest);

            // When
            PedidoDTO resultado = pedidoService.crearPedido(convertToDTO(pedidoTest));

            // Then
            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getUsuarioNombre()).isEqualTo("Juan");
            assertThat(resultado.getEstado()).isEqualTo("Pendiente");
            verify(pedidoRepository).save(any(Pedido.class));
        }

        @Test
        @DisplayName("GREEN: Debería asignar fecha de creación al crear")
        void deberiaAsignarFechaDeCreacionAlCrear() {
            // Given
            Pedido pedidoNuevo = new Pedido();
            pedidoNuevo.setUsuario(usuarioTest);
            pedidoNuevo.setEstado("Pendiente");
            
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTest));
            when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoTest);

            // When
            PedidoDTO resultado = pedidoService.crearPedido(convertToDTO(pedidoNuevo));

            // Then
            assertThat(resultado).isNotNull();
            verify(pedidoRepository).save(any(Pedido.class));
        }
    }

    // ===========================================
    // TESTS para actualizarEstadoPedido()
    // ===========================================
    @Nested
    @DisplayName("Tests para actualizarEstadoPedido()")
    class TestsActualizarEstadoPedido {

        @Test
        @DisplayName("GREEN: Debería actualizar estado correctamente")
        void deberiaActualizarEstadoCorrectamente() {
            // Given
            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoTest));
            when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoTest);

            // When
            PedidoDTO resultado = pedidoService.actualizarEstadoPedido(1L, "Completado");

            // Then
            assertThat(resultado).isNotNull();
            verify(pedidoRepository).findById(1L);
            verify(pedidoRepository).save(any(Pedido.class));
        }

        @Test
        @DisplayName("RED: Debería lanzar excepción cuando pedido no existe")
        void deberiaLanzarExcepcionCuandoPedidoNoExiste() {
            // Given
            when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(RuntimeException.class, () -> {
                pedidoService.actualizarEstadoPedido(999L, "Completado");
            });
            
            verify(pedidoRepository).findById(999L);
        }
    }

    // ===========================================
    // TESTS para eliminarPedido()
    // ===========================================
    @Nested
    @DisplayName("Tests para eliminarPedido()")
    class TestsEliminarPedido {

        @Test
        @DisplayName("GREEN: Debería eliminar pedido correctamente")
        void deberiaEliminarPedidoCorrectamente() {
            // Given
            when(pedidoRepository.existsById(1L)).thenReturn(true);

            // When
            pedidoService.eliminarPedido(1L);

            // Then
            verify(pedidoRepository).existsById(1L);
            verify(pedidoRepository).deleteById(1L);
        }

        @Test
        @DisplayName("RED: Debería lanzar excepción cuando pedido no existe")
        void deberiaLanzarExcepcionCuandoPedidoNoExisteParaEliminar() {
            // Given
            when(pedidoRepository.existsById(999L)).thenReturn(false);

            // When & Then
            assertThrows(RuntimeException.class, () -> {
                pedidoService.eliminarPedido(999L);
            });
            
            verify(pedidoRepository).existsById(999L);
        }
    }

    // ===========================================
    // MÉTODOS AUXILIARES
    // ===========================================
    private PedidoDTO convertToDTO(Pedido pedido) {
        PedidoDTO dto = new PedidoDTO();
        dto.setId(pedido.getId());
        dto.setUsuarioNombre(pedido.getUsuario() != null ? pedido.getUsuario().getNombre() : null);
        dto.setEstado(pedido.getEstado());
        dto.setSubtotal(pedido.getSubtotal());
        dto.setCostoTotal(pedido.getCostoTotal());
        dto.setDireccionEntrega(pedido.getDireccionEntrega());
        dto.setFechaCreacion(pedido.getFechaCreacion());
        return dto;
    }
}
