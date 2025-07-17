package com.comoencasa_backend.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 📊 Servicio de monitoreo y métricas del sistema
 * Recopila y registra métricas de rendimiento, memoria y sistema
 */
@Service
@Slf4j
public class SystemMonitoringService {

     private static final Logger metricsLogger = LoggerFactory.getLogger("METRICS");
     private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");
     private static final Logger performanceLogger = LoggerFactory.getLogger("PERFORMANCE");

     // Contadores de métricas
     private final AtomicLong requestCount = new AtomicLong(0);
     private final AtomicLong errorCount = new AtomicLong(0);
     private final AtomicLong totalResponseTime = new AtomicLong(0);

     // Beans de gestión del sistema
     private final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
     private final OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
     private final RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();

     @PostConstruct
     public void init() {
          log.info("🚀 Sistema de Monitoreo iniciado");
          auditLogger.info("SYSTEM_MONITORING_STARTED|{}|{}",
                    System.currentTimeMillis(),
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
     }

     /**
      * 📊 Reporte de métricas cada 5 minutos
      */
     @Scheduled(fixedRate = 300000) // 5 minutos
     public void reportMetrics() {
          try {
               long requests = requestCount.get();
               long errors = errorCount.get();
               long avgResponseTime = requests > 0 ? totalResponseTime.get() / requests : 0;

               // Métricas de memoria
               long usedMemory = memoryBean.getHeapMemoryUsage().getUsed();
               long maxMemory = memoryBean.getHeapMemoryUsage().getMax();
               long freeMemory = maxMemory - usedMemory;
               double memoryUsagePercent = (double) usedMemory / maxMemory * 100;

               // Métricas del sistema
               double cpuLoad = osBean.getSystemLoadAverage(); // Usar load average en lugar de process CPU
               long uptime = runtimeBean.getUptime();

               log.info("📊 MÉTRICAS DEL SISTEMA:");
               log.info("   🔄 Requests: {} | Errores: {} | Tiempo promedio: {}ms",
                         requests, errors, avgResponseTime);
               log.info("   💾 Memoria: {:.1f}% usado ({} MB libres de {} MB)",
                         memoryUsagePercent,
                         freeMemory / (1024 * 1024),
                         maxMemory / (1024 * 1024));
               log.info("   🔥 CPU: {:.1f}% | ⏱️ Uptime: {}min",
                         cpuLoad, uptime / (1000 * 60));

               // Log de métricas para análisis
               metricsLogger.info("METRICS|{}|requests={}|errors={}|avgTime={}|memUsed={:.1f}%|cpu={:.1f}%|uptime={}",
                         System.currentTimeMillis(),
                         requests, errors, avgResponseTime, memoryUsagePercent, cpuLoad, uptime);

               // Alertas automáticas
               checkSystemAlerts(memoryUsagePercent, cpuLoad, avgResponseTime);

          } catch (Exception e) {
               log.error("❌ Error al generar métricas del sistema: {}", e.getMessage());
          }
     }

     /**
      * 🚨 Sistema de alertas automáticas
      */
     private void checkSystemAlerts(double memoryUsagePercent, double cpuLoad, long avgResponseTime) {
          StringBuilder alerts = new StringBuilder();

          // Alerta de memoria alta
          if (memoryUsagePercent > 85) {
               String alert = String.format("🚨 ALERTA MEMORIA: %.1f%% usado", memoryUsagePercent);
               log.warn(alert);
               alerts.append(alert).append(" | ");
               auditLogger.warn("ALERT_HIGH_MEMORY|{}|{:.1f}%", System.currentTimeMillis(), memoryUsagePercent);
          }

          // Alerta de CPU alta
          if (cpuLoad > 80) {
               String alert = String.format("🔥 ALERTA CPU: %.1f%% uso", cpuLoad);
               log.warn(alert);
               alerts.append(alert).append(" | ");
               auditLogger.warn("ALERT_HIGH_CPU|{}|{:.1f}%", System.currentTimeMillis(), cpuLoad);
          }

          // Alerta de respuesta lenta
          if (avgResponseTime > 2000) {
               String alert = String.format("🐌 ALERTA RENDIMIENTO: {}ms promedio", avgResponseTime);
               log.warn(alert);
               alerts.append(alert).append(" | ");
               auditLogger.warn("ALERT_SLOW_RESPONSE|{}|{}ms", System.currentTimeMillis(), avgResponseTime);
          }

          // Si hay alertas, enviar notificación (aquí podrías integrar email, Slack,
          // etc.)
          if (alerts.length() > 0) {
               log.error("🚨 ALERTAS DEL SISTEMA: {}", alerts.toString());
          }
     }

     /**
      * 🌡️ Chequeo de salud del sistema cada minuto
      */
     @Scheduled(fixedRate = 60000) // 1 minuto
     public void healthCheck() {
          try {
               boolean isHealthy = true;
               StringBuilder healthStatus = new StringBuilder();

               // Verificar memoria
               double memoryUsage = (double) memoryBean.getHeapMemoryUsage().getUsed() /
                         memoryBean.getHeapMemoryUsage().getMax() * 100;
               if (memoryUsage > 90) {
                    isHealthy = false;
                    healthStatus.append("MEMORY_CRITICAL ");
               }

               // Verificar threads
               int activeThreads = Thread.activeCount();
               if (activeThreads > 100) {
                    isHealthy = false;
                    healthStatus.append("TOO_MANY_THREADS ");
               }

               String status = isHealthy ? "HEALTHY" : "UNHEALTHY";
               String emoji = isHealthy ? "✅" : "❌";

               log.debug("{} HEALTH CHECK: {} | Threads: {} | Mem: {:.1f}%",
                         emoji, status, activeThreads, memoryUsage);

               // Log solo si hay problemas
               if (!isHealthy) {
                    log.warn("⚠️ HEALTH CHECK FAILED: {} | Details: {}", status, healthStatus);
                    auditLogger.warn("HEALTH_CHECK_FAILED|{}|{}|{}",
                              System.currentTimeMillis(), status, healthStatus.toString());
               }

          } catch (Exception e) {
               log.error("❌ Error en health check: {}", e.getMessage());
          }
     }

     /**
      * 📈 Métricas de aplicación cada hora
      */
     @Scheduled(fixedRate = 3600000) // 1 hora
     public void applicationMetrics() {
          try {
               // Aquí podrías agregar métricas específicas de tu aplicación
               // como número de usuarios activos, pedidos procesados, etc.

               log.info("📈 MÉTRICAS DE APLICACIÓN:");
               log.info("   📊 Requests totales: {}", requestCount.get());
               log.info("   ❌ Errores totales: {}", errorCount.get());
               log.info("   ⏱️ Tiempo promedio respuesta: {}ms",
                         requestCount.get() > 0 ? totalResponseTime.get() / requestCount.get() : 0);

               auditLogger.info("APP_METRICS_HOURLY|{}|requests={}|errors={}|avgTime={}",
                         System.currentTimeMillis(),
                         requestCount.get(),
                         errorCount.get(),
                         requestCount.get() > 0 ? totalResponseTime.get() / requestCount.get() : 0);

          } catch (Exception e) {
               log.error("❌ Error al generar métricas de aplicación: {}", e.getMessage());
          }
     }

     /**
      * 🧹 Limpieza de logs antiguos cada día a medianoche
      */
     @Scheduled(cron = "0 0 0 * * *") // Todos los días a medianoche
     public void dailyMaintenance() {
          try {
               log.info("🧹 MANTENIMIENTO DIARIO iniciado");

               // Resetear contadores diarios
               long dailyRequests = requestCount.getAndSet(0);
               long dailyErrors = errorCount.getAndSet(0);
               totalResponseTime.set(0);

               // Log de resumen diario
               log.info("📊 RESUMEN DIARIO: {} requests, {} errores", dailyRequests, dailyErrors);
               auditLogger.info("DAILY_SUMMARY|{}|requests={}|errors={}",
                         System.currentTimeMillis(), dailyRequests, dailyErrors);

               // Forzar garbage collection
               System.gc();

               log.info("✅ MANTENIMIENTO DIARIO completado");

          } catch (Exception e) {
               log.error("❌ Error en mantenimiento diario: {}", e.getMessage());
          }
     }

     // Métodos públicos para incrementar contadores
     public void incrementRequestCount() {
          requestCount.incrementAndGet();
     }

     public void incrementErrorCount() {
          errorCount.incrementAndGet();
     }

     public void addResponseTime(long responseTime) {
          totalResponseTime.addAndGet(responseTime);
     }

     // Métodos para obtener métricas actuales
     public long getRequestCount() {
          return requestCount.get();
     }

     public long getErrorCount() {
          return errorCount.get();
     }

     public long getAverageResponseTime() {
          long requests = requestCount.get();
          return requests > 0 ? totalResponseTime.get() / requests : 0;
     }

     /**
      * 🔄 Backup automático de base de datos cada día a las 2:00 AM
      */
     @Scheduled(cron = "0 0 2 * * *")
     public void scheduleAutomaticDatabaseBackup() {
          try {
               log.info("💾 INICIANDO BACKUP AUTOMÁTICO DE BASE DE DATOS");
               auditLogger.info("AUTO_BACKUP_STARTED|{}",
                         LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

               // Ejecutar script de backup
               ProcessBuilder pb = new ProcessBuilder("cmd", "/c",
                         "scripts\\backup-database.bat");
               pb.directory(new java.io.File("."));

               Process process = pb.start();
               int exitCode = process.waitFor();

               if (exitCode == 0) {
                    log.info("✅ BACKUP AUTOMÁTICO COMPLETADO EXITOSAMENTE");
                    auditLogger.info("AUTO_BACKUP_COMPLETED|SUCCESS|{}",
                              LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
               } else {
                    log.error("❌ ERROR EN BACKUP AUTOMÁTICO - Exit code: {}", exitCode);
                    auditLogger.error("AUTO_BACKUP_FAILED|ERROR_CODE:{}|{}",
                              exitCode, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
               }

          } catch (Exception e) {
               log.error("❌ Excepción en backup automático: {}", e.getMessage(), e);
               auditLogger.error("AUTO_BACKUP_EXCEPTION|{}|{}",
                         e.getMessage(), LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
          }
     }

     /**
      * 📊 Generación de reportes semanales cada domingo a las 6:00 AM
      */
     @Scheduled(cron = "0 0 6 * * SUN")
     public void generateWeeklyReport() {
          try {
               log.info("📊 GENERANDO REPORTE SEMANAL DE SISTEMA");

               // Calcular métricas de la semana
               String weeklyMetrics = calculateWeeklyMetrics();

               // Crear reporte
               String reportContent = String.format(
                         "=== REPORTE SEMANAL - COMO EN CASA ===\n" +
                                   "Período: %s\n" +
                                   "Métricas de Sistema:\n%s\n" +
                                   "Estado General: OPERATIVO\n" +
                                   "Próximo reporte: %s",
                         LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                         weeklyMetrics,
                         LocalDateTime.now().plusDays(7).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

               // Log del reporte
               auditLogger.info("WEEKLY_REPORT|{}", reportContent.replace("\n", "|"));
               log.info("✅ REPORTE SEMANAL GENERADO");

          } catch (Exception e) {
               log.error("❌ Error generando reporte semanal: {}", e.getMessage(), e);
          }
     }

     /**
      * 🧹 Limpieza automática de archivos temporales cada 6 horas
      */
     @Scheduled(fixedRate = 21600000) // 6 horas
     public void automaticTempCleanup() {
          try {
               log.debug("🧹 Iniciando limpieza automática de temporales");

               int filesDeleted = 0;
               long spaceFreed = 0;

               // Limpiar directorio temp si existe
               java.io.File tempDir = new java.io.File("temp");
               if (tempDir.exists() && tempDir.isDirectory()) {
                    java.io.File[] tempFiles = tempDir.listFiles();
                    if (tempFiles != null) {
                         for (java.io.File file : tempFiles) {
                              if (file.isFile() && isOlderThanHours(file, 24)) {
                                   spaceFreed += file.length();
                                   if (file.delete()) {
                                        filesDeleted++;
                                   }
                              }
                         }
                    }
               }

               if (filesDeleted > 0) {
                    log.info("🧹 Limpieza automática: {} archivos eliminados, {} KB liberados",
                              filesDeleted, spaceFreed / 1024);
               }

          } catch (Exception e) {
               log.warn("⚠️ Error en limpieza automática: {}", e.getMessage());
          }
     }

     /**
      * 📈 Análisis de performance cada hora durante horas laborales
      */
     @Scheduled(cron = "0 0 8-18 * * MON-FRI")
     public void hourlyPerformanceCheck() {
          try {
               long currentTime = System.currentTimeMillis();

               // Verificar tiempo de respuesta promedio
               long avgResponse = totalResponseTime.get() / Math.max(requestCount.get(), 1);

               // Obtener uso de memoria actual
               long usedMemory = memoryBean.getHeapMemoryUsage().getUsed();
               long maxMemory = memoryBean.getHeapMemoryUsage().getMax();
               double memoryPercent = (usedMemory * 100.0) / maxMemory;

               performanceLogger.info("HOURLY_CHECK|MEMORY:{}%|AVG_RESPONSE:{}ms|REQUESTS:{}|ERRORS:{}|TIMESTAMP:{}",
                         String.format("%.1f", memoryPercent),
                         avgResponse,
                         requestCount.get(),
                         errorCount.get(),
                         currentTime);

               // Alertas de performance
               if (avgResponse > 5000) {
                    log.warn("🐌 ALERTA PERFORMANCE: Tiempo respuesta promedio alto ({}ms)", avgResponse);
               }

               if (memoryPercent > 80) {
                    log.warn("🚨 ALERTA MEMORIA: Uso alto de memoria ({}%)", String.format("%.1f", memoryPercent));
               }

          } catch (Exception e) {
               log.warn("⚠️ Error en verificación de performance: {}", e.getMessage());
          }
     }

     // ==========================================
     // MÉTODOS AUXILIARES
     // ==========================================

     /**
      * 📊 Calcular métricas semanales
      */
     private String calculateWeeklyMetrics() {
          try {
               Runtime runtime = Runtime.getRuntime();
               long totalMemory = runtime.totalMemory();
               long freeMemory = runtime.freeMemory();
               long usedMemory = totalMemory - freeMemory;

               return String.format(
                         "• Memoria utilizada: %.2f MB\n" +
                                   "• Requests procesados: %d\n" +
                                   "• Errores registrados: %d\n" +
                                   "• Tiempo promedio respuesta: %d ms\n" +
                                   "• Uptime: %d horas",
                         usedMemory / (1024.0 * 1024.0),
                         requestCount.get(),
                         errorCount.get(),
                         getAverageResponseTime(),
                         runtimeBean.getUptime() / (1000 * 60 * 60));
          } catch (Exception e) {
               return "Error calculando métricas: " + e.getMessage();
          }
     }

     /**
      * 🕐 Verificar si archivo es más antiguo que X horas
      */
     private boolean isOlderThanHours(java.io.File file, int hours) {
          long hoursInMs = hours * 60 * 60 * 1000L;
          return (System.currentTimeMillis() - file.lastModified()) > hoursInMs;
     }
}
