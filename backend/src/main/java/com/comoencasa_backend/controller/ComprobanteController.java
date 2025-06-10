package com.comoencasa_backend.controller;

import com.comoencasa_backend.dto.ComprobanteDTO;
import com.comoencasa_backend.model.TipoComprobante;
import com.comoencasa_backend.service.ComprobanteService;
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
@RequestMapping("/api/admin/comprobantes")
@RequiredArgsConstructor

public class ComprobanteController {

    private final ComprobanteService service;

    /** Generar y guardar comprobante */
    @PostMapping("/generate")
    public ResponseEntity<ComprobanteDTO> generate(
            @RequestParam Long pedidoId,
            @RequestParam TipoComprobante tipo) {
        ComprobanteDTO dto = service.generarComprobante(pedidoId, tipo);
        return ResponseEntity.ok(dto);
    }

    /** Listar y filtrar comprobantes */
    @GetMapping
    public ResponseEntity<List<ComprobanteDTO>> list(
            @RequestParam Optional<String> clienteDocumento,
            @RequestParam Optional<Long> pedidoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> hasta) {
        // Convertimos la LocalDate al inicio y fin de día como LocalDateTime
        Optional<LocalDateTime> f1 = desde.map(d -> d.atStartOfDay());
        Optional<LocalDateTime> f2 = hasta.map(d -> d.atTime(LocalTime.MAX));

        List<ComprobanteDTO> listado = service.listarComprobantes(
                f1, f2, clienteDocumento, pedidoId);
        return ResponseEntity.ok(listado);
    }

    /** Exportar a EXCEL */
    @GetMapping("/{id}/export.xlsx")
    public ResponseEntity<byte[]> exportExcel(@PathVariable Long id) throws IOException {
        ByteArrayInputStream in = service.generarExcel(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(
                MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(
                ContentDisposition.builder("attachment")
                        .filename("comprobante_" + id + ".xlsx")
                        .build());
        return new ResponseEntity<>(in.readAllBytes(), headers, HttpStatus.OK);
    }

    /** Exportar a PDF */
    @GetMapping("/{id}/export.pdf")
    public ResponseEntity<byte[]> exportPdf(@PathVariable Long id) throws IOException {
        ByteArrayInputStream in = service.generarPdf(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.builder("attachment")
                        .filename("comprobante_" + id + ".pdf")
                        .build());
        return new ResponseEntity<>(in.readAllBytes(), headers, HttpStatus.OK);
    }
}
