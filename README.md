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

> � **[Ver Análisis Técnico Completo](docs/ANALISIS-TECNICO-COMPLETO.md)**

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

---

## 👥 Development Team

- Correa Acosta, Benjamin Emanuel
- Contreras Palacios, David Angel
- Barboza Ataco, Mijhael Hamed
- Meléndez Torre, José Martín
- Llacctas Pereyra, Marco A.
