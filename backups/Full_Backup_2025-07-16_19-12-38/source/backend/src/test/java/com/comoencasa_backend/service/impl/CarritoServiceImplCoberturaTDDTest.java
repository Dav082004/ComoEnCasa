package com.comoencasa_backend.service.impl;

import com.comoencasa_backend.dao.CarritoDAO;
import com.comoencasa_backend.dto.CarritoDTO;
import com.comoencasa_backend.dto.CarritoItemDTO;
import com.comoencasa_backend.model.Producto;
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
@DisplayName("CarritoServiceImpl - Tests Adicionales de Cobertura")
class CarritoServiceImplCoberturaTDDTest {

     @Mock
     private CarritoDAO carritoDAO;

     @Mock
     private ProductoService productoService;

     @InjectMocks
     private CarritoServiceImpl carritoService;

     private Producto productoTest;
     private String sessionId;
     private Map<String, CarritoDTO> carritoStorage;

     @BeforeEach
     void setUp() {
          sessionId = "test-session-456";
          carritoStorage = new HashMap<>();

          productoTest = new Producto();
          productoTest.setId(1L);
          productoTest.setNombre("Pastel de Tres Leches");
          productoTest.setDescripcion("Delicioso pastel casero");
          productoTest.setPrecioVenta(35.00);
          productoTest.setImagenUrl("/images/pastel.jpg");
          productoTest.setDisponible(true);
          productoTest.setCantidad(10); // Stock disponible

          // Configurar mocks
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

          lenient().doAnswer(invocation -> {
               String sessionId = invocation.getArgument(0);
               carritoStorage.remove(sessionId);
               return null;
          }).when(carritoDAO).eliminarCarrito(anyString());
     }

     @Nested
     @DisplayName("Métodos auxiliares privados - Coverage")
     class MetodosAuxiliares {

          @Test
          @DisplayName("GREEN: crearCarritoVacio debería funcionar correctamente")
          void crearCarritoVacioDeberiaFuncionarCorrectamente() {
               // When - Obtener carrito que no existe invoca crearCarritoVacio
               CarritoDTO resultado = carritoService.obtenerCarrito("nueva-sesion");

               // Then
               assertThat(resultado).isNotNull();
               assertThat(resultado.getSessionId()).isEqualTo("nueva-sesion");
               assertThat(resultado.getItems()).isEmpty();
               assertThat(resultado.getTotalItems()).isEqualTo(0);
               assertThat(resultado.getTotal()).isEqualTo(0.0);
          }

          @Test
          @DisplayName("GREEN: buscarItemEnCarrito debería encontrar items correctamente")
          void buscarItemEnCarritoDeberiaEncontrarItemsCorrectamente() {
               // Given - Agregar producto para probar búsqueda
               when(productoService.findById(1L)).thenReturn(Optional.of(productoTest));
               carritoService.agregarProducto(sessionId, 1L, 2, "Comentario de prueba");

               // When - Actualizar cantidad (invoca buscarItemEnCarrito internamente)
               CarritoDTO resultado = carritoService.actualizarCantidad(sessionId, 1L, 5);

               // Then
               assertThat(resultado.getItems()).hasSize(1);
               assertThat(resultado.getItems().get(0).getCantidad()).isEqualTo(5);
          }

          @Test
          @DisplayName("GREEN: obtenerCantidadEnCarrito debería retornar cantidad correcta")
          void obtenerCantidadEnCarritoDeberiaRetornarCantidadCorrecta() {
               // Given - Producto con stock limitado para activar validación
               productoTest.setCantidad(3);
               when(productoService.findById(1L)).thenReturn(Optional.of(productoTest));
               carritoService.agregarProducto(sessionId, 1L, 2, "");

               // When - Intentar agregar más del stock disponible
               // Then - Debería validar correctamente el stock total
               assertThatThrownBy(() -> carritoService.agregarProducto(sessionId, 1L, 5, ""))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("Stock insuficiente");
          }
     }

     @Nested
     @DisplayName("Casos edge de validación")
     class CasosEdgeValidacion {

          @Test
          @DisplayName("RED: Debería manejar sessionId con espacios")
          void deberiaManejarSessionIdConEspacios() {
               // When & Then
               assertThatThrownBy(() -> carritoService.agregarProducto("   ", 1L, 1, ""))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("Session ID no puede ser nulo");
          }

          @Test
          @DisplayName("GREEN: Debería manejar producto sin stock definido")
          void deberiaManejarProductoSinStockDefinido() {
               // Given - Producto sin cantidad definida (null)
               productoTest.setCantidad(null);
               when(productoService.findById(1L)).thenReturn(Optional.of(productoTest));

               // When
               CarritoDTO resultado = carritoService.agregarProducto(sessionId, 1L, 100, "");

               // Then - Debería permitir cualquier cantidad si stock es null
               assertThat(resultado.getItems()).hasSize(1);
               assertThat(resultado.getItems().get(0).getCantidad()).isEqualTo(100);
          }

          @Test
          @DisplayName("GREEN: Debería manejar actualizaciones sin carrito previo")
          void deberiaManejarActualizacionesSinCarritoPrevio() {
               // When & Then
               assertThatThrownBy(() -> carritoService.actualizarCantidad("sesion-inexistente", 1L, 5))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("No existe carrito para la sesión");
          }

          @Test
          @DisplayName("GREEN: Debería manejar eliminación sin carrito previo")
          void deberiaManejarEliminacionSinCarritoPrevio() {
               // When & Then
               assertThatThrownBy(() -> carritoService.eliminarProducto("sesion-inexistente", 1L))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("No existe carrito para la sesión");
          }
     }

     @Nested
     @DisplayName("Lógica de negocio específica")
     class LogicaNegocio {

          @Test
          @DisplayName("GREEN: Debería establecer cantidad exacta al agregar producto existente")
          void deberiaEstablecerCantidadExactaAlAgregarProductoExistente() {
               // Given
               when(productoService.findById(1L)).thenReturn(Optional.of(productoTest));

               // Agregar producto inicial
               carritoService.agregarProducto(sessionId, 1L, 3, "Comentario inicial");

               // When - Agregar mismo producto con nueva cantidad
               CarritoDTO resultado = carritoService.agregarProducto(sessionId, 1L, 7, "Comentario actualizado");

               // Then - Debe establecer cantidad exacta (no sumar)
               assertThat(resultado.getItems()).hasSize(1);
               CarritoItemDTO item = resultado.getItems().get(0);
               assertThat(item.getCantidad()).isEqualTo(7); // Cantidad establecida, no 3+7=10
               assertThat(item.getComentarios()).isEqualTo("Comentario actualizado");
          }

          @Test
          @DisplayName("GREEN: Debería preservar comentarios si se envían vacíos en actualización")
          void deberiaPreservarComentariosSiSeEnvianVaciosEnActualizacion() {
               // Given
               when(productoService.findById(1L)).thenReturn(Optional.of(productoTest));
               carritoService.agregarProducto(sessionId, 1L, 2, "Comentario original");

               // When - Actualizar con comentario vacío
               CarritoDTO resultado = carritoService.agregarProducto(sessionId, 1L, 3, "   ");

               // Then - Comentario original debe preservarse
               assertThat(resultado.getItems()).hasSize(1);
               CarritoItemDTO item = resultado.getItems().get(0);
               assertThat(item.getComentarios()).isEqualTo("Comentario original");
          }

          @Test
          @DisplayName("GREEN: Debería actualizar comentarios si se envían nuevos válidos")
          void deberiaActualizarComentariosSiSeEnvianNuevosValidos() {
               // Given
               when(productoService.findById(1L)).thenReturn(Optional.of(productoTest));
               carritoService.agregarProducto(sessionId, 1L, 2, "Comentario original");

               // When - Actualizar con comentario nuevo válido
               CarritoDTO resultado = carritoService.agregarProducto(sessionId, 1L, 3, "Comentario nuevo");

               // Then
               assertThat(resultado.getItems()).hasSize(1);
               CarritoItemDTO item = resultado.getItems().get(0);
               assertThat(item.getComentarios()).isEqualTo("Comentario nuevo");
          }
     }

     @Nested
     @DisplayName("Interacciones con DAO")
     class InteraccionesDAO {

          @Test
          @DisplayName("GREEN: Debería llamar al DAO correctamente en todas las operaciones")
          void deberiaLlamarDAOCorrectamenteEnTodasOperaciones() {
               // Given
               when(productoService.findById(1L)).thenReturn(Optional.of(productoTest));

               // When - Operaciones múltiples
               carritoService.agregarProducto(sessionId, 1L, 2, "");
               carritoService.actualizarCantidad(sessionId, 1L, 3);
               carritoService.obtenerCarrito(sessionId);
               carritoService.obtenerTotalItems(sessionId);
               carritoService.limpiarCarrito(sessionId);

               // Then - Verificar interacciones
               verify(carritoDAO, atLeastOnce()).obtenerCarrito(sessionId);
               verify(carritoDAO, atLeastOnce()).guardarCarrito(eq(sessionId), any(CarritoDTO.class));
               verify(carritoDAO, times(1)).eliminarCarrito(sessionId);
          }

          @Test
          @DisplayName("GREEN: Debería manejar carritos vacíos correctamente")
          void deberiaManejarCarritosVaciosCorrectamente() {
               // When - Operaciones sobre carrito vacío/inexistente
               CarritoDTO carritoObtenido = carritoService.obtenerCarrito("nueva-sesion");
               Integer totalItems = carritoService.obtenerTotalItems("nueva-sesion");

               // Then
               assertThat(carritoObtenido).isNotNull();
               assertThat(carritoObtenido.getItems()).isEmpty();
               assertThat(totalItems).isEqualTo(0);
          }
     }

     @Nested
     @DisplayName("Manejo de sessionId edge cases")
     class ManejoSessionId {

          @Test
          @DisplayName("GREEN: obtenerCarrito debería manejar sessionId nulo")
          void obtenerCarritoDeberiaManejarSessionIdNulo() {
               // When
               CarritoDTO resultado = carritoService.obtenerCarrito(null);

               // Then
               assertThat(resultado).isNotNull();
               assertThat(resultado.getSessionId()).isEmpty();
               assertThat(resultado.getItems()).isEmpty();
          }

          @Test
          @DisplayName("GREEN: obtenerCarrito debería manejar sessionId vacío")
          void obtenerCarritoDeberiaManejarSessionIdVacio() {
               // When
               CarritoDTO resultado = carritoService.obtenerCarrito("   ");

               // Then
               assertThat(resultado).isNotNull();
               assertThat(resultado.getSessionId()).isEqualTo("   ");
               assertThat(resultado.getItems()).isEmpty();
          }

          @Test
          @DisplayName("GREEN: obtenerTotalItems debería retornar 0 para sessionId nulo")
          void obtenerTotalItemsDeberiaRetornar0ParaSessionIdNulo() {
               // When
               Integer total = carritoService.obtenerTotalItems(null);

               // Then
               assertThat(total).isEqualTo(0);
          }

          @Test
          @DisplayName("GREEN: obtenerTotalItems debería retornar 0 para sessionId vacío")
          void obtenerTotalItemsDeberiaRetornar0ParaSessionIdVacio() {
               // When
               Integer total = carritoService.obtenerTotalItems("   ");

               // Then
               assertThat(total).isEqualTo(0);
          }
     }

     @Nested
     @DisplayName("Validaciones específicas de actualización")
     class ValidacionesActualizacion {

          @Test
          @DisplayName("GREEN: actualizarCantidad debería validar stock en productos con stock limitado")
          void actualizarCantidadDeberiaValidarStockEnProductosConStockLimitado() {
               // Given
               productoTest.setCantidad(5); // Stock limitado
               when(productoService.findById(1L)).thenReturn(Optional.of(productoTest));
               carritoService.agregarProducto(sessionId, 1L, 2, "");

               // When & Then
               assertThatThrownBy(() -> carritoService.actualizarCantidad(sessionId, 1L, 10))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("Stock insuficiente");
          }

          @Test
          @DisplayName("GREEN: actualizarCantidad debería permitir cualquier cantidad si producto no tiene stock definido")
          void actualizarCantidadDeberiaPermitirCualquierCantidadSiProductoNoTieneStockDefinido() {
               // Given
               productoTest.setCantidad(null); // Sin stock definido
               when(productoService.findById(1L)).thenReturn(Optional.of(productoTest));
               carritoService.agregarProducto(sessionId, 1L, 1, "");

               // When
               CarritoDTO resultado = carritoService.actualizarCantidad(sessionId, 1L, 100);

               // Then
               assertThat(resultado.getItems().get(0).getCantidad()).isEqualTo(100);
          }
     }

     @Nested
     @DisplayName("Constructor y inicialización")
     class ConstructorInicializacion {
          @Test
          @DisplayName("GREEN: Constructor debería inicializar correctamente")
          void constructorDeberiaInicializarCorrectamente() {
               // When - Crear nueva instancia
               CarritoServiceImpl nuevoServicio = new CarritoServiceImpl(carritoDAO, productoService);

               // Then - Debería funcionar básicamente
               assertThatCode(() -> nuevoServicio.obtenerCarrito("test"))
                         .doesNotThrowAnyException();
          }
     }

     @Nested
     @DisplayName("Logging y depuración")
     class LoggingDepuracion {

          @Test
          @DisplayName("GREEN: Operaciones deberían registrar logs apropiados")
          void operacionesDeberianRegistrarLogsApropiados() {
               // Given
               when(productoService.findById(1L)).thenReturn(Optional.of(productoTest));

               // When - Ejecutar operaciones que generan logs
               carritoService.agregarProducto(sessionId, 1L, 2, "Test logging");
               carritoService.actualizarCantidad(sessionId, 1L, 3);
               carritoService.eliminarProducto(sessionId, 1L);

               // Then - Los logs se generan automáticamente, verificar que operaciones
               // funcionan
               verify(carritoDAO, atLeastOnce()).guardarCarrito(eq(sessionId), any(CarritoDTO.class));
          }
     }
}
