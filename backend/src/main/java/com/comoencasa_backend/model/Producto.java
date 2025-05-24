package com.comoencasa_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import jakarta.persistence.*;

@Entity
@Data
@Table(name = "producto")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "categoria_id")
    private Long categoriaId;

    @Column(nullable = false)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "precio_venta", nullable = false)
    private Double precioVenta;

    @Column(name = "costo_produccion", nullable = false)
    private Double costoProduccion;

    private Boolean disponible = true;

    @Column(name = "imagen_url")
    private String imagenUrl;

    private Integer cantidad;
}