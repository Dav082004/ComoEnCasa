package com.comoencasa_backend.service;

import com.comoencasa_backend.model.Producto;
import java.util.List;
import java.util.Optional;

public interface ProductoService {
    List<Producto> findAllAvailable();
    List<Producto> findAll(); // Para el panel de administración
    Optional<Producto> findById(Long id);
    List<Producto> findByCategoriaId(Long categoriaId);
    
    /**
     * Actualizar stock del producto - Para uso administrativo
     */
    Producto actualizarStock(Long productoId, Integer nuevaCantidad);
    
    /**
     * Reducir stock después de una venta
     */
    void reducirStock(Long productoId, Integer cantidadVendida);
    
    /**
     * Cambiar disponibilidad del producto
     */
    Producto cambiarDisponibilidad(Long productoId, Boolean disponible);
}