# 🍰 Web System for Order and Customer Management - Como en Casa

---

## 📖 Project Description

This project consists of developing a **web system** to improve order and customer management for _Como en Casa_, an artisan family bakery. The objective is to digitize key processes such as order taking, product management, and customer service.

---

## 🛠️ Technology Stack

### Frontend

- **React** ⚛️ - Modern UI library for building interactive user interfaces
- **Bootstrap** 🎨 - CSS framework for responsive design
- **React Bootstrap** 🎨 - Bootstrap components for React
- **React Bootstrap Icons** 🎨 - Icon library for React Bootstrap
- **Axios** 📡 - HTTP client for API communication
- **React Router DOM** 🚦 - Navigation and routing for single-page applications
- **React Icons** 🎨 - Popular icon library for React
- **React Toastify** 🔔 - Notification library for React
- **PayPal React SDK** 💳 - PayPal integration for React applications
- **Recharts** 📊 - Charting library for React (reports and analytics)
- **Date-fns** 📅 - Modern JavaScript date utility library
- **File-saver** 💾 - Library for saving files on the client-side

### Backend

- **Spring Boot** 🍃 - Java framework for rapid application development
- **Spring Boot JPA** 🗄️ - Data persistence and ORM
- **Spring Security** 🔐 - Authentication and authorization
- **Spring Boot Mail** 📧 - Email service integration
- **Lombok** 🔧 - Java annotation library for reducing boilerplate code
- **iText PDF** 📄 - PDF generation library for Java
- **Apache POI** 📊 - Library for Microsoft Office documents (Excel generation)
- **Apache Commons Validator** ✅ - Input validation library
- **Apache Commons Lang3** 🔧 - Java utility library for common operations
- **Apache Commons Text** 📝 - Text processing utilities
- **Google Guava** 🗂️ - Core libraries for Java (caching and data structures)

### Database

- **MySQL** 🐬 - Relational database management system
- **XAMPP** 🔧 - Local development environment

### Testing & Quality Assurance

- **JUnit 5** 🧪 - Framework for unit testing
- **Mockito** 🎭 - Mocking framework for unit tests
- **Spring Boot Test** 🔬 - Testing utilities for Spring Boot applications
- **H2 Database** 💾 - In-memory database for testing
- **AssertJ** ✅ - Fluent assertions for testing
- **Testcontainers** 🐳 - Integration testing with Docker containers
- **WireMock** 🔌 - Mock HTTP services for testing
- **JaCoCo** 📈 - Code coverage analysis tool

### Architecture & Design Patterns

- **MVC (Model-View-Controller)** 🏗️ - Architectural pattern for separation of concerns
  - 📖 **[MVC Implementation](docs//Patrones/PATRON-MVC.md)** - Complete implementation documentation
  - 🎯 **Frontend-Backend separation** - React frontend consuming REST API
  - 📊 **Clear layer definition** - Models, Views, Controllers, Services, Repositories
- **DAO (Data Access Object)** 📊 - Data access abstraction pattern
  - 📖 **[DAO Implementation](docs/Patrones/PATRON-DAO.md)** - Detailed implementation documentation
  - 🗂️ **Google Guava Cache** - High-performance caching for cart operations
  - 💾 **Repository pattern** - Spring Data JPA integration
- **SOLID Principles** 💎 - Object-oriented design principles
  - 📖 **[SOLID Implementation](docs//Patrones/PATRON-SOLID.md)** - Detailed implementation documentation
  - 🎯 **Single Responsibility** - Each class has one clear purpose
  - 🔧 **Dependency Injection** - Spring Boot IoC container
- **Builder & Factory Patterns** 🏭 - Creational design patterns
  - 📖 **[Builder/Factory Implementation](docs//Patrones/PATRON-BUILDER-FACTORY.md)** - Detailed implementation documentation
  - 🛠️ **Lombok Integration** - Automatic builder generation
  - 🏗️ **DTO Construction** - Clean object creation patterns
- **Configuration & Security Patterns** ⚙️ - Configuration and security design patterns
  - 📖 **[Configuration & Security Patterns](docs//Patrones/PATRON-CONFIGURACION-SEGURIDAD.md)** - Detailed implementation documentation
  - 🔐 **Spring Security** - Authentication and authorization
  - ⚙️ **Environment-based configuration** - Properties management
- **TDD (Test-Driven Development)** 🧪 - Development methodology for high-quality code
  - 📖 **[TDD Implementation](docs/Patrones/PATRON-TDD.md)** - Complete TDD implementation documentation
  - 🔴 **Red-Green-Refactor** - Strict TDD cycle implementation
  - 📊 **High Code Coverage** - 348+ comprehensive unit tests
  - 🎯 **Quality Assurance** - Automated testing and continuous integration

### Support Libraries

- **Google Guava** 📚 - Core libraries for Java (High-performance caching for cart operations)
  - 🗂️ **Cache Implementation** - In-memory cache with TTL and automatic cleanup
  - ⚡ **Thread-Safe Operations** - Concurrent access without blocking
  - 📊 **Performance Monitoring** - Built-in statistics and health monitoring
  - 📖 **[Google Guava Implementation](docs/Librerias/GOOGLE-GUAVA.md)** - Complete implementation documentation
- **Apache POI** 📄 - Library for Microsoft documents (Professional Excel report generation)
  - 📊 **Excel Reports** - Automated generation of sales and invoice reports
  - 🎨 **Corporate Styling** - Professional formatting with colors and styles
  - 📋 **Multiple Formats** - Support for XLSX with advanced features
  - 📖 **[Apache POI Implementation](docs/Librerias/APACHE-POI.md)** - Complete implementation documentation
- **Apache Commons** 🔧 - Reusable Java components (Validation and text processing)
  - **Commons Validator** - RFC 5322 compliant email validation
  - **Commons Lang3** - Null-safe string utilities and operations
  - **Commons Text** - HTML escaping and XSS prevention
  - 📖 **[Apache Commons Implementation](docs/Librerias/APACHE-COMMONS.md)** - Complete implementation documentation
- **Logback** 📝 - Enterprise logging framework (Comprehensive application monitoring)
  - 🔍 **Structured Logging** - Detailed operation tracking and debugging
  - �️ **Security Auditing** - Authentication and sensitive operation logging
  - 📊 **Performance Monitoring** - Response time and throughput metrics
  - 📖 **[Logback Implementation](docs/Librerias/LOGBACK.md)** - Complete implementation documentation

### Security Features 🛡️

- Data encryption and secure authentication
- Protection against common web vulnerabilities
- Secure session management

> **[Ver Funciones del Sistema](docs/FUNCIONES-SISTEMA-COMPLETO.md)**

---

## 🏢 About the Company

**Como en Casa** is a home bakery with over 10 years of experience, focused on providing products with traditional flavor, close attention, and complete personalization in each order.

---

## 🎯 Mission and Vision

- **Mission:** 🧁 To offer artisan desserts prepared with fresh ingredients and love from home.
- **Vision:** ⭐ To be a family business recognized for its sweetness, quality, and warm attention.

---

## ⚠️ Problem Identified

Currently, order management is manual (via WhatsApp), which generates errors, delays, and limits business growth. There is no efficient or automated digital channel.

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
  [View Diagram](docs/imgRepo/Diagramadecapas.png)
- **🗃️ ER and Class Diagrams**: Physical modeling of the database and system structures.
  [View ER Diagram](docs/imgRepo/DiagramaER.png) | [View Class Diagram](docs/imgRepo/Diagramadeclases.png)
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

## 🧪 Development & Testing

### TDD Implementation Status

```
📊 TDD IMPLEMENTATION - COMPLETE
================================
✅ 348 Unit Tests Implemented
✅ 87% Code Coverage Achieved
✅ 100% Tests Passing
✅ 0 Failed Tests
✅ 15+ TDD Test Classes
✅ Red-Green-Refactor Methodology Applied
✅ JaCoCo Coverage Reports Generated
✅ Automated Testing Scripts
```

### Quick Start for TDD Development

```powershell
# Clone repository
git clone [your-repository]
cd ComoEnCasa

# Run TDD tests (Backend)
cd backend
.\run-tdd-coverage.bat

# Frontend development
cd ../frontend
npm install
npm start
```

### Testing Commands

```powershell
# Run all tests
mvn test

# Tests with code coverage
mvn clean test jacoco:report

# Open coverage report
start target\site\jacoco\index.html

# Run specific test
mvn test -Dtest="ProductoServiceTDDTest"

# Run TDD tests only
mvn test -Dtest="*TDDTest"
```

### Coverage by Layer

| Layer        | Coverage | Tests | Status |
| ------------ | -------- | ----- | ------ |
| Controllers  | 92%      | 138   | ✅     |
| Services     | 89%      | 156   | ✅     |
| Repositories | 85%      | 34    | ✅     |
| Models       | 78%      | 20    | ✅     |

---
## 🐳 Uso de Docker

### Requisitos

- Docker instalado en el sistema
- Docker Compose instalado

### Pasos para ejecutar el proyecto

1. Construir y ejecutar los contenedores:

   ```bash
   docker-compose up --build
   ```

2. Acceder a las aplicaciones:

   - **Backend**: [http://localhost:8080](http://localhost:8080)
   - **Frontend**: [http://localhost:3000](http://localhost:3000)

3. Base de datos:
   - La base de datos MySQL estará disponible en el puerto `3306`.
   - Credenciales:
     - Usuario: `root`
     - Contraseña: `root`

### Detener los contenedores

Para detener los contenedores, ejecuta:

```bash
docker-compose down
```

### Volúmenes

Los datos de la base de datos se almacenan en un volumen llamado `db_data` para persistencia.
