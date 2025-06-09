package com.comoencasa_backend.controller;

import com.comoencasa_backend.dto.LoginRequest;
import com.comoencasa_backend.dto.RegistroRequest;
import com.comoencasa_backend.model.Usuario;
import com.comoencasa_backend.repository.UsuarioRepository;
import com.comoencasa_backend.service.EmailService;
import com.comoencasa_backend.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // ✅ NUEVO: servicios para enviar correo y manejar tokens de verificación
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
        System.out.println("=== INTENTO DE LOGIN ===");
        System.out.println("Email recibido: " + loginRequest.getEmail());

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(loginRequest.getEmail());

        if (usuarioOpt.isEmpty()) {
            System.out.println("⚠️ Usuario no encontrado en la BD");
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }

        Usuario usuario = usuarioOpt.get();
        System.out.println("Usuario encontrado: " + usuario.getEmail());
        System.out.println("Nombre completo: " + usuario.getNombre() + " " + usuario.getApellido());
        System.out.println("Hash almacenado: " + usuario.getPassword());
        System.out.println("Activado?: " + usuario.getActivado());

        if (!usuario.getActivado()) {
            return ResponseEntity.status(403).body("La cuenta aún no ha sido verificada.");
        }

        boolean coincide = passwordEncoder.matches(
                loginRequest.getPassword(),
                usuario.getPassword()
        );

        if (!coincide) {
            System.out.println("❌ Contraseña incorrecta");
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }

        System.out.println("✅ Login exitoso");
        return ResponseEntity.ok(Map.of(
                "usuario", Map.of(
                        "id", usuario.getId(),
                        "nombreCompleto", usuario.getNombre() + " " + usuario.getApellido(),
                        "email", usuario.getEmail(),
                        "rol", usuario.getRol().name()
                )
        ));
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody RegistroRequest registroRequest) {
        try {
            System.out.println("Solicitud de registro recibida: " + registroRequest.getEmail());

            if (usuarioRepository.existsByEmail(registroRequest.getEmail())) {
                return ResponseEntity.badRequest().body(createErrorResponse("El email ya está registrado"));
            }

            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(registroRequest.getNombre());
            nuevoUsuario.setApellido(registroRequest.getApellido());
            nuevoUsuario.setEmail(registroRequest.getEmail());
            nuevoUsuario.setPassword(passwordEncoder.encode(registroRequest.getPassword()));
            nuevoUsuario.setFechaRegistro(LocalDateTime.now());
            nuevoUsuario.setTelefono("");
            nuevoUsuario.setDireccion("");
            nuevoUsuario.setRol(Usuario.Rol.CLIENTE);
            nuevoUsuario.setActivado(false); // ❗ Inicia como NO activado

            usuarioRepository.save(nuevoUsuario);

            // ✅ Generamos token y lo enviamos por correo
            String token = verificationTokenService.generarToken(nuevoUsuario.getEmail());
            emailService.enviarTokenVerificacion(nuevoUsuario.getEmail(), token);

            return ResponseEntity.ok(createSuccessResponse(
                    nuevoUsuario,
                    "Registro exitoso. Revisa tu correo para verificar tu cuenta."
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(createErrorResponse("Error interno al registrar usuario"));
        }
    }

    // ✅ NUEVO: Endpoint para activar cuenta usando el token
    @GetMapping("/verificar")
    public ResponseEntity<?> verificarCuenta(@RequestParam("token") String token) {
        String email = verificationTokenService.obtenerEmailPorToken(token);

        if (email == null) {
            return ResponseEntity.badRequest().body("Token inválido o expirado");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("No se encontró un usuario para este token");
        }

        Usuario usuario = usuarioOpt.get();
        usuario.setActivado(true);
        usuarioRepository.save(usuario);

        verificationTokenService.eliminarToken(token);
        return ResponseEntity.ok("Cuenta verificada correctamente. Ya puedes iniciar sesión.");
    }

    // Métodos auxiliares
    private Map<String, Object> createSuccessResponse(Usuario usuario, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("usuario", createUserData(usuario));
        return response;
    }

    private Map<String, Object> createErrorResponse(String error) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", error);
        return response;
    }

    private Map<String, Object> createUserData(Usuario usuario) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", usuario.getId());
        userData.put("nombreCompleto", usuario.getNombre() + " " + usuario.getApellido());
        userData.put("email", usuario.getEmail());
        userData.put("fechaRegistro", usuario.getFechaRegistro());
        userData.put("rol", usuario.getRol().name());
        return userData;
    }
}
