package com.comoencasa_backend.controller;

import com.comoencasa_backend.dto.FacturaDTO;
import com.comoencasa_backend.model.TipoComprobante;
import com.comoencasa_backend.service.FacturaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/facturas")
@RequiredArgsConstructor
public class FacturaController {

    private final ThreadLocal<FacturaService> service = new ThreadLocal<FacturaService>();

    /** Generar y guardar factura */
    @PostMapping("/generate")
    public ResponseEntity<FacturaDTO> generate(
            @RequestParam Long pedidoId,
            @RequestParam TipoComprobante tipo) {
        FacturaDTO dto = service.get().generarFactura(pedidoId, tipo);
        return ResponseEntity.ok(dto);
    }

    /** Listar y filtrar facturas */
    @GetMapping
    public ResponseEntity<List<FacturaDTO>> list(
            @RequestParam Optional<String> clienteDocumento,
            @RequestParam Optional<Long> pedidoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> hasta) {

        Optional<LocalDateTime> f1 = desde.map(d -> d.atStartOfDay());
        Optional<LocalDateTime> f2 = hasta.map(d -> d.atTime(LocalTime.MAX));

        List<FacturaDTO> listado = service.get().listarFacturas(f1, f2, clienteDocumento, pedidoId);
        return ResponseEntity.ok(listado);
    }

    /** Exportar a EXCEL */
    @GetMapping("/{id}/export.xlsx")
    public ResponseEntity<byte[]> exportExcel(@PathVariable Long id) throws IOException {
        ByteArrayInputStream in = service.get().generarExcel(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(
                MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(
                ContentDisposition.builder("attachment")
                        .filename("factura_" + id + ".xlsx")
                        .build());
        return new ResponseEntity<>(in.readAllBytes(), headers, HttpStatus.OK);
    }

    /** Exportar a PDF */
    @GetMapping("/{id}/export.pdf")
    public ResponseEntity<byte[]> exportPdf(@PathVariable Long id) throws IOException {
        ByteArrayInputStream in = service.get().generarPdf(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.builder("attachment")
                        .filename("factura_" + id + ".pdf")
                        .build());
        return new ResponseEntity<>(in.readAllBytes(), headers, HttpStatus.OK);
    }
}
