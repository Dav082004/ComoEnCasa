package com.comoencasa_backend.service.impl;

import com.comoencasa_backend.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remitente;

    @Value("${frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean esEmailValido(String email) {
        if (email == null || email.isEmpty()) return false;
        int atIndex = email.indexOf('@');
        if (atIndex <= 0 || atIndex == email.length() - 1) return false;
        String domainPart = email.substring(atIndex + 1);
        if (domainPart.isEmpty() || !domainPart.contains(".") || domainPart.startsWith(".") || domainPart.endsWith(".")) return false;
        return email.indexOf('@', atIndex + 1) == -1;
    }

    @Override
    public void enviarNuevaContrasena(String destinoEmail, String nuevaContrasena) {
        if (destinoEmail == null || destinoEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("El email del destinatario no puede ser nulo o vacío");
        }
        if (nuevaContrasena == null || nuevaContrasena.trim().isEmpty()) {
            throw new IllegalArgumentException("La nueva contraseña no puede ser nula o vacía");
        }
        if (!esEmailValido(destinoEmail.trim())) {
            throw new IllegalArgumentException("Formato de email inválido");
        }
        if (remitente == null || remitente.trim().isEmpty()) {
            throw new IllegalStateException("El remitente del email no está configurado");
        }

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(remitente);
        mensaje.setTo(destinoEmail.trim());
        mensaje.setSubject("Recuperación de cuenta - Como En Casa");
        mensaje.setText("Tu nueva contraseña es: " + nuevaContrasena);
        mailSender.send(mensaje);

        System.out.println("📨 Correo de recuperación enviado a: " + destinoEmail);
    }

    @Override
    public void enviarEmailRecuperacion(String destinoEmail, String token) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(remitente);
        mensaje.setTo(destinoEmail);
        mensaje.setSubject("Recuperación de cuenta - Como En Casa");
        mensaje.setText("Para recuperar tu cuenta, utiliza el siguiente token: " + token);
        mailSender.send(mensaje);

        System.out.println("📨 Token de recuperación enviado a: " + destinoEmail);
    }

    @Override
    public void enviarTokenVerificacion(String destinoEmail, String token) {
        if (destinoEmail == null || destinoEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("El email del destinatario no puede ser nulo o vacío");
        }
        if (!esEmailValido(destinoEmail.trim())) {
            throw new IllegalArgumentException("Formato de email inválido");
        }
        if (remitente == null || remitente.trim().isEmpty()) {
            throw new IllegalStateException("El remitente del email no está configurado");
        }

        try {
            String enlace = frontendUrl + "/verificar?token=" + token;

            // ✨ Plantilla HTML estilizada
            String html = "<html><body style='font-family:Arial, sans-serif; background-color:#f7f7f7; padding:20px;'>"
                    + "<div style='max-width:600px;margin:auto;background:#ffffff;border-radius:8px;padding:30px;'>"
                    + "<h2 style='color:#333;'>¡Bienvenido a <span style='color:#dc3545;'>Como En Casa</span>! 🎉</h2>"
                    + "<p style='font-size:16px;color:#555;'>Gracias por registrarte. Para activar tu cuenta, haz clic en el botón a continuación:</p>"
                    + "<div style='text-align:center;margin:20px;'>"
                    + "<a href='" + enlace + "' style='padding:12px 25px;background-color:#28a745;color:#fff;"
                    + "text-decoration:none;border-radius:5px;font-size:16px;'>Verificar Cuenta</a>"
                    + "</div>"
                    + "<p style='font-size:14px;color:#999;'>Si tú no realizaste este registro, puedes ignorar este correo.</p>"
                    + "<hr style='margin-top:30px;border:none;border-top:1px solid #eee;'/>"
                    + "<p style='font-size:12px;color:#aaa;text-align:center;'>© 2025 Como En Casa - Todos los derechos reservados</p>"
                    + "</div></body></html>";

            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setFrom(remitente);
            helper.setTo(destinoEmail.trim());
            helper.setSubject("Verificación de cuenta - Como En Casa");
            helper.setText(html, true); // true = HTML

            mailSender.send(mensaje);

            System.out.println("📨 Correo de verificación enviado a: " + destinoEmail);
            System.out.println("🔗 Enlace generado: " + enlace);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al enviar email de verificación", e);
        }
    }
}