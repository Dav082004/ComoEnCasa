package com.comoencasa_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pago")
@Getter
@Setter
public class Pago {

     public enum MetodoPago {
          Yape("Yape"),
          Plin("Plin"),
          Tarjeta("Tarjeta"),
          Efectivo("Efectivo"),
          PayPal("PayPal");

          private final String displayName;

          MetodoPago(String displayName) {
               this.displayName = displayName;
          }

          public String getDisplayName() {
               return displayName;
          }
     }

     public enum EstadoPago {
          Pagado("Pagado"),
          Pendiente("Pendiente"),
          Rechazado("Rechazado");

          private final String displayName;

          EstadoPago(String displayName) {
               this.displayName = displayName;
          }

          public String getDisplayName() {
               return displayName;
          }
     }

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "pedido_id", nullable = false)
     private Pedido pedido;

     @Column(nullable = false)
     private LocalDateTime fecha;

     @Enumerated(EnumType.STRING)
     @Column(nullable = false)
     private MetodoPago metodo;

     @Enumerated(EnumType.STRING)
     @Column(nullable = true)
     private EstadoPago estado = EstadoPago.Pendiente;

     @Column(nullable = false, precision = 10, scale = 2)
     private BigDecimal monto;

     // Campos opcionales para PayPal
     @Column(name = "paypal_email")
     private String paypalEmail;

     @Column(name = "paypal_id")
     private String paypalId;

     @Column(name = "payer_id")
     private String payerId;
}
