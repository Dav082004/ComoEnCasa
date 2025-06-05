package com.comoencasa_backend.controller;

import com.comoencasa_backend.dto.LoginRequest;
import com.comoencasa_backend.dto.RegistroRequest;
import com.comoencasa_backend.model.Usuario;
import com.comoencasa_backend.repository.UsuarioRepository;
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

    @Autowired
    public AuthController(UsuarioRepository usuarioRepository,
                          BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
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
        System.out.println("Nombre completo: " + usuario.getNombreCompleto());
        System.out.println("Hash almacenado: " + usuario.getPassword());
        System.out.println("Activo?: " + usuario.getActivo());

        boolean coincide = passwordEncoder.matches(
                loginRequest.getPassword(),
                usuario.getPassword()
        );
        System.out.println("🔑 ¿Contraseña coincide?: " + coincide);

        if (!coincide) {
            System.out.println("❌ Contraseña incorrecta");
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }

        System.out.println("✅ Login exitoso");
        return ResponseEntity.ok(Map.of(
                "usuario", Map.of(
                        "id", usuario.getId(),
                        "nombreCompleto", usuario.getNombreCompleto(),
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
            nuevoUsuario.setNombreCompleto(registroRequest.getNombreCompleto());
            nuevoUsuario.setEmail(registroRequest.getEmail());
            nuevoUsuario.setPassword(passwordEncoder.encode(registroRequest.getPassword()));
            nuevoUsuario.setFechaRegistro(LocalDateTime.now());
            nuevoUsuario.setTelefono("");
            nuevoUsuario.setDireccion("");
            nuevoUsuario.setRol(Usuario.Rol.CLIENTE);
            nuevoUsuario.setActivo(true);

            usuarioRepository.save(nuevoUsuario);
            return ResponseEntity.ok(createSuccessResponse(nuevoUsuario, "Usuario registrado con éxito"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(createErrorResponse("Error interno al registrar usuario"));
        }
    }

    @GetMapping("/perfil/{id}")
    public ResponseEntity<?> obtenerPerfil(@PathVariable Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        Map<String, Object> perfil = new HashMap<>();
        perfil.put("nombreCompleto", usuario.getNombreCompleto());
        perfil.put("email", usuario.getEmail());
        perfil.put("telefono", usuario.getTelefono());
        perfil.put("direccion", usuario.getDireccion());
        perfil.put("fechaRegistro", usuario.getFechaRegistro());

        return ResponseEntity.ok(perfil);
    }

    @PutMapping("/perfil/{id}")
    public ResponseEntity<?> actualizarPerfil(@PathVariable Long id, @RequestBody Map<String, String> datos) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        usuario.setNombreCompleto(datos.get("nombreCompleto"));
        usuario.setEmail(datos.get("email"));
        usuario.setTelefono(datos.get("telefono"));
        usuario.setDireccion(datos.get("direccion"));

        if (datos.containsKey("nuevaContraseña") && !datos.get("nuevaContraseña").isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(datos.get("nuevaContraseña")));
        }

        usuarioRepository.save(usuario);
        return ResponseEntity.ok("Perfil actualizado correctamente");
    }

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
        userData.put("nombreCompleto", usuario.getNombreCompleto());
        userData.put("email", usuario.getEmail());
        userData.put("fechaRegistro", usuario.getFechaRegistro());
        userData.put("rol", usuario.getRol().name());
        return userData;
    }
}
