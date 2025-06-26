package com.comoencasa_backend.controller;

import com.comoencasa_backend.dto.PedidoDTO;
import com.comoencasa_backend.service.PedidoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class PedidoController {

    private final PedidoService pedidoService;

    @Autowired
    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    /** Listar todos los pedidos */
    @GetMapping
    public ResponseEntity<List<PedidoDTO>> getAllPedidos() {
        log.info("ADMIN accedió a GET /api/pedidos");
        return ResponseEntity.ok(pedidoService.findAll());
    }

    /** Obtener un pedido por ID */
    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> getPedidoById(@PathVariable Long id) {
        log.info("ADMIN accedió a GET /api/pedidos/{}", id);
        try {
            PedidoDTO pedido = pedidoService.findById(id);
            return ResponseEntity.ok(pedido);
        } catch (IllegalArgumentException ex) {
            log.warn("Pedido no encontrado con ID {}: {}", id, ex.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /** Listar pedidos de un usuario */
    @GetMapping("/usuario/{id}")
    public ResponseEntity<List<PedidoDTO>> getPedidosPorUsuario(@PathVariable Long id) {
        log.info("ADMIN accedió a GET /api/pedidos/usuario/{}", id);
        try {
            return ResponseEntity.ok(pedidoService.obtenerPedidosPorUsuario(id));
        } catch (IllegalArgumentException ex) {
            log.warn("Validación fallida para usuario {}: {}", id, ex.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /** Actualizar estado de pedido */
    @PutMapping("/{id}/estado")
    public ResponseEntity<PedidoDTO> actualizarEstadoPedido(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        log.info("ADMIN accedió a PUT /api/pedidos/{}/estado", id);
        try {
            String nuevoEstado = request.get("estado");
            PedidoDTO pedidoActualizado = pedidoService.actualizarEstadoPedido(id, nuevoEstado);
            return ResponseEntity.ok(pedidoActualizado);
        } catch (IllegalArgumentException ex) {
            log.warn("Error al actualizar estado del pedido {}: {}", id, ex.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /** Actualizar estado de pedido de forma forzada */
    @PutMapping("/{id}/estado/forzado")
    public ResponseEntity<PedidoDTO> actualizarEstadoPedidoForzado(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        log.info("ADMIN accedió a PUT /api/pedidos/{}/estado/forzado", id);
        try {
            String nuevoEstado = request.get("estado");
            String password = request.get("password");
            PedidoDTO pedidoActualizado = pedidoService.actualizarEstadoPedidoForzado(id, nuevoEstado, password);
            return ResponseEntity.ok(pedidoActualizado);
        } catch (IllegalArgumentException ex) {
            log.warn("Error al actualizar estado forzado del pedido {}: {}", id, ex.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /** Obtener estados disponibles */
    @GetMapping("/estados")
    public ResponseEntity<List<String>> getEstadosDisponibles() {
        log.info("ADMIN accedió a GET /api/pedidos/estados");
        List<String> estados = Arrays.asList("Pendiente", "En preparación", "Entregado", "Cancelado");
        return ResponseEntity.ok(estados);
    }

    /** Crear un nuevo pedido */
    @PostMapping
    public ResponseEntity<PedidoDTO> crearPedido(@RequestBody PedidoDTO pedidoDTO) {
        log.info("Creando nuevo pedido para usuario ID: {}", pedidoDTO.getUsuarioId());
        try {
            PedidoDTO nuevoPedido = pedidoService.crearPedido(pedidoDTO);
            return ResponseEntity.ok(nuevoPedido);
        } catch (IllegalArgumentException ex) {
            log.warn("Error al crear pedido: {}", ex.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /** Obtener transiciones disponibles para un estado */
    @GetMapping("/transiciones/{estado}")
    public ResponseEntity<List<String>> getTransicionesDisponibles(@PathVariable String estado) {
        log.info("ADMIN accedió a GET /api/pedidos/transiciones/{}", estado);
        List<String> transiciones = pedidoService.getTransicionesDisponibles(estado);
        return ResponseEntity.ok(transiciones);
    }

    @GetMapping("/ventas/export.xlsx")
    public ResponseEntity<byte[]> exportarReporteVentas(
            @RequestParam Optional<LocalDateTime> desde,
            @RequestParam Optional<LocalDateTime> hasta) throws IOException {
        ByteArrayInputStream in = pedidoService.generarReporteVentasExcel(desde, hasta);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.builder("attachment").filename("reporte_ventas.xlsx").build());
        return new ResponseEntity<>(in.readAllBytes(), headers, HttpStatus.OK);
    }

    /** Eliminar un pedido */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarPedido(@PathVariable Long id) {
        log.info("ADMIN accedió a DELETE /api/pedidos/{}", id);
        try {
            pedidoService.eliminarPedido(id);
            Map<String, String> response = Map.of("mensaje", "Pedido eliminado exitosamente");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            log.warn("Error al eliminar pedido {}: {}", id, ex.getMessage());
            Map<String, String> error = Map.of("error", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception ex) {
            log.error("Error interno al eliminar pedido {}: {}", id, ex.getMessage());
            Map<String, String> error = Map.of("error", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

}
