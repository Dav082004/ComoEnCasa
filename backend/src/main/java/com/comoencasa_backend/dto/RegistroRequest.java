package com.comoencasa_backend.dto;

import com.comoencasa_backend.model.Usuario;

public class RegistroRequest {
    private String nombre;
    private String apellido;
    private String correo; // Cambiado de 'email' a 'correo' para coincidir con el JSON
    private String password;
    private String telefono;
    private String direccion;
    private Usuario.TipoDocumento tipoDocumento;
    private String numeroDocumento;

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    // Método de compatibilidad para mantener el código existente
    public String getEmail() {
        return correo;
    }

    public void setEmail(String email) {
        this.correo = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Usuario.TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(Usuario.TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }
}