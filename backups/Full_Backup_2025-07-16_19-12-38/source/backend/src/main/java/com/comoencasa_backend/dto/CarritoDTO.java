package com.comoencasa_backend.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.util.ArrayList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarritoDTO {
    private String sessionId;
    private List<CarritoItemDTO> items;
    private Double subtotal;
    private Double igv;
    private Double total;
    private Integer totalItems;

    public CarritoDTO(String sessionId) {
        this.sessionId = sessionId;
        this.items = new ArrayList<>();
        this.subtotal = 0.0;
        this.igv = 0.0;
        this.total = 0.0;
        this.totalItems = 0;
    }

    // Calcular totales automáticamente
    public void calcularTotales() {
        this.subtotal = items.stream()
            .mapToDouble(CarritoItemDTO::getSubtotal)
            .sum();
        
        this.igv = this.subtotal * 0.18; // IGV 18%
        this.total = this.subtotal + this.igv;
        
        this.totalItems = items.stream()
            .mapToInt(CarritoItemDTO::getCantidad)
            .sum();
    }

    // Métodos de conveniencia
    public void addItem(CarritoItemDTO item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
        calcularTotales();
    }

    public void removeItem(CarritoItemDTO item) {
        if (this.items != null) {
            this.items.remove(item);
            calcularTotales();
        }
    }

    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }
}
