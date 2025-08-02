# 🏗️ Arquitectura Técnica - Como en Casa

## Resumen Ejecutivo

Sistema web full-stack desarrollado con **Spring Boot 3.4.5** y **React 19.1.0**, implementando patrones de diseño empresariales y buenas prácticas de desarrollo. El proyecto demuestra competencias en desarrollo full-stack, arquitectura de microservicios, testing automatizado y despliegue con contenedores.

## Stack Tecnológico

### Backend

- **Framework:** Spring Boot 3.4.5 (Java 21)
- **Seguridad:** Spring Security con BCrypt
- **Base de Datos:** MySQL 8.0 + Spring Data JPA
- **Cache:** Google Guava Cache
- **Documentos:** Apache POI (Excel) + iText PDF
- **Testing:** JUnit 5, Mockito, Testcontainers

### Frontend

- **Framework:** React 19.1.0 con Hooks
- **UI Framework:** React Bootstrap 5
- **HTTP Client:** Axios con interceptores
- **Routing:** React Router DOM 7
- **Charts:** Recharts para visualización

### DevOps

- **Contenedores:** Docker + Docker Compose
- **Build:** Maven Wrapper + npm
- **CI/CD:** GitHub Actions ready

## Patrones de Diseño Implementados

### 1. MVC (Model-View-Controller)

```
Frontend (View) ←→ REST API (Controller) ←→ Service Layer ←→ Repository (Model)
```

### 2. DAO (Data Access Object)

- Repositorios Spring Data JPA
- Cache Guava para operaciones frecuentes
- Transacciones declarativas

### 3. Builder Pattern

- DTOs con Lombok @Builder
- Construcción fluida de entidades complejas

### 4. Factory Pattern

- Configuración por perfiles (dev, test, prod, docker)
- Beans condicionales según entorno

### 5. SOLID Principles

- **S:** Servicios con responsabilidad única
- **O:** Extensibilidad mediante interfaces
- **L:** Herencia coherente en jerarquías
- **I:** Interfaces segregadas por dominio
- **D:** Inyección de dependencias con Spring

## Arquitectura de Capas

```
┌─────────────────────────────────────────┐
│           FRONTEND (React)              │
│  ┌─────────────┐ ┌─────────────────────┐│
│  │ Components  │ │ Services (Axios)    ││
│  └─────────────┘ └─────────────────────┘│
└─────────────────┬───────────────────────┘
                  │ HTTP/REST
┌─────────────────▼───────────────────────┐
│           BACKEND (Spring Boot)         │
│  ┌─────────────┐ ┌─────────────────────┐│
│  │ Controllers │ │ Security Config     ││
│  └─────────────┘ └─────────────────────┘│
│  ┌─────────────┐ ┌─────────────────────┐│
│  │ Services    │ │ DTOs                ││
│  └─────────────┘ └─────────────────────┘│
│  ┌─────────────┐ ┌─────────────────────┐│
│  │ Repositories│ │ Entities (JPA)      ││
│  └─────────────┘ └─────────────────────┘│
└─────────────────┬───────────────────────┘
                  │ JDBC
┌─────────────────▼───────────────────────┐
│            DATABASE (MySQL)             │
└─────────────────────────────────────────┘
```

## APIs REST Principales

| Método | Endpoint                 | Descripción                 |
| ------ | ------------------------ | --------------------------- |
| GET    | `/api/productos`         | Lista productos disponibles |
| POST   | `/api/carrito/agregar`   | Agrega items al carrito     |
| POST   | `/api/checkout`          | Procesa pedido              |
| GET    | `/api/reportes/ventas`   | Genera reporte Excel        |
| POST   | `/api/usuarios/registro` | Registro de usuarios        |
| GET    | `/actuator/health`       | Health check                |

## Base de Datos

### Entidades Principales

- **Producto:** Gestión de inventario
- **Usuario:** Autenticación y perfiles
- **Pedido:** Transacciones de venta
- **DetallePedido:** Items del pedido
- **Categoria:** Clasificación de productos

### Relaciones

```sql
Usuario (1) ←→ (N) Pedido
Pedido (1) ←→ (N) DetallePedido
Producto (1) ←→ (N) DetallePedido
Categoria (1) ←→ (N) Producto
```

## Características Técnicas Destacadas

### 1. Sistema de Cache Inteligente

```java
@Cacheable(value = "carritos", key = "#usuarioId")
public CarritoDTO obtenerCarrito(Long usuarioId) {
    // Implementación con Google Guava
}
```

### 2. Generación de Reportes

- **PDF:** Facturas con iText PDF
- **Excel:** Reportes de ventas con Apache POI
- **Gráficos:** Dashboard con Recharts

### 3. Seguridad Robusta

- CORS configurado para múltiples orígenes
- Encriptación BCrypt para contraseñas
- Validación de entrada con Apache Commons

### 4. Testing Comprehensivo

- **348+ tests** unitarios e integración
- **85%+ cobertura** de código con JaCoCo
- Tests de contratos con TestContainers

## Configuración por Entornos

| Perfil   | Base de Datos    | Logging | CORS               |
| -------- | ---------------- | ------- | ------------------ |
| `dev`    | MySQL local      | DEBUG   | localhost:3000     |
| `test`   | H2 en memoria    | WARN    | Deshabilitado      |
| `docker` | MySQL contenedor | INFO    | frontend:3000      |
| `prod`   | MySQL remoto     | ERROR   | Dominio específico |

## Métricas del Proyecto

- **📋 Líneas de Código:** 15,000+
- **🧪 Tests:** 348+ unitarios e integración
- **📊 Cobertura:** 85%+
- **🏗️ Patrones:** 6 patrones implementados
- **📚 Componentes React:** 25+
- **🔌 APIs REST:** 30+ endpoints
- **⚡ Performance:** < 200ms respuesta promedio

## Despliegue y DevOps

### Docker Compose

```yaml
services:
  backend:
    build: ./backend
    ports: ["8080:8080"]
    depends_on: [db]

  frontend:
    build: ./frontend
    ports: ["3000:3000"]

  db:
    image: mysql:8.0
    volumes: [db_data:/var/lib/mysql]
```

### Scripts de Automatización

- `install.sh` - Instalación automática
- `docker-compose.yml` - Orquestación de contenedores
- Maven Wrapper - Build reproducible

## Consideraciones de Escalabilidad

1. **Horizontal:** Load balancer + múltiples instancias
2. **Cache:** Redis para cache distribuido
3. **Base de Datos:** Read replicas + particionado
4. **CDN:** Assets estáticos optimizados
5. **Monitoring:** Actuator + Micrometer metrics

---

**🎯 Este proyecto demuestra competencias full-stack enterprise con enfoque en calidad, escalabilidad y mantenibilidad.**
