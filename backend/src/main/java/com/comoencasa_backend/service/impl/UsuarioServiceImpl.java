package com.comoencasa_backend.service.impl;

import com.comoencasa_backend.model.Usuario;
import com.comoencasa_backend.repository.UsuarioRepository;
import com.comoencasa_backend.service.EmailService;
import com.comoencasa_backend.service.UsuarioService;
import org.apache.commons.validator.routines.EmailValidator; // ← Usamos Apache Commons Validator
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // ← Usamos SLF4J con Logback
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // Logger para registrar intentos de recuperación
    private static final Logger logger = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    @Autowired
    public UsuarioServiceImpl(
            UsuarioRepository usuarioRepository,
            BCryptPasswordEncoder passwordEncoder,
            EmailService emailService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override
    public void actualizarContrasena(Usuario usuario, String nuevaContrasena) {
        usuario.setPassword(passwordEncoder.encode(nuevaContrasena));
        usuarioRepository.save(usuario);
    }

    @Override
    public void recuperarCuenta(String email) {
        // === Validación del correo con Apache Commons Validator ===
        if (!EmailValidator.getInstance().isValid(email)) {
            logger.warn("Intento de recuperación con email inválido: {}", email); // ← Registramos intento inválido
            throw new IllegalArgumentException("Formato de correo electrónico inválido.");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            logger.warn("Recuperación fallida: no existe usuario con el email {}", email); // ← Usuario no encontrado
            throw new RuntimeException("No se encontró un usuario con ese correo.");
        }

        Usuario usuario = usuarioOpt.get();
        String nuevaPassword = generarPasswordAleatoria();

        // Actualiza y guarda nueva contraseña en BD
        actualizarContrasena(usuario, nuevaPassword);

        // Envía por correo la nueva contraseña
        emailService.enviarNuevaContrasena(email, nuevaPassword);

        // === Registramos éxito en intento de recuperación ===
        logger.info("Se envió nueva contraseña al usuario con email: {}", email);
    }

    private String generarPasswordAleatoria() {
        int length = 8;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }
}
