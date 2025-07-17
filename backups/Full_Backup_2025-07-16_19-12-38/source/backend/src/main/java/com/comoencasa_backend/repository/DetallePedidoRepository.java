package com.comoencasa_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.comoencasa_backend.model.DetallePedido;
import java.util.List;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
     /** Para obtener todas las líneas de un pedido concreto */
    List<DetallePedido> findByPedido_Id(Long pedidoId);
    
}
