package com.comoencasa_backend.service.impl;

import com.comoencasa_backend.model.Producto;
import com.comoencasa_backend.repository.ProductoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.comoencasa_backend.testutil.TestDataFactory.*;

/**
 * Tests TDD exhaustivos para ProductoServiceImpl
 * Enfocados en aumentar la cobertura de métodos no cubiertos
 * Patrón Red-Green-Refactor aplicado estrictamente
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductoServiceImpl Cobertura Completa TDD")
class ProductoServiceImplCoberturaTDDTest {

     @Mock
     private ProductoRepository productoRepository;

     @InjectMocks
     private ProductoServiceImpl productoService;

     @Nested
     @DisplayName("Tests TDD para findAll()")
     class TestsFindAll {

          @Test
          @DisplayName("RED: findAll() debería retornar lista vacía cuando no hay productos")
          void findAll_DeberiaRetornarListaVacia_CuandoNoHayProductos() {
               // Given
               when(productoRepository.findAll()).thenReturn(Collections.emptyList());

               // When
               List<Producto> resultado = productoService.findAll();

               // Then
               assertThat(resultado).isEmpty();
               verify(productoRepository).findAll();
          }

          @Test
          @DisplayName("GREEN: findAll() debería retornar todos los productos incluyendo no disponibles")
          void findAll_DeberiaRetornarTodosLosProductos_IncluyendoNoDisponibles() {
               // Given
               Producto disponible = unProducto()
                         .conNombre("Producto Disponible")
                         .build();

               Producto noDisponible = unProducto()
                         .conNombre("Producto No Disponible")
                         .noDisponible()
                         .build();

               when(productoRepository.findAll()).thenReturn(Arrays.asList(disponible, noDisponible));

               // When
               List<Producto> resultado = productoService.findAll();

               // Then
               assertThat(resultado)
                         .hasSize(2)
                         .extracting(Producto::getNombre)
                         .containsExactly("Producto Disponible", "Producto No Disponible");

               verify(productoRepository).findAll();
          }
     }

     @Nested
     @DisplayName("Tests TDD para actualizarStock()")
     class TestsActualizarStock {

          @Test
          @DisplayName("RED: actualizarStock() debería fallar con producto ID nulo")
          void actualizarStock_DeberiaFallar_ConProductoIdNulo() {
               // When & Then
               assertThatThrownBy(() -> productoService.actualizarStock(null, 10))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("El ID del producto no puede ser nulo");
          }

          @Test
          @DisplayName("RED: actualizarStock() debería fallar con cantidad nula")
          void actualizarStock_DeberiaFallar_ConCantidadNula() {
               // When & Then
               assertThatThrownBy(() -> productoService.actualizarStock(1L, null))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("La cantidad no puede ser nula o negativa");
          }

          @Test
          @DisplayName("RED: actualizarStock() debería fallar con cantidad negativa")
          void actualizarStock_DeberiaFallar_ConCantidadNegativa() {
               // When & Then
               assertThatThrownBy(() -> productoService.actualizarStock(1L, -5))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("La cantidad no puede ser nula o negativa");
          }

          @Test
          @DisplayName("RED: actualizarStock() debería fallar cuando producto no existe")
          void actualizarStock_DeberiaFallar_CuandoProductoNoExiste() {
               // Given
               Long idInexistente = 999L;
               when(productoRepository.findById(idInexistente)).thenReturn(Optional.empty());

               // When & Then
               assertThatThrownBy(() -> productoService.actualizarStock(idInexistente, 10))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("Producto no encontrado con ID: " + idInexistente);
          }

          @Test
          @DisplayName("GREEN: actualizarStock() debería actualizar stock y marcar como disponible")
          void actualizarStock_DeberiaActualizarStockYMarcarComoDisponible() {
               // Given
               Long productoId = 1L;
               Integer nuevaCantidad = 50;
               Producto producto = unProducto()
                         .conId(productoId)
                         .conCantidad(10)
                         .noDisponible()
                         .build();

               Producto productoActualizado = unProducto()
                         .conId(productoId)
                         .conCantidad(nuevaCantidad)
                         .build();

               when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));
               when(productoRepository.save(any(Producto.class))).thenReturn(productoActualizado);

               // When
               Producto resultado = productoService.actualizarStock(productoId, nuevaCantidad);

               // Then
               assertThat(resultado.getCantidad()).isEqualTo(nuevaCantidad);
               assertThat(resultado.getDisponible()).isTrue();

               verify(productoRepository).findById(productoId);
               verify(productoRepository)
                         .save(argThat(p -> p.getCantidad().equals(nuevaCantidad) && p.getDisponible()));
          }

          @Test
          @DisplayName("GREEN: actualizarStock() debería marcar como no disponible cuando stock es 0")
          void actualizarStock_DeberiaMarcarComoNoDisponible_CuandoStockEsCero() {
               // Given
               Long productoId = 1L;
               Integer cantidadCero = 0;
               Producto producto = unProducto()
                         .conId(productoId)
                         .conCantidad(10)
                         .build();

               Producto productoActualizado = unProducto()
                         .conId(productoId)
                         .conCantidad(cantidadCero)
                         .noDisponible()
                         .build();

               when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));
               when(productoRepository.save(any(Producto.class))).thenReturn(productoActualizado);

               // When
               Producto resultado = productoService.actualizarStock(productoId, cantidadCero);

               // Then
               assertThat(resultado.getCantidad()).isEqualTo(cantidadCero);
               assertThat(resultado.getDisponible()).isFalse();

               verify(productoRepository)
                         .save(argThat(p -> p.getCantidad().equals(cantidadCero) && !p.getDisponible()));
          }
     }

     @Nested
     @DisplayName("Tests TDD para cambiarDisponibilidad()")
     class TestsCambiarDisponibilidad {

          @Test
          @DisplayName("RED: cambiarDisponibilidad() debería fallar con producto ID nulo")
          void cambiarDisponibilidad_DeberiaFallar_ConProductoIdNulo() {
               // When & Then
               assertThatThrownBy(() -> productoService.cambiarDisponibilidad(null, true))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("El ID del producto no puede ser nulo");
          }

          @Test
          @DisplayName("RED: cambiarDisponibilidad() debería fallar con disponibilidad nula")
          void cambiarDisponibilidad_DeberiaFallar_ConDisponibilidadNula() {
               // When & Then
               assertThatThrownBy(() -> productoService.cambiarDisponibilidad(1L, null))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("El estado de disponibilidad no puede ser nulo");
          }

          @Test
          @DisplayName("RED: cambiarDisponibilidad() debería fallar cuando producto no existe")
          void cambiarDisponibilidad_DeberiaFallar_CuandoProductoNoExiste() {
               // Given
               Long idInexistente = 999L;
               when(productoRepository.findById(idInexistente)).thenReturn(Optional.empty());

               // When & Then
               assertThatThrownBy(() -> productoService.cambiarDisponibilidad(idInexistente, true))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("Producto no encontrado con ID: " + idInexistente);
          }

          @Test
          @DisplayName("GREEN: cambiarDisponibilidad() debería activar disponibilidad")
          void cambiarDisponibilidad_DeberiaActivarDisponibilidad() {
               // Given
               Long productoId = 1L;
               Producto producto = unProducto()
                         .conId(productoId)
                         .noDisponible()
                         .build();

               Producto productoActualizado = unProducto()
                         .conId(productoId)
                         .build();

               when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));
               when(productoRepository.save(any(Producto.class))).thenReturn(productoActualizado);

               // When
               Producto resultado = productoService.cambiarDisponibilidad(productoId, true);

               // Then
               assertThat(resultado.getDisponible()).isTrue();

               verify(productoRepository).findById(productoId);
               verify(productoRepository).save(argThat(p -> p.getDisponible()));
          }

          @Test
          @DisplayName("GREEN: cambiarDisponibilidad() debería desactivar disponibilidad")
          void cambiarDisponibilidad_DeberiaDesactivarDisponibilidad() {
               // Given
               Long productoId = 1L;
               Producto producto = unProducto()
                         .conId(productoId)
                         .build();

               Producto productoActualizado = unProducto()
                         .conId(productoId)
                         .noDisponible()
                         .build();

               when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));
               when(productoRepository.save(any(Producto.class))).thenReturn(productoActualizado);

               // When
               Producto resultado = productoService.cambiarDisponibilidad(productoId, false);

               // Then
               assertThat(resultado.getDisponible()).isFalse();

               verify(productoRepository).save(argThat(p -> !p.getDisponible()));
          }
     }

     @Nested
     @DisplayName("Tests TDD para reducirStock()")
     class TestsReducirStock {

          @Test
          @DisplayName("RED: reducirStock() debería fallar con producto ID nulo")
          void reducirStock_DeberiaFallar_ConProductoIdNulo() {
               // When & Then
               assertThatThrownBy(() -> productoService.reducirStock(null, 5))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("El ID del producto no puede ser nulo");
          }

          @Test
          @DisplayName("RED: reducirStock() debería fallar con cantidad vendida nula")
          void reducirStock_DeberiaFallar_ConCantidadVendidaNula() {
               // When & Then
               assertThatThrownBy(() -> productoService.reducirStock(1L, null))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("La cantidad vendida debe ser mayor a 0");
          }

          @Test
          @DisplayName("RED: reducirStock() debería fallar con cantidad vendida menor o igual a cero")
          void reducirStock_DeberiaFallar_ConCantidadVendidaMenorOIgualACero() {
               // When & Then
               assertThatThrownBy(() -> productoService.reducirStock(1L, 0))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("La cantidad vendida debe ser mayor a 0");

               assertThatThrownBy(() -> productoService.reducirStock(1L, -5))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("La cantidad vendida debe ser mayor a 0");
          }

          @Test
          @DisplayName("RED: reducirStock() debería fallar cuando producto no existe")
          void reducirStock_DeberiaFallar_CuandoProductoNoExiste() {
               // Given
               Long idInexistente = 999L;
               when(productoRepository.findById(idInexistente)).thenReturn(Optional.empty());

               // When & Then
               assertThatThrownBy(() -> productoService.reducirStock(idInexistente, 5))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("Producto no encontrado con ID: " + idInexistente);
          }

          @Test
          @DisplayName("RED: reducirStock() debería fallar con stock insuficiente")
          void reducirStock_DeberiaFallar_ConStockInsuficiente() {
               // Given
               Long productoId = 1L;

               Producto producto = unProducto()
                         .conId(productoId)
                         .conCantidad(5)
                         .build();

               when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));

               // When & Then
               assertThatThrownBy(() -> productoService.reducirStock(productoId, 10))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("Stock insuficiente. Disponible: 5, Solicitado: 10");
          }

          @Test
          @DisplayName("GREEN: reducirStock() debería reducir stock correctamente")
          void reducirStock_DeberiaReducirStockCorrectamente() {
               // Given
               Long productoId = 1L;
               Integer stockInicial = 20;
               Integer cantidadVendida = 5;

               Producto producto = unProducto()
                         .conId(productoId)
                         .conCantidad(stockInicial)
                         .conDisponible(true)
                         .build();

               when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));

               // When
               productoService.reducirStock(productoId, cantidadVendida);

               // Then
               verify(productoRepository).save(argThat(p -> p.getCantidad().equals(stockInicial - cantidadVendida) &&
                         p.getDisponible()));
          }

          @Test
          @DisplayName("GREEN: reducirStock() debería marcar como no disponible cuando stock llega a 0")
          void reducirStock_DeberiaMarcarComoNoDisponible_CuandoStockLlegaACero() {
               // Given
               Long productoId = 1L;
               Integer stockInicial = 5;
               Integer cantidadVendida = 5;

               Producto producto = unProducto()
                         .conId(productoId)
                         .conCantidad(stockInicial)
                         .build();

               when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));

               // When
               productoService.reducirStock(productoId, cantidadVendida);

               // Then
               verify(productoRepository).save(argThat(p -> p.getCantidad().equals(0) &&
                         !p.getDisponible()));
          }

          @Test
          @DisplayName("REFACTOR: reducirStock() debería manejar cantidad nula en producto")
          void reducirStock_DeberiaManejarCantidadNulaEnProducto() {
               // Given
               Long productoId = 1L;

               Producto producto = unProducto()
                         .conId(productoId)
                         .conCantidad(null) // Cantidad nula
                         .build();

               when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));

               // When & Then
               assertThatThrownBy(() -> productoService.reducirStock(productoId, 1))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("Stock insuficiente. Disponible: 0, Solicitado: 1");
          }
     }

     @Nested
     @DisplayName("Tests TDD para create()")
     class TestsCreate {

          @Test
          @DisplayName("GREEN: create() debería crear producto con datos sanitizados")
          void create_DeberiaCrearProductoConDatosSanitizados() {
               // Given
               Producto productoConDatosSucios = unProducto()
                         .conNombre("<script>alert('hack')</script>Torta")
                         .conDescripcion("Descripción con   espacios   extra")
                         .conImagenUrl("  /imagen.jpg  ")
                         .build();

               Producto productoGuardado = unProducto()
                         .conId(1L)
                         .conNombre("&lt;script&gt;alert(&#x27;hack&#x27;)&lt;/script&gt;Torta")
                         .conDescripcion("Descripción con   espacios   extra")
                         .conImagenUrl("/imagen.jpg")
                         .build();

               when(productoRepository.save(any(Producto.class))).thenReturn(productoGuardado);

               // When
               Producto resultado = productoService.create(productoConDatosSucios);

               // Then
               assertThat(resultado.getId()).isEqualTo(1L);

               verify(productoRepository).save(argThat(p -> !p.getNombre().contains("<script>") &&
                         !p.getDescripcion().startsWith(" ") &&
                         !p.getImagenUrl().startsWith(" ")));
          }

          @Test
          @DisplayName("REFACTOR: create() debería manejar datos completamente nulos")
          void create_DeberiaManejarDatosCompletamenteNulos() {
               // Given
               Producto productoConNulos = new Producto();
               productoConNulos.setNombre(null);
               productoConNulos.setDescripcion(null);
               productoConNulos.setImagenUrl(null);

               Producto productoGuardado = unProducto()
                         .conId(1L)
                         .conNombre("")
                         .conDescripcion("")
                         .conImagenUrl("")
                         .build();

               when(productoRepository.save(any(Producto.class))).thenReturn(productoGuardado);

               // When
               Producto resultado = productoService.create(productoConNulos);

               // Then
               assertThat(resultado.getId()).isEqualTo(1L);

               verify(productoRepository).save(argThat(p -> p.getNombre().equals("") &&
                         p.getDescripcion().equals("") &&
                         p.getImagenUrl().equals("")));
          }
     }

     @Nested
     @DisplayName("Tests TDD para update()")
     class TestsUpdate {

          @Test
          @DisplayName("RED: update() debería fallar cuando producto no existe")
          void update_DeberiaFallar_CuandoProductoNoExiste() {
               // Given
               Long idInexistente = 999L;
               Producto datosNuevos = unProducto().build();

               when(productoRepository.findById(idInexistente)).thenReturn(Optional.empty());

               // When & Then
               assertThatThrownBy(() -> productoService.update(idInexistente, datosNuevos))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("No existe producto " + idInexistente);
          }

          @Test
          @DisplayName("GREEN: update() debería actualizar producto existente")
          void update_DeberiaActualizarProductoExistente() {
               // Given
               Long productoId = 1L;

               Producto productoExistente = unProducto()
                         .conId(productoId)
                         .conNombre("Nombre Viejo")
                         .conDescripcion("Descripción Vieja")
                         .conPrecio(10.0)
                         .conCantidad(5)
                         .build();

               Producto datosNuevos = unProducto()
                         .conNombre("Nombre Nuevo")
                         .conDescripcion("Descripción Nueva")
                         .conPrecio(15.0)
                         .conCantidad(10)
                         .build();

               Producto productoActualizado = unProducto()
                         .conId(productoId)
                         .conNombre("Nombre Nuevo")
                         .conDescripcion("Descripción Nueva")
                         .conPrecio(15.0)
                         .conCantidad(10)
                         .conDisponible(true)
                         .build();

               when(productoRepository.findById(productoId)).thenReturn(Optional.of(productoExistente));
               when(productoRepository.save(any(Producto.class))).thenReturn(productoActualizado);

               // When
               Producto resultado = productoService.update(productoId, datosNuevos);

               // Then
               assertThat(resultado.getNombre()).isEqualTo("Nombre Nuevo");
               assertThat(resultado.getDescripcion()).isEqualTo("Descripción Nueva");
               assertThat(resultado.getPrecioVenta()).isEqualTo(15.0);
               assertThat(resultado.getCantidad()).isEqualTo(10);
               assertThat(resultado.getDisponible()).isTrue();

               verify(productoRepository).findById(productoId);
               verify(productoRepository).save(any(Producto.class));
          }

          @Test
          @DisplayName("GREEN: update() debería marcar como no disponible si cantidad es 0")
          void update_DeberiaMarcarComoNoDisponible_SiCantidadEsCero() {
               // Given
               Long productoId = 1L;
               Producto productoExistente = unProducto()
                         .conId(productoId)
                         .conCantidad(10)
                         .build();

               Producto datosNuevos = unProducto()
                         .conCantidad(0)
                         .build();

               Producto productoActualizado = unProducto()
                         .conId(productoId)
                         .conCantidad(0)
                         .noDisponible()
                         .build();

               when(productoRepository.findById(productoId)).thenReturn(Optional.of(productoExistente));
               when(productoRepository.save(any(Producto.class))).thenReturn(productoActualizado);

               // When
               Producto resultado = productoService.update(productoId, datosNuevos);

               // Then
               assertThat(resultado.getCantidad()).isEqualTo(0);
               assertThat(resultado.getDisponible()).isFalse();

               verify(productoRepository).save(argThat(p -> p.getCantidad().equals(0) && !p.getDisponible()));
          }

          @Test
          @DisplayName("REFACTOR: update() debería sanitizar datos de entrada")
          void update_DeberiaSanitizarDatosDeEntrada() {
               // Given
               Long productoId = 1L;

               Producto productoExistente = unProducto()
                         .conId(productoId)
                         .build();

               Producto datosConDatosSucios = unProducto()
                         .conNombre("<script>alert('hack')</script>")
                         .conDescripcion("  Descripción con espacios  ")
                         .conImagenUrl("<img src='x' onerror='alert(1)'>")
                         .conCantidad(1)
                         .build();

               when(productoRepository.findById(productoId)).thenReturn(Optional.of(productoExistente));
               when(productoRepository.save(any(Producto.class))).thenReturn(productoExistente);

               // When
               productoService.update(productoId, datosConDatosSucios);

               // Then
               verify(productoRepository).save(argThat(p -> !p.getNombre().contains("<script>") &&
                         !p.getDescripcion().startsWith(" ") &&
                         !p.getImagenUrl().contains("<img")));
          }
     }

     @Nested
     @DisplayName("Tests TDD para delete()")
     class TestsDelete {

          @Test
          @DisplayName("RED: delete() debería fallar cuando producto no existe")
          void delete_DeberiaFallar_CuandoProductoNoExiste() {
               // Given
               Long idInexistente = 999L;
               when(productoRepository.existsById(idInexistente)).thenReturn(false);

               // When & Then
               assertThatThrownBy(() -> productoService.delete(idInexistente))
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessageContaining("No existe producto " + idInexistente);

               verify(productoRepository, never()).deleteById(any());
          }

          @Test
          @DisplayName("GREEN: delete() debería eliminar producto existente")
          void delete_DeberiaEliminarProductoExistente() {
               // Given
               Long productoId = 1L;
               when(productoRepository.existsById(productoId)).thenReturn(true);

               // When
               productoService.delete(productoId);

               // Then
               verify(productoRepository).existsById(productoId);
               verify(productoRepository).deleteById(productoId);
          }
     }

     @Nested
     @DisplayName("Tests de Edge Cases y Validaciones Complejas")
     class TestsEdgeCasesYValidacionesComplejas {

          @Test
          @DisplayName("REFACTOR: Debería manejar productos con valores límite correctamente")
          void deberiaManejarProductosConValoresLimiteCorrectamente() {
               // Given
               Producto productoConValoresLimite = unProducto()
                         .conCantidad(Integer.MAX_VALUE)
                         .conPrecio(Double.MAX_VALUE)
                         .build();

               when(productoRepository.save(any(Producto.class))).thenReturn(productoConValoresLimite);

               // When
               Producto resultado = productoService.create(productoConValoresLimite);

               // Then
               assertThat(resultado.getCantidad()).isEqualTo(Integer.MAX_VALUE);
               assertThat(resultado.getPrecioVenta()).isEqualTo(Double.MAX_VALUE);
          }

          @Test
          @DisplayName("REFACTOR: Debería manejar múltiples operaciones en secuencia")
          void deberiaManejarMultiplesOperacionesEnSecuencia() {
               // Given
               Long productoId = 1L;
               Producto producto = unProducto()
                         .conId(productoId)
                         .conCantidad(100)
                         .build();

               when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));

               // When - Múltiples reducciones de stock
               productoService.reducirStock(productoId, 10);
               productoService.reducirStock(productoId, 20);
               productoService.reducirStock(productoId, 30);

               // Then
               verify(productoRepository, times(3)).save(any(Producto.class));
          }
     }
}
