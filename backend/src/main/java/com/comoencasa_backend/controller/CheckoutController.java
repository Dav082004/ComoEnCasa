package com.comoencasa_backend.controller;

import com.comoencasa_backend.dto.CheckoutDTO;
import com.comoencasa_backend.dto.CheckoutResponseDTO;
import com.comoencasa_backend.service.CheckoutService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class CheckoutController {

     private final CheckoutService checkoutService;

     public CheckoutController(CheckoutService checkoutService) {
          this.checkoutService = checkoutService;
     }

     /**
      * Procesa el checkout completo: pedido, pago y comprobante
      */
     @PostMapping("/procesar")
     public ResponseEntity<CheckoutResponseDTO> procesarCheckout(@RequestBody CheckoutDTO checkoutDTO) {
          log.info("Procesando checkout para usuario ID: {}", checkoutDTO.getUsuarioId());

          try {
               CheckoutResponseDTO response = checkoutService.procesarCheckout(checkoutDTO);

               if (response.isExitoso()) {
                    log.info("Checkout procesado exitosamente. Pedido ID: {}", response.getPedidoId());
                    return ResponseEntity.ok(response);
               } else {
                    log.warn("Error en checkout: {}", response.getMensaje());
                    return ResponseEntity.badRequest().body(response);
               }

          } catch (IllegalArgumentException e) {
               log.error("Error de validación en checkout: {}", e.getMessage());
               CheckoutResponseDTO errorResponse = new CheckoutResponseDTO();
               errorResponse.setExitoso(false);
               errorResponse.setMensaje("Error de validación: " + e.getMessage());
               return ResponseEntity.badRequest().body(errorResponse);

          } catch (Exception e) {
               log.error("Error interno en checkout", e);
               CheckoutResponseDTO errorResponse = new CheckoutResponseDTO();
               errorResponse.setExitoso(false);
               errorResponse.setMensaje("Error interno del servidor");
               return ResponseEntity.internalServerError().body(errorResponse);
          }
     }

     /**
      * Simula el procesamiento de pago para testing
      */
     @PostMapping("/simular-pago")
     public ResponseEntity<Boolean> simularPago(
               @RequestParam String metodoPago,
               @RequestParam java.math.BigDecimal monto) {
          log.info("Simulando pago: método={}, monto={}", metodoPago, monto);

          try {
               boolean exitoso = checkoutService.procesarPago(metodoPago, monto);
               return ResponseEntity.ok(exitoso);
          } catch (Exception e) {
               log.error("Error simulando pago", e);
               return ResponseEntity.badRequest().body(false);
          }
     }
}
