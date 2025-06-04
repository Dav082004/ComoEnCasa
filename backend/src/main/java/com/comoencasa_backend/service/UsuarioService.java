package com.comoencasa_backend.service;

import com.comoencasa_backend.model.Usuario;

import java.util.Optional;

public interface UsuarioService {
    Optional<Usuario> buscarPorEmail(String email);
    void actualizarContrasena(Usuario usuario, String nuevaContrasena);

    // NUEVO MÉTODO
    void recuperarCuenta(String email);
}
