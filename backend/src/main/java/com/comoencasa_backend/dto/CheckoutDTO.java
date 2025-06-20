package com.comoencasa_backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CheckoutDTO {
     // Datos del pedido
     private Long usuarioId;
     private String direccionEntrega;
     private String distrito;
     private String referencia;
     private String notas;
     private Boolean necesitaFactura;
     private BigDecimal subtotal;
     private BigDecimal costoEnvio;
     private BigDecimal igv;
     private BigDecimal total;
     private LocalDateTime fechaEntrega;

     // Datos del pago
     private String metodoPago; // Yape, Plin, Tarjeta, Efectivo
     private BigDecimal montoPago;

     // Datos del comprobante
     private String tipoComprobante; // Boleta o Factura
     private String documento; // DNI o RUC

     // Items del carrito
     private List<CheckoutItemDTO> items;

     @Data
     public static class CheckoutItemDTO {
          private Long productoId;
          private String nombre;
          private Integer cantidad;
          private BigDecimal precioUnitario;
          private String personalizacion;
     }
}
