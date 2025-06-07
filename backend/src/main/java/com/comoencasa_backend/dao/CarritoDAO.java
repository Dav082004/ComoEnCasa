package com.comoencasa_backend.dao;

import com.comoencasa_backend.dto.CarritoDTO;
import com.comoencasa_backend.dto.CarritoItemDTO;

import java.util.Optional;

/**
 * DAO interface para operaciones de carrito siguiendo el patrón DAO
 * Abstrae las operaciones de persistencia del carrito de compras
 */
public interface CarritoDAO {
    
    /**
     * Guardar o actualizar carrito en el storage
     */
    void guardarCarrito(String sessionId, CarritoDTO carrito);
    
    /**
     * Recuperar carrito por session ID
     */
    Optional<CarritoDTO> obtenerCarrito(String sessionId);
    
    /**
     * Eliminar carrito del storage
     */
    void eliminarCarrito(String sessionId);
    
    /**
     * Verificar si existe carrito para la session
     */
    boolean existeCarrito(String sessionId);
    
    /**
     * Limpiar todos los carritos expirados
     */
    void limpiarCarritosExpirados();
    
    /**
     * Obtener estadísticas de carritos activos
     */
    long contarCarritosActivos();
    
    /**
     * Agregar o actualizar item específico en carrito
     */
    void actualizarItem(String sessionId, CarritoItemDTO item);
    
    /**
     * Eliminar item específico del carrito
     */
    void eliminarItem(String sessionId, Long productoId);
}
