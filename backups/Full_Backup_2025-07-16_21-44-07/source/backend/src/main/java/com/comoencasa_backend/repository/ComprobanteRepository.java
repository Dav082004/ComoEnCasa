package com.comoencasa_backend.repository;

import com.comoencasa_backend.model.Comprobante;
import com.comoencasa_backend.model.TipoComprobante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ComprobanteRepository extends JpaRepository<Comprobante, Long> {
    long countByTipo(TipoComprobante tipo);
    List<Comprobante> findByPedido_Id(Long pedidoId);
    List<Comprobante> findByPedido_Usuario_NumeroDocumento(String numeroDocumento);
    List<Comprobante> findByFechaEmisionBetween(LocalDateTime desde, LocalDateTime hasta);
    
    @Query("SELECT c FROM Comprobante c " +
           "JOIN FETCH c.pedido p " +
           "JOIN FETCH p.usuario u " +
           "ORDER BY c.fechaEmision DESC")
    List<Comprobante> findAllWithPedidoAndUsuario();
}