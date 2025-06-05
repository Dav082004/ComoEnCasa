package com.comoencasa_backend.dto;

public class RecuperarCuentaDTO {
    private String email;

    // Constructor vacío
    public RecuperarCuentaDTO() {}

    // Constructor con email
    public RecuperarCuentaDTO(String email) {
        this.email = email;
    }

    // Getter y Setter
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
