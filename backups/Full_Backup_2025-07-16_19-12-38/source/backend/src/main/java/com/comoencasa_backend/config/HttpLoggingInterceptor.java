package com.comoencasa_backend.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 🌐 Interceptor para logging de requests HTTP
 * Registra todas las peticiones HTTP entrantes y salientes con detalles de
 * rendimiento
 */
@Component
@Slf4j
public class HttpLoggingInterceptor implements HandlerInterceptor {

     private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");
     private static final Logger httpLogger = LoggerFactory.getLogger("HTTP_REQUEST");

     @Override
     public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
          String sessionId = request.getSession(false) != null ? request.getSession().getId() : "NO_SESSION";
          String userAgent = request.getHeader("User-Agent");
          String clientIP = getClientIP(request);
          String currentUser = getCurrentUser(request);

          // Log general en consola
          log.info("🌐 HTTP REQUEST: {} {} | IP: {} | User: {} | Session: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    clientIP,
                    currentUser,
                    sessionId.substring(0, Math.min(8, sessionId.length())));

          // Log detallado para auditoría
          auditLogger.info("REQUEST|{}|{}|{}|{}|{}|{}|{}",
                    System.currentTimeMillis(),
                    request.getMethod(),
                    request.getRequestURI(),
                    clientIP,
                    currentUser,
                    sessionId,
                    getQueryParams(request));

          // Log HTTP específico
          httpLogger.info("🔍 REQ: {} {} | UA: {} | Params: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    getUserAgentShort(userAgent),
                    getQueryParams(request));

          request.setAttribute("startTime", System.currentTimeMillis());
          request.setAttribute("requestId", generateRequestId());

          return true;
     }

     @Override
     public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
               Object handler, Exception ex) {
          Long startTime = (Long) request.getAttribute("startTime");
          String requestId = (String) request.getAttribute("requestId");
          long duration = startTime != null ? System.currentTimeMillis() - startTime : 0;
          String currentUser = getCurrentUser(request);

          // Determinar el nivel de log según el status
          String statusEmoji = getStatusEmoji(response.getStatus());
          String logLevel = response.getStatus() >= 400 ? "ERROR" : "INFO";

          if (response.getStatus() >= 400) {
               log.error("{} HTTP RESPONSE: {} {} | Status: {} | Duration: {}ms | User: {} | Error: {}",
                         statusEmoji,
                         request.getMethod(),
                         request.getRequestURI(),
                         response.getStatus(),
                         duration,
                         currentUser,
                         ex != null ? ex.getMessage() : "HTTP Error");
          } else {
               log.info("{} HTTP RESPONSE: {} {} | Status: {} | Duration: {}ms | User: {}",
                         statusEmoji,
                         request.getMethod(),
                         request.getRequestURI(),
                         response.getStatus(),
                         duration,
                         currentUser);
          }

          // Log de auditoría
          auditLogger.info("RESPONSE|{}|{}|{}|{}|{}ms|{}|{}",
                    System.currentTimeMillis(),
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration,
                    currentUser,
                    requestId);

          // Log HTTP detallado
          httpLogger.info("📤 RES: {} {} | Status: {} | Duration: {}ms | Size: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration,
                    getResponseSize(response));

          // Log de rendimiento si es lento
          if (duration > 1000) {
               log.warn("🐌 SLOW REQUEST: {} {} took {}ms | User: {} | Status: {}",
                         request.getMethod(),
                         request.getRequestURI(),
                         duration,
                         currentUser,
                         response.getStatus());
          }
     }

     /**
      * Obtiene la IP real del cliente considerando proxies
      */
     private String getClientIP(HttpServletRequest request) {
          String xForwardedFor = request.getHeader("X-Forwarded-For");
          if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
               return xForwardedFor.split(",")[0].trim();
          }

          String xRealIP = request.getHeader("X-Real-IP");
          if (xRealIP != null && !xRealIP.isEmpty() && !"unknown".equalsIgnoreCase(xRealIP)) {
               return xRealIP;
          }

          return request.getRemoteAddr();
     }

     /**
      * Obtiene el usuario actual de la sesión
      */
     private String getCurrentUser(HttpServletRequest request) {
          try {
               Principal principal = request.getUserPrincipal();
               if (principal != null) {
                    return principal.getName();
               }

               // Intentar obtener de la sesión
               if (request.getSession(false) != null) {
                    Object userAttr = request.getSession().getAttribute("user");
                    if (userAttr != null) {
                         return userAttr.toString();
                    }
               }

               return "ANONYMOUS";
          } catch (Exception e) {
               return "UNKNOWN";
          }
     }

     /**
      * Obtiene parámetros de query de manera segura
      */
     private String getQueryParams(HttpServletRequest request) {
          try {
               if (request.getQueryString() != null && !request.getQueryString().isEmpty()) {
                    // Filtrar parámetros sensibles
                    return Arrays.stream(request.getQueryString().split("&"))
                              .filter(param -> !isSensitiveParam(param))
                              .collect(Collectors.joining("&"));
               }
               return "";
          } catch (Exception e) {
               return "ERROR_PARSING_PARAMS";
          }
     }

     /**
      * Verifica si un parámetro es sensible (password, token, etc.)
      */
     private boolean isSensitiveParam(String param) {
          String paramLower = param.toLowerCase();
          return paramLower.contains("password") ||
                    paramLower.contains("token") ||
                    paramLower.contains("secret") ||
                    paramLower.contains("key");
     }

     /**
      * Obtiene User Agent resumido
      */
     private String getUserAgentShort(String userAgent) {
          if (userAgent == null || userAgent.isEmpty()) {
               return "Unknown";
          }

          if (userAgent.contains("Chrome"))
               return "Chrome";
          if (userAgent.contains("Firefox"))
               return "Firefox";
          if (userAgent.contains("Safari"))
               return "Safari";
          if (userAgent.contains("Edge"))
               return "Edge";
          if (userAgent.contains("Postman"))
               return "Postman";
          if (userAgent.contains("curl"))
               return "curl";

          return userAgent.substring(0, Math.min(20, userAgent.length()));
     }

     /**
      * Genera ID único para el request
      */
     private String generateRequestId() {
          return String.valueOf(System.currentTimeMillis() % 100000);
     }

     /**
      * Obtiene emoji según status code
      */
     private String getStatusEmoji(int status) {
          if (status >= 200 && status < 300)
               return "✅";
          if (status >= 300 && status < 400)
               return "🔄";
          if (status >= 400 && status < 500)
               return "⚠️";
          if (status >= 500)
               return "❌";
          return "❓";
     }

     /**
      * Obtiene tamaño aproximado de respuesta
      */
     private String getResponseSize(HttpServletResponse response) {
          try {
               String contentLength = response.getHeader("Content-Length");
               if (contentLength != null) {
                    long bytes = Long.parseLong(contentLength);
                    if (bytes < 1024)
                         return bytes + "B";
                    if (bytes < 1024 * 1024)
                         return (bytes / 1024) + "KB";
                    return (bytes / (1024 * 1024)) + "MB";
               }
               return "Unknown";
          } catch (Exception e) {
               return "Error";
          }
     }
}
