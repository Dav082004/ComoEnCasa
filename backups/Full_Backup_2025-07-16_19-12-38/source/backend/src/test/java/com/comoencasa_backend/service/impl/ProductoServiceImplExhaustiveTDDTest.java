package com.comoencasa_backend.service.impl;

import com.comoencasa_backend.model.Producto;
import com.comoencasa_backend.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductoServiceImpl - Tests Exhaustivos TDD")
class ProductoServiceImplExhaustiveTDDTest {

     @Mock
     private ProductoRepository productoRepository;

     @InjectMocks
     private ProductoServiceImpl productoService;

     private Producto productoTest;
     private Producto productoTest2;

     @BeforeEach
     void setUp() {
          productoTest = new Producto();
          productoTest.setId(1L);
          productoTest.setNombre("Torta de Chocolate");
          productoTest.setDescripcion("Deliciosa torta casera");
          productoTest.setPrecioVenta(25.99);
          productoTest.setCostoProduccion(15.00);
          productoTest.setCategoriaId(1L);
          productoTest.setDisponible(true);
          productoTest.setCantidad(10);
          productoTest.setImagenUrl("/images/torta.jpg");

          productoTest2 = new Producto();
          productoTest2.setId(2L);
          productoTest2.setNombre("Brownie Premium");
          productoTest2.setDescripcion("Brownie con chocolate belga");
          productoTest2.setPrecioVenta(18.50);
          productoTest2.setCostoProduccion(10.00);
          productoTest2.setCategoriaId(2L);
          productoTest2.setDisponible(true);
          productoTest2.setCantidad(5);
          productoTest2.setImagenUrl("/images/brownie.jpg");
     }

     @Nested
     @DisplayName("actualizarStock - TDD")
     class ActualizarStock {

          @Test
          @DisplayName("RED: Debería lanzar excepción si productoId es nulo")
          void deberiaLanzarExcepcionSiProductoIdEsNulo() {
               // When & Then
               assertThatThrownBy(() -> productoService.actualizarStock(null, 5))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("El ID del producto no puede ser nulo");
          }

          @Test
          @DisplayName("RED: Debería lanzar excepción si nuevaCantidad es nula")
          void deberiaLanzarExcepcionSiNuevaCantidadEsNula() {
               // When & Then
               assertThatThrownBy(() -> productoService.actualizarStock(1L, null))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("La cantidad no puede ser nula o negativa");
          }

          @Test
          @DisplayName("RED: Debería lanzar excepción si nuevaCantidad es negativa")
          void deberiaLanzarExcepcionSiNuevaCantidadEsNegativa() {
               // When & Then
               assertThatThrownBy(() -> productoService.actualizarStock(1L, -1))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("La cantidad no puede ser nula o negativa");
          }

          @Test
          @DisplayName("RED: Debería lanzar excepción si producto no existe")
          void deberiaLanzarExcepcionSiProductoNoExiste() {
               // Given
               when(productoRepository.findById(999L)).thenReturn(Optional.empty());

               // When & Then
               assertThatThrownBy(() -> productoService.actualizarStock(999L, 5))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("Producto no encontrado con ID: 999");
          }

          @Test
          @DisplayName("GREEN: Debería actualizar stock y mantener disponible si cantidad > 0")
          void deberiaActualizarStockYMantenerDisponibleSiCantidadMayorACero() {
               // Given
               when(productoRepository.findById(1L)).thenReturn(Optional.of(productoTest));
               when(productoRepository.save(any(Producto.class))).thenReturn(productoTest);

               // When
               Producto resultado = productoService.actualizarStock(1L, 15);

               // Then
               assertThat(resultado.getCantidad()).isEqualTo(15);
               assertThat(resultado.getDisponible()).isTrue();
               verify(productoRepository).save(productoTest);
          }

          @Test
          @DisplayName("GREEN: Debería marcar como no disponible si stock es 0")
          void deberiaMacarComoNoDisponibleSiStockEsCero() {
               // Given
               when(productoRepository.findById(1L)).thenReturn(Optional.of(productoTest));
               when(productoRepository.save(any(Producto.class))).thenReturn(productoTest);

               // When
               Producto resultado = productoService.actualizarStock(1L, 0);

               // Then
               assertThat(resultado.getCantidad()).isEqualTo(0);
               assertThat(resultado.getDisponible()).isFalse();
               verify(productoRepository).save(productoTest);
          }

          @Test
          @DisplayName("GREEN: Debería activar producto si se agrega stock a producto inactivo")
          void deberiaActivarProductoSiSeAgregaStockAProductoInactivo() {
               // Given
               productoTest.setDisponible(false);
               productoTest.setCantidad(0);
               when(productoRepository.findById(1L)).thenReturn(Optional.of(productoTest));
               when(productoRepository.save(any(Producto.class))).thenReturn(productoTest);

               // When
               Producto resultado = productoService.actualizarStock(1L, 8);

               // Then
               assertThat(resultado.getCantidad()).isEqualTo(8);
               assertThat(resultado.getDisponible()).isTrue();
          }
     }

     @Nested
     @DisplayName("cambiarDisponibilidad - TDD")
     class CambiarDisponibilidad {

          @Test
          @DisplayName("RED: Debería lanzar excepción si productoId es nulo")
          void deberiaLanzarExcepcionSiProductoIdEsNulo() {
               // When & Then
               assertThatThrownBy(() -> productoService.cambiarDisponibilidad(null, true))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("El ID del producto no puede ser nulo");
          }

          @Test
          @DisplayName("RED: Debería lanzar excepción si disponible es nulo")
          void deberiaLanzarExcepcionSiDisponibleEsNulo() {
               // When & Then
               assertThatThrownBy(() -> productoService.cambiarDisponibilidad(1L, null))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("El estado de disponibilidad no puede ser nulo");
          }

          @Test
          @DisplayName("RED: Debería lanzar excepción si producto no existe")
          void deberiaLanzarExcepcionSiProductoNoExiste() {
               // Given
               when(productoRepository.findById(999L)).thenReturn(Optional.empty());

               // When & Then
               assertThatThrownBy(() -> productoService.cambiarDisponibilidad(999L, false))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("Producto no encontrado con ID: 999");
          }

          @Test
          @DisplayName("GREEN: Debería cambiar disponibilidad a false exitosamente")
          void deberiaCambiarDisponibilidadAFalseExitosamente() {
               // Given
               when(productoRepository.findById(1L)).thenReturn(Optional.of(productoTest));
               when(productoRepository.save(any(Producto.class))).thenReturn(productoTest);

               // When
               Producto resultado = productoService.cambiarDisponibilidad(1L, false);

               // Then
               assertThat(resultado.getDisponible()).isFalse();
               verify(productoRepository).save(productoTest);
          }

          @Test
          @DisplayName("GREEN: Debería cambiar disponibilidad a true exitosamente")
          void deberiaCambiarDisponibilidadATrueExitosamente() {
               // Given
               productoTest.setDisponible(false);
               when(productoRepository.findById(1L)).thenReturn(Optional.of(productoTest));
               when(productoRepository.save(any(Producto.class))).thenReturn(productoTest);

               // When
               Producto resultado = productoService.cambiarDisponibilidad(1L, true);

               // Then
               assertThat(resultado.getDisponible()).isTrue();
               verify(productoRepository).save(productoTest);
          }
     }

     @Nested
     @DisplayName("reducirStock - TDD")
     class ReducirStock {

          @Test
          @DisplayName("RED: Debería lanzar excepción si productoId es nulo")
          void deberiaLanzarExcepcionSiProductoIdEsNulo() {
               // When & Then
               assertThatThrownBy(() -> productoService.reducirStock(null, 2))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("El ID del producto no puede ser nulo");
          }

          @Test
          @DisplayName("RED: Debería lanzar excepción si cantidadVendida es nula")
          void deberiaLanzarExcepcionSiCantidadVendidaEsNula() {
               // When & Then
               assertThatThrownBy(() -> productoService.reducirStock(1L, null))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("La cantidad vendida debe ser mayor a 0");
          }

          @Test
          @DisplayName("RED: Debería lanzar excepción si cantidadVendida es 0")
          void deberiaLanzarExcepcionSiCantidadVendidaEsCero() {
               // When & Then
               assertThatThrownBy(() -> productoService.reducirStock(1L, 0))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("La cantidad vendida debe ser mayor a 0");
          }

          @Test
          @DisplayName("RED: Debería lanzar excepción si cantidadVendida es negativa")
          void deberiaLanzarExcepcionSiCantidadVendidaEsNegativa() {
               // When & Then
               assertThatThrownBy(() -> productoService.reducirStock(1L, -1))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("La cantidad vendida debe ser mayor a 0");
          }

          @Test
          @DisplayName("RED: Debería lanzar excepción si producto no existe")
          void deberiaLanzarExcepcionSiProductoNoExiste() {
               // Given
               when(productoRepository.findById(999L)).thenReturn(Optional.empty());

               // When & Then
               assertThatThrownBy(() -> productoService.reducirStock(999L, 2))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("Producto no encontrado con ID: 999");
          }

          @Test
          @DisplayName("RED: Debería lanzar excepción si stock insuficiente")
          void deberiaLanzarExcepcionSiStockInsuficiente() {
               // Given
               productoTest.setCantidad(3);
               when(productoRepository.findById(1L)).thenReturn(Optional.of(productoTest));

               // When & Then
               assertThatThrownBy(() -> productoService.reducirStock(1L, 5))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("Stock insuficiente. Disponible: 3, Solicitado: 5");
          }

          @Test
          @DisplayName("GREEN: Debería reducir stock correctamente sin llegar a 0")
          void deberiaReducirStockCorrectamenteSinLlegarACero() {
               // Given
               productoTest.setCantidad(10);
               when(productoRepository.findById(1L)).thenReturn(Optional.of(productoTest));

               // When
               productoService.reducirStock(1L, 3);

               // Then
               assertThat(productoTest.getCantidad()).isEqualTo(7);
               assertThat(productoTest.getDisponible()).isTrue(); // Debe seguir disponible
               verify(productoRepository).save(productoTest);
          }

          @Test
          @DisplayName("GREEN: Debería marcar como no disponible si stock llega a 0")
          void deberiaMacarComoNoDisponibleSiStockLlegaACero() {
               // Given
               productoTest.setCantidad(5);
               when(productoRepository.findById(1L)).thenReturn(Optional.of(productoTest));

               // When
               productoService.reducirStock(1L, 5);

               // Then
               assertThat(productoTest.getCantidad()).isEqualTo(0);
               assertThat(productoTest.getDisponible()).isFalse();
               verify(productoRepository).save(productoTest);
          }

          @Test
          @DisplayName("GREEN: Debería manejar producto sin stock definido")
          void deberiaManejarProductoSinStockDefinido() {
               // Given
               productoTest.setCantidad(null);
               when(productoRepository.findById(1L)).thenReturn(Optional.of(productoTest));

               // When & Then
               assertThatThrownBy(() -> productoService.reducirStock(1L, 1))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("Stock insuficiente. Disponible: 0, Solicitado: 1");
          }
     }

     @Nested
     @DisplayName("create - TDD")
     class Create {

          @Test
          @DisplayName("GREEN: Debería crear producto exitosamente")
          void deberiaCrearProductoExitosamente() {
               // Given
               Producto productoNuevo = new Producto();
               productoNuevo.setNombre("Nuevo Producto");
               productoNuevo.setDescripcion("Descripción del producto");
               productoNuevo.setPrecioVenta(30.00);
               productoNuevo.setCostoProduccion(20.00);

               when(productoRepository.save(any(Producto.class))).thenReturn(productoNuevo);

               // When
               Producto resultado = productoService.create(productoNuevo);

               // Then
               assertThat(resultado).isNotNull();
               assertThat(resultado.getNombre()).isEqualTo("Nuevo Producto");
               verify(productoRepository).save(any(Producto.class));
          }

          @Test
          @DisplayName("GREEN: Debería sanitizar campos al crear producto")
          void deberiaSanitizarCamposAlCrearProducto() {
               // Given
               Producto productoConHTML = new Producto();
               productoConHTML.setNombre("<script>alert('xss')</script>Producto Peligroso");
               productoConHTML.setDescripcion("Descripción con <b>HTML</b>");
               productoConHTML.setImagenUrl("/images/<script>evil.js");

               when(productoRepository.save(any(Producto.class))).thenReturn(productoConHTML);

               // When
               Producto resultado = productoService.create(productoConHTML);

               // Then
               assertThat(resultado.getNombre()).doesNotContain("<script>");
               assertThat(resultado.getNombre()).contains("&lt;script&gt;");
               verify(productoRepository).save(any(Producto.class));
          }

          @Test
          @DisplayName("GREEN: Debería manejar campos nulos en sanitización")
          void deberiaManejarCamposNulosEnSanitizacion() {
               // Given
               Producto productoConNulos = new Producto();
               productoConNulos.setNombre(null);
               productoConNulos.setDescripcion(null);
               productoConNulos.setImagenUrl(null);
               productoConNulos.setPrecioVenta(15.00);

               when(productoRepository.save(any(Producto.class))).thenReturn(productoConNulos);

               // When & Then - No debería lanzar excepción
               assertThatCode(() -> productoService.create(productoConNulos))
                         .doesNotThrowAnyException();
          }
     }

     @Nested
     @DisplayName("update - TDD")
     class Update {

          @Test
          @DisplayName("RED: Debería lanzar excepción si producto no existe")
          void deberiaLanzarExcepcionSiProductoNoExiste() {
               // Given
               when(productoRepository.findById(999L)).thenReturn(Optional.empty());
               Producto datosActualizacion = new Producto();

               // When & Then
               assertThatThrownBy(() -> productoService.update(999L, datosActualizacion))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("No existe producto 999");
          }

          @Test
          @DisplayName("GREEN: Debería actualizar producto exitosamente")
          void deberiaActualizarProductoExitosamente() {
               // Given
               when(productoRepository.findById(1L)).thenReturn(Optional.of(productoTest));
               when(productoRepository.save(any(Producto.class))).thenReturn(productoTest);

               Producto datosActualizacion = new Producto();
               datosActualizacion.setNombre("Torta Actualizada");
               datosActualizacion.setDescripcion("Nueva descripción");
               datosActualizacion.setPrecioVenta(35.00);
               datosActualizacion.setCostoProduccion(20.00);
               datosActualizacion.setImagenUrl("/images/nueva.jpg");
               datosActualizacion.setCantidad(15);

               // When
               Producto resultado = productoService.update(1L, datosActualizacion); // Then
               assertThat(resultado.getNombre()).isEqualTo("Torta Actualizada");
               assertThat(resultado.getDescripcion()).isEqualTo("Nueva descripci&oacute;n"); // Texto sanitizado
               assertThat(resultado.getPrecioVenta()).isEqualTo(35.00);
               assertThat(resultado.getCantidad()).isEqualTo(15);
               assertThat(resultado.getDisponible()).isTrue(); // Debe ser true porque cantidad > 0
               verify(productoRepository).save(productoTest);
          }

          @Test
          @DisplayName("GREEN: Debería marcar como no disponible si cantidad es 0")
          void deberiaMacarComoNoDisponibleSiCantidadEsCero() {
               // Given
               when(productoRepository.findById(1L)).thenReturn(Optional.of(productoTest));
               when(productoRepository.save(any(Producto.class))).thenReturn(productoTest);

               Producto datosActualizacion = new Producto();
               datosActualizacion.setNombre("Producto Sin Stock");
               datosActualizacion.setDescripcion("Descripción");
               datosActualizacion.setPrecioVenta(25.00);
               datosActualizacion.setCostoProduccion(15.00);
               datosActualizacion.setImagenUrl("/images/producto.jpg");
               datosActualizacion.setCantidad(0);

               // When
               Producto resultado = productoService.update(1L, datosActualizacion);

               // Then
               assertThat(resultado.getCantidad()).isEqualTo(0);
               assertThat(resultado.getDisponible()).isFalse();
          }

          @Test
          @DisplayName("GREEN: Debería sanitizar datos en actualización")
          void deberiaSanitizarDatosEnActualizacion() {
               // Given
               when(productoRepository.findById(1L)).thenReturn(Optional.of(productoTest));
               when(productoRepository.save(any(Producto.class))).thenReturn(productoTest);

               Producto datosConHTML = new Producto();
               datosConHTML.setNombre("<b>Producto con HTML</b>");
               datosConHTML.setDescripcion("Descripción <script>alert('test')</script>");
               datosConHTML.setPrecioVenta(25.00);
               datosConHTML.setCostoProduccion(15.00);
               datosConHTML.setImagenUrl("/images/<img>test.jpg");
               datosConHTML.setCantidad(5);

               // When
               productoService.update(1L, datosConHTML);

               // Then
               assertThat(productoTest.getNombre()).contains("&lt;b&gt;");
               assertThat(productoTest.getDescripcion()).contains("&lt;script&gt;");
               assertThat(productoTest.getImagenUrl()).contains("&lt;img&gt;");
          }
     }

     @Nested
     @DisplayName("delete - TDD")
     class Delete {

          @Test
          @DisplayName("RED: Debería lanzar excepción si producto no existe")
          void deberiaLanzarExcepcionSiProductoNoExiste() {
               // Given
               when(productoRepository.existsById(999L)).thenReturn(false);

               // When & Then
               assertThatThrownBy(() -> productoService.delete(999L))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("No existe producto 999");
          }

          @Test
          @DisplayName("GREEN: Debería eliminar producto exitosamente")
          void deberiaEliminarProductoExitosamente() {
               // Given
               when(productoRepository.existsById(1L)).thenReturn(true);

               // When
               productoService.delete(1L);

               // Then
               verify(productoRepository).deleteById(1L);
          }
     }

     @Nested
     @DisplayName("Métodos existentes - Cobertura adicional")
     class MetodosExistentesCobertura {

          @Test
          @DisplayName("GREEN: findAllAvailable debería retornar productos disponibles")
          void findAllAvailableDeberiaRetornarProductosDisponibles() {
               // Given
               List<Producto> productosDisponibles = Arrays.asList(productoTest, productoTest2);
               when(productoRepository.findByDisponibleTrue()).thenReturn(productosDisponibles);

               // When
               List<Producto> resultado = productoService.findAllAvailable();

               // Then
               assertThat(resultado).hasSize(2);
               assertThat(resultado).containsExactly(productoTest, productoTest2);
               verify(productoRepository).findByDisponibleTrue();
          }

          @Test
          @DisplayName("GREEN: findAll debería retornar todos los productos")
          void findAllDeberiaRetornarTodosLosProductos() {
               // Given
               List<Producto> todosLosProductos = Arrays.asList(productoTest, productoTest2);
               when(productoRepository.findAll()).thenReturn(todosLosProductos);

               // When
               List<Producto> resultado = productoService.findAll();

               // Then
               assertThat(resultado).hasSize(2);
               assertThat(resultado).containsExactly(productoTest, productoTest2);
               verify(productoRepository).findAll();
          }

          @Test
          @DisplayName("GREEN: findByCategoriaId debería retornar productos por categoría")
          void findByCategoriaIdDeberiaRetornarProductosPorCategoria() {
               // Given
               List<Producto> productosPorCategoria = Arrays.asList(productoTest);
               when(productoRepository.findByCategoriaIdAndDisponibleTrue(1L))
                         .thenReturn(productosPorCategoria);

               // When
               List<Producto> resultado = productoService.findByCategoriaId(1L);

               // Then
               assertThat(resultado).hasSize(1);
               assertThat(resultado).containsExactly(productoTest);
               verify(productoRepository).findByCategoriaIdAndDisponibleTrue(1L);
          }
     }
}
