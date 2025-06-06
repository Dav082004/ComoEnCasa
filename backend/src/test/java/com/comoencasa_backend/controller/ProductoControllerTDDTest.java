package com.comoencasa_backend.controller;

import com.comoencasa_backend.model.Producto;
import com.comoencasa_backend.repository.ProductoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("ProductoController TDD Integration Test")
class ProductoControllerTDDTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductoRepository productoRepository;

    @Test
    @DisplayName("Debería listar todos los productos por endpoint REST")
    void deberiaListarProductosDisponibles() throws Exception {
        // Crear un producto de prueba
        Producto producto = new Producto();
        producto.setNombre("Producto REST");
        producto.setDescripcion("Descripción de prueba");
        producto.setPrecioVenta(15.99);
        producto.setCostoProduccion(10.00);
        producto.setCategoriaId(1L);
        producto.setDisponible(true);
        productoRepository.save(producto);

        mockMvc.perform(get("/api/productos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Producto REST")));
    }

    @Test
    @DisplayName("Debería retornar lista vacía si no hay productos")
    void deberiaRetornar404SiNoHayProductosDisponibles() throws Exception {
        // Limpiar la base de datos
        productoRepository.deleteAll();
        
        mockMvc.perform(get("/api/productos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    @DisplayName("Debería retornar error 400 si el parámetro de categoría es inválido")
    void deberiaRetornar400SiParametroInvalido() throws Exception {
        mockMvc.perform(get("/api/productos/categoria/abc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debería retornar producto por ID si existe")
    void deberiaRetornarProductoPorIdSiExiste() throws Exception {
        // Crear un producto de prueba
        Producto producto = new Producto();
        producto.setNombre("Producto por ID");
        producto.setDescripcion("Descripción de prueba");
        producto.setPrecioVenta(25.99);
        producto.setCostoProduccion(15.00);
        producto.setCategoriaId(1L);
        producto.setDisponible(true);
        Producto savedProducto = productoRepository.save(producto);
        
        mockMvc.perform(get("/api/productos/" + savedProducto.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Producto por ID")));
    }

    @Test
    @DisplayName("Debería retornar 404 si el producto por ID no existe")
    void deberiaRetornar404SiProductoPorIdNoExiste() throws Exception {
        mockMvc.perform(get("/api/productos/99999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
