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

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) { // Elimina BindingResult
        if (loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("Email y contraseña son requeridos"));
        }

        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail());

        if (usuario == null || !passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())) {
            return ResponseEntity.status(401).body(createErrorResponse("Email o contraseña incorrectos"));
        }

        if (!usuario.getActivo()) {
            return ResponseEntity.status(403).body(createErrorResponse("Cuenta desactivada"));
        }

        return ResponseEntity.ok(createSuccessResponse(usuario, "Inicio de sesión exitoso"));
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody RegistroRequest registroRequest) {
        if (usuarioRepository.existsByEmail(registroRequest.getEmail())) {
            return ResponseEntity.badRequest().body(createErrorResponse("El email ya está registrado"));
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombreCompleto(registroRequest.getNombreCompleto());
        nuevoUsuario.setEmail(registroRequest.getEmail());
        nuevoUsuario.setPassword(passwordEncoder.encode(registroRequest.getPassword()));
        nuevoUsuario.setFechaRegistro(LocalDateTime.now());

        usuarioRepository.save(nuevoUsuario);

        return ResponseEntity.ok(createSuccessResponse(nuevoUsuario, "Usuario registrado con éxito"));
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
        userData.put("nombreCompleto", usuario.getNombreCompleto());
        userData.put("email", usuario.getEmail());
        userData.put("fechaRegistro", usuario.getFechaRegistro());
        return userData;
    }
}