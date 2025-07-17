package com.comoencasa_backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PerfilUsuarioDTO {
     private Long id;
     private String nombre;
     private String apellido;
     private String email;
     private String telefono;
     private String direccion;
     private LocalDateTime fechaRegistro;
     private String recomendacion;
}
