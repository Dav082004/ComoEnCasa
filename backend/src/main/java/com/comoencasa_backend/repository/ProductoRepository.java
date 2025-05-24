package com.comoencasa_backend.repository;

import com.comoencasa_backend.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByDisponibleTrue();
}