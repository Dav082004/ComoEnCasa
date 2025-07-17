package com.comoencasa_backend.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarritoItemDTO {
    private Long productoId;
    private String nombre;
    private String descripcion;
    private Double precioVenta;
    private String imagenUrl;
    private Integer cantidad;
    private String comentarios;
    private Double subtotal;

    // Constructor de conveniencia
    public CarritoItemDTO(Long productoId, String nombre, Double precioVenta, 
                         Integer cantidad, String comentarios) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.precioVenta = precioVenta;
        this.cantidad = cantidad;
        this.comentarios = comentarios;
        this.subtotal = precioVenta * cantidad;
    }

    // Calcular subtotal automáticamente
    public Double getSubtotal() {
        return this.precioVenta != null && this.cantidad != null 
            ? this.precioVenta * this.cantidad 
            : 0.0;
    }
}
