package com.comoencasa_backend.repository;

import com.comoencasa_backend.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByDisponibleTrue();

    List<Producto> findByCategoriaIdAndDisponibleTrue(Long categoriaId);

    /**
     * Cuenta los productos disponibles por categoría
     * 
     * @param categoriaId ID de la categoría
     * @return número de productos disponibles en la categoría
     */
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.categoriaId = :categoriaId AND p.disponible = true")
    Long countByCategoriaIdAndDisponibleTrue(@Param("categoriaId") Long categoriaId);
}