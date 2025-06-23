package com.comoencasa_backend.service;

import com.comoencasa_backend.dto.FacturaDTO;
import com.comoencasa_backend.model.TipoComprobante;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FacturaService {

    /** Genera la factura y la guarda en BD */
    FacturaDTO generarFactura(Long pedidoId, TipoComprobante tipo);

    /** Lista y filtra facturas */
    List<FacturaDTO> listarFacturas(
            Optional<LocalDateTime> desde,
            Optional<LocalDateTime> hasta,
            Optional<String> clienteDni,
            Optional<Long> pedidoId
    );

    /** Genera el archivo Excel de la factura */
    ByteArrayInputStream generarExcel(Long facturaId) throws IOException;

    /** Genera el archivo PDF de la factura */
    ByteArrayInputStream generarPdf(Long facturaId) throws IOException;
}
