package com.comoencasa_backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar una categoría con información adicional
 * Incluye el conteo de productos asociados a la categoría
 * Sigue el patrón DTO para separar la representación de datos del modelo
 */
@Data
@NoArgsConstructor
public class CategoriaConConteoDTO {
     private Long id;
     private String nombre;
     private String descripcion;
     private Long cantidadProductos;

     /**
      * Constructor para crear desde una Categoria con conteo
      * 
      * @param id                ID de la categoría
      * @param nombre            Nombre de la categoría
      * @param descripcion       Descripción de la categoría
      * @param cantidadProductos Número de productos en la categoría
      */
     public CategoriaConConteoDTO(Long id, String nombre, String descripcion, Long cantidadProductos) {
          this.id = id;
          this.nombre = nombre;
          this.descripcion = descripcion;
          this.cantidadProductos = cantidadProductos != null ? cantidadProductos : 0L;
     }
}
