package com.comoencasa_backend.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerificationTokenService {

    // ✅ Almacenamiento temporal en memoria (email asociado a token)
    private final Map<String, String> tokenToEmailMap = new ConcurrentHashMap<>();
    private final Map<String, String> emailToTokenMap = new ConcurrentHashMap<>();

    /**
     * ✅ Genera un token único y lo asocia con un email.
     */
    public String generarToken(String email) {
        // Si el usuario ya tenía un token anterior, lo eliminamos
        if (emailToTokenMap.containsKey(email)) {
            String oldToken = emailToTokenMap.remove(email);
            tokenToEmailMap.remove(oldToken);
        }

        String token = UUID.randomUUID().toString(); // Token único
        tokenToEmailMap.put(token, email);
        emailToTokenMap.put(email, token);
        return token;
    }

    /**
     * ✅ Devuelve el email asociado a un token, o null si no existe.
     */
    public String obtenerEmailPorToken(String token) {
        return tokenToEmailMap.get(token);
    }

    /**
     * ✅ Elimina el token una vez verificada la cuenta.
     */
    public void eliminarToken(String token) {
        String email = tokenToEmailMap.remove(token);
        if (email != null) {
            emailToTokenMap.remove(email);
        }
    }

    /**
     * ✅ (Opcional) Verifica si un token es válido.
     */
    public boolean tokenEsValido(String token) {
        return tokenToEmailMap.containsKey(token);
    }
}
