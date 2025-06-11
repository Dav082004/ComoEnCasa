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

import java.util.List;
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
        Validate.notNull(usuarioId, "El ID de usuario no puede ser nulo");
        Validate.isTrue(usuarioId > 0, "El ID de usuario debe ser mayor a 0");
        log.info("ADMIN: obteniendo pedidos del usuario ID={}", usuarioId);
        return pedidoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public PedidoDTO crearPedido(PedidoDTO pedidoDTO) {
        Validate.notNull(pedidoDTO, "El pedido no puede ser nulo");
        Validate.notNull(pedidoDTO.getUsuarioId(), "El ID de usuario no puede ser nulo");

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
