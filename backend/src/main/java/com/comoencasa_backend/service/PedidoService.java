package com.comoencasa_backend.service;

import com.comoencasa_backend.dto.PedidoDTO;
import java.util.List;


public interface PedidoService {
    /**
     * Listar todos los pedidos (para el panel de administración)
     */
    List<PedidoDTO> findAll();
    /**
     * Obtener pedidos por ID de usuario
     * @param usuarioId ID del usuario
     * @return Lista de pedidos del usuario
     */
    List<PedidoDTO> obtenerPedidosPorUsuario(Long usuarioId);
}