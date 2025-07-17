package com.comoencasa_backend.service.impl;

import com.comoencasa_backend.model.DetallePedido;
import com.comoencasa_backend.model.Pedido;
import com.comoencasa_backend.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

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
        if (email == null || email.isEmpty())
            return false;
        int atIndex = email.indexOf('@');
        if (atIndex <= 0 || atIndex == email.length() - 1)
            return false;
        String domainPart = email.substring(atIndex + 1);
        if (domainPart.isEmpty() || !domainPart.contains(".") || domainPart.startsWith(".") || domainPart.endsWith("."))
            return false;
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

    @Override
    public void enviarNotificacionCambioEstado(Pedido pedido, String estadoAnterior, String estadoNuevo) {
        if (pedido == null || pedido.getUsuario() == null || pedido.getUsuario().getEmail() == null) {
            System.out.println("⚠️ No se puede enviar notificación: datos del pedido o usuario incompletos");
            return;
        }

        String destinoEmail = pedido.getUsuario().getEmail();

        if (!esEmailValido(destinoEmail)) {
            System.out.println("⚠️ Email inválido, no se enviará notificación: " + destinoEmail);
            return;
        }

        try {
            String asunto = obtenerAsuntoSegunEstado(estadoNuevo, pedido.getId());
            String contenidoHtml = generarContenidoHtmlSegunEstado(pedido, estadoAnterior, estadoNuevo);

            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setFrom(remitente);
            helper.setTo(destinoEmail);
            helper.setSubject(asunto);
            helper.setText(contenidoHtml, true);

            mailSender.send(mensaje);

            System.out.println("📨 Notificación de cambio de estado enviada a: " + destinoEmail +
                    " | Pedido: " + pedido.getId() + " | Estado: " + estadoAnterior + " → " + estadoNuevo);

        } catch (Exception e) {
            System.err.println("❌ Error al enviar notificación de cambio de estado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String obtenerAsuntoSegunEstado(String estado, Long pedidoId) {
        switch (estado) {
            case "En preparación":
                return "🧁 ¡Tu pedido está en preparación! - Pedido #" + pedidoId + " - Como En Casa";
            case "Entregado":
                return "✅ ¡Tu pedido ha sido entregado! - Pedido #" + pedidoId + " - Como En Casa";
            case "Cancelado":
                return "⚠️ Actualización de tu pedido - Pedido #" + pedidoId + " - Como En Casa";
            default:
                return "📋 Actualización de tu pedido - Pedido #" + pedidoId + " - Como En Casa";
        }
    }

    private String generarContenidoHtmlSegunEstado(Pedido pedido, String estadoAnterior, String estadoNuevo) {
        String nombreCliente = pedido.getUsuario().getNombre() != null ? pedido.getUsuario().getNombre()
                : "Estimado cliente";

        StringBuilder html = new StringBuilder();

        // Encabezado común
        html.append(
                "<html><body style='font-family: Arial, sans-serif; background-color: #f9f9f9; margin: 0; padding: 20px;'>")
                .append("<div style='max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.1);'>")
                // Header con logo de la pastelería
                .append("<div style='background: linear-gradient(135deg, #ff6b9d 0%, #c94b7c 100%); padding: 30px; text-align: center;'>")
                .append("<h1 style='color: white; margin: 0; font-size: 28px; font-weight: bold;'>🎂 Como En Casa</h1>")
                .append("<p style='color: #ffe6f0; margin: 5px 0 0 0; font-size: 16px;'>Pastelería Artesanal</p>")
                .append("</div>")

                // Contenido principal
                .append("<div style='padding: 40px 30px;'>");

        // Saludo personalizado
        html.append("<h2 style='color: #333; margin: 0 0 20px 0; font-size: 24px;'>¡Hola ").append(nombreCliente)
                .append("!</h2>");

        // Contenido específico según el estado
        switch (estadoNuevo) {
            case "En preparación":
                html.append(
                        "<div style='background: #e8f5e8; border-left: 4px solid #28a745; padding: 20px; margin: 20px 0; border-radius: 8px;'>")
                        .append("<h3 style='color: #28a745; margin: 0 0 10px 0; font-size: 20px;'>🧁 ¡Excelentes noticias!</h3>")
                        .append("<p style='color: #333; margin: 0; font-size: 16px; line-height: 1.6;'>")
                        .append("Hemos verificado tu pago y tu pedido <strong>#").append(pedido.getId())
                        .append("</strong> ")
                        .append("ya está <strong>en preparación</strong>. Nuestros pasteleros están trabajando con mucho cariño ")
                        .append("para crear tus deliciosos productos.")
                        .append("</p>")
                        .append("</div>")
                        .append("<p style='color: #666; font-size: 16px; line-height: 1.6;'>")
                        .append("Nos complace informarte que tu pago ha sido procesado exitosamente y hemos comenzado ")
                        .append("a preparar tu pedido con la calidad artesanal que nos caracteriza. Te mantendremos ")
                        .append("informado sobre el progreso de tu orden.")
                        .append("</p>");
                break;

            case "Entregado":
                html.append(
                        "<div style='background: #e6f3ff; border-left: 4px solid #007bff; padding: 20px; margin: 20px 0; border-radius: 8px;'>")
                        .append("<h3 style='color: #007bff; margin: 0 0 10px 0; font-size: 20px;'>✅ ¡Entrega completada!</h3>")
                        .append("<p style='color: #333; margin: 0; font-size: 16px; line-height: 1.6;'>")
                        .append("Tu pedido <strong>#").append(pedido.getId()).append("</strong> ha sido ")
                        .append("<strong>entregado exitosamente</strong>. Esperamos que disfrutes mucho ")
                        .append("de nuestros productos artesanales.")
                        .append("</p>")
                        .append("</div>")
                        .append("<p style='color: #666; font-size: 16px; line-height: 1.6;'>")
                        .append("¡Gracias por confiar en Como En Casa! Esperamos que cada bocado te transporte ")
                        .append("a esos sabores caseros que tanto nos enorgullecen. No olvides compartir tu ")
                        .append("experiencia con nosotros y volver pronto por más delicias.")
                        .append("</p>")
                        .append("<div style='background: #fff8e1; padding: 15px; border-radius: 8px; margin: 20px 0;'>")
                        .append("<p style='color: #f57c00; margin: 0; font-size: 14px; text-align: center;'>")
                        .append("💝 ¡Tu opinión es muy valiosa para nosotros! ")
                        .append("</p>")
                        .append("</div>");
                break;

            case "Cancelado":
                html.append(
                        "<div style='background: #ffeaea; border-left: 4px solid #dc3545; padding: 20px; margin: 20px 0; border-radius: 8px;'>")
                        .append("<h3 style='color: #dc3545; margin: 0 0 10px 0; font-size: 20px;'>⚠️ Pedido cancelado</h3>")
                        .append("<p style='color: #333; margin: 0; font-size: 16px; line-height: 1.6;'>")
                        .append("Lamentamos informarte que tu pedido <strong>#").append(pedido.getId())
                        .append("</strong> ")
                        .append("ha sido <strong>cancelado</strong>. Si realizaste un pago, será reembolsado ")
                        .append("en un plazo de 3-5 días hábiles.")
                        .append("</p>")
                        .append("</div>")
                        .append("<p style='color: #666; font-size: 16px; line-height: 1.6;'>")
                        .append("Sentimos mucho las molestias que esto pueda ocasionar. En Como En Casa valoramos ")
                        .append("mucho tu preferencia y te invitamos a realizar un nuevo pedido cuando gustes. ")
                        .append("Estamos aquí para endulzar tus momentos especiales.")
                        .append("</p>")
                        .append("<div style='background: #f0f8ff; padding: 15px; border-radius: 8px; margin: 20px 0;'>")
                        .append("<p style='color: #0066cc; margin: 0; font-size: 14px; text-align: center;'>")
                        .append("💙 ¡Te esperamos de vuelta muy pronto! Tenemos muchas delicias preparándose para ti.")
                        .append("</p>")
                        .append("</div>");
                break;

            default:
                html.append("<p style='color: #666; font-size: 16px; line-height: 1.6;'>")
                        .append("Tu pedido <strong>#").append(pedido.getId()).append("</strong> ")
                        .append("ha sido actualizado al estado: <strong>").append(estadoNuevo).append("</strong>.")
                        .append("</p>");
                break;
        }

        // Resumen del pedido
        html.append(generarResumenPedido(pedido));

        // Información de contacto
        html.append("<div style='background: #f8f9fa; padding: 20px; border-radius: 8px; margin: 30px 0;'>")
                .append("<h4 style='color: #333; margin: 0 0 15px 0; font-size: 18px;'>📞 ¿Tienes alguna pregunta?</h4>")
                .append("<p style='color: #666; margin: 0; font-size: 14px; line-height: 1.6;'>")
                .append("Estamos aquí para ayudarte. Puedes contactarnos a través de nuestros canales de atención al cliente ")
                .append("o visitar nuestra página web para más información sobre nuestros productos.")
                .append("</p>")
                .append("</div>");

        // Footer
        html.append("</div>") // Cierre contenido principal
                .append("<div style='background: #2c3e50; padding: 25px; text-align: center;'>")
                .append("<p style='color: #ecf0f1; margin: 0; font-size: 14px;'>")
                .append("Gracias por elegir <strong>Como En Casa</strong> 🎂")
                .append("</p>")
                .append("<p style='color: #95a5a6; margin: 10px 0 0 0; font-size: 12px;'>")
                .append("© 2025 Como En Casa - Pastelería Artesanal. Todos los derechos reservados.")
                .append("</p>")
                .append("</div>")
                .append("</div></body></html>");

        return html.toString();
    }

    private String generarResumenPedido(Pedido pedido) {
        StringBuilder resumen = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        resumen.append("<div style='background: #f8f9fa; padding: 20px; border-radius: 8px; margin: 25px 0;'>")
                .append("<h4 style='color: #333; margin: 0 0 15px 0; font-size: 18px;'>📋 Resumen de tu pedido</h4>")
                .append("<div style='display: flex; justify-content: space-between; margin-bottom: 8px;'>")
                .append("<span style='color: #666;'><strong>Número de pedido:</strong></span>")
                .append("<span style='color: #333; font-weight: bold;'>#").append(pedido.getId()).append("</span>")
                .append("</div>");

        if (pedido.getFechaCreacion() != null) {
            resumen.append("<div style='display: flex; justify-content: space-between; margin-bottom: 8px;'>")
                    .append("<span style='color: #666;'><strong>Fecha del pedido:</strong></span>")
                    .append("<span style='color: #333;'>").append(pedido.getFechaCreacion().format(formatter))
                    .append("</span>")
                    .append("</div>");
        }

        if (pedido.getCostoTotal() != null) {
            resumen.append("<div style='display: flex; justify-content: space-between; margin-bottom: 8px;'>")
                    .append("<span style='color: #666;'><strong>Total:</strong></span>")
                    .append("<span style='color: #28a745; font-weight: bold; font-size: 16px;'>S/ ")
                    .append(String.format("%.2f", pedido.getCostoTotal().doubleValue()))
                    .append("</span>")
                    .append("</div>");
        }

        if (pedido.getDireccionEntrega() != null && !pedido.getDireccionEntrega().trim().isEmpty()) {
            resumen.append("<div style='display: flex; justify-content: space-between; margin-bottom: 8px;'>")
                    .append("<span style='color: #666;'><strong>Dirección:</strong></span>")
                    .append("<span style='color: #333;'>").append(pedido.getDireccionEntrega()).append("</span>")
                    .append("</div>");
        } // Mostrar productos del pedido
        if (pedido.getDetallePedidos() != null && !pedido.getDetallePedidos().isEmpty()) {
            resumen.append("<div style='margin-top: 15px; padding-top: 15px; border-top: 1px solid #dee2e6;'>")
                    .append("<p style='color: #666; margin: 0 0 10px 0; font-weight: bold;'>Productos:</p>");

            for (DetallePedido detalle : pedido.getDetallePedidos()) {
                String nombreProducto = detalle.getProducto() != null ? detalle.getProducto().getNombre() : "Producto";
                BigDecimal subtotal = detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad()));

                resumen.append(
                        "<div style='display: flex; justify-content: space-between; margin-bottom: 5px; padding: 5px 0;'>")
                        .append("<span style='color: #333; font-size: 14px;'>")
                        .append(detalle.getCantidad()).append("x ").append(nombreProducto)
                        .append("</span>")
                        .append("<span style='color: #666; font-size: 14px;'>S/ ")
                        .append(String.format("%.2f", subtotal.doubleValue()))
                        .append("</span>")
                        .append("</div>");
            }
        }

        resumen.append("</div>");

        return resumen.toString();
    }
}