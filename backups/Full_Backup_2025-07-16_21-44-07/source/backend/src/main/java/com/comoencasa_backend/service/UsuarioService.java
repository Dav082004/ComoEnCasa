package com.comoencasa_backend.service;

import com.comoencasa_backend.model.Usuario;

import java.util.Optional;

public interface UsuarioService {
    Optional<Usuario> buscarPorEmail(String email);

    void actualizarContrasena(Usuario usuario, String nuevaContrasena);

    // Recuperar cuenta por email
    void recuperarCuenta(String email);

    // ✅ Nuevo método para generar token de verificación
    String generarTokenVerificacion(String email);

    // ✅ Nuevo método para activar cuenta
    boolean activarCuenta(String token);

    // Métodos para recomendaciones
    void actualizarRecomendacion(Long usuarioId, String recomendacion);

    Optional<String> obtenerRecomendacion(Long usuarioId);
}
