package com.comoencasa_backend.service.impl;

import com.comoencasa_backend.model.Producto;
import com.comoencasa_backend.repository.ProductoRepository;
import com.comoencasa_backend.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public List<Producto> findAllAvailable() {
        return productoRepository.findByDisponibleTrue();
    }

    @Override
    public Optional<Producto> findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo");
        }
        return productoRepository.findById(id);
    }

    @Override
    public List<Producto> findByCategoriaId(Long categoriaId) {
        if (categoriaId == null) {
            throw new IllegalArgumentException("El ID de la categoría no puede ser nulo");
        }
        return productoRepository.findByCategoriaIdAndDisponibleTrue(categoriaId);
    }
}