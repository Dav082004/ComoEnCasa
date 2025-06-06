# 🧪 Guía Completa de Test-Driven Development (TDD) - Como En Casa

## 📖 Tabla de Contenidos

- [¿Qué es TDD?](#qué-es-tdd)
- [Arquitectura TDD del Proyecto](#arquitectura-tdd-del-proyecto)
- [Configuración del Entorno](#configuración-del-entorno)
- [Librerías y Herramientas TDD](#librerías-y-herramientas-tdd)
- [Estructura de Tests](#estructura-de-tests)
- [Ciclo TDD en Acción](#ciclo-tdd-en-acción)
- [Comandos de Ejecución](#comandos-de-ejecución)
- [Análisis de Cobertura](#análisis-de-cobertura)
- [Mejores Prácticas](#mejores-prácticas)
- [Troubleshooting](#troubleshooting)
- [Guía para Replicar el Proyecto TDD](#guía-para-replicar-el-proyecto-tdd)
- [Recursos y Referencias](#recursos-y-referencias)

## 🎯 ¿Qué es TDD?

**Test-Driven Development (TDD)** es una metodología de desarrollo que invierte el proceso tradicional: **primero escribes los tests, luego el código**. Sigue el ciclo **Red-Green-Refactor**:

1. **🔴 RED** - Escribir un test que falle (porque aún no existe la funcionalidad)
2. **🟢 GREEN** - Escribir el código mínimo para que el test pase
3. **🔄 REFACTOR** - Mejorar el código manteniendo todos los tests pasando

### ✅ Beneficios del TDD en ComoEnCasa:

- **Calidad de código superior**: 47 tests aseguran la funcionalidad
- **Menos bugs en producción**: Detección temprana de errores
- **Código más mantenible**: Los tests documentan el comportamiento esperado
- **Refactoring seguro**: Cambios con confianza gracias a la cobertura de tests
- **Diseño más limpio**: TDD fuerza interfaces más simples y desacopladas

## 🏗️ Arquitectura TDD del Proyecto

### Pirámide de Tests Implementada

El proyecto ComoEnCasa implementa una **pirámide de tests** completa siguiendo las mejores prácticas:

```
                    🔺 E2E Tests (Futuros)
                 🔶 Integration Tests (10%)
             🟫 Service/Unit Tests (80%)
         🟦 Repository Tests (10%)
```

### Capas de Testing

```
┌─────────────────────────────────────────────────────────┐
│                    CAPA WEB                             │
│  ProductoControllerTDDTest.java (5 tests)              │
│  ✓ Tests de endpoints HTTP                              │
│  ✓ Validación de responses JSON                         │
│  ✓ Códigos de estado HTTP                               │
└─────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────┐
│                 CAPA DE SERVICIOS                       │
│  ProductoServiceTDDTest.java (10 tests)                │
│  EmailServiceTDDTest.java (15 tests)                   │
│  UsuarioServiceTDDTest.java (13 tests)                 │
│  ✓ Lógica de negocio                                   │
│  ✓ Validaciones                                         │
│  ✓ Manejo de excepciones                               │
└─────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────┐
│               CAPA DE PERSISTENCIA                      │
│  Base de datos H2 en memoria para tests                │
│  ✓ Auto-generación de esquemas                         │
│  ✓ Transacciones automáticas                           │
│  ✓ Datos aislados por test                              │
└─────────────────────────────────────────────────────────┘
```

## ⚙️ Configuración del Entorno

### Requisitos Previos

```bash
✅ Java 21+ (Recomendado: Java 21)
✅ Maven 3.9+
✅ IDE con soporte para JUnit 5 (IntelliJ IDEA, Eclipse, VS Code)
✅ Git para control de versiones
```

### Verificación del Entorno

```powershell
# Verificar versión de Java
java -version
# Debe mostrar: java version "21..." o superior

# Verificar Maven
mvn -version
# Debe mostrar: Apache Maven 3.9.x

# Verificar que el proyecto compila
cd backend
mvn clean compile
```

## 🔧 Librerías y Herramientas TDD

### Stack Tecnológico de Testing

| Librería             | Versión | Propósito                 | Uso en el Proyecto               |
| -------------------- | ------- | ------------------------- | -------------------------------- |
| **JUnit 5**          | 5.10+   | Framework base de testing | Tests unitarios e integración    |
| **Mockito**          | 5.8+    | Mocking y stubbing        | Simular dependencias externas    |
| **Spring Boot Test** | 3.4.5   | Testing para Spring       | `@SpringBootTest`, `@WebMvcTest` |
| **H2 Database**      | 2.2+    | Base de datos en memoria  | Tests de persistencia            |
| **JaCoCo**           | 0.8.13  | Análisis de cobertura     | Reportes de cobertura de código  |
| **AssertJ**          | 3.24+   | Assertions fluidas        | Assertions más legibles          |
| **MockMvc**          | 6.2+    | Testing web               | Simular peticiones HTTP          |

> **✅ Estado actual:** Todas las librerías están actualizadas y funcionando correctamente. Los 47 tests del proyecto pasan sin errores.

### Configuración en `pom.xml`

```xml
<!-- Framework de Testing Principal -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
    <!-- Incluye: JUnit 5, Mockito, AssertJ, Hamcrest, Spring Test -->
</dependency>

<!-- Base de datos de pruebas -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>

<!-- Plugin de cobertura -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.12</version>
</plugin>
```

### Configuración de Base de Datos de Pruebas

**Archivo**: `src/test/resources/application-test.properties`

```properties
# Base de datos H2 en memoria para tests
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# Auto-generación de esquemas para tests
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# Perfil de test activo
spring.profiles.active=test

# Configuración de logging para tests
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

## 📁 Estructura de Tests

### Organización por Capas

```
src/test/java/com/comoencasa_backend/
├── controller/
│   └── ProductoControllerTDDTest.java     # 🌐 Tests de API REST
├── service/
│   ├── ProductoServiceTDDTest.java        # 🔧 Lógica de productos
│   ├── EmailServiceTDDTest.java           # 📧 Servicios de email
│   ├── EmailServiceImplTest.java          # 📧 Implementación específica
│   └── UsuarioServiceTDDTest.java         # 👤 Gestión de usuarios
└── resources/
    └── application-test.properties        # ⚙️ Configuración de tests
```

### Estadísticas de Tests Actuales

```
📊 RESUMEN DE TESTS EJECUTADOS
┌─────────────────────────────────────────────────────────┐
│ ✅ Total Tests: 47                                      │
│ ✅ Tests Pasados: 47 (100%)                             │
│ ❌ Tests Fallidos: 0                                    │
│ ⚠️  Tests Omitidos: 0                                   │
│ ⏱️  Tiempo Total: ~10 segundos                          │
└─────────────────────────────────────────────────────────┘

📈 DESGLOSE POR MÓDULO:
• ProductoControllerTDDTest:    5 tests  ✅
• EmailServiceImplTest:         4 tests  ✅
• EmailServiceTDDTest:         15 tests  ✅
• ProductoServiceTDDTest:      10 tests  ✅
• UsuarioServiceTDDTest:       13 tests  ✅
```

### Patrón de Organización con `@Nested`

```java
@SpringBootTest
@DisplayName("🧪 Tests TDD - Servicio de Productos")
class ProductoServiceTDDTest {

    @Nested
    @DisplayName("🔍 Buscar Todos los Productos Disponibles")
    class BuscarTodosDisponibles {

        @Test
        @DisplayName("✅ Debería retornar lista de productos disponibles")
        void deberiaRetornarListaProductosDisponibles() { /* ... */ }

        @Test
        @DisplayName("📋 Debería retornar lista vacía cuando no hay productos")
        void deberiaRetornarListaVaciaCuandoNoHayProductos() { /* ... */ }
    }

    @Nested
    @DisplayName("🔍 Buscar por ID")
    class BuscarPorId { /* ... */ }

    @Nested
    @DisplayName("🏷️ Buscar por Categoría")
    class BuscarPorCategoria { /* ... */ }
}
```

## 🔄 Ciclo TDD en Acción

### Ejemplo Completo: Implementando `esEmailValido()`

#### **Fase 1: 🔴 RED - Escribir Tests que Fallan**

```java
@Nested
@DisplayName("📧 Validación de Email")
class ValidacionEmail {

    @Test
    @DisplayName("✅ Debería validar emails correctos")
    void deberiaValidarEmailsCorrectos() {
        // ARRANGE
        EmailServiceImpl emailServiceImpl = (EmailServiceImpl) emailService;

        // ACT & ASSERT
        assertTrue(emailServiceImpl.esEmailValido("usuario@dominio.com"));
        assertTrue(emailServiceImpl.esEmailValido("usuario.nombre@dominio.com"));
        assertTrue(emailServiceImpl.esEmailValido("usuario_nombre@dominio.com"));
        assertTrue(emailServiceImpl.esEmailValido("usuario+nombre@dominio.com"));
    }

    @Test
    @DisplayName("❌ Debería rechazar emails inválidos")
    void deberiaRechazarEmailsInvalidos() {
        // ARRANGE
        EmailServiceImpl emailServiceImpl = (EmailServiceImpl) emailService;

        // ACT & ASSERT - Casos nulos y vacíos
        assertFalse(emailServiceImpl.esEmailValido(null));
        assertFalse(emailServiceImpl.esEmailValido(""));

        // Casos sin formato válido
        assertFalse(emailServiceImpl.esEmailValido("usuario"));
        assertFalse(emailServiceImpl.esEmailValido("usuario@"));
        assertFalse(emailServiceImpl.esEmailValido("@dominio.com"));
        assertFalse(emailServiceImpl.esEmailValido("usuario@dominio@com"));
    }
}
```

**🔴 Resultado**: Los tests fallan porque el método `esEmailValido()` no existe.

#### **Fase 2: 🟢 GREEN - Código Mínimo para Pasar Tests**

```java
public boolean esEmailValido(String email) {
    // Implementación mínima para pasar tests
    if (email == null || email.isEmpty()) {
        return false;
    }

    int atIndex = email.indexOf('@');
    if (atIndex <= 0 || atIndex == email.length() - 1) {
        return false;
    }

    String domainPart = email.substring(atIndex + 1);
    if (domainPart.isEmpty() || !domainPart.contains(".") ||
        domainPart.indexOf('.') == 0 ||
        domainPart.indexOf('.') == domainPart.length() - 1) {
        return false;
    }

    // Verificar que no haya múltiples @
    if (email.indexOf('@', atIndex + 1) != -1) {
        return false;
    }

    return true;
}
```

**🟢 Resultado**: Todos los tests pasan.

#### **Fase 3: 🔄 REFACTOR - Mejorar sin Romper Tests**

```java
/**
 * Valida si un email tiene un formato válido siguiendo estándares básicos.
 *
 * @param email El email a validar
 * @return true si el email tiene un formato válido, false en caso contrario
 */
public boolean esEmailValido(String email) {
    if (email == null || email.isEmpty()) {
        return false;
    }

    // Verificar formato básico: @ con contenido antes y después
    int atIndex = email.indexOf('@');
    if (atIndex <= 0 || atIndex == email.length() - 1) {
        return false;  // @ inexistente, al inicio o al final
    }

    // Validar parte del dominio
    String domainPart = email.substring(atIndex + 1);
    if (!isValidDomain(domainPart)) {
        return false;
    }

    // Verificar que no haya múltiples @
    return email.indexOf('@', atIndex + 1) == -1;
}

private boolean isValidDomain(String domain) {
    if (domain.isEmpty() || !domain.contains(".")) {
        return false;
    }

    int dotIndex = domain.indexOf('.');
    return dotIndex > 0 && dotIndex < domain.length() - 1;
}
```

**🔄 Resultado**: Código más limpio y mantenible, tests siguen pasando.

## 🚀 Comandos de Ejecución

### Scripts Automatizados Disponibles

El proyecto incluye scripts automatizados para simplificar la ejecución de tests:

#### ✅ **Scripts Funcionales**

| Script                 | Propósito                   | Estado       |
| ---------------------- | --------------------------- | ------------ |
| `run-tdd-coverage.bat` | Tests completos + cobertura | ✅ Funcional |
| `run-tdd-tests.bat`    | Solo ejecución de tests     | ✅ Funcional |
| `verificar-tdd.bat`    | Verificación rápida         | ✅ Funcional |
| `verificar-tdd.ps1`    | Verificación PowerShell     | ✅ Funcional |

#### ❌ **Scripts Deprecados**

| Script               | Problema                       | Acción Recomendada |
| -------------------- | ------------------------------ | ------------------ |
| `run-tdd-pitest.bat` | PITest no configurado en Maven | 🗑️ Eliminar        |
| `run-tdd-clover.bat` | Clover no configurado en Maven | 🗑️ Eliminar        |

### Opción 1: Scripts Automatizados (Recomendado)

El proyecto incluye scripts que configuran automáticamente el entorno para Java 24:

```powershell
# Navegar al directorio backend
cd C:\Users\User\Desktop\ComoEnCasa\backend

# Ejecutar todos los tests TDD con cobertura
.\run-tdd-coverage.bat

# Solo ejecutar tests (más rápido)
.\run-tdd-tests.bat

# Verificación rápida del entorno
.\verificar-tdd.bat
```

**💡 Ventajas de los scripts:**

- ✅ Configuración automática para Java 24
- ✅ Variables de entorno optimizadas
- ✅ Generación automática de reportes
- ✅ Apertura automática de reportes en navegador

### Opción 2: Comandos Maven Manuales

```powershell
# 1. Limpiar proyecto
mvn clean

# 2. Compilar
mvn compile

# 3. Ejecutar TODOS los tests
mvn test

# 4. Tests + Reporte de cobertura
mvn clean test jacoco:report

# 5. Test específico
mvn test -Dtest="ProductoServiceTDDTest"

# 6. Tests de una clase específica con patrón
mvn test -Dtest="*TDDTest"

# 7. Test específico con método
mvn test -Dtest="ProductoServiceTDDTest#deberiaRetornarListaVacia"

# 8. Ejecutar tests en modo verbose
mvn test -X

# 9. Ejecutar tests sin compilar (si ya está compilado)
mvn surefire:test
```

### Opción 3: Comandos por Categoría

```powershell
# Tests de servicios únicamente
mvn test -Dtest="com.comoencasa_backend.service.*Test"

# Tests de controladores únicamente
mvn test -Dtest="com.comoencasa_backend.controller.*Test"

# Tests que contengan "Email" en el nombre
mvn test -Dtest="*Email*Test"

# Ejecutar tests con perfil específico
mvn test -Dspring.profiles.active=test
```

### Configuración de Entorno para Java 24

Si experimentas problemas con Java 24, usa estas variables de entorno:

```powershell
# Variables para compatibilidad Java 24
$env:JAVA_OPTS = "-XX:+EnableDynamicAgentLoading --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/sun.nio.ch=ALL-UNNAMED"
$env:MAVEN_OPTS = $env:JAVA_OPTS

# Luego ejecutar tests
mvn test
```

### Comandos de Verificación

```powershell
# Verificar que el entorno está correcto
java -version
mvn -version

# Verificar que el proyecto compila sin errores
mvn clean compile

# Verificar dependencias
mvn dependency:tree

# Validar configuración de tests
mvn help:effective-pom | findstr -i test
```

## 📊 Análisis de Cobertura

### Herramientas de Cobertura Configuradas

El proyecto utiliza **JaCoCo** (Java Code Coverage) para medir la cobertura de código:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.13</version>
    <configuration>
        <includes>
            <include>com/comoencasa_backend/service/**</include>
        </includes>
        <excludes>
            <exclude>com/comoencasa_backend/service/**/test/**</exclude>
        </excludes>
    </configuration>
</plugin>
```

> **⚠️ Nota importante:** El proyecto utiliza únicamente JaCoCo para análisis de cobertura. Otras herramientas como PITest (mutation testing) y Clover no están configuradas en el Maven actual, por lo que sus scripts correspondientes han sido identificados como no funcionales.

### Generación de Reportes

```powershell
# Generar reporte de cobertura completo
mvn clean test jacoco:report

# Solo generar reporte (si tests ya se ejecutaron)
mvn jacoco:report

# Opción rápida usando script automatizado
.\run-tdd-coverage.bat
```

### Ubicación de Reportes

```
backend/target/
├── site/jacoco/
│   ├── index.html              # 🌐 Reporte HTML principal
│   ├── jacoco.csv              # 📊 Datos en formato CSV
│   ├── jacoco.xml              # 📋 Datos en formato XML
│   └── jacoco-resources/       # 🎨 Recursos CSS/JS
├── surefire-reports/           # 📄 Reportes de tests individuales
└── jacoco.exec                 # 🔍 Datos de ejecución binarios
```

### Abrir Reportes

```powershell
# Abrir reporte HTML en navegador (Windows)
start backend\target\site\jacoco\index.html

# Desde PowerShell con ruta completa
Invoke-Item "C:\Users\User\Desktop\ComoEnCasa\backend\target\site\jacoco\index.html"

# Abrir directorio de reportes
explorer backend\target\site\jacoco\
```

### Métricas de Cobertura Actual

```
📈 MÉTRICAS DE COBERTURA ACTUALES
┌─────────────────────────────────────────────────────────┐
│ 🎯 Cobertura de Servicios: ~85%                        │
│ 📊 Líneas Cubiertas: 3 clases analizadas               │
│ ✅ Branch Coverage: Variable por método                 │
│ 🔍 Complejidad Ciclomática: Baja-Media                 │
└─────────────────────────────────────────────────────────┘

📂 DESGLOSE POR PAQUETE:
• com.comoencasa_backend.service.impl: ✅ Analizado
• Clases excluidas: Tests, DTOs, Configuraciones
• Total clases analizadas: 3
```

### Interpretación de Métricas

| Métrica             | Descripción              | Objetivo |
| ------------------- | ------------------------ | -------- |
| **Line Coverage**   | % de líneas ejecutadas   | > 80%    |
| **Branch Coverage** | % de ramas condicionales | > 70%    |
| **Method Coverage** | % de métodos invocados   | > 90%    |
| **Class Coverage**  | % de clases utilizadas   | > 95%    |

### Configuración de Umbrales

```xml
<!-- Configuración de umbrales en pom.xml -->
<execution>
    <id>jacoco-check</id>
    <goals>
        <goal>check</goal>
    </goals>
    <configuration>
        <rules>
            <rule>
                <element>PACKAGE</element>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.80</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</execution>
```

## 🎯 Mejores Prácticas

### Convenciones de Nomenclatura

```java
// ✅ CORRECTO: Nombres descriptivos con @DisplayName
@Test
@DisplayName("✅ Debería retornar producto cuando existe el ID")
void deberiaRetornarProductoCuandoExisteId() { /* ... */ }

// ❌ INCORRECTO: Nombres genéricos sin contexto
@Test
void testGetProducto() { /* ... */ }
```

### Estructura AAA (Arrange-Act-Assert)

```java
@Test
@DisplayName("📋 Debería retornar lista vacía cuando no hay productos disponibles")
void deberiaRetornarListaVaciaCuandoNoHayProductosDisponibles() {
    // ARRANGE - Preparar datos y mocks
    when(productoRepository.findByDisponibleTrue()).thenReturn(Collections.emptyList());

    // ACT - Ejecutar la funcionalidad
    List<Producto> resultado = productoService.buscarTodosDisponibles();

    // ASSERT - Verificar resultados
    assertThat(resultado).isEmpty();
    verify(productoRepository).findByDisponibleTrue();
}
```

### Uso de AssertJ para Assertions Fluidas

```java
// ✅ CORRECTO: AssertJ fluido y legible
assertThat(productos)
    .hasSize(2)
    .extracting(Producto::getNombre)
    .containsExactly("Producto A", "Producto B");

// ❌ MENOS LEGIBLE: JUnit assertions tradicionales
assertEquals(2, productos.size());
assertEquals("Producto A", productos.get(0).getNombre());
assertEquals("Producto B", productos.get(1).getNombre());
```

### Manejo de Excepciones

```java
@Test
@DisplayName("⚠️ Debería lanzar excepción cuando el ID es nulo")
void deberiaLanzarExcepcionCuandoIdEsNulo() {
    // ARRANGE & ACT & ASSERT
    assertThatThrownBy(() -> productoService.buscarPorId(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El ID del producto no puede ser nulo");
}
```

### Organización con @Nested

```java
@SpringBootTest
@DisplayName("🧪 Tests TDD - Servicio de Email")
class EmailServiceTDDTest {

    @Nested
    @DisplayName("📧 Validación de Email")
    class ValidacionEmail { /* tests relacionados */ }

    @Nested
    @DisplayName("📤 Envío de Emails")
    class EnvioEmails { /* tests relacionados */ }

    @Nested
    @DisplayName("⚙️ Configuración")
    class Configuracion { /* tests relacionados */ }
}
```

## 🔧 Troubleshooting

### Problemas Comunes y Soluciones

#### 1. **Error: "Unsupported class file major version 68"**

**Causa**: Incompatibilidad con Java 24

**Solución**:

```powershell
# Configurar variables de entorno
$env:JAVA_OPTS = "-XX:+EnableDynamicAgentLoading --add-opens java.base/java.lang=ALL-UNNAMED"
$env:MAVEN_OPTS = $env:JAVA_OPTS

# O usar el script automatizado
.\run-tdd-coverage.bat
```

#### 2. **Error: "Table 'PRODUCTO' doesn't exist"**

**Causa**: Base de datos H2 no configurada correctamente

**Solución**:

```properties
# Verificar en application-test.properties
spring.jpa.hibernate.ddl-auto=create-drop
spring.datasource.url=jdbc:h2:mem:testdb
```

#### 3. **Tests lentos o colgados**

**Causa**: Configuración de timeouts

**Solución**:

```xml
<!-- En pom.xml -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <forkedProcessTimeoutInSeconds>300</forkedProcessTimeoutInSeconds>
    </configuration>
</plugin>
```

#### 4. **SpotBugs falla el build**

**Causa**: Incompatibilidad con Java 24

**Solución**: Ya está configurado con `<skip>true</skip>` en el pom.xml

#### 5. **No se generan reportes JaCoCo**

**Verificación**:

```powershell
# Verificar que el plugin está ejecutándose
mvn clean test jacoco:report -X | findstr -i jacoco

# Verificar archivos generados
ls backend\target\site\jacoco\
```

### Comandos de Diagnóstico

```powershell
# Verificar configuración Maven
mvn help:effective-pom | findstr -i jacoco

# Ver logs detallados de tests
mvn test -X -Dmaven.test.failure.ignore=true

# Verificar dependencias de test
mvn dependency:tree | findstr -i test

# Limpiar completamente el proyecto
mvn clean install -DskipTests
```

### Logs Útiles para Debug

```java
// Habilitar logs en tests
@TestPropertySource(properties = {
    "logging.level.com.comoencasa_backend=DEBUG",
    "logging.level.org.springframework.test=DEBUG"
})
```

## 🚀 Guía para Replicar el Proyecto TDD

### Paso a Paso: Crear tu Propio Proyecto TDD

#### 1. **Configuración Inicial del Proyecto**

```powershell
# Crear proyecto Spring Boot con dependencias TDD
curl https://start.spring.io/starter.zip \
  -d dependencies=web,jpa,h2,test \
  -d javaVersion=21 \
  -d bootVersion=3.4.5 \
  -d name=mi-proyecto-tdd \
  -o mi-proyecto-tdd.zip

# Extraer y navegar al proyecto
Expand-Archive mi-proyecto-tdd.zip
cd mi-proyecto-tdd
```

#### 2. **Configurar `pom.xml` para TDD**

```xml
<!-- Agregar dependencias TDD específicas -->
<dependencies>
    <!-- Testing completo -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- Base de datos H2 para tests -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>

<!-- Plugin JaCoCo para cobertura -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.12</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

#### 3. **Crear Configuración de Tests**

```properties
# src/test/resources/application-test.properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
spring.profiles.active=test
logging.level.com.tudominio=DEBUG
```

#### 4. **Estructura de Directorios TDD**

```
src/test/java/com/tudominio/
├── controller/
│   └── MiControllerTDDTest.java
├── service/
│   └── MiServiceTDDTest.java
└── repository/
    └── MiRepositoryTest.java
```

#### 5. **Plantilla de Test TDD**

```java
@SpringBootTest
@DisplayName("🧪 Tests TDD - Mi Servicio")
class MiServiceTDDTest {

    @MockBean
    private MiRepository miRepository;

    @Autowired
    private MiService miService;

    @Nested
    @DisplayName("🔍 Funcionalidad Principal")
    class FuncionalidadPrincipal {

        @Test
        @DisplayName("✅ Debería hacer X cuando Y")
        void deberiaHacerXCuandoY() {
            // ARRANGE
            // ... preparar datos y mocks

            // ACT
            // ... ejecutar funcionalidad

            // ASSERT
            // ... verificar resultados
        }
    }
}
```

### Scripts de Automatización

#### **Script Windows** (`run-tests.bat`)

```batch
@echo off
echo ================================================
echo    EJECUTANDO TESTS TDD
echo ================================================

set JAVA_OPTS=-XX:+EnableDynamicAgentLoading
set MAVEN_OPTS=%JAVA_OPTS%

echo [1/3] Limpiando proyecto...
call mvn clean

echo [2/3] Ejecutando tests...
call mvn test

echo [3/3] Generando reportes...
call mvn jacoco:report

echo ✅ Tests completados
start target\site\jacoco\index.html
pause
```

#### **Script Linux/Mac** (`run-tests.sh`)

```bash
#!/bin/bash
echo "================================================"
echo "    EJECUTANDO TESTS TDD"
echo "================================================"

export JAVA_OPTS="-XX:+EnableDynamicAgentLoading"
export MAVEN_OPTS="$JAVA_OPTS"

echo "[1/3] Limpiando proyecto..."
mvn clean

echo "[2/3] Ejecutando tests..."
mvn test

echo "[3/3] Generando reportes..."
mvn jacoco:report

echo "✅ Tests completados"
open target/site/jacoco/index.html || xdg-open target/site/jacoco/index.html
```

### Integración con IDEs

#### **IntelliJ IDEA**

```
1. Instalar plugins:
   - JUnit
   - Coverage
   - SonarLint

2. Configurar Run Configurations:
   - VM Options: -XX:+EnableDynamicAgentLoading
   - Test runner: Platform (JUnit 5)

3. Habilitar "Run tests with coverage"
```

#### **Visual Studio Code**

```json
// .vscode/settings.json
{
  "java.test.config": {
    "vmArgs": ["-XX:+EnableDynamicAgentLoading"]
  },
  "java.test.defaultConfig": "test"
}
```

#### **Eclipse**

```
1. Instalar EclEmma para cobertura
2. Configurar Run Configurations:
   - Arguments > VM arguments: -XX:+EnableDynamicAgentLoading
3. Run > Coverage As > JUnit Test
```

## 📚 Recursos y Referencias

### 📖 **Libros Recomendados**

| Título                             | Autor            | Enfoque           |
| ---------------------------------- | ---------------- | ----------------- |
| Test-Driven Development by Example | Kent Beck        | Fundamentos TDD   |
| Clean Code                         | Robert C. Martin | Calidad de código |
| Effective Unit Testing             | Lasse Koskela    | Testing práctico  |
| Spring Boot in Action              | Craig Walls      | Testing en Spring |

### 🌐 **Documentación Oficial**

- **JUnit 5**: [junit.org/junit5](https://junit.org/junit5/docs/current/user-guide/)
- **Mockito**: [javadoc.io/doc/org.mockito](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- **Spring Boot Test**: [spring.io/guides/gs/testing-web](https://spring.io/guides/gs/testing-web/)
- **AssertJ**: [assertj.github.io/doc](https://assertj.github.io/doc/)
- **JaCoCo**: [jacoco.org/jacoco](https://www.jacoco.org/jacoco/trunk/doc/)

### 🎥 **Cursos y Tutoriales**

- **Kent Beck - TDD Course** (Clean Coders)
- **Spring Boot Testing** (Baeldung)
- **JUnit 5 Complete Guide** (Udemy)
- **Test-Driven Development in Java** (Pluralsight)

### 🛠️ **Herramientas Adicionales**

| Herramienta        | Propósito                       | Estado en Proyecto |
| ------------------ | ------------------------------- | ------------------ |
| **Testcontainers** | Tests de integración con Docker | ⚠️ No configurado  |
| **WireMock**       | Mock de servicios HTTP          | ⚠️ No configurado  |
| **Pitest**         | Mutation testing                | ❌ No configurado  |
| **ArchUnit**       | Tests de arquitectura           | ⚠️ No configurado  |
| **SonarQube**      | Análisis de calidad             | ⚠️ No configurado  |

> **📝 Nota:** El proyecto ComoEnCasa utiliza únicamente JaCoCo para análisis de cobertura. Las herramientas marcadas como "No configurado" requieren configuración adicional en Maven si se desean utilizar.

### 🏃‍♂️ **Ejercicios Prácticos**

1. **Kata FizzBuzz**: Implementar usando TDD estricto
2. **String Calculator**: Ejercicio clásico de TDD
3. **Bowling Game**: Cálculo de puntuación de bolos
4. **Roman Numerals**: Conversión de números romanos
5. **Bank Account**: Simulación de cuenta bancaria

### 🤝 **Comunidades y Eventos**

- **Java Testing** (Reddit: r/java)
- **Spring Community** (spring.io/community)
- **TDD Practitioners** (LinkedIn Groups)
- **Local JUGs** (Java User Groups)
- **Coding Dojos** (meetup.com)

## 📞 **Soporte del Proyecto ComoEnCasa**

### 🐛 **Reportar Issues**

```powershell
# Recopilar información para reportes
mvn --version
java -version
mvn dependency:tree > dependencies.txt
mvn test -X > test-logs.txt
```

### 📋 **Checklist de Verificación**

- [ ] ✅ Java 21+ instalado
- [ ] ✅ Maven 3.9+ configurado
- [ ] ✅ Tests pasan: `mvn test`
- [ ] ✅ Cobertura genera: `mvn jacoco:report`
- [ ] ✅ Reportes abren en navegador
- [ ] ✅ No hay errores de compilación

### 🔄 **Ciclo de Contribución**

1. **Fork** del repositorio
2. **Crear rama** para nueva feature
3. **Escribir tests** (Red)
4. **Implementar código** (Green)
5. **Refactorizar** (Refactor)
6. **Pull Request** con tests incluidos

---

## 📈 **Métricas de Éxito del Proyecto**

```
🎯 OBJETIVOS ALCANZADOS
┌─────────────────────────────────────────────────────────┐
│ ✅ 47 tests implementados y pasando (100% success)     │
│ ✅ Cobertura JaCoCo configurada y funcional            │
│ ✅ Scripts optimizados (eliminados no funcionales)     │
│ ✅ Documentación actualizada y simplificada            │
│ ✅ Compatibilidad Java 21/24 verificada                │
│ ✅ Base de datos H2 para tests configurada             │
│ ✅ Integración Maven completamente funcional           │
│ 🗑️ Scripts PITest/Clover eliminados (no configurados)  │
└─────────────────────────────────────────────────────────┘
```

**¡El proyecto ComoEnCasa es un ejemplo completo y funcional de TDD en Spring Boot!** 🚀

---

_Última actualización: 6 de junio de 2025_  
_Versión del documento: 2.1_  
_Estado: Optimizado y sincronizado con backend actual_  
_Compatibilidad: Java 21+, Spring Boot 3.4.5, Maven 3.9+_
