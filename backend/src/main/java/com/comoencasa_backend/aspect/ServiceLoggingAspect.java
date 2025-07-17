package com.comoencasa_backend.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 🎯 Aspect para logging automático de métodos de servicio
 * Registra entrada, salida, duración y errores de todos los servicios
 */
@Aspect
@Component
@Slf4j
public class ServiceLoggingAspect {

     private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");

     /**
      * Intercepta todos los métodos de servicios para logging automático
      */
     @Around("execution(* com.comoencasa_backend.service..*(..))")
     public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
          String className = joinPoint.getTarget().getClass().getSimpleName();
          String methodName = joinPoint.getSignature().getName();
          Object[] args = joinPoint.getArgs();

          // Log de entrada
          log.debug("🔧 SERVICIO INICIADO: {}.{}() con parámetros: {}",
                    className, methodName, formatArgs(args));

          // Log de auditoría para métodos críticos
          if (isCriticalMethod(methodName)) {
               auditLogger.info("SERVICE_START|{}|{}.{}|{}",
                         System.currentTimeMillis(), className, methodName, formatArgs(args));
          }

          long startTime = System.currentTimeMillis();

          try {
               Object result = joinPoint.proceed();
               long duration = System.currentTimeMillis() - startTime;

               // Log de éxito
               if (duration > 1000) {
                    log.warn("🐌 SERVICIO LENTO: {}.{}() completado en {}ms",
                              className, methodName, duration);
               } else {
                    log.debug("✅ SERVICIO COMPLETADO: {}.{}() en {}ms",
                              className, methodName, duration);
               }

               // Log de auditoría para métodos críticos
               if (isCriticalMethod(methodName)) {
                    auditLogger.info("SERVICE_SUCCESS|{}|{}.{}|{}ms|{}",
                              System.currentTimeMillis(), className, methodName, duration,
                              formatResult(result));
               }

               return result;

          } catch (Exception e) {
               long duration = System.currentTimeMillis() - startTime;

               // Log de error
               log.error("❌ SERVICIO FALLÓ: {}.{}() en {}ms - Error: {} | Causa: {}",
                         className, methodName, duration, e.getMessage(),
                         e.getCause() != null ? e.getCause().getMessage() : "N/A");

               // Log de auditoría para errores
               auditLogger.error("SERVICE_ERROR|{}|{}.{}|{}ms|{}|{}",
                         System.currentTimeMillis(), className, methodName, duration,
                         e.getClass().getSimpleName(), e.getMessage());

               throw e;
          }
     }

     /**
      * Intercepta métodos de controladores para logging
      */
     @Around("execution(* com.comoencasa_backend.controller..*(..))")
     public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
          String className = joinPoint.getTarget().getClass().getSimpleName();
          String methodName = joinPoint.getSignature().getName();

          long startTime = System.currentTimeMillis();

          try {
               Object result = joinPoint.proceed();
               long duration = System.currentTimeMillis() - startTime;

               log.info("🎯 CONTROLLER: {}.{}() ejecutado en {}ms",
                         className, methodName, duration);

               return result;

          } catch (Exception e) {
               long duration = System.currentTimeMillis() - startTime;

               log.error("💥 CONTROLLER ERROR: {}.{}() falló en {}ms - {}",
                         className, methodName, duration, e.getMessage());

               throw e;
          }
     }

     /**
      * Intercepta operaciones de base de datos críticas
      */
     @Around("execution(* com.comoencasa_backend.repository..*(..))")
     public Object logRepositoryMethods(ProceedingJoinPoint joinPoint) throws Throwable {
          String className = joinPoint.getTarget().getClass().getSimpleName();
          String methodName = joinPoint.getSignature().getName();

          // Solo loggear métodos de escritura (save, delete, update)
          if (isWriteOperation(methodName)) {
               long startTime = System.currentTimeMillis();

               try {
                    Object result = joinPoint.proceed();
                    long duration = System.currentTimeMillis() - startTime;

                    log.info("💾 DB WRITE: {}.{}() ejecutado en {}ms",
                              className, methodName, duration);

                    // Log de auditoría para operaciones de escritura
                    auditLogger.info("DB_WRITE|{}|{}.{}|{}ms",
                              System.currentTimeMillis(), className, methodName, duration);

                    return result;

               } catch (Exception e) {
                    long duration = System.currentTimeMillis() - startTime;

                    log.error("🚨 DB ERROR: {}.{}() falló en {}ms - {}",
                              className, methodName, duration, e.getMessage());

                    auditLogger.error("DB_ERROR|{}|{}.{}|{}ms|{}",
                              System.currentTimeMillis(), className, methodName, duration, e.getMessage());

                    throw e;
               }
          } else {
               return joinPoint.proceed();
          }
     }

     /**
      * Formatea argumentos para logging seguro
      */
     private String formatArgs(Object[] args) {
          if (args == null || args.length == 0) {
               return "[]";
          }

          return Arrays.stream(args)
                    .map(this::sanitizeArg)
                    .limit(5) // Limitar a 5 argumentos para evitar logs muy largos
                    .reduce((a, b) -> a + ", " + b)
                    .map(s -> "[" + s + "]")
                    .orElse("[]");
     }

     /**
      * Sanitiza argumentos sensibles
      */
     private String sanitizeArg(Object arg) {
          if (arg == null) {
               return "null";
          }

          String argStr = arg.toString();
          String argClass = arg.getClass().getSimpleName();

          // Ocultar información sensible
          if (argClass.toLowerCase().contains("password") ||
                    argStr.toLowerCase().contains("password")) {
               return "[PASSWORD_HIDDEN]";
          }

          // Truncar strings muy largos
          if (argStr.length() > 100) {
               return argStr.substring(0, 97) + "...";
          }

          return argClass + ":" + argStr;
     }

     /**
      * Formatea resultado para logging
      */
     private String formatResult(Object result) {
          if (result == null) {
               return "null";
          }

          String resultClass = result.getClass().getSimpleName();

          // Para colecciones, mostrar solo el tamaño
          if (result instanceof java.util.Collection) {
               return resultClass + "[size=" + ((java.util.Collection<?>) result).size() + "]";
          }

          // Para entidades, mostrar solo la clase
          if (resultClass.toLowerCase().contains("entity") ||
                    resultClass.toLowerCase().contains("dto")) {
               return resultClass;
          }

          return resultClass;
     }

     /**
      * Determina si un método es crítico para auditoría
      */
     private boolean isCriticalMethod(String methodName) {
          return methodName.toLowerCase().contains("save") ||
                    methodName.toLowerCase().contains("delete") ||
                    methodName.toLowerCase().contains("update") ||
                    methodName.toLowerCase().contains("create") ||
                    methodName.toLowerCase().contains("generate") ||
                    methodName.toLowerCase().contains("login") ||
                    methodName.toLowerCase().contains("auth");
     }

     /**
      * Determina si es una operación de escritura en BD
      */
     private boolean isWriteOperation(String methodName) {
          return methodName.startsWith("save") ||
                    methodName.startsWith("delete") ||
                    methodName.startsWith("update") ||
                    methodName.startsWith("insert") ||
                    methodName.startsWith("create");
     }
}
