package com.comoencasa_backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CheckoutRequestDTO {
    private Long usuarioId;
    private String direccionEntrega;
    private String distrito;
    private String referencia;
    private String notas;
    private boolean necesitaFactura;
    private BigDecimal subtotal;
    private BigDecimal costoEnvio;
    private BigDecimal igv;
    private BigDecimal total;
    private String fechaEntrega;

    private String metodoPago;
    private BigDecimal montoPago;

    private String paypalId;
    private String paypalEmail;
    private String payerId;

    private String tipoComprobante;
    private String documento;

    private List<CheckoutDTO.CheckoutItemDTO> items;
}
