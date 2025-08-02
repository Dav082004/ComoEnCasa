# 🍰 Como en Casa - Sistema de Gestión de Pedidos

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19.1.0-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)](https://reactjs.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-005C84?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-Supported-0db7ed?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)

## 📖 Descripción del Proyecto

**Como en Casa** es un sistema web completo desarrollado para mejorar la gestión de pedidos y atención al cliente de una panadería artesanal familiar. La aplicación digitaliza procesos clave como toma de pedidos, gestión de productos y servicio al cliente, proporcionando una solución integral y escalable.

### 🎯 Características Principales

- **🛒 Gestión de Carrito de Compras** - Sistema de carrito en tiempo real con persistencia
- **📦 Gestión de Inventario** - Control de stock y disponibilidad de productos
- **👥 Gestión de Usuarios** - Registro, autenticación y perfiles de usuario
- **📄 Generación de Reportes** - Reportes en PDF y Excel con datos de ventas
- **💳 Integración de Pagos** - PayPal SDK para procesamiento de pagos
- **📧 Sistema de Notificaciones** - Envío de correos de confirmación
- **📊 Dashboard Analítico** - Visualización de métricas y estadísticas

## 🏗️ Arquitectura

### Stack Tecnológico

**Backend:**

- **Spring Boot 3.4.5** - Framework principal de Java
- **Spring Security** - Autenticación y autorización
- **Spring Data JPA** - Persistencia de datos y ORM
- **MySQL 8.0** - Base de datos relacional
- **Apache POI** - Generación de documentos Excel
- **iText PDF** - Generación de documentos PDF
- **Google Guava** - Cachéo de alto rendimiento

**Frontend:**

- **React 19.1.0** - Biblioteca de interfaz de usuario
- **React Bootstrap** - Componentes de interfaz
- **Axios** - Cliente HTTP para comunicación con API
- **Recharts** - Gráficos y visualización de datos
- **React Router** - Navegación y enrutamiento

**DevOps & Testing:**

- **Docker & Docker Compose** - Contenedorización
- **JUnit 5 & Mockito** - Testing unitario e integración
- **H2 Database** - Base de datos en memoria para testing
- **JaCoCo** - Análisis de cobertura de código

### Patrones de Diseño Implementados

- **🏗️ MVC (Model-View-Controller)** - Separación clara de responsabilidades
- **📊 DAO (Data Access Object)** - Abstracción de acceso a datos
- **🏭 Builder & Factory** - Creación flexible de objetos
- **💎 SOLID Principles** - Principios de diseño orientado a objetos
- **🔐 Configuration & Security** - Configuración por entornos y seguridad

## 🚀 Instalación y Configuración

### Prerequisitos

- Java 21 o superior
- Node.js 18+ y npm
- MySQL 8.0
- Docker (opcional)

### Configuración con Docker

1. **Clonar el repositorio:**

```bash
git clone https://github.com/tuusuario/como-en-casa.git
cd como-en-casa
```

2. **Ejecutar con Docker Compose:**

```bash
docker-compose up -d
```

3. **Acceder a la aplicación:**

- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- Base de datos: localhost:3306

### Configuración Manual

#### Backend

```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```

#### Frontend

```bash
cd frontend
npm install
npm start
```

#### Base de Datos

```sql
CREATE DATABASE comoencasa_db;
-- Ejecutar schema.sql para crear las tablas
```

## 📁 Estructura del Proyecto

```
como-en-casa/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/comoencasa_backend/
│   │   │   │   ├── config/           # Configuraciones de Spring
│   │   │   │   ├── controller/       # Controladores REST
│   │   │   │   ├── model/           # Entidades JPA
│   │   │   │   ├── repository/      # Repositorios Spring Data
│   │   │   │   ├── service/         # Lógica de negocio
│   │   │   │   └── dto/             # Data Transfer Objects
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/                    # Tests unitarios e integración
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/              # Componentes React
│   │   ├── services/               # Servicios de API
│   │   ├── config/                 # Configuraciones
│   │   └── App.js
│   ├── Dockerfile
│   └── package.json
├── docs/                           # Documentación técnica
├── docker-compose.yml
├── schema.sql                      # Schema de base de datos
└── README.md
```

## 🧪 Testing

El proyecto incluye una suite completa de tests:

- **Tests Unitarios:** JUnit 5 + Mockito
- **Tests de Integración:** Spring Boot Test + Testcontainers
- **Cobertura de Código:** JaCoCo

```bash
# Ejecutar tests
./mvnw test

# Generar reporte de cobertura
./mvnw jacoco:report
```

## 📊 Funcionalidades Destacadas

### Sistema de Carrito Inteligente

- Cachéo con Google Guava para alto rendimiento
- Gestión de stock en tiempo real
- Persistencia automática

### Generación de Reportes

- **PDF:** Facturas y boletas profesionales
- **Excel:** Reportes de ventas detallados
- **Gráficos:** Visualización con Recharts

### Seguridad Robusta

- Encriptación BCrypt para contraseñas
- Configuración CORS flexible
- Validación de entrada con Apache Commons

## 🔧 Variables de Entorno

```properties
# Base de datos
DB_USERNAME=tu_usuario
DB_PASSWORD=tu_password

# Email
EMAIL_USERNAME=tu_email@gmail.com
EMAIL_PASSWORD=tu_app_password

# PayPal
PAYPAL_CLIENT_ID=tu_client_id
PAYPAL_CLIENT_SECRET=tu_client_secret
PAYPAL_ENVIRONMENT=sandbox

# Frontend URL
FRONTEND_URL=http://localhost:3000
```

## 📈 Métricas del Proyecto

- **📋 Líneas de Código:** 15,000+ líneas
- **🧪 Tests:** 348+ tests unitarios e integración
- **📊 Cobertura:** 85%+ cobertura de código
- **🏗️ Patrones:** 6 patrones de diseño implementados
- **📚 Componentes:** 25+ componentes React
- **🔌 APIs:** 30+ endpoints REST

## 🚀 Despliegue

### Producción

1. Configurar variables de entorno de producción
2. Construir imágenes Docker optimizadas
3. Desplegar con orquestadores (Kubernetes, Docker Swarm)

### Desarrollo

- Perfiles de Spring Boot para diferentes entornos
- Hot reload para desarrollo frontend/backend
- Base de datos H2 para testing

## 📝 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo [LICENSE](LICENSE) para más detalles.

## 🤝 Contribución

¿Interesado en contribuir? ¡Excelente!

1. Fork el proyecto
2. Crea tu feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la branch (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📞 Contacto

**Desarrollador:** [Tu Nombre]

- 📧 Email: tu.email@ejemplo.com
- 💼 LinkedIn: [Tu LinkedIn]
- 🐙 GitHub: [Tu GitHub]

---

⭐ **¡Dale una estrella a este proyecto si te resultó útil!**
