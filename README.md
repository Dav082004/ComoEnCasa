# 🍰 Web System for Order and Customer Management - Como en Casa

## 📖 Project Description

This project consists of developing a **web system** to improve order and customer management for _Como en Casa_, an artisan family bakery. The objective is to digitize key processes such as order taking, product management, and customer service.

---

## 🛠️ Technology Stack

### Frontend

- **React** ⚛️ - Modern UI library for building interactive user interfaces
- **Bootstrap** 🎨 - CSS framework for responsive design
- **Axios** 📡 - HTTP client for API communication
- **React Router** 🚦 - Navigation and routing for single-page applications

### Backend

- **Spring Boot** 🍃 - Java framework for rapid application development
- **Spring Boot JPA** 🗄️ - Data persistence and ORM
- **Spring Security** 🔐 - Authentication and authorization

### Database

- **MySQL** 🐬 - Relational database management system
- **XAMPP** 🔧 - Local development environment

### Architecture & Design Patterns

- **MVC (Model-View-Controller)** 🏗️ - Architectural pattern for separation of concerns
  - 📖 **[Implementación de MVC](docs/PATRON-MVC.md)** - Documentación detallada de implementación
- **TDD (Test-Driven Development)** 🧪 - Development methodology with **47 tests implemented**
  - 📖 **[Guía Completa de TDD](docs/PATRON-TDD.md)** - Documentación detallada de implementación
  - ✅ **100% tests passing** - Cobertura de código con JaCoCo
  - 🚀 **Scripts automatizados** - Ejecución de tests con un comando
- **DAO (Data Access Object)** 📊 - Data access pattern
  - 📖 **[Implementación de DAO](docs/PATRON-DAO.md)** - Documentación detallada de implementación
- **SOLID Principles** 💎 - Object-oriented design principles
  - 📖 **[Implementación de SOLID](docs/PATRON-SOLID.md)** - Documentación detallada de implementación
- **Builder & Factory Patterns** 🏭 - Creational design patterns
  - 📖 **[Implementación de Builder/Factory](docs/PATRON-BUILDER-FACTORY.md)** - Documentación detallada de implementación
- **Configuration & Security Patterns** ⚙️ - Configuration and security design patterns
  - 📖 **[Patrones de Configuración y Seguridad](docs/PATRON-CONFIGURACION-SEGURIDAD.md)** - Documentación detallada de implementación

### Testing & Quality Assurance 🧪

- **JUnit 5** - Framework principal de testing
- **Mockito** - Mocking y stubbing para tests unitarios
- **Spring Boot Test** - Testing para aplicaciones Spring
- **JaCoCo** - Análisis de cobertura de código
- **H2 Database** - Base de datos en memoria para tests
- **AssertJ** - Assertions fluidas y legibles

### Support Libraries

- **Google Guava** 📚 - Core libraries for Java (Cache implementation for CarritoDAO)
  - 📖 **[Implementación de Builder/Factory](docs/PATRON-BUILDER-FACTORY.md)** - Incluye uso de Google Guava Cache
- **Apache POI** 📄 - Library for Microsoft documents (Excel generation for reports)
  - 📖 **[Implementación de Apache POI](docs/APACHE-POI-ES.md)** - Documentación detallada de implementación
- **Apache Commons** 🔧 - Reusable Java components
  - **Commons Validator** - Email validation (RFC 5322 compliant)
  - **Commons Lang3** - String utilities and null-safe operations
  - **Commons Text** - Advanced text processing
  - 📖 **[Implementación de Apache Commons](docs/APACHE-COMMONS-ES.md)** - Documentación detallada de implementación
- **Logback** 📝 - Enterprise logging framework
  - 📖 **[Implementación de Logback](docs/LOGBACK-ES.md)** - Documentación detallada de implementación

### Security Features 🛡️

- Data encryption and secure authentication
- Protection against common web vulnerabilities
- Secure session management

---

## 🏢 About the Company

**Como en Casa** is a home bakery with over 10 years of experience, focused on providing products with traditional flavor, close attention, and complete personalization in each order.

---

## 🎯 Mission and Vision

- **Mission:** 🧁 To offer artisan desserts prepared with fresh ingredients and love from home.
- **Vision:** ⭐ To be a family business recognized for its sweetness, quality, and warm attention.

---

## ⚠️ Problem Identified

Currently, order management is manual (via social media and WhatsApp), which generates errors, delays, and limits business growth. There is no efficient or automated digital channel.

---

## 💡 Proposed Solution

Development of a **web system** that will allow:

- 👤 User registration and secure authentication
- 🤖 Automated management of personalized orders
- 💳 Integrated payment gateway
- 📊 Administrative panel with inventory control, sales, and reports
- 📱 Experience adapted for mobile and desktop

---

## ⚙️ Functional Requirements

| Code | Requirement                                       |
| ---- | ------------------------------------------------- |
| RF1  | 👤 User registration and login                    |
| RF2  | 📝 Personalized order form                        |
| RF3  | 🧾 Receipt/invoice generation and sending         |
| RF4  | 🍰 Product management (cakes, cupcakes, etc.)     |
| RF5  | 🔍 Search and filtering by category, flavor, etc. |

---

## 🚀 Non-Functional Requirements

- 🔒 Data security (encryption)
- 📱 Compatibility with multiple devices
- 🎨 User-friendly and intuitive interface
- ⏰ 24/7 availability
- 📈 Guaranteed scalability and performance

---

## 📋 Lean Canvas

Strategic model used to visualize the value proposition, customer segments, competitive advantages, among others.

---

## 🏗️ Technical Structure

- **📊 Layer Diagram**: Organization of backend, frontend and services.
  [View Diagram](docs/imgRepo/Diagrama%20de%20capas.png)
- **🗃️ ER and Class Diagrams**: Physical modeling of the database and system structures.
  [View ER Diagram](docs/imgRepo/DiagramaER.png) | [View Class Diagram](docs/imgRepo/Diagrama%20de%20clases.png)
- **📅 WBS and Gantt Chart**: Project planning and breakdown
- **🔄 Process Diagrams**: Flow before and after implementation.
  [View Process Diagram](docs/imgRepo/Procesosfinal.png)

---

## 🎨 Mockups

Visual designs of the proposed system created in Balsamiq:

- 🖥️ [Option 1](https://balsamiq.cloud/sagiann/p75tct)
- 🖥️ [Option 2](https://balsamiq.cloud/sagiann/pyg1j4x)
- 🖥️ [Option 3](https://balsamiq.cloud/suwyr74/pb0usuu)

---

## 👥 Development Team

- Correa Acosta, Benjamin Emanuel
- Contreras Palacios, David Angel
- Barboza Ataco, Mijhael Hamed
- Meléndez Torre, José Martín
- Llacctas Pereyra, Marco A.

---

## 🧪 Development & Testing

### Quick Start para Desarrollo TDD

```powershell
# Clonar el repositorio
git clone [tu-repositorio]
cd ComoEnCasa

# Ejecutar tests TDD (Backend)
cd backend
.\run-tdd-coverage.bat

# Desarrollo Frontend
cd ../frontend
npm install
npm start
```

### Comandos de Testing

```powershell
# Ejecutar todos los tests
mvn test

# Tests con cobertura de código
mvn clean test jacoco:report

# Abrir reporte de cobertura
start target\site\jacoco\index.html

# Test específico
mvn test -Dtest="ProductoServiceTDDTest"
```

### Documentación TDD

Para una guía completa sobre la implementación TDD del proyecto:

📖 **[Ver Guía Completa de TDD](docs/TDD-GUIDE-ES.md)**

La guía incluye:

- 🔄 Ciclo Red-Green-Refactor explicado con ejemplos
- 🏗️ Arquitectura de tests en capas
- 🛠️ Configuración de herramientas (JaCoCo, JUnit 5, Mockito)
- 🚀 Scripts de automatización
- 📊 Análisis de cobertura de código
- 🎯 Mejores prácticas y convenciones
- 🔧 Troubleshooting para problemas comunes
