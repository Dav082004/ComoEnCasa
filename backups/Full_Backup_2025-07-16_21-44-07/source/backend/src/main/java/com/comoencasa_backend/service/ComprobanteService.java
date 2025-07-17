package com.comoencasa_backend.service;

import com.comoencasa_backend.dto.ComprobanteDTO;
import com.comoencasa_backend.model.TipoComprobante;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ComprobanteService {
    /** Genera el comprobante y lo guarda en BD */
    ComprobanteDTO generarComprobante(Long pedidoId, TipoComprobante tipo);

    /** Lista y filtra comprobantes por opcionales */
    List<ComprobanteDTO> listarComprobantes(
        Optional<LocalDateTime> desde,
        Optional<LocalDateTime> hasta,
        Optional<String> clienteDni,
        Optional<Long> pedidoId
    );

    /** Genera el archivo Excel del comprobante */
    ByteArrayInputStream generarExcel(Long comprobanteId) throws IOException;

    /** Genera el archivo PDF del comprobante */
    ByteArrayInputStream generarPdf(Long comprobanteId) throws IOException;

    /** Genera reporte Excel de todas las ventas */
    ByteArrayInputStream generarReporteVentasExcel(Optional<LocalDateTime> desde, Optional<LocalDateTime> hasta) throws IOException;
    
    /** Genera reporte Excel filtrado por tipo de comprobante */
    ByteArrayInputStream generarReporteComprobantesExcel(Optional<LocalDateTime> desde, Optional<LocalDateTime> hasta, TipoComprobante tipo) throws IOException;
}
