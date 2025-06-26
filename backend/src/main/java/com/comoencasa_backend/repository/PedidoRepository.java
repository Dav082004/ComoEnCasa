package com.comoencasa_backend.repository;

import com.comoencasa_backend.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Encuentra pedidos por el ID del usuario (relación @ManyToOne)
    List<Pedido> findByUsuarioId(Long usuarioId);

    // Consulta para cargar todos los pedidos con usuario y detalles (sin pagos)
    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.usuario LEFT JOIN FETCH p.detallePedidos")
    List<Pedido> findAllWithDetails();

    // Consulta para cargar un pedido específico con sus pagos y usuario
    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.pagos LEFT JOIN FETCH p.usuario WHERE p.id = :id")
    Optional<Pedido> findByIdWithPayments(Long id);

    // Consulta para cargar pedidos de un usuario con detalles
    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.usuario LEFT JOIN FETCH p.detallePedidos WHERE p.usuarioId = :usuarioId")
    List<Pedido> findByUsuarioIdWithDetails(Long usuarioId);
}
