package com.comoencasa_backend.dto;

import lombok.Data;

@Data
public class ActualizarPerfilRequest {
     private String email;
     private String telefono;
     private String direccion;
     // Nota: nombre y apellido no se incluyen porque no son editables
}
