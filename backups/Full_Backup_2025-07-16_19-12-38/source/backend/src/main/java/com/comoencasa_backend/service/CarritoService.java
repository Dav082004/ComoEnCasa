package com.comoencasa_backend.service;

import com.comoencasa_backend.dto.CarritoDTO;
import com.comoencasa_backend.dto.CarritoItemDTO;

public interface CarritoService {
    
    /**
     * Agregar producto al carrito
     * @param sessionId ID de sesión del cliente
     * @param productoId ID del producto
     * @param cantidad Cantidad a agregar
     * @param comentarios Comentarios especiales
     * @return Carrito actualizado
     */
    CarritoDTO agregarProducto(String sessionId, Long productoId, Integer cantidad, String comentarios);

    /**
     * Actualizar cantidad de un producto en el carrito
     * @param sessionId ID de sesión del cliente
     * @param productoId ID del producto
     * @param nuevaCantidad Nueva cantidad
     * @return Carrito actualizado
     */
    CarritoDTO actualizarCantidad(String sessionId, Long productoId, Integer nuevaCantidad);

    /**
     * Eliminar producto del carrito
     * @param sessionId ID de sesión del cliente
     * @param productoId ID del producto a eliminar
     * @return Carrito actualizado
     */
    CarritoDTO eliminarProducto(String sessionId, Long productoId);

    /**
     * Obtener carrito por sesión
     * @param sessionId ID de sesión del cliente
     * @return Carrito del cliente
     */
    CarritoDTO obtenerCarrito(String sessionId);

    /**
     * Limpiar carrito
     * @param sessionId ID de sesión del cliente
     * @return Carrito vacío
     */
    CarritoDTO limpiarCarrito(String sessionId);

    /**
     * Obtener total de items en el carrito
     * @param sessionId ID de sesión del cliente
     * @return Número total de items
     */
    Integer obtenerTotalItems(String sessionId);
}
