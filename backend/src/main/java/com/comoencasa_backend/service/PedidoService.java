package com.comoencasa_backend.service;

import com.comoencasa_backend.dto.PedidoDTO;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

public interface PedidoService {
    /**
     * Listar todos los pedidos (para el panel de administración)
     */
    List<PedidoDTO> findAll();

    /**
     * Obtener un pedido por su ID
     * 
     * @param pedidoId ID del pedido
     * @return Pedido encontrado
     * @throws IllegalArgumentException si el pedido no existe
     */
    PedidoDTO findById(Long pedidoId);

    /**
     * Obtener pedidos por ID de usuario
     * 
     * @param usuarioId ID del usuario
     * @return Lista de pedidos del usuario
     */
    List<PedidoDTO> obtenerPedidosPorUsuario(Long usuarioId);

    @Transactional
    PedidoDTO crearPedido(PedidoDTO pedidoDTO);

    /**
     * Actualizar estado de pedido con validaciones de flujo normal
     * 
     * @param pedidoId    ID del pedido
     * @param nuevoEstado Nuevo estado del pedido
     * @return Pedido actualizado
     * @throws IllegalArgumentException si la transición no es válida
     */
    @Transactional
    PedidoDTO actualizarEstadoPedido(Long pedidoId, String nuevoEstado);

    /**
     * Actualizar estado de pedido de forma forzada con confirmación especial
     * 
     * @param pedidoId    ID del pedido
     * @param nuevoEstado Nuevo estado del pedido
     * @param password    Contraseña de confirmación especial
     * @return Pedido actualizado
     * @throws IllegalArgumentException si la contraseña es incorrecta
     */
    @Transactional
    PedidoDTO actualizarEstadoPedidoForzado(Long pedidoId, String nuevoEstado, String password);

    /**
     * Validar si un estado es válido
     * 
     * @param estado Estado a validar
     * @return true si el estado es válido
     */
    boolean esEstadoValido(String estado);

    /**
     * Obtener transiciones disponibles desde un estado dado
     * 
     * @param estadoActual Estado actual del pedido
     * @return Lista de estados a los que se puede transicionar
     */
    List<String> getTransicionesDisponibles(String estadoActual);
    ByteArrayInputStream generarReporteVentasExcel(Optional<LocalDateTime> desde, Optional<LocalDateTime> hasta) throws IOException;

}