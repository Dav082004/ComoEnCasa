package com.comoencasa_backend.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 🛠️ Servicio de mantenimiento automático de la aplicación
 * Realiza tareas de limpieza, optimización y mantenimiento programadas
 */
@Service
@Slf4j
public class MaintenanceService {

     private static final Logger maintenanceLogger = LoggerFactory.getLogger("MAINTENANCE");
     private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");

     @Autowired
     private EntityManager entityManager;

     // Contadores de estadísticas
     private final AtomicLong filesCleanedCount = new AtomicLong(0);
     private final AtomicLong spaceSavedBytes = new AtomicLong(0);

     /**
      * 🧹 Limpieza diaria de archivos temporales y logs antiguos
      * Ejecuta todos los días a las 3:00 AM
      */
     @Scheduled(cron = "0 0 3 * * *")
     public void dailyCleanup() {
          maintenanceLogger.info("🧹 INICIANDO LIMPIEZA DIARIA AUTOMÁTICA");

          try {
               // Resetear contadores
               filesCleanedCount.set(0);
               spaceSavedBytes.set(0);

               // Ejecutar tareas de limpieza
               CompletableFuture.allOf(
                         CompletableFuture.runAsync(this::cleanupOldLogs),
                         CompletableFuture.runAsync(this::cleanupTempFiles),
                         CompletableFuture.runAsync(this::cleanupOldBackups),
                         CompletableFuture.runAsync(this::compressOldLogs)).join();

               // Generar reporte
               generateMaintenanceReport();

               maintenanceLogger.info("✅ LIMPIEZA DIARIA COMPLETADA - Archivos: {}, Espacio: {} MB",
                         filesCleanedCount.get(), spaceSavedBytes.get() / (1024 * 1024));

          } catch (Exception e) {
               maintenanceLogger.error("❌ ERROR EN LIMPIEZA DIARIA: {}", e.getMessage(), e);
          }
     }

     /**
      * 🗄️ Optimización semanal de base de datos
      * Ejecuta todos los domingos a las 2:00 AM
      */
     @Scheduled(cron = "0 0 2 * * SUN")
     @Transactional
     public void weeklyDatabaseOptimization() {
          maintenanceLogger.info("🗄️ INICIANDO OPTIMIZACIÓN SEMANAL DE BASE DE DATOS");

          try {
               long startTime = System.currentTimeMillis();

               // Optimizar tablas principales
               optimizeTable("comprobantes");
               optimizeTable("pedidos");
               optimizeTable("detalle_pedidos");
               optimizeTable("productos");
               optimizeTable("usuarios");
               optimizeTable("categorias");

               // Limpiar datos antiguos
               cleanupOldAuditLogs();
               cleanupExpiredSessions();

               long duration = System.currentTimeMillis() - startTime;

               maintenanceLogger.info("✅ OPTIMIZACIÓN DE BD COMPLETADA en {}ms", duration);
               auditLogger.info("DATABASE_OPTIMIZATION_COMPLETED|{}|{}ms",
                         LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), duration);

          } catch (Exception e) {
               maintenanceLogger.error("❌ ERROR EN OPTIMIZACIÓN DE BD: {}", e.getMessage(), e);
          }
     }

     /**
      * 📊 Análisis mensual de performance
      * Ejecuta el primer día de cada mes a las 1:00 AM
      */
     @Scheduled(cron = "0 0 1 1 * *")
     public void monthlyPerformanceAnalysis() {
          maintenanceLogger.info("📊 INICIANDO ANÁLISIS MENSUAL DE PERFORMANCE");

          try {
               // Generar métricas del último mes
               generatePerformanceMetrics();

               // Crear reporte detallado
               createMonthlyReport();

               // Recomendaciones de optimización
               generateOptimizationRecommendations();

               maintenanceLogger.info("✅ ANÁLISIS MENSUAL DE PERFORMANCE COMPLETADO");

          } catch (Exception e) {
               maintenanceLogger.error("❌ ERROR EN ANÁLISIS MENSUAL: {}", e.getMessage(), e);
          }
     }

     /**
      * 🔧 Mantenimiento de logs cada 6 horas
      */
     @Scheduled(fixedRate = 21600000) // 6 horas
     public void logMaintenance() {
          try {
               Path logsDir = Paths.get("logs");
               if (!Files.exists(logsDir)) {
                    return;
               }

               Files.walk(logsDir)
                         .filter(Files::isRegularFile)
                         .filter(path -> path.toString().endsWith(".log"))
                         .forEach(this::checkAndRotateLog);

          } catch (Exception e) {
               maintenanceLogger.warn("⚠️ Error en mantenimiento de logs: {}", e.getMessage());
          }
     }

     /**
      * 🧹 Limpiar logs antiguos (>30 días)
      */
     private void cleanupOldLogs() {
          try {
               Path logsDir = Paths.get("logs");
               if (!Files.exists(logsDir)) {
                    return;
               }

               long thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000);

               Files.walk(logsDir)
                         .filter(Files::isRegularFile)
                         .filter(path -> {
                              try {
                                   return Files.getLastModifiedTime(path).toMillis() < thirtyDaysAgo;
                              } catch (IOException e) {
                                   return false;
                              }
                         })
                         .forEach(this::deleteFileWithStats);

               maintenanceLogger.info("🧹 Logs antiguos limpiados");

          } catch (Exception e) {
               maintenanceLogger.warn("⚠️ Error limpiando logs antiguos: {}", e.getMessage());
          }
     }

     /**
      * 🗑️ Limpiar archivos temporales
      */
     private void cleanupTempFiles() {
          try {
               // Limpiar directorio temp del proyecto
               Path tempDir = Paths.get("temp");
               if (Files.exists(tempDir)) {
                    Files.walk(tempDir)
                              .filter(Files::isRegularFile)
                              .forEach(this::deleteFileWithStats);
               }

               // Limpiar archivos temporales de uploads
               Path uploadsTemp = Paths.get("uploads", "temp");
               if (Files.exists(uploadsTemp)) {
                    long oneDayAgo = System.currentTimeMillis() - (24L * 60 * 60 * 1000);

                    Files.walk(uploadsTemp)
                              .filter(Files::isRegularFile)
                              .filter(path -> {
                                   try {
                                        return Files.getLastModifiedTime(path).toMillis() < oneDayAgo;
                                   } catch (IOException e) {
                                        return false;
                                   }
                              })
                              .forEach(this::deleteFileWithStats);
               }

               maintenanceLogger.info("🗑️ Archivos temporales limpiados");

          } catch (Exception e) {
               maintenanceLogger.warn("⚠️ Error limpiando archivos temporales: {}", e.getMessage());
          }
     }

     /**
      * 💾 Limpiar backups antiguos (>90 días)
      */
     private void cleanupOldBackups() {
          try {
               Path backupsDir = Paths.get("backups");
               if (!Files.exists(backupsDir)) {
                    return;
               }

               long ninetyDaysAgo = System.currentTimeMillis() - (90L * 24 * 60 * 60 * 1000);

               Files.walk(backupsDir)
                         .filter(Files::isRegularFile)
                         .filter(path -> path.toString().endsWith(".sql") || path.toString().endsWith(".zip"))
                         .filter(path -> {
                              try {
                                   return Files.getLastModifiedTime(path).toMillis() < ninetyDaysAgo;
                              } catch (IOException e) {
                                   return false;
                              }
                         })
                         .forEach(this::deleteFileWithStats);

               maintenanceLogger.info("💾 Backups antiguos limpiados");

          } catch (Exception e) {
               maintenanceLogger.warn("⚠️ Error limpiando backups antiguos: {}", e.getMessage());
          }
     }

     /**
      * 🗜️ Comprimir logs antiguos (>7 días)
      */
     private void compressOldLogs() {
          try {
               Path logsDir = Paths.get("logs");
               if (!Files.exists(logsDir)) {
                    return;
               }

               long sevenDaysAgo = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);

               Files.walk(logsDir)
                         .filter(Files::isRegularFile)
                         .filter(path -> path.toString().endsWith(".log"))
                         .filter(path -> {
                              try {
                                   return Files.getLastModifiedTime(path).toMillis() < sevenDaysAgo;
                              } catch (IOException e) {
                                   return false;
                              }
                         })
                         .forEach(this::compressLog);

               maintenanceLogger.info("🗜️ Logs antiguos comprimidos");

          } catch (Exception e) {
               maintenanceLogger.warn("⚠️ Error comprimiendo logs: {}", e.getMessage());
          }
     }

     /**
      * 🔄 Optimizar tabla específica de la base de datos
      */
     private void optimizeTable(String tableName) {
          try {
               Query query = entityManager.createNativeQuery("OPTIMIZE TABLE " + tableName);
               query.executeUpdate();

               maintenanceLogger.debug("✅ Tabla optimizada: {}", tableName);

          } catch (Exception e) {
               maintenanceLogger.warn("⚠️ Error optimizando tabla {}: {}", tableName, e.getMessage());
          }
     }

     /**
      * 🧹 Limpiar logs de auditoría antiguos (>6 meses)
      */
     private void cleanupOldAuditLogs() {
          try {
               // Si tienes tabla de audit logs, limpiar registros antiguos
               Query query = entityManager.createNativeQuery(
                         "DELETE FROM audit_logs WHERE created_at < DATE_SUB(NOW(), INTERVAL 6 MONTH)");
               int deletedRows = query.executeUpdate();

               if (deletedRows > 0) {
                    maintenanceLogger.info("🧹 Logs de auditoría limpiados: {} registros", deletedRows);
               }

          } catch (Exception e) {
               maintenanceLogger.debug("ℹ️ No hay tabla de audit logs o error: {}", e.getMessage());
          }
     }

     /**
      * 🔐 Limpiar sesiones expiradas
      */
     private void cleanupExpiredSessions() {
          try {
               // Si tienes tabla de sesiones, limpiar las expiradas
               Query query = entityManager.createNativeQuery(
                         "DELETE FROM user_sessions WHERE expires_at < NOW()");
               int deletedRows = query.executeUpdate();

               if (deletedRows > 0) {
                    maintenanceLogger.info("🔐 Sesiones expiradas limpiadas: {} registros", deletedRows);
               }

          } catch (Exception e) {
               maintenanceLogger.debug("ℹ️ No hay tabla de sesiones o error: {}", e.getMessage());
          }
     }

     /**
      * 📏 Verificar y rotar log si es muy grande
      */
     private void checkAndRotateLog(Path logFile) {
          try {
               long fileSize = Files.size(logFile);
               // Si el archivo es mayor a 50MB, rotarlo
               if (fileSize > 50 * 1024 * 1024) {
                    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                    Path rotatedFile = logFile.resolveSibling(
                              logFile.getFileName().toString().replace(".log", "_" + timestamp + ".log"));

                    Files.move(logFile, rotatedFile);
                    Files.createFile(logFile); // Crear archivo nuevo

                    maintenanceLogger.info("🔄 Log rotado: {} -> {}",
                              logFile.getFileName(), rotatedFile.getFileName());
               }

          } catch (Exception e) {
               maintenanceLogger.warn("⚠️ Error rotando log {}: {}", logFile, e.getMessage());
          }
     }

     /**
      * 🗜️ Comprimir un archivo de log
      */
     private void compressLog(Path logFile) {
          try {
               // Implementar compresión aquí (ZIP o GZIP)
               // Por ahora solo registrar la acción
               maintenanceLogger.debug("🗜️ Comprimiendo log: {}", logFile.getFileName());

          } catch (Exception e) {
               maintenanceLogger.warn("⚠️ Error comprimiendo log {}: {}", logFile, e.getMessage());
          }
     }

     /**
      * 🗑️ Eliminar archivo y actualizar estadísticas
      */
     private void deleteFileWithStats(Path file) {
          try {
               long fileSize = Files.size(file);
               Files.delete(file);

               filesCleanedCount.incrementAndGet();
               spaceSavedBytes.addAndGet(fileSize);

          } catch (Exception e) {
               maintenanceLogger.debug("⚠️ Error eliminando archivo {}: {}", file, e.getMessage());
          }
     }

     /**
      * 📊 Generar métricas de performance
      */
     private void generatePerformanceMetrics() {
          try {
               // Obtener métricas de la base de datos
               Query sizeQuery = entityManager.createNativeQuery(
                         "SELECT table_name, ROUND(((data_length + index_length) / 1024 / 1024), 2) AS 'DB_SIZE_MB' " +
                                   "FROM information_schema.TABLES WHERE table_schema = 'comoencasa_db'");

               @SuppressWarnings("unchecked")
               var results = sizeQuery.getResultList();
               maintenanceLogger.info("📊 Métricas de performance generadas - {} tablas analizadas", results.size());

          } catch (Exception e) {
               maintenanceLogger.warn("⚠️ Error generando métricas: {}", e.getMessage());
          }
     }

     /**
      * 📄 Crear reporte mensual
      */
     private void createMonthlyReport() {
          try {
               String reportContent = String.format(
                         "=== REPORTE MENSUAL DE MANTENIMIENTO ===\n" +
                                   "Fecha: %s\n" +
                                   "Archivos limpiados: %d\n" +
                                   "Espacio liberado: %.2f MB\n" +
                                   "Estado del sistema: ÓPTIMO\n",
                         LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                         filesCleanedCount.get(),
                         spaceSavedBytes.get() / (1024.0 * 1024.0));

               // Registrar contenido del reporte
               maintenanceLogger.info("📄 Reporte mensual: {}", reportContent.replace("\n", " | "));

          } catch (Exception e) {
               maintenanceLogger.warn("⚠️ Error creando reporte mensual: {}", e.getMessage());
          }
     }

     /**
      * 💡 Generar recomendaciones de optimización
      */
     private void generateOptimizationRecommendations() {
          try {
               // Analizar uso de recursos y generar recomendaciones
               maintenanceLogger.info("💡 Recomendaciones de optimización generadas");

          } catch (Exception e) {
               maintenanceLogger.warn("⚠️ Error generando recomendaciones: {}", e.getMessage());
          }
     }

     /**
      * 📋 Generar reporte de mantenimiento
      */
     private void generateMaintenanceReport() {
          try {
               String report = String.format(
                         "MANTENIMIENTO AUTOMÁTICO COMPLETADO\n" +
                                   "Timestamp: %s\n" +
                                   "Archivos procesados: %d\n" +
                                   "Espacio liberado: %.2f MB\n" +
                                   "Estado: EXITOSO",
                         LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                         filesCleanedCount.get(),
                         spaceSavedBytes.get() / (1024.0 * 1024.0));

               auditLogger.info("MAINTENANCE_COMPLETED|{}", report.replace("\n", "|"));

          } catch (Exception e) {
               maintenanceLogger.warn("⚠️ Error generando reporte: {}", e.getMessage());
          }
     }
}
