package com.comoencasa_backend.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerificationTokenService {

    // Mapa que asocia tokens con correos electrónicos
    private final Map<String, String> tokenToEmailMap = new ConcurrentHashMap<>();
    private final Map<String, String> emailToTokenMap = new ConcurrentHashMap<>();

    /**
     * Genera un token único para el email y lo guarda en memoria.
     */
    public String generarToken(String email) {
        // Eliminar token anterior si ya existe
        if (emailToTokenMap.containsKey(email)) {
            String tokenAnterior = emailToTokenMap.remove(email);
            tokenToEmailMap.remove(tokenAnterior);
        }

        String nuevoToken = UUID.randomUUID().toString();
        tokenToEmailMap.put(nuevoToken, email);
        emailToTokenMap.put(email, nuevoToken);

        return nuevoToken;
    }

    /**
     * Devuelve el correo electrónico asociado a un token.
     */
    public String obtenerEmailPorToken(String token) {
        System.out.println("🔍 Buscando email para token: " + token);
        String email = tokenToEmailMap.get(token);
        if (email == null) {
            System.out.println("❌ Token no encontrado en memoria.");
        } else {
            System.out.println("📧 Email asociado al token: " + email);
        }
        return email;
    }

    /**
     * Elimina el token una vez que se usó.
     */
    public void eliminarToken(String token) {
        String email = tokenToEmailMap.remove(token);
        if (email != null) {
            emailToTokenMap.remove(email);
        }
    }

    /**
     * Verifica si un token aún está activo en memoria.
     */
    public boolean tokenEsValido(String token) {
        return tokenToEmailMap.containsKey(token);
    }
}