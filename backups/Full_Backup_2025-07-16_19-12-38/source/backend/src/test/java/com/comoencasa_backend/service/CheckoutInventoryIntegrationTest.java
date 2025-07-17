package com.comoencasa_backend.service;

import com.comoencasa_backend.dto.CheckoutDTO;
import com.comoencasa_backend.dto.CheckoutResponseDTO;
import com.comoencasa_backend.model.Producto;
import com.comoencasa_backend.model.Usuario;
import com.comoencasa_backend.repository.ProductoRepository;
import com.comoencasa_backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CheckoutInventoryIntegrationTest {

     @Autowired
     private CheckoutService checkoutService;

     @Autowired
     private ProductoRepository productoRepository;

     @Autowired
     private UsuarioRepository usuarioRepository;

     private Usuario usuarioTest;
     private Producto productoTest;

     @BeforeEach
     void setUp() { // Crear usuario de prueba
          usuarioTest = new Usuario();
          usuarioTest.setNombre("Usuario");
          usuarioTest.setApellido("Test");
          usuarioTest.setEmail("test@example.com");
          usuarioTest.setTelefono("123456789");
          usuarioTest.setPassword("password");
          usuarioTest = usuarioRepository.save(usuarioTest);

          // Crear producto de prueba con stock limitado
          productoTest = new Producto();
          productoTest.setCategoriaId(1L);
          productoTest.setNombre("Torta Test");
          productoTest.setDescripcion("Torta de prueba para test de inventario");
          productoTest.setPrecioVenta(50.0);
          productoTest.setCostoProduccion(30.0);
          productoTest.setDisponible(true);
          productoTest.setCantidad(5); // Stock inicial de 5 unidades
          productoTest = productoRepository.save(productoTest);
     }

     @Test
     void testReduccionStockExitosa() {
          // Crear checkout para comprar 2 unidades
          CheckoutDTO checkoutDTO = crearCheckoutDTO(2, BigDecimal.valueOf(100.0));

          // Verificar stock inicial
          assertEquals(5, productoTest.getCantidad());
          assertTrue(productoTest.getDisponible());

          // Procesar checkout
          CheckoutResponseDTO response = checkoutService.procesarCheckout(checkoutDTO);

          // Verificar que el checkout fue exitoso
          assertTrue(response.isExitoso());

          // Verificar que el stock se redujo correctamente
          Optional<Producto> productoActualizado = productoRepository.findById(productoTest.getId());
          assertTrue(productoActualizado.isPresent());
          assertEquals(3, productoActualizado.get().getCantidad()); // 5 - 2 = 3
          assertTrue(productoActualizado.get().getDisponible()); // Sigue disponible
     }

     @Test
     void testAgotamientoStock() {
          // Crear checkout para comprar todas las 5 unidades
          CheckoutDTO checkoutDTO = crearCheckoutDTO(5, BigDecimal.valueOf(250.0));

          // Procesar checkout
          CheckoutResponseDTO response = checkoutService.procesarCheckout(checkoutDTO);

          // Verificar que el checkout fue exitoso
          assertTrue(response.isExitoso()); // Verificar que el stock se agotó pero el producto sigue disponible para
                                            // reabastecimiento
          Optional<Producto> productoActualizado = productoRepository.findById(productoTest.getId());
          assertTrue(productoActualizado.isPresent());
          assertEquals(0, productoActualizado.get().getCantidad());
          assertTrue(productoActualizado.get().getDisponible()); // ✅ CAMBIO: El producto debe seguir disponible
     }

     @Test
     void testStockInsuficiente() {
          // Intentar comprar más unidades de las disponibles
          CheckoutDTO checkoutDTO = crearCheckoutDTO(10, BigDecimal.valueOf(500.0));

          // Procesar checkout y verificar que no fue exitoso
          CheckoutResponseDTO response = checkoutService.procesarCheckout(checkoutDTO);

          // Verificar que el checkout falló
          assertFalse(response.isExitoso());
          assertTrue(response.getMensaje().contains("Stock insuficiente"));

          // Verificar que el stock no cambió
          Optional<Producto> productoSinCambios = productoRepository.findById(productoTest.getId());
          assertTrue(productoSinCambios.isPresent());
          assertEquals(5, productoSinCambios.get().getCantidad()); // Stock sin modificar
     }

     @Test
     void testComprasMultiples() {
          // Primera compra: 2 unidades
          CheckoutDTO checkout1 = crearCheckoutDTO(2, BigDecimal.valueOf(100.0));
          CheckoutResponseDTO response1 = checkoutService.procesarCheckout(checkout1);
          assertTrue(response1.isExitoso());

          // Verificar stock después de primera compra
          Optional<Producto> productoAfterFirstPurchase = productoRepository.findById(productoTest.getId());
          assertEquals(3, productoAfterFirstPurchase.get().getCantidad());

          // Segunda compra: 3 unidades (debería agotar el stock)
          CheckoutDTO checkout2 = crearCheckoutDTO(3, BigDecimal.valueOf(150.0));
          CheckoutResponseDTO response2 = checkoutService.procesarCheckout(checkout2);
          assertTrue(response2.isExitoso()); // Verificar que el stock se agotó pero el producto sigue disponible para
                                             // reabastecimiento
          Optional<Producto> productoAfterSecondPurchase = productoRepository.findById(productoTest.getId());
          assertEquals(0, productoAfterSecondPurchase.get().getCantidad());
          assertTrue(productoAfterSecondPurchase.get().getDisponible()); // ✅ CAMBIO: El producto debe seguir disponible

          // Tercera compra: debería fallar por stock insuficiente
          CheckoutDTO checkout3 = crearCheckoutDTO(1, BigDecimal.valueOf(50.0));
          CheckoutResponseDTO response3 = checkoutService.procesarCheckout(checkout3);
          // Verificar que el checkout falló por stock insuficiente (no por
          // disponibilidad)
          assertFalse(response3.isExitoso());
          assertTrue(response3.getMensaje().contains("Stock insuficiente")); // ✅ Solo validar por stock, no por
                                                                             // disponibilidad
     }

     private CheckoutDTO crearCheckoutDTO(int cantidad, BigDecimal total) {
          CheckoutDTO checkoutDTO = new CheckoutDTO();
          checkoutDTO.setUsuarioId(usuarioTest.getId());
          checkoutDTO.setDireccionEntrega("Dirección de prueba 123");
          checkoutDTO.setDistrito("Lima");
          checkoutDTO.setReferencia("Casa azul");
          checkoutDTO.setFechaEntrega(LocalDateTime.now().plusDays(2));
          checkoutDTO.setMetodoPago("efectivo"); // Usar efectivo para garantizar pago exitoso
          checkoutDTO.setTipoComprobante("boleta");
          checkoutDTO.setDocumento("12345678");
          checkoutDTO.setSubtotal(total);
          checkoutDTO.setTotal(total);

          // Crear item del checkout
          CheckoutDTO.CheckoutItemDTO item = new CheckoutDTO.CheckoutItemDTO();
          item.setProductoId(productoTest.getId());
          item.setCantidad(cantidad);
          item.setPrecioUnitario(BigDecimal.valueOf(productoTest.getPrecioVenta()));
          item.setPersonalizacion("Personalización de prueba");

          checkoutDTO.setItems(Arrays.asList(item));

          return checkoutDTO;
     }
}
