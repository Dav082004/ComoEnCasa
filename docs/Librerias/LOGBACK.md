# 📝 Logback - Implementación en Como en Casa

## 📋 Descripción General

Logback es el framework de logging de última generación para Java, diseñado como sucesor de Log4J. En el proyecto "Como en Casa", Logback proporciona un sistema de logging robusto, eficiente y configurable que permite monitorear el comportamiento de la aplicación, detectar errores y realizar auditorías de seguridad.

## 🎯 ¿Qué es Logback?

Logback es un framework de logging nativo para Java que ofrece mejor rendimiento, mayor flexibilidad y características avanzadas comparado con otros sistemas de logging. Es el framework de logging por defecto en Spring Boot.

### 💼 Beneficios en el Proyecto

- **📊 Monitoreo Completo**: Seguimiento detallado de operaciones críticas
- **🔍 Debugging Eficiente**: Información detallada para resolución de problemas
- **🛡️ Auditoría de Seguridad**: Logging de intentos de autenticación y operaciones sensibles
- **📈 Análisis de Performance**: Métricas de rendimiento y tiempo de respuesta

## 🔄 Flujo de Información con Logback

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Aplicación    │────│     Logback     │────│     Destinos    │
│                 │    │                 │    │                 │
│ • Controllers   │    │ • Filters       │    │ • Consola       │
│ • Services      │    │ • Formatters    │    │ • Archivos      │
│ • Security      │    │ • Appenders     │    │ • Sistemas      │
│ • Exceptions    │    │ • Loggers       │    │   Externos      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🏗️ Arquitectura de Logging

```
Application Components
        ↓
    SLF4J API
        ↓
    Logback Core
        ↓
   ┌─────────────────┐
   │    Appenders    │
   ├─────────────────┤
   │ • Console       │
   │ • File          │
   │ • Rolling File  │
   │ • Email         │
   │ • Database      │
   └─────────────────┘
```

## 🛠️ Configuración Principal

### 📦 Dependencias (Spring Boot)

```xml
<!-- Incluido automáticamente en Spring Boot Starter -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-logging</artifactId>
</dependency>
```

### 🔧 Configuración XML

**Archivo:** `logback-spring.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- Configuración de Logback para Como en Casa -->

    <!-- Appender para consola con colores -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %highlight(%-5level) %magenta(%logger{36}) - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- Logger específico para eventos de autenticación -->
    <logger name="com.comoencasa_backend.controller.AuthController" level="INFO" additivity="false">
        <appender-ref ref="CONSOLE" />
    </logger>

    <!-- Logger para servicios de usuario -->
    <logger name="com.comoencasa_backend.service.impl.UsuarioServiceImpl" level="INFO" additivity="false">
        <appender-ref ref="CONSOLE" />
    </logger>

    <!-- Logger para recuperación de cuenta -->
    <logger name="com.comoencasa_backend.controller.RecuperarCuentaController" level="INFO" additivity="false">
        <appender-ref ref="CONSOLE" />
    </logger>

    <!-- Logger para el paquete principal -->
    <logger name="com.comoencasa_backend" level="DEBUG" />

    <!-- Logger para Spring Security -->
    <logger name="org.springframework.security" level="WARN" />

    <!-- Logger para SQL (opcional) -->
    <logger name="org.hibernate.SQL" level="WARN" />
    <logger name="org.hibernate.type.descriptor.sql.BasicBinder" level="WARN" />

    <!-- Configuración del root logger -->
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
    </root>
</configuration>
```

## 💻 Implementación en el Código

### 1. 🔐 Logging de Autenticación

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        logger.info("Intento de login para usuario: {}", loginRequest.getEmail());

        try {
            // Proceso de autenticación
            Optional<Usuario> usuario = usuarioRepository.findByEmail(loginRequest.getEmail());

            if (usuario.isEmpty()) {
                logger.warn("Usuario no encontrado: {}", loginRequest.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales inválidas");
            }

            if (!passwordEncoder.matches(loginRequest.getPassword(), usuario.get().getPassword())) {
                logger.warn("Contraseña incorrecta para usuario: {}", loginRequest.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales inválidas");
            }

            if (!usuario.get().isActivado()) {
                logger.info("Usuario no activado intentando login: {}", loginRequest.getEmail());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Cuenta no activada");
            }

            logger.info("Login exitoso para usuario: {}", loginRequest.getEmail());
            return ResponseEntity.ok(new LoginResponse(usuario.get()));

        } catch (Exception e) {
            logger.error("Error durante el login para usuario: {}", loginRequest.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error interno del servidor");
        }
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody RegistroRequest request) {
        logger.info("Intento de registro para usuario: {}", request.getEmail());

        try {
            // Validaciones
            if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
                logger.warn("Intento de registro con email ya existente: {}", request.getEmail());
                return ResponseEntity.badRequest().body("Email ya registrado");
            }

            // Crear usuario
            Usuario usuario = new Usuario();
            usuario.setEmail(request.getEmail());
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));

            Usuario savedUsuario = usuarioRepository.save(usuario);
            logger.info("Usuario registrado exitosamente: ID={}, Email={}",
                       savedUsuario.getId(), savedUsuario.getEmail());

            return ResponseEntity.ok("Usuario registrado exitosamente");

        } catch (Exception e) {
            logger.error("Error durante el registro para usuario: {}", request.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error interno del servidor");
        }
    }
}
```

### 2. 🛠️ Logging de Servicios

```java
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j  // Lombok annotation para Logger
public class UsuarioServiceImpl implements UsuarioService {

    @Override
    public Usuario create(Usuario usuario) {
        log.info("Creando nuevo usuario: {}", usuario.getEmail());

        try {
            // Validaciones
            if (!EmailValidator.getInstance().isValid(usuario.getEmail())) {
                log.warn("Email inválido durante creación de usuario: {}", usuario.getEmail());
                throw new IllegalArgumentException("Email inválido");
            }

            // Verificar duplicados
            if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
                log.warn("Intento de crear usuario con email duplicado: {}", usuario.getEmail());
                throw new IllegalArgumentException("Email ya existe");
            }

            // Crear usuario
            Usuario nuevoUsuario = usuarioRepository.save(usuario);
            log.info("Usuario creado exitosamente: ID={}, Email={}",
                    nuevoUsuario.getId(), nuevoUsuario.getEmail());

            return nuevoUsuario;

        } catch (Exception e) {
            log.error("Error creando usuario: {}", usuario.getEmail(), e);
            throw e;
        }
    }

    @Override
    public void activarCuenta(String token) {
        log.info("Activando cuenta con token: {}", token);

        try {
            VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));

            Usuario usuario = verificationToken.getUsuario();
            usuario.setActivado(true);
            usuarioRepository.save(usuario);

            log.info("Cuenta activada exitosamente: ID={}, Email={}",
                    usuario.getId(), usuario.getEmail());

        } catch (Exception e) {
            log.error("Error activando cuenta con token: {}", token, e);
            throw e;
        }
    }
}
```

### 3. 🔧 Logging de Operaciones Críticas

```java
@Service
@Slf4j
public class ComprobanteServiceImpl implements ComprobanteService {

    @Override
    @Transactional
    public ComprobanteDTO generarComprobante(Long pedidoId, TipoComprobante tipo) {
        log.info("Generando comprobante tipo {} para pedido ID: {}", tipo, pedidoId);

        try {
            Pedido pedido = pedidoRepo.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + pedidoId));

            // Generar comprobante
            Comprobante comprobante = new Comprobante();
            comprobante.setPedido(pedido);
            comprobante.setTipo(tipo);

            Comprobante saved = comprobanteRepo.save(comprobante);
            log.info("Comprobante generado exitosamente: ID={}, Tipo={}, Pedido={}",
                    saved.getId(), saved.getTipo(), pedidoId);

            return toDto(saved);

        } catch (Exception e) {
            log.error("Error generando comprobante tipo {} para pedido ID: {}",
                     tipo, pedidoId, e);
            throw e;
        }
    }

    @Override
    public ByteArrayInputStream generarExcel(Long comprobanteId) {
        log.info("Generando Excel para comprobante ID: {}", comprobanteId);

        try {
            Comprobante comprobante = comprobanteRepo.findById(comprobanteId)
                .orElseThrow(() -> new IllegalArgumentException("Comprobante no encontrado"));

            // Generar Excel
            ByteArrayInputStream excel = crearExcel(comprobante);

            log.info("Excel generado exitosamente para comprobante ID: {}", comprobanteId);
            return excel;

        } catch (Exception e) {
            log.error("Error generando Excel para comprobante ID: {}", comprobanteId, e);
            throw e;
        }
    }
}
```

## 🔍 Configuraciones Avanzadas

### 1. 📁 Logging a Archivos

```xml
<!-- Appender para archivos con rotación -->
<appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>logs/comoencasa.log</file>
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
        <fileNamePattern>logs/comoencasa.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
        <maxFileSize>10MB</maxFileSize>
        <maxHistory>30</maxHistory>
        <totalSizeCap>1GB</totalSizeCap>
    </rollingPolicy>
    <encoder>
        <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
</appender>

<!-- Appender específico para errores -->
<appender name="ERROR_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>logs/errors.log</file>
    <filter class="ch.qos.logback.classic.filter.LevelFilter">
        <level>ERROR</level>
        <onMatch>ACCEPT</onMatch>
        <onMismatch>DENY</onMismatch>
    </filter>
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
        <fileNamePattern>logs/errors.%d{yyyy-MM-dd}.log</fileNamePattern>
        <maxHistory>90</maxHistory>
    </rollingPolicy>
    <encoder>
        <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
</appender>
```

### 2. 📊 Logging de Auditoría

```xml
<!-- Appender para auditoría -->
<appender name="AUDIT_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>logs/audit.log</file>
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
        <fileNamePattern>logs/audit.%d{yyyy-MM-dd}.log</fileNamePattern>
        <maxHistory>365</maxHistory>
    </rollingPolicy>
    <encoder>
        <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} - %msg%n</pattern>
    </encoder>
</appender>

<!-- Logger específico para auditoría -->
<logger name="AUDIT" level="INFO" additivity="false">
    <appender-ref ref="AUDIT_FILE" />
</logger>
```

### 3. 🔐 Logging de Seguridad

```java
@Service
@Slf4j
public class SecurityAuditService {

    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");

    public void logLoginAttempt(String email, String ip, boolean success) {
        if (success) {
            auditLogger.info("LOGIN_SUCCESS: email={}, ip={}", email, ip);
        } else {
            auditLogger.warn("LOGIN_FAILURE: email={}, ip={}", email, ip);
        }
    }

    public void logPasswordChange(String email, String ip) {
        auditLogger.info("PASSWORD_CHANGE: email={}, ip={}", email, ip);
    }

    public void logAccountActivation(String email, String ip) {
        auditLogger.info("ACCOUNT_ACTIVATION: email={}, ip={}", email, ip);
    }

    public void logDataAccess(String operation, String resource, String user) {
        auditLogger.info("DATA_ACCESS: operation={}, resource={}, user={}",
                         operation, resource, user);
    }
}
```

## 🧪 Testing con Logback

### 1. 📝 Configuración de Tests

```xml
<!-- logback-test.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- Solo INFO y superior en tests -->
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
    </root>
</configuration>
```

### 2. 🔍 Testing de Logging

```java
@ExtendWith(MockitoExtension.class)
class LoggingTest {

    @Mock
    private Logger mockLogger;

    @Test
    void testLoggingBehavior() {
        // Given
        AuthController controller = new AuthController();

        // When
        controller.login(new LoginRequest("test@example.com", "password"));

        // Then
        verify(mockLogger).info("Intento de login para usuario: {}", "test@example.com");
    }
}
```

### 3. 📊 Captura de Logs en Tests

```java
@Test
void testLogOutput() {
    // Usar ListAppender para capturar logs
    Logger logger = (Logger) LoggerFactory.getLogger(AuthController.class);
    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);

    // Ejecutar operación
    authController.login(new LoginRequest("test@example.com", "password"));

    // Verificar logs
    List<ILoggingEvent> logsList = listAppender.list;
    assertThat(logsList).hasSize(1);
    assertThat(logsList.get(0).getMessage()).contains("Intento de login para usuario");
}
```

## 📊 Métricas y Monitoreo

### 1. 📈 Logging de Performance

```java
@Service
@Slf4j
public class PerformanceMonitoringService {

    public void monitorMethodExecution(String methodName, long executionTime) {
        if (executionTime > 1000) { // > 1 segundo
            log.warn("SLOW_METHOD: method={}, time={}ms", methodName, executionTime);
        } else {
            log.debug("METHOD_EXECUTION: method={}, time={}ms", methodName, executionTime);
        }
    }

    @Around("@annotation(Monitored)")
    public Object monitorExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            monitorMethodExecution(methodName, executionTime);
            return result;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("METHOD_ERROR: method={}, time={}ms, error={}",
                     methodName, executionTime, e.getMessage());
            throw e;
        }
    }
}
```

### 2. 📊 Estadísticas de Uso

```java
@Service
@Slf4j
public class UsageStatisticsService {

    private final AtomicLong requestCount = new AtomicLong(0);
    private final AtomicLong errorCount = new AtomicLong(0);

    @Scheduled(fixedRate = 60000) // Cada minuto
    public void logStatistics() {
        long requests = requestCount.getAndSet(0);
        long errors = errorCount.getAndSet(0);

        log.info("STATISTICS: requests={}, errors={}, error_rate={}%",
                requests, errors, errors > 0 ? (errors * 100 / requests) : 0);
    }

    public void incrementRequestCount() {
        requestCount.incrementAndGet();
    }

    public void incrementErrorCount() {
        errorCount.incrementAndGet();
    }
}
```

## 🎯 Mejores Prácticas Implementadas

### 1. 📝 Niveles de Log Apropiados

```java
public class BestPracticesExample {

    private static final Logger log = LoggerFactory.getLogger(BestPracticesExample.class);

    public void demonstrateLogLevels() {
        // ERROR: Para errores críticos
        log.error("Error crítico en el sistema: {}", errorMessage);

        // WARN: Para situaciones potencialmente problemáticas
        log.warn("Advertencia: configuración no óptima detectada");

        // INFO: Para información general importante
        log.info("Usuario {} ha iniciado sesión", username);

        // DEBUG: Para información detallada de debugging
        log.debug("Procesando objeto: {}", object);

        // TRACE: Para información muy detallada
        log.trace("Entrando al método processData()");
    }
}
```

### 2. 🔧 Logging Estructurado

```java
@Service
@Slf4j
public class StructuredLoggingService {

    public void logBusinessEvent(String eventType, String entityId,
                                String userId, Map<String, Object> context) {
        // Usar formato estructurado para facilitar análisis
        log.info("BUSINESS_EVENT: type={}, entity_id={}, user_id={}, context={}",
                eventType, entityId, userId, context);
    }

    public void logAPICall(String endpoint, String method, String userAgent,
                          long responseTime, int statusCode) {
        log.info("API_CALL: endpoint={}, method={}, user_agent={}, " +
                "response_time={}ms, status={}",
                endpoint, method, userAgent, responseTime, statusCode);
    }
}
```

### 3. 🛡️ Logging Seguro

```java
@Service
@Slf4j
public class SecureLoggingService {

    public void logSensitiveOperation(String operation, String userId,
                                     String sensitiveData) {
        // NO logear datos sensibles directamente
        String maskedData = maskSensitiveData(sensitiveData);

        log.info("SENSITIVE_OPERATION: operation={}, user_id={}, data_hash={}",
                operation, userId, maskedData);
    }

    private String maskSensitiveData(String data) {
        if (data == null || data.length() < 4) {
            return "***";
        }
        return data.substring(0, 2) + "***" + data.substring(data.length() - 2);
    }

    public void logSecurityEvent(String eventType, String source,
                                String additionalInfo) {
        log.warn("SECURITY_EVENT: type={}, source={}, info={}",
                eventType, source, additionalInfo);
    }
}
```

## 📈 Beneficios Alcanzados

### 1. 🔍 Debugging Eficiente

- **Trazabilidad completa**: Seguimiento de operaciones de inicio a fin
- **Contexto detallado**: Información suficiente para reproduir problemas
- **Timestamps precisos**: Análisis temporal de eventos

### 2. 🛡️ Seguridad Mejorada

- **Auditoría completa**: Registro de todas las operaciones sensibles
- **Detección de amenazas**: Identificación de patrones sospechosos
- **Cumplimiento normativo**: Logs para auditorías de seguridad

### 3. 📊 Monitoreo Operacional

- **Métricas de rendimiento**: Tiempos de respuesta y throughput
- **Detección proactiva**: Alertas automáticas por anomalías
- **Análisis de tendencias**: Datos históricos para optimización

## 🔧 Configuración por Entorno

### 1. 🏗️ Desarrollo (logback-spring.xml)

```xml
<springProfile name="dev">
    <root level="DEBUG">
        <appender-ref ref="CONSOLE" />
    </root>
</springProfile>
```

### 2. 🔧 Testing (logback-test.xml)

```xml
<springProfile name="test">
    <root level="WARN">
        <appender-ref ref="CONSOLE" />
    </root>
</springProfile>
```

### 3. 🚀 Producción

```xml
<springProfile name="prod">
    <root level="INFO">
        <appender-ref ref="FILE" />
        <appender-ref ref="ERROR_FILE" />
    </root>
</springProfile>
```

## 🔮 Roadmap Futuro

### 1. 📊 Integración con Sistemas Externos

- **ELK Stack**: Elasticsearch, Logstash, Kibana
- **Splunk**: Análisis avanzado de logs
- **New Relic**: Monitoreo de aplicaciones

### 2. 🚨 Alertas Inteligentes

- **Slack notifications**: Alertas automáticas por errores críticos
- **Email alerts**: Notificaciones de eventos importantes
- **SMS alerts**: Alertas críticas para administradores

### 3. 🔬 Análisis Avanzado

- **Machine Learning**: Detección de anomalías
- **Predictive analytics**: Predicción de problemas
- **Real-time dashboards**: Visualización en tiempo real

---

## 📚 Recursos Adicionales

- **[Logback Documentation](https://logback.qos.ch/documentation.html)**
- **[SLF4J Manual](https://www.slf4j.org/manual.html)**
- **[Spring Boot Logging](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.logging)**
- **[Logback Configuration](https://logback.qos.ch/manual/configuration.html)**

---

_Esta documentación forma parte del proyecto "Como en Casa" - Sistema Web para Gestión de Pedidos y Clientes_
