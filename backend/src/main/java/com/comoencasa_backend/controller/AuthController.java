package com.comoencasa_backend.controller;

import com.comoencasa_backend.dto.ActualizarPerfilRequest;
import com.comoencasa_backend.dto.CambiarContrasenaRequest;
import com.comoencasa_backend.dto.LoginRequest;
import com.comoencasa_backend.dto.PerfilUsuarioDTO;
import com.comoencasa_backend.dto.RegistroRequest;
import com.comoencasa_backend.dto.RecomendacionDTO;
import com.comoencasa_backend.model.Usuario;
import com.comoencasa_backend.repository.UsuarioRepository;
import com.comoencasa_backend.service.EmailService;
import com.comoencasa_backend.service.UsuarioService;
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
import java.util.stream.Collectors;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final VerificationTokenService verificationTokenService;
    private final UsuarioService usuarioService;

    @Autowired
    public AuthController(UsuarioRepository usuarioRepository,
            BCryptPasswordEncoder passwordEncoder,
            EmailService emailService,
            VerificationTokenService verificationTokenService,
            UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.verificationTokenService = verificationTokenService;
        this.usuarioService = usuarioService;
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

    // Endpoint para guardar/actualizar recomendación
    @PostMapping("/recomendacion/{userId}")
    public ResponseEntity<?> guardarRecomendacion(@PathVariable Long userId, @RequestBody RecomendacionDTO request) {
        try {
            if (request.getRecomendacion() == null || StringUtils.isBlank(request.getRecomendacion().trim())) {
                return ResponseEntity.status(400).body("La recomendación no puede estar vacía");
            }

            String recomendacion = request.getRecomendacion().trim();
            if (recomendacion.length() > 1000) {
                return ResponseEntity.status(400).body("La recomendación no puede exceder 1000 caracteres");
            }

            usuarioService.actualizarRecomendacion(userId, recomendacion);
            logger.info("Recomendación guardada para usuario ID: {}", userId);
            return ResponseEntity.ok("Recomendación guardada correctamente");

        } catch (RuntimeException e) {
            logger.error("Error al guardar recomendación para usuario {}: {}", userId, e.getMessage());
            return ResponseEntity.status(404).body("Usuario no encontrado");
        } catch (Exception e) {
            logger.error("Error interno al guardar recomendación para usuario {}: {}", userId, e.getMessage());
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }

    // Endpoint para obtener recomendación del usuario
    @GetMapping("/recomendacion/{userId}")
    public ResponseEntity<?> obtenerRecomendacion(@PathVariable Long userId) {
        try {
            Optional<String> recomendacion = usuarioService.obtenerRecomendacion(userId);

            if (recomendacion.isPresent() && !StringUtils.isBlank(recomendacion.get())) {
                Map<String, String> response = new HashMap<>();
                response.put("recomendacion", recomendacion.get());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.ok(Collections.emptyMap());
            }

        } catch (Exception e) {
            logger.error("Error al obtener recomendación para usuario {}: {}", userId, e.getMessage());
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }

    // Endpoint para obtener todas las recomendaciones públicas
    @GetMapping("/recomendaciones")
    public ResponseEntity<?> obtenerRecomendaciones() {
        try {
            List<Usuario> usuariosConRecomendaciones = usuarioRepository.findByRecomendacionIsNotNull();
            logger.info("Encontrados {} usuarios con recomendaciones", usuariosConRecomendaciones.size());

            List<Map<String, String>> recomendaciones = usuariosConRecomendaciones.stream()
                    .filter(usuario -> usuario.getRecomendacion() != null
                            && !usuario.getRecomendacion().trim().isEmpty())
                    .map(usuario -> {
                        Map<String, String> testimonio = new HashMap<>();

                        // Manejar apellido de forma segura
                        String apellidoInicial = "";
                        if (usuario.getApellido() != null && !usuario.getApellido().trim().isEmpty()) {
                            apellidoInicial = " " + usuario.getApellido().trim().substring(0, 1) + ".";
                        }

                        String nombreCompleto = usuario.getNombre() + apellidoInicial;
                        testimonio.put("nombre", nombreCompleto);
                        testimonio.put("recomendacion", usuario.getRecomendacion());

                        logger.debug("Agregando testimonio de: {}", nombreCompleto);
                        return testimonio;
                    })
                    .collect(Collectors.toList());

            logger.info("Retornando {} recomendaciones válidas", recomendaciones.size());
            return ResponseEntity.ok(recomendaciones);

        } catch (Exception e) {
            logger.error("Error al obtener recomendaciones: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }

    private String maskEmail(String email) {
        if (email == null)
            return "unknown";
        return email.replaceAll("(.{3}).*(@.*)", "$1***$2");
    }
}