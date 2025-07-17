package com.comoencasa_backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetallePedidoDTO {
    private Long id;
    private Long productoId;
    private String nombreProducto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal costoUnitario;
    private String personalizacion;
    private BigDecimal subtotal; // cantidad * precioUnitario
}
