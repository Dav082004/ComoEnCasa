package com.comoencasa_backend.service;

import com.comoencasa_backend.dto.CheckoutDTO;
import com.comoencasa_backend.dto.CheckoutResponseDTO;
import org.springframework.transaction.annotation.Transactional;

public interface CheckoutService {

     /**
      * Procesa el checkout completo: crea el pedido, registra el pago y genera el
      * comprobante
      * 
      * @param checkoutDTO Datos del checkout
      * @return Respuesta con información del pedido, pago y comprobante
      */
     @Transactional
     CheckoutResponseDTO procesarCheckout(CheckoutDTO checkoutDTO);

     /**
      * Simula el proceso de pago según el método seleccionado
      * 
      * @param metodoPago Método de pago (Yape, Plin, Tarjeta)
      * @param monto      Monto a pagar
      * @return true si el pago fue exitoso
      */
     boolean procesarPago(String metodoPago, java.math.BigDecimal monto);
}
