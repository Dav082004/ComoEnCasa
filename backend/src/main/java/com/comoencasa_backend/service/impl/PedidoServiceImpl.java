package com.comoencasa_backend.service.impl;

import com.comoencasa_backend.dto.PedidoDTO;
import com.comoencasa_backend.model.Pedido;
import com.comoencasa_backend.model.Usuario;
import com.comoencasa_backend.repository.PedidoRepository;
import com.comoencasa_backend.repository.UsuarioRepository;
import com.comoencasa_backend.service.PedidoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public PedidoServiceImpl(PedidoRepository pedidoRepository, UsuarioRepository usuarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoDTO> findAll() {
        log.info("ADMIN: obteniendo todos los pedidos");
        return pedidoRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoDTO> obtenerPedidosPorUsuario(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID de usuario no puede ser nulo");
        }
        if (usuarioId <= 0) {
            throw new IllegalArgumentException("El ID de usuario debe ser mayor a 0");
        }
        log.info("ADMIN: obteniendo pedidos del usuario ID={}", usuarioId);
        return pedidoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public PedidoDTO crearPedido(PedidoDTO pedidoDTO) {
        if (pedidoDTO == null) {
            throw new IllegalArgumentException("El pedido no puede ser nulo");
        }
        if (pedidoDTO.getUsuarioId() == null) {
            throw new IllegalArgumentException("El ID de usuario no puede ser nulo");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(pedidoDTO.getUsuarioId());
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado con ID=" + pedidoDTO.getUsuarioId());
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuarioOpt.get());
        pedido.setFechaCreacion(pedidoDTO.getFechaCreacion());
        pedido.setFechaEntrega(pedidoDTO.getFechaEntrega());
        pedido.setEstado(pedidoDTO.getEstado());
        pedido.setSubtotal(pedidoDTO.getSubtotal());
        pedido.setCostoTotal(pedidoDTO.getCostoTotal());
        pedido.setDireccionEntrega(pedidoDTO.getDireccionEntrega());
        pedido.setNotas(pedidoDTO.getNotas());
        pedido.setNecesitaFactura(pedidoDTO.getNecesitaFactura());

        Pedido guardado = pedidoRepository.save(pedido);
        log.info("Pedido creado con ID={}", guardado.getId());

        return toDTO(guardado);
    }

    @Transactional
    @Override
    public PedidoDTO actualizarEstadoPedido(Long pedidoId, String nuevoEstado) {
        // Validaciones RED
        if (pedidoId == null) {
            throw new IllegalArgumentException("El ID del pedido no puede ser nulo");
        }
        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado no puede ser nulo o vacío");
        }

        log.info("Actualizando estado de pedido ID={} a estado={}", pedidoId, nuevoEstado);

        // Buscar pedido
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);
        if (pedidoOpt.isEmpty()) {
            throw new IllegalArgumentException("Pedido no encontrado con ID=" + pedidoId);
        }

        Pedido pedido = pedidoOpt.get();
        String estadoActual = pedido.getEstado();

        // Validar transición
        if (!esTransicionValida(estadoActual, nuevoEstado)) {
            throw new IllegalArgumentException(
                    String.format("Transición no válida desde estado %s a %s", estadoActual, nuevoEstado));
        }

        // Validar que no es un retroceso que requiere confirmación especial
        if (requiereConfirmacionEspecial(estadoActual, nuevoEstado)) {
            throw new IllegalArgumentException("Transición de retroceso requiere confirmación especial");
        }

        // Actualizar estado
        pedido.setEstado(nuevoEstado);
        Pedido pedidoActualizado = pedidoRepository.save(pedido);

        log.info("Estado de pedido actualizado: ID={}, estado anterior={}, nuevo estado={}",
                pedidoId, estadoActual, nuevoEstado);

        return toDTO(pedidoActualizado);
    }

    @Transactional
    @Override
    public PedidoDTO actualizarEstadoPedidoForzado(Long pedidoId, String nuevoEstado, String password) {
        // Validaciones RED
        if (pedidoId == null) {
            throw new IllegalArgumentException("El ID del pedido no puede ser nulo");
        }
        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado no puede ser nulo o vacío");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede ser nula o vacía");
        }

        // Validar contraseña de administrador
        if (!"123".equals(password)) {
            throw new IllegalArgumentException("Contraseña de confirmación incorrecta");
        }

        log.info("Actualizando estado FORZADO de pedido ID={} a estado={}", pedidoId, nuevoEstado);

        // Buscar pedido
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);
        if (pedidoOpt.isEmpty()) {
            throw new IllegalArgumentException("Pedido no encontrado con ID=" + pedidoId);
        }

        Pedido pedido = pedidoOpt.get();
        String estadoActual = pedido.getEstado();

        // Para cambio forzado, permitir cualquier transición válida de estado
        if (!esEstadoValido(nuevoEstado)) {
            throw new IllegalArgumentException("Estado no válido: " + nuevoEstado);
        }

        // Actualizar estado
        pedido.setEstado(nuevoEstado);
        Pedido pedidoActualizado = pedidoRepository.save(pedido);

        log.warn("Estado de pedido actualizado FORZADAMENTE: ID={}, estado anterior={}, nuevo estado={}",
                pedidoId, estadoActual, nuevoEstado);

        return toDTO(pedidoActualizado);
    }

    @Override
    public boolean esEstadoValido(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            return false;
        }

        List<String> estadosValidos = Arrays.asList("Pendiente", "En preparación", "Entregado", "Cancelado");
        return estadosValidos.contains(estado);
    }

    @Override
    public List<String> getTransicionesDisponibles(String estadoActual) {
        if (estadoActual == null) {
            return Collections.emptyList();
        }

        Map<String, List<String>> transiciones = Map.of(
                "Pendiente", Arrays.asList("En preparación", "Cancelado"),
                "En preparación", Arrays.asList("Entregado", "Cancelado", "Pendiente"), // Incluye retroceso
                "Entregado", Collections.emptyList(), // Estado final
                "Cancelado", Collections.emptyList() // Estado final
        );

        return transiciones.getOrDefault(estadoActual, Collections.emptyList());
    }

    // Métodos auxiliares privados

    private boolean esTransicionValida(String estadoActual, String nuevoEstado) {
        // Validar que el nuevo estado es válido
        if (!esEstadoValido(nuevoEstado)) {
            return false;
        }

        // Si es el mismo estado, no hacer nada
        if (estadoActual.equals(nuevoEstado)) {
            return false;
        }

        // Cancelado se puede hacer desde cualquier estado
        if ("Cancelado".equals(nuevoEstado)) {
            return true;
        }

        // Estados finales no pueden cambiar (excepto con forzado)
        if ("Entregado".equals(estadoActual) || "Cancelado".equals(estadoActual)) {
            return false;
        }

        // Transiciones normales válidas
        List<String> transicionesPermitidas = getTransicionesDisponibles(estadoActual);
        return transicionesPermitidas.contains(nuevoEstado);
    }

    private boolean requiereConfirmacionEspecial(String estadoActual, String nuevoEstado) {
        // Estados finales que cambian a cualquier otro
        if (("Entregado".equals(estadoActual) || "Cancelado".equals(estadoActual)) &&
                !estadoActual.equals(nuevoEstado)) {
            return true;
        }

        // Retroceso: De "En preparación" a "Pendiente"
        if ("En preparación".equals(estadoActual) && "Pendiente".equals(nuevoEstado)) {
            return true;
        }

        return false;
    }

    private PedidoDTO toDTO(Pedido p) {
        PedidoDTO dto = new PedidoDTO();
        dto.setId(p.getId());
        dto.setUsuarioId(p.getUsuario().getId());
        dto.setUsuarioNombre(p.getUsuario().getNombre());
        dto.setFechaCreacion(p.getFechaCreacion());
        dto.setFechaEntrega(p.getFechaEntrega());
        dto.setEstado(p.getEstado());
        dto.setSubtotal(p.getSubtotal());
        dto.setCostoTotal(p.getCostoTotal());
        dto.setDireccionEntrega(p.getDireccionEntrega());
        dto.setNotas(p.getNotas());
        dto.setNecesitaFactura(p.getNecesitaFactura());
        return dto;
    }
}
