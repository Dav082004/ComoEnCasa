package com.comoencasa_backend.repository;

import com.comoencasa_backend.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

     /**
      * Encuentra pagos por ID de pedido
      */
     List<Pago> findByPedidoId(Long pedidoId);

     /**
      * Encuentra pagos por método de pago
      */
     List<Pago> findByMetodo(Pago.MetodoPago metodo);

     /**
      * Encuentra pagos por estado
      */
     List<Pago> findByEstado(Pago.EstadoPago estado);
}
