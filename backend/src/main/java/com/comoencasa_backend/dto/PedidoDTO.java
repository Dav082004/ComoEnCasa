package com.comoencasa_backend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PedidoDTO {
    private Long id;
    private Long usuarioId;
    private String usuarioNombre;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEntrega;
    private String estado;
    private BigDecimal subtotal;
    private BigDecimal costoTotal;
    private String direccionEntrega;
    private Boolean necesitaFactura;
    private List<DetallePedidoDTO> detalles;

    // Información de pago
    private String metodoPago;
    private String estadoPago;
}
