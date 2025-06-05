package com.comoencasa_backend.service;

public interface EmailService {
    void enviarNuevaContrasena(String destinoEmail, String nuevaContrasena);
}
