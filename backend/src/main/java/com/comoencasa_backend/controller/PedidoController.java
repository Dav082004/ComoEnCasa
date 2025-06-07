package com.comoencasa_backend.controller;

import com.comoencasa_backend.model.Pedido;
import com.comoencasa_backend.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "http://localhost:3000")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/usuario/{id}")
    public ResponseEntity<List<Pedido>> obtenerPedidosPorIdUsuario(@PathVariable Long id) {
        List<Pedido> pedidos = pedidoService.obtenerPedidosPorUsuario(id);
        return ResponseEntity.ok(pedidos);
    }

}
