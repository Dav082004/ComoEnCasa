package com.comoencasa_backend.controller;

import com.comoencasa_backend.dto.PedidoDTO;
import com.comoencasa_backend.service.PedidoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
}
