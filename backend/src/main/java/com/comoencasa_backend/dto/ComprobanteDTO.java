package com.comoencasa_backend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ComprobanteDTO {
    private Long id;
    private Long pedidoId;
    private String tipo; // Cambiado de TipoComprobante a String
    private LocalDateTime fechaEmision;
    private String numeroSerie;
    private String numeroComprobante;
    private BigDecimal subtotal;
    private BigDecimal total;
    // Datos del cliente
    private String clienteNombre;      
    private String clienteDocumento;   
    private String clienteEmail;  
}