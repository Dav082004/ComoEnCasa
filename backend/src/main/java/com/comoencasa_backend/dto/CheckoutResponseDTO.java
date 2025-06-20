package com.comoencasa_backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CheckoutResponseDTO {
     private Long pedidoId;
     private String numeroPedido;
     private String estado;
     private BigDecimal total;
     private LocalDateTime fechaCreacion;
     private LocalDateTime fechaEntrega;

     // Datos del pago
     private Long pagoId;
     private String metodoPago;
     private String estadoPago;

     // Datos del comprobante
     private Long comprobanteId;
     private String tipoComprobante;
     private String numeroSerie;
     private String numeroComprobante;
     private String urlComprobante; // Para descargar el PDF

     private String mensaje;
     private boolean exitoso;
}
