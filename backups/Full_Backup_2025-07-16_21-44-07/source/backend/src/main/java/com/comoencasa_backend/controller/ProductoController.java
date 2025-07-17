package com.comoencasa_backend.controller;

import com.comoencasa_backend.model.Producto;
import com.comoencasa_backend.service.ProductoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<Producto>> getAllProductos() {
        return ResponseEntity.ok(productoService.findAllAvailable());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoById(@PathVariable Long id) {
        return productoService.findById(id)
                .map(producto -> {
                    // Si el stock es 0, marcar como no disponible
                    if (producto.getCantidad() != null && producto.getCantidad() <= 0) {
                        producto.setDisponible(false);
                        // Aquí podrías actualizar en la base de datos si es necesario
                    }
                    return ResponseEntity.ok(producto);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<Producto>> getProductosByCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(productoService.findByCategoriaId(categoriaId));
    }

    // ENDPOINTS DE ADMINISTRACIÓN - Para uso futuro del panel de admin

    /**
     * Obtener todos los productos (incluyendo no disponibles) - Solo para
     * administradores
     */
    @GetMapping("/admin/all")
    public ResponseEntity<List<Producto>> getAllProductosAdmin() {
        return ResponseEntity.ok(productoService.findAll());
    }

    /**
     * Actualizar stock de un producto - Solo para administradores
     */
    @PutMapping("/{id}/stock")
    public ResponseEntity<Producto> actualizarStock(
            @PathVariable Long id,
            @RequestBody StockUpdateRequest request) {
        try {
            Producto productoActualizado = productoService.actualizarStock(id, request.getCantidad());
            return ResponseEntity.ok(productoActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Cambiar disponibilidad de un producto - Solo para administradores
     */
    @PutMapping("/{id}/disponibilidad")
    public ResponseEntity<Producto> cambiarDisponibilidad(
            @PathVariable Long id,
            @RequestBody DisponibilidadUpdateRequest request) {
        try {
            Producto productoActualizado = productoService.cambiarDisponibilidad(id, request.getDisponible());
            return ResponseEntity.ok(productoActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** Crear producto */
    @PostMapping
    public ResponseEntity<Producto> create(@RequestBody Producto producto) {
        log.info("ADMIN accedió a POST /api/productos");
        Producto created = productoService.create(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** Actualizar producto completo */
    @PutMapping("/{id}")
    public ResponseEntity<Producto> update(
            @PathVariable Long id,
            @RequestBody Producto producto) {
        log.info("ADMIN accedió a PUT /api/productos/{}", id);
        try {
            Producto updated = productoService.update(id, producto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** Eliminar producto */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("ADMIN accedió a DELETE /api/productos/{}", id);
        try {
            productoService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Clases DTO para las peticiones
    public static class StockUpdateRequest {
        private Integer cantidad;

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }
    }

    public static class DisponibilidadUpdateRequest {
        private Boolean disponible;

        public Boolean getDisponible() {
            return disponible;
        }

        public void setDisponible(Boolean disponible) {
            this.disponible = disponible;
        }
    }
}