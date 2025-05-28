package com.comoencasa_backend.service;

import com.comoencasa_backend.model.Producto;
import java.util.List;
import java.util.Optional;

public interface ProductoService {
    List<Producto> findAllAvailable();
    Optional<Producto> findById(Long id);
    List<Producto> findByCategoriaId(Long categoriaId);
}