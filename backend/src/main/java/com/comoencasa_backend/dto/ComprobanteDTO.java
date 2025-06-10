package com.comoencasa_backend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.comoencasa_backend.model.TipoComprobante;

@Data
public class ComprobanteDTO {
    private Long id;
    private Long pedidoId;
    private TipoComprobante tipo;
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