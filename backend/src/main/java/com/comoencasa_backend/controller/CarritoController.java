package com.comoencasa_backend.controller;

import com.comoencasa_backend.dto.CarritoDTO;
import com.comoencasa_backend.service.CarritoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/carrito")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3002"})
public class CarritoController {

    private final CarritoService carritoService;

    @Autowired
    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    /**
     * Agregar producto al carrito
     */
    @PostMapping("/agregar")
    public ResponseEntity<CarritoDTO> agregarProducto(
            @RequestBody Map<String, Object> request,
            HttpSession session) {
        try {
            String sessionId = session.getId();
            Long productoId = Long.valueOf(request.get("productoId").toString());
            Integer cantidad = Integer.valueOf(request.get("cantidad").toString());
            String comentarios = request.get("comentarios") != null 
                ? request.get("comentarios").toString() 
                : "";

            log.info("Request agregar producto: sessionId={}, productoId={}, cantidad={}", 
                sessionId, productoId, cantidad);

            CarritoDTO carrito = carritoService.agregarProducto(sessionId, productoId, cantidad, comentarios);
            return ResponseEntity.ok(carrito);        } catch (IllegalArgumentException e) {
            log.error("Error de validación al agregar producto: {}", e.getMessage());
            // Crear respuesta con mensaje específico para errores de stock
            Map<String, String> errorResponse = Map.of(
                "error", e.getMessage(),
                "type", e.getMessage().contains("Stock insuficiente") ? "STOCK_ERROR" : "VALIDATION_ERROR"
            );
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            log.error("Error interno al agregar producto: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Actualizar cantidad de un producto
     */
    @PutMapping("/actualizar/{productoId}")
    public ResponseEntity<CarritoDTO> actualizarCantidad(
            @PathVariable Long productoId,
            @RequestBody Map<String, Integer> request,
            HttpSession session) {
        try {
            String sessionId = session.getId();
            Integer nuevaCantidad = request.get("cantidad");

            log.info("Actualizando cantidad: sessionId={}, productoId={}, cantidad={}", 
                sessionId, productoId, nuevaCantidad);

            CarritoDTO carrito = carritoService.actualizarCantidad(sessionId, productoId, nuevaCantidad);
            return ResponseEntity.ok(carrito);

        } catch (Exception e) {
            log.error("Error al actualizar cantidad: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Eliminar producto del carrito
     */
    @DeleteMapping("/eliminar/{productoId}")
    public ResponseEntity<CarritoDTO> eliminarProducto(
            @PathVariable Long productoId,
            HttpSession session) {
        try {
            String sessionId = session.getId();

            log.info("Eliminando producto: sessionId={}, productoId={}", sessionId, productoId);

            CarritoDTO carrito = carritoService.eliminarProducto(sessionId, productoId);
            return ResponseEntity.ok(carrito);

        } catch (Exception e) {
            log.error("Error al eliminar producto: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtener carrito actual
     */
    @GetMapping
    public ResponseEntity<CarritoDTO> obtenerCarrito(HttpSession session) {
        try {
            String sessionId = session.getId();
            CarritoDTO carrito = carritoService.obtenerCarrito(sessionId);
            return ResponseEntity.ok(carrito);

        } catch (Exception e) {
            log.error("Error al obtener carrito: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Limpiar carrito
     */
    @DeleteMapping("/limpiar")
    public ResponseEntity<CarritoDTO> limpiarCarrito(HttpSession session) {
        try {
            String sessionId = session.getId();
            CarritoDTO carrito = carritoService.limpiarCarrito(sessionId);
            return ResponseEntity.ok(carrito);

        } catch (Exception e) {
            log.error("Error al limpiar carrito: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtener total de items en el carrito
     */
    @GetMapping("/total-items")
    public ResponseEntity<Map<String, Integer>> obtenerTotalItems(HttpSession session) {
        try {
            String sessionId = session.getId();
            Integer totalItems = carritoService.obtenerTotalItems(sessionId);
            return ResponseEntity.ok(Map.of("totalItems", totalItems));

        } catch (Exception e) {
            log.error("Error al obtener total de items: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
