package com.comoencasa_backend.service;

import com.comoencasa_backend.model.Producto;
import com.comoencasa_backend.repository.ProductoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DisplayName("ProductoService Integration Test (DataJpaTest)")
class ProductoServiceIntegrationTest {

    @Autowired
    private ProductoRepository productoRepository;

    @Test
    @DisplayName("Debería guardar y recuperar productos correctamente")
    void deberiaGuardarYRecuperarProductos() {
        Producto producto = new Producto();
        producto.setNombre("Test Producto");
        producto.setCategoriaId(1L);
        producto.setPrecioVenta(10.0);
        producto.setCostoProduccion(5.0);
        producto.setDisponible(true);
        productoRepository.save(producto);

        List<Producto> productos = productoRepository.findByDisponibleTrue();
        assertThat(productos).isNotEmpty();
        assertThat(productos.get(0).getNombre()).isEqualTo("Test Producto");
    }
}

