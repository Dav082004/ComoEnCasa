package com.comoencasa_backend.service.impl;

import com.comoencasa_backend.model.Usuario;
import com.comoencasa_backend.repository.UsuarioRepository;
import com.comoencasa_backend.service.EmailService;
import com.comoencasa_backend.service.UsuarioService;
import com.comoencasa_backend.service.VerificationTokenService;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final VerificationTokenService verificationTokenService;

    private static final Logger logger = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    @Autowired
    public UsuarioServiceImpl(
            UsuarioRepository usuarioRepository,
            BCryptPasswordEncoder passwordEncoder,
            EmailService emailService,
            VerificationTokenService verificationTokenService // ✅ Inyección del servicio de tokens
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.verificationTokenService = verificationTokenService;
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
        if (!EmailValidator.getInstance().isValid(email)) {
            logger.warn("Intento de recuperación con email inválido: {}", email);
            throw new IllegalArgumentException("Formato de correo electrónico inválido.");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            logger.warn("Recuperación fallida: no existe usuario con el email {}", email);
            throw new RuntimeException("No se encontró un usuario con ese correo.");
        }

        Usuario usuario = usuarioOpt.get();
        String nuevaPassword = generarPasswordAleatoria();
        actualizarContrasena(usuario, nuevaPassword);
        emailService.enviarNuevaContrasena(email, nuevaPassword);
        logger.info("Se envió nueva contraseña al usuario con email: {}", email);
    }

    // ✅ Método para generar token de verificación
    @Override
    public String generarTokenVerificacion(String email) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("No se encontró el usuario.");
        }

        String token = verificationTokenService.generarToken(email);
        String enlace = "http://localhost:3000/verificar?token=" + token;

        emailService.enviarTokenVerificacion(email, enlace);
        logger.info("Token de verificación generado y enviado a {}", email);
        return token;
    }

    // ✅ Método para activar la cuenta
    @Override
    public boolean activarCuenta(String token) {
        String email = verificationTokenService.obtenerEmailPorToken(token);
        if (email == null) return false;

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) return false;

        Usuario usuario = usuarioOpt.get();
        usuario.setActivado(true);
        usuarioRepository.save(usuario);

        verificationTokenService.eliminarToken(token);
        logger.info("Cuenta activada exitosamente para {}", email);
        return true;
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
