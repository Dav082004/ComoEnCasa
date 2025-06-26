package com.comoencasa_backend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FacturaDTO {
    private Long id;
    private Long pedidoId;
    private LocalDateTime fechaEmision;
    private String numeroSerie;
    private String numeroFactura;
    private BigDecimal subtotal;
    private BigDecimal total;

    // Datos del cliente
    private String clienteNombre;
    private String clienteDocumento;
    private String clienteEmail;
}
