package com.comoencasa_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ⏰ Configuración para habilitar tareas programadas (Scheduling)
 * Permite ejecutar métricas y monitoreo automático
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
     // Esta clase habilita las anotaciones @Scheduled en toda la aplicación
}
