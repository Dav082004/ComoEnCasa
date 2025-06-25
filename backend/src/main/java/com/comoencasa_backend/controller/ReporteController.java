package com.comoencasa_backend.controller;

import com.comoencasa_backend.service.ComprobanteService;
import com.comoencasa_backend.model.TipoComprobante;
import org.springframework.core.io.InputStreamResource;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ReporteController {

     private final ComprobanteService comprobanteService;

     @GetMapping("/ventas.xlsx")
     public ResponseEntity<InputStreamResource> generarReporteVentasExcel(
               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> desde,
               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> hasta) {

          try {
               System.out.println("🔍 Generando reporte de ventas Excel (TODOS)...");
               System.out.println("📅 Desde: " + desde.orElse(null));
               System.out.println("📅 Hasta: " + hasta.orElse(null));

               ByteArrayInputStream excelStream = comprobanteService.generarReporteVentasExcel(desde, hasta);
               InputStreamResource resource = new InputStreamResource(excelStream);

               String fileName = "reporte_ventas_" +
                         LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
                         ".xlsx";

               System.out.println("✅ Reporte generado exitosamente: " + fileName);

               return ResponseEntity.ok()
                         .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                         .contentType(MediaType
                                   .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                         .body(resource);

          } catch (Exception e) {
               System.err.println("❌ Error generando reporte Excel:");
               e.printStackTrace();
               return ResponseEntity.status(500)
                         .body(new InputStreamResource(
                                   new java.io.ByteArrayInputStream("Error interno del servidor".getBytes())));
          }
     }

     @GetMapping("/facturas.xlsx")
     public ResponseEntity<InputStreamResource> generarReporteFacturasExcel(
               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> desde,
               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> hasta) {

          try {
               System.out.println("🔍 Generando reporte de FACTURAS Excel...");
               System.out.println("📅 Desde: " + desde.orElse(null));
               System.out.println("📅 Hasta: " + hasta.orElse(null));
               ByteArrayInputStream excelStream = comprobanteService.generarReporteComprobantesExcel(
                         desde, hasta, TipoComprobante.Factura);
               InputStreamResource resource = new InputStreamResource(excelStream);

               String fileName = "reporte_facturas_" +
                         LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
                         ".xlsx";

               System.out.println("✅ Reporte de facturas generado exitosamente: " + fileName);

               return ResponseEntity.ok()
                         .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                         .contentType(MediaType
                                   .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                         .body(resource);

          } catch (Exception e) {
               System.err.println("❌ Error generando reporte de facturas:");
               e.printStackTrace();
               return ResponseEntity.status(500)
                         .body(new InputStreamResource(
                                   new java.io.ByteArrayInputStream("Error interno del servidor".getBytes())));
          }
     }

     @GetMapping("/boletas.xlsx")
     public ResponseEntity<InputStreamResource> generarReporteBoletasExcel(
               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> desde,
               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> hasta) {

          try {
               System.out.println("🔍 Generando reporte de BOLETAS Excel...");
               System.out.println("📅 Desde: " + desde.orElse(null));
               System.out.println("📅 Hasta: " + hasta.orElse(null));
               ByteArrayInputStream excelStream = comprobanteService.generarReporteComprobantesExcel(
                         desde, hasta, TipoComprobante.Boleta);
               InputStreamResource resource = new InputStreamResource(excelStream);

               String fileName = "reporte_boletas_" +
                         LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
                         ".xlsx";

               System.out.println("✅ Reporte de boletas generado exitosamente: " + fileName);

               return ResponseEntity.ok()
                         .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                         .contentType(MediaType
                                   .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                         .body(resource);

          } catch (Exception e) {
               System.err.println("❌ Error generando reporte de boletas:");
               e.printStackTrace();
               return ResponseEntity.status(500)
                         .body(new InputStreamResource(
                                   new java.io.ByteArrayInputStream("Error interno del servidor".getBytes())));
          }
     }

     @GetMapping("/test")
     public ResponseEntity<String> testEndpoint() {
          return ResponseEntity.ok("✅ Endpoint de reportes funcionando correctamente");
     }
}
