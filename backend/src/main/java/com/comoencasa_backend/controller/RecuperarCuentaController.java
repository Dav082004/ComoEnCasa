package com.comoencasa_backend.controller;

import com.comoencasa_backend.service.UsuarioService;
import com.comoencasa_backend.service.EmailService;
import com.comoencasa_backend.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Permite llamadas desde el frontend
public class RecuperarCuentaController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/recuperar")
    public ResponseEntity<?> recuperarCuenta(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("El correo es requerido.");
        }

        Optional<Usuario> usuarioOpt = usuarioService.buscarPorEmail(email);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Correo no registrado.");
        }

        Usuario usuario = usuarioOpt.get();

        String nuevaContrasena = generarPasswordAleatoria();
        usuarioService.actualizarContrasena(usuario, nuevaContrasena);
        emailService.enviarNuevaContrasena(email, nuevaContrasena);

        return ResponseEntity.ok("Se envió la nueva contraseña al correo.");
    }

    private String generarPasswordAleatoria() {
        int length = 10;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
