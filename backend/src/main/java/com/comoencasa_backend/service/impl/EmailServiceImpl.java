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

    @Override
    public void enviarNuevaContrasena(String destinoEmail, String nuevaContrasena) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(remitente);
        mensaje.setTo(destinoEmail);
        mensaje.setSubject("Recuperación de cuenta - Como En Casa");
        mensaje.setText("Tu nueva contraseña es: " + nuevaContrasena);
        mailSender.send(mensaje);
    }
}
