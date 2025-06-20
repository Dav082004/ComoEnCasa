package com.comoencasa_backend.controller;

import com.comoencasa_backend.dto.ActualizarPerfilRequest;
import com.comoencasa_backend.dto.CambiarContrasenaRequest;
import com.comoencasa_backend.dto.LoginRequest;
import com.comoencasa_backend.dto.PerfilUsuarioDTO;
import com.comoencasa_backend.dto.RegistroRequest;
import com.comoencasa_backend.model.Usuario;
import com.comoencasa_backend.repository.UsuarioRepository;
import com.comoencasa_backend.service.EmailService;
import com.comoencasa_backend.service.VerificationTokenService;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final VerificationTokenService verificationTokenService;

    @Autowired
    public AuthController(UsuarioRepository usuarioRepository,
            BCryptPasswordEncoder passwordEncoder,
            EmailService emailService,
            VerificationTokenService verificationTokenService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.verificationTokenService = verificationTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String email = StringUtils.trimToNull(loginRequest.getEmail());
        String password = StringUtils.trimToNull(loginRequest.getPassword());

        logger.info("Login recibido para: {}", maskEmail(email));

        if (email == null || !EmailValidator.getInstance().isValid(email)) {
            return ResponseEntity.status(401).body("Email inválido");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            logger.warn("Usuario no encontrado: {}", maskEmail(email));
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }

        Usuario usuario = usuarioOpt.get();
        if (!usuario.getActivado()) {
            return ResponseEntity.status(403).body("La cuenta aún no ha sido verificada.");
        }

        boolean coincide = passwordEncoder.matches(password, usuario.getPassword());
        if (!coincide) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }

        return ResponseEntity.ok(Map.of(
                "usuario", Map.of(
                        "id", usuario.getId(),
                        "nombreCompleto", usuario.getNombre() + " " + usuario.getApellido(),
                        "email", usuario.getEmail(),
                        "rol", usuario.getRol().name())));
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody RegistroRequest registroRequest) {
        try {
            String email = StringUtils.trimToNull(registroRequest.getEmail());
            if (usuarioRepository.existsByEmail(email)) {
                return ResponseEntity.badRequest().body("El email ya está registrado");
            }

            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(registroRequest.getNombre());
            nuevoUsuario.setApellido(registroRequest.getApellido());
            nuevoUsuario.setEmail(email);
            nuevoUsuario.setPassword(passwordEncoder.encode(registroRequest.getPassword()));
            nuevoUsuario.setFechaRegistro(LocalDateTime.now());
            nuevoUsuario.setTelefono("");
            nuevoUsuario.setDireccion("");
            nuevoUsuario.setRol(Usuario.Rol.CLIENTE);
            nuevoUsuario.setActivado(false);

            usuarioRepository.save(nuevoUsuario);

            String token = verificationTokenService.generarToken(email);
            emailService.enviarTokenVerificacion(email, token);

            logger.info("🟢 Usuario registrado: {} {} ({})", nuevoUsuario.getNombre(), nuevoUsuario.getApellido(),
                    email);
            logger.info("📧 Token enviado: {}", token);

            return ResponseEntity.ok("Registro exitoso. Revisa tu correo para verificar tu cuenta.");
        } catch (Exception e) {
            logger.error("❌ Error en el registro: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error interno al registrar usuario");
        }
    }

    @GetMapping("/verificar")
    public ResponseEntity<?> verificarCuenta(@RequestParam("token") String token) {
        logger.info("🔍 Intentando verificar con token: {}", token);

        String email = verificationTokenService.obtenerEmailPorToken(token);

        if (email == null) {

            return ResponseEntity.badRequest().body("Token inválido o ya expirado.");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            logger.warn("❌ No se encontró usuario con email del token: {}", email);
            return ResponseEntity.badRequest().body("No se encontró un usuario asociado a este token.");
        }

        Usuario usuario = usuarioOpt.get();

        if (usuario.getActivado()) {
            logger.info("⚠️ Cuenta ya verificada anteriormente: {}", email);
            return ResponseEntity.ok("La cuenta ya estaba verificada.");
        }

        usuario.setActivado(true);
        usuarioRepository.save(usuario);
        verificationTokenService.eliminarToken(token);

        logger.info("✅ Cuenta activada correctamente para: {}", email);
        return ResponseEntity.ok("Cuenta activada correctamente. Ya puedes iniciar sesión.");
    }

    // ==================== ENDPOINTS DE PERFIL ====================

    @GetMapping("/perfil/{userId}")
    public ResponseEntity<?> obtenerPerfil(@PathVariable Long userId) {
        try {
            logger.info("Obteniendo perfil del usuario ID: {}", userId);

            Optional<Usuario> usuarioOpt = usuarioRepository.findById(userId);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Usuario no encontrado");
            }

            Usuario usuario = usuarioOpt.get();
            PerfilUsuarioDTO perfil = new PerfilUsuarioDTO();
            perfil.setId(usuario.getId());
            perfil.setNombre(usuario.getNombre());
            perfil.setApellido(usuario.getApellido());
            perfil.setEmail(usuario.getEmail());
            perfil.setTelefono(usuario.getTelefono());
            perfil.setDireccion(usuario.getDireccion());
            perfil.setFechaRegistro(usuario.getFechaRegistro());

            return ResponseEntity.ok(perfil);

        } catch (Exception e) {
            logger.error("Error al obtener perfil del usuario {}: {}", userId, e.getMessage());
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }

    @PutMapping("/perfil/{userId}")
    public ResponseEntity<?> actualizarPerfil(@PathVariable Long userId, @RequestBody ActualizarPerfilRequest request) {
        try {
            logger.info("Actualizando perfil del usuario ID: {}", userId);

            Optional<Usuario> usuarioOpt = usuarioRepository.findById(userId);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Usuario no encontrado");
            }

            Usuario usuario = usuarioOpt.get();

            // Solo actualizar los campos editables: email, teléfono y dirección
            if (StringUtils.isNotBlank(request.getEmail())) {
                // Validar formato de email
                String email = StringUtils.trim(request.getEmail());
                if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                    return ResponseEntity.status(400).body("Formato de email inválido");
                }

                // Verificar que el email no esté en uso por otro usuario
                Optional<Usuario> existeEmail = usuarioRepository.findByEmail(email);
                if (existeEmail.isPresent() && !existeEmail.get().getId().equals(userId)) {
                    return ResponseEntity.status(400).body("El email ya está en uso por otro usuario");
                }

                usuario.setEmail(email);
            }
            if (StringUtils.isNotBlank(request.getTelefono())) {
                usuario.setTelefono(StringUtils.trim(request.getTelefono()));
            }
            if (StringUtils.isNotBlank(request.getDireccion())) {
                usuario.setDireccion(StringUtils.trim(request.getDireccion()));
            }

            usuarioRepository.save(usuario);
            logger.info("Perfil actualizado correctamente para usuario ID: {}", userId);

            return ResponseEntity.ok("Perfil actualizado correctamente");

        } catch (Exception e) {
            logger.error("Error al actualizar perfil del usuario {}: {}", userId, e.getMessage());
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }

    @PutMapping("/perfil/{userId}/cambiar-contrasena")
    public ResponseEntity<?> cambiarContrasena(@PathVariable Long userId,
            @RequestBody CambiarContrasenaRequest request) {
        try {
            logger.info("Cambiando contraseña del usuario ID: {}", userId);

            // Validaciones básicas
            if (StringUtils.isBlank(request.getContrasenaActual())
                    || StringUtils.isBlank(request.getNuevaContrasena())) {
                return ResponseEntity.status(400).body("La contraseña actual y nueva contraseña son requeridas");
            }

            if (request.getNuevaContrasena().length() < 6) {
                return ResponseEntity.status(400).body("La nueva contraseña debe tener al menos 6 caracteres");
            }

            Optional<Usuario> usuarioOpt = usuarioRepository.findById(userId);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Usuario no encontrado");
            }

            Usuario usuario = usuarioOpt.get();

            // Verificar contraseña actual
            if (!passwordEncoder.matches(request.getContrasenaActual(), usuario.getPassword())) {
                return ResponseEntity.status(400).body("La contraseña actual es incorrecta");
            }

            // Actualizar contraseña
            usuario.setPassword(passwordEncoder.encode(request.getNuevaContrasena()));
            usuarioRepository.save(usuario);

            logger.info("Contraseña cambiada correctamente para usuario ID: {}", userId);
            return ResponseEntity.ok("Contraseña cambiada correctamente");

        } catch (Exception e) {
            logger.error("Error al cambiar contraseña del usuario {}: {}", userId, e.getMessage());
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }

    private String maskEmail(String email) {
        if (email == null)
            return "unknown";
        return email.replaceAll("(.{3}).*(@.*)", "$1***$2");
    }
}