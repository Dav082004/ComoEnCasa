package com.comoencasa_backend.service;

import com.comoencasa_backend.model.Pedido;

public interface EmailService {
    void enviarNuevaContrasena(String destinoEmail, String nuevaContrasena);

    void enviarEmailRecuperacion(String destinoEmail, String token);

    /**
     * Valida si un email tiene un formato válido básico
     * 
     * @param email El email a validar
     * @return true si el email contiene @ y ., false en caso contrario
     */
    boolean esEmailValido(String email);

    // NUEVO MÉTODO → Usamos este para enviar enlace con token al registrar cuenta
    void enviarTokenVerificacion(String destino, String token);

    /**
     * Envía notificación de cambio de estado del pedido
     * 
     * @param pedido         El pedido cuyo estado cambió
     * @param estadoAnterior El estado anterior del pedido
     * @param estadoNuevo    El nuevo estado del pedido
     */
    void enviarNotificacionCambioEstado(Pedido pedido, String estadoAnterior, String estadoNuevo);
}
