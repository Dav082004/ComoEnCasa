package com.comoencasa_backend.controller;

import com.comoencasa_backend.model.Producto;
import com.comoencasa_backend.repository.ProductoRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "http://localhost:3000") // Solo para este controlador
public class ProductoController {
    private final ProductoRepository productoRepository;

    public ProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @GetMapping
    public List<Producto> obtenerTodosLosProductos() {
        return productoRepository.findByDisponibleTrue();
    }
}