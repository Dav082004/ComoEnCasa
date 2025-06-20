package com.comoencasa_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "pago")
public class Pago {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

     @Column(name = "pedido_id", insertable = false, updatable = false)
     private Long pedidoId;

     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "pedido_id", nullable = false)
     private Pedido pedido;

     @Column(nullable = false)
     private LocalDateTime fecha;

     @Enumerated(EnumType.STRING)
     @Column(nullable = false)
     private MetodoPago metodo;

     @Enumerated(EnumType.STRING)
     @Column(nullable = false)
     private EstadoPago estado;

     @Column(nullable = false, precision = 10, scale = 2)
     private BigDecimal monto;

     @PrePersist
     public void prePersist() {
          if (this.fecha == null) {
               this.fecha = LocalDateTime.now();
          }
          if (this.estado == null) {
               this.estado = EstadoPago.PENDIENTE;
          }
     }

     public enum MetodoPago {
          YAPE("Yape"),
          PLIN("Plin"),
          TARJETA("Tarjeta"),
          EFECTIVO("Efectivo");

          private final String displayName;

          MetodoPago(String displayName) {
               this.displayName = displayName;
          }

          public String getDisplayName() {
               return displayName;
          }
     }

     public enum EstadoPago {
          PENDIENTE("Pendiente"),
          PAGADO("Pagado"),
          RECHAZADO("Rechazado");

          private final String displayName;

          EstadoPago(String displayName) {
               this.displayName = displayName;
          }

          public String getDisplayName() {
               return displayName;
          }
     }
}
