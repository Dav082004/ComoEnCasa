# 📊 Sistema de Logging y Monitoreo - Como en Casa

## 🎯 Descripción General

Este proyecto incluye un **sistema completo de logging y monitoreo** implementado para la aplicación "Como en Casa". El sistema proporciona trazabilidad completa, métricas de rendimiento, auditoría de seguridad y herramientas de análisis.

## 🏗️ Arquitectura del Sistema de Logging

### 📁 Estructura de Archivos de Log

```
logs/
├── comoencasa-app.log          # 📄 Log principal de la aplicación
├── comoencasa-error.log        # ❌ Log específico de errores
├── comoencasa-audit.log        # 🔐 Log de auditoría y seguridad
├── comoencasa-http.log         # 🌐 Log de requests HTTP
├── comoencasa-database.log     # 💾 Log de operaciones de BD
└── archived/                   # 📦 Logs archivados (comprimidos)
    ├── comoencasa-app-2024-07-16.1.log.gz
    ├── comoencasa-error-2024-07-16.log.gz
    └── ...
```

### 🔧 Componentes Implementados

#### 1. **Configuración de Logback** (`logback-spring.xml`)

- ✅ Múltiples appenders especializados
- ✅ Rotación automática de archivos
- ✅ Compresión de logs antiguos
- ✅ Separación por tipo de evento
- ✅ Formato colorizado para consola

#### 2. **Interceptor HTTP** (`HttpLoggingInterceptor.java`)

- ✅ Captura automática de todos los requests
- ✅ Métricas de tiempo de respuesta
- ✅ Identificación de IP real del cliente
- ✅ Detección de User-Agent
- ✅ Filtrado de parámetros sensibles
- ✅ Alertas automáticas para requests lentos

#### 3. **Aspect de Servicios** (`ServiceLoggingAspect.java`)

- ✅ Logging automático de métodos de servicio
- ✅ Métricas de duración de operaciones
- ✅ Captura de excepciones con contexto
- ✅ Auditoría de operaciones críticas
- ✅ Sanitización de datos sensibles

#### 4. **Servicio de Monitoreo** (`SystemMonitoringService.java`)

- ✅ Métricas de sistema cada 5 minutos
- ✅ Health checks cada minuto
- ✅ Alertas automáticas por recursos
- ✅ Reporte diario de estadísticas
- ✅ Mantenimiento automático

## 🚀 Scripts de Monitoreo

### 📊 Sistema Principal (`scripts/sistema-monitoreo.bat`)

**Script maestro con interfaz completa:**

```bash
# Ejecutar sistema de monitoreo
./scripts/sistema-monitoreo.bat
```

**Funcionalidades:**

- 🔍 Monitor en tiempo real
- 📊 Dashboard de métricas
- 📈 Análisis de performance
- 🛠️ Herramientas de mantenimiento
- 📋 Generación de reportes

### 🔍 Monitor Específico (`scripts/monitor-logs.bat`)

```bash
# Monitor especializado de logs
./scripts/monitor-logs.bat
```

### 📊 Análisis de Performance (`scripts/analyze-performance.bat`)

```bash
# Análisis detallado de rendimiento
./scripts/analyze-performance.bat
```

## 📈 Métricas Implementadas

### 🎯 Métricas de Aplicación

- **Requests por minuto/hora/día**
- **Tiempo promedio de respuesta**
- **Tasa de errores**
- **Requests por endpoint**
- **Distribución de códigos de estado HTTP**

### 💾 Métricas de Sistema

- **Uso de memoria JVM (heap/non-heap)**
- **Carga de CPU**
- **Threads activos**
- **Conexiones de BD activas**
- **Espacio en disco disponible**

### 🔐 Métricas de Seguridad

- **Intentos de login**
- **Fallos de autenticación**
- **Accesos por IP**
- **Operaciones sensibles**
- **Cambios en configuración**

## 🚨 Sistema de Alertas

### ⚠️ Alertas Automáticas

El sistema genera alertas automáticas cuando:

- **Memoria > 85%**: `🚨 ALERTA MEMORIA: 87.5% usado`
- **CPU > 80%**: `🔥 ALERTA CPU: 85.2% uso`
- **Response > 2s**: `🐌 ALERTA RENDIMIENTO: 2500ms promedio`
- **Errores críticos**: `💥 CONTROLLER ERROR: PaymentController.processPayment() falló`

### 📧 Integración de Notificaciones

_Preparado para integrar con:_

- Email (SMTP configurado)
- Slack webhooks
- Discord webhooks
- Teams notifications

## 🔧 Configuración

### 📝 Niveles de Log por Ambiente

#### Desarrollo (`application.properties`)

```properties
logging.level.com.comoencasa_backend=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.springframework.security=DEBUG
```

#### Producción (`application-prod.properties`)

```properties
logging.level.com.comoencasa_backend=INFO
logging.level.org.hibernate.SQL=WARN
logging.level.org.springframework.security=INFO
```

### ⚙️ Rotación de Archivos

```xml
<!-- Configuración en logback-spring.xml -->
<rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
    <fileNamePattern>logs/archived/comoencasa-app-%d{yyyy-MM-dd}.%i.log.gz</fileNamePattern>
    <maxFileSize>10MB</maxFileSize>
    <maxHistory>30</maxHistory>
    <totalSizeCap>500MB</totalSizeCap>
</rollingPolicy>
```

## 📊 Ejemplos de Logs

### 🌐 Log HTTP Request

```
2024-07-16 14:30:25.123 [http-nio-8081-exec-1] INFO  HttpLoggingInterceptor - 🌐 HTTP REQUEST: POST /api/pedidos | IP: 192.168.1.100 | User: juan.perez | Session: 1a2b3c4d
2024-07-16 14:30:25.456 [http-nio-8081-exec-1] INFO  HttpLoggingInterceptor - ✅ HTTP RESPONSE: POST /api/pedidos | Status: 201 | Duration: 333ms | User: juan.perez
```

### 🔧 Log de Servicio

```
2024-07-16 14:30:25.200 [http-nio-8081-exec-1] DEBUG ServiceLoggingAspect - 🔧 SERVICIO INICIADO: PedidoServiceImpl.crearPedido() con parámetros: [PedidoDTO:Cliente Juan, 3 productos]
2024-07-16 14:30:25.420 [http-nio-8081-exec-1] DEBUG ServiceLoggingAspect - ✅ SERVICIO COMPLETADO: PedidoServiceImpl.crearPedido() en 220ms
```

### 🔐 Log de Auditoría

```
AUDIT|1721136625123|COMPROBANTE_GENERATED|id=156|tipo=Factura|serie=001|numero=00000156|pedidoId=89|monto=150.50|duration=285ms
AUDIT|1721136625456|SERVICE_SUCCESS|PedidoServiceImpl.crearPedido|220ms|PedidoDTO
```

### ❌ Log de Error

```
2024-07-16 14:30:25.789 [http-nio-8081-exec-2] ERROR ComprobanteServiceImpl - 💥 Error generando comprobante para pedido 123 en 1250ms: Connection timeout
java.sql.SQLException: Connection timeout
    at com.mysql.cj.jdbc.ConnectionImpl.connectOneTryOnly(ConnectionImpl.java:1086)
    ...
```

## 📊 Dashboard de Métricas

### 🎯 Métricas en Tiempo Real

```
📊 MÉTRICAS DEL SISTEMA:
   🔄 Requests: 1,247 | Errores: 3 | Tiempo promedio: 245ms
   💾 Memoria: 68.5% usado (512 MB libres de 1,024 MB)
   🔥 CPU: 25.3% | ⏱️ Uptime: 1,440min
```

### 📈 Análisis de Performance

```
📊 ANÁLISIS DE PERFORMANCE:
  ✅ Tiempo promedio respuesta: 245ms
  ✅ Requests por minuto: 85
  ⚠️ Requests lentos detectados: 12 (>1s)
  ❌ Errores en última hora: 3
```

## 🛠️ Comandos Útiles

### 🔍 Ver Logs en Tiempo Real

```bash
# Logs de aplicación
powershell Get-Content -Path "logs\comoencasa-app.log" -Wait -Tail 50

# Solo errores
powershell Get-Content -Path "logs\comoencasa-error.log" -Wait

# Filtrar por palabra clave
powershell Get-Content -Path "logs\comoencasa-app.log" | Where-Object { $_ -match "ERROR|WARN" }
```

### 📊 Generar Reportes

```bash
# Reporte completo
./scripts/analyze-performance.bat

# Solo errores
powershell Get-Content -Path "logs\comoencasa-error.log" | Out-File error-summary.txt
```

### 🧹 Mantenimiento

```bash
# Limpiar logs antiguos (>7 días)
forfiles /p logs\archived /m *.log* /d -7 /c "cmd /c del @path"

# Backup completo
powershell Compress-Archive -Path "logs\*" -DestinationPath "backup-logs-$(Get-Date -f yyyy-MM-dd).zip"
```

## 🚀 Roadmap Futuro

### 📧 Notificaciones

- [ ] Integración con email para alertas críticas
- [ ] Webhooks para Slack/Discord
- [ ] SMS para emergencias

### 📊 Analytics Avanzados

- [ ] Dashboard web en tiempo real
- [ ] Métricas de negocio (ventas, usuarios)
- [ ] Predicción de fallos

### 🔐 Seguridad Avanzada

- [ ] Detección de anomalías
- [ ] Análisis de patrones de ataque
- [ ] Integración con SIEM

### ☁️ Cloud Integration

- [ ] Envío a ElasticSearch/Kibana
- [ ] Integración con AWS CloudWatch
- [ ] Logs centralizados multi-instancia

## 📞 Soporte

Para soporte técnico o consultas sobre el sistema de logging:

- 📧 Email: comoencasabakerype@gmail.com
- 📱 WhatsApp: +51 972-166-643
- 🌐 Documentación: Ver archivos en `/docs`

---

**✨ Sistema desarrollado para proporcionar observabilidad completa de la aplicación Como en Casa ✨**
