package com.comoencasa_backend.service.impl;

import com.comoencasa_backend.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remitente;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Valida si un email tiene un formato válido
     * @param email El email a validar
     * @return true si el email tiene un formato válido, false en caso contrario
     */
    public boolean esEmailValido(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        
        // Verificar formato básico: debe tener @ con texto antes y después,
        // y al menos un punto en la parte del dominio
        int atIndex = email.indexOf('@');
        if (atIndex <= 0 || atIndex == email.length() - 1) {
            return false;  // @ no existe o está al inicio o al final
        }
        
        // Verificar que haya un punto después del @
        String domainPart = email.substring(atIndex + 1);
        if (domainPart.isEmpty() || !domainPart.contains(".") || domainPart.indexOf('.') == 0 || domainPart.indexOf('.') == domainPart.length() - 1) {
            return false;  // No hay punto en el dominio o está al inicio/final del dominio
        }
        
        // Verificar que no haya múltiples @
        if (email.indexOf('@', atIndex + 1) != -1) {
            return false;
        }
        
        return true;
    }

    @Override
    public void enviarNuevaContrasena(String destinoEmail, String nuevaContrasena) {
        // Validaciones de entrada
        if (destinoEmail == null) {
            throw new IllegalArgumentException("El email del destinatario no puede ser nulo");
        }
        if (destinoEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("El email del destinatario no puede estar vacío");
        }
        if (nuevaContrasena == null) {
            throw new IllegalArgumentException("La nueva contraseña no puede ser nula");
        }
        if (nuevaContrasena.trim().isEmpty()) {
            throw new IllegalArgumentException("La nueva contraseña no puede estar vacía");
        }
        
        // Validación de formato de email
        if (!esEmailValido(destinoEmail.trim())) {
            throw new IllegalArgumentException("Formato de email inválido");
        }
        
        // Validación de que el remitente esté configurado
        if (remitente == null || remitente.trim().isEmpty()) {
            throw new IllegalStateException("El remitente del email no está configurado");
        }
        
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(remitente);
            mensaje.setTo(destinoEmail.trim());
            mensaje.setSubject("Recuperación de cuenta - Como En Casa");
            mensaje.setText("Tu nueva contraseña es: " + nuevaContrasena);
            mailSender.send(mensaje);
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar email de recuperación", e);
        }
    }

    @Override
    public void enviarEmailRecuperacion(String destinoEmail, String token) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(remitente);
        mensaje.setTo(destinoEmail);
        mensaje.setSubject("Recuperación de cuenta - Como En Casa");
        mensaje.setText("Para recuperar tu cuenta, utiliza el siguiente token: " + token);
        mailSender.send(mensaje);
    }
}
