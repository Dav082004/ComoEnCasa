 package com.comoencasa_backend.controller;

import com.comoencasa_backend.service.UsuarioService;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class RecuperarCuentaController {

    private static final Logger logger = LoggerFactory.getLogger(RecuperarCuentaController.class);
    
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/recuperar")
    public ResponseEntity<?> recuperarCuenta(@RequestBody Map<String, String> body) {
        String email = StringUtils.trimToNull(body.get("email"));

        logger.info("Intento de recuperación de cuenta para email: {}", maskEmail(email));

        // Validar que se proporcione email
        if (email == null) {
            logger.warn("Recuperación fallida: email no proporcionado");
            return ResponseEntity.badRequest().body(createErrorResponse("El correo es requerido"));
        }

        // Validar formato de email con Apache Commons
        if (!EmailValidator.getInstance().isValid(email)) {
            logger.warn("Recuperación fallida: email inválido: {}", maskEmail(email));
            return ResponseEntity.badRequest().body(createErrorResponse("Formato de email inválido"));
        }

        try {
            // Usar el servicio que ya tiene la lógica mejorada
            usuarioService.recuperarCuenta(email);
            
            logger.info("Recuperación de cuenta exitosa para email: {}", maskEmail(email));
            return ResponseEntity.ok(createSuccessResponse("Se envió la nueva contraseña al correo"));
            
        } catch (IllegalArgumentException e) {
            logger.warn("Recuperación fallida por validación: {} para email: {}", e.getMessage(), maskEmail(email));
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            logger.warn("Recuperación fallida: {} para email: {}", e.getMessage(), maskEmail(email));
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error interno durante recuperación para email {}: {}", maskEmail(email), e.getMessage(), e);
            return ResponseEntity.status(500).body(createErrorResponse("Error interno del servidor"));
        }
    }

    // Métodos auxiliares
    private String maskEmail(String email) {
        if (email == null) return "unknown";
        return email.replaceAll("(.{3}).*(@.*)", "$1***$2");
    }

    private Map<String, Object> createSuccessResponse(String message) {
        return Map.of("success", true, "message", message);
    }

    private Map<String, Object> createErrorResponse(String error) {
        return Map.of("success", false, "error", error);
    }

    /**
     * Genera una contraseña aleatoria segura usando SecureRandom
     * @return contraseña aleatoria de 12 caracteres
     */
    private String generarPasswordAleatoria() {
        int length = 12; // Aumentamos la longitud para mayor seguridad
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom(); // Más seguro que Random
        
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return sb.toString();
    }
}
