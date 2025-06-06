# 🚀 Comandos Rápidos TDD - ComoEnCasa

## ⚡ Comandos Esenciales

```powershell
# Navegar al backend
cd C:\Users\User\Desktop\ComoEnCasa\backend

# 🧪 EJECUTAR TODOS LOS TESTS
mvn test

# 📊 TESTS + COBERTURA
mvn clean test jacoco:report

# 🎯 TEST ESPECÍFICO
mvn test -Dtest="ProductoServiceTDDTest"

# 🌐 ABRIR REPORTE
start target\site\jacoco\index.html
```

## 🔧 Scripts Automatizados ✅

```powershell
# Script completo con configuración Java 24
.\run-tdd-coverage.bat

# Solo tests (más rápido)
.\run-tdd-tests.bat

# Verificación del entorno
.\verificar-tdd.bat
```

> **✅ Estado:** Todos los scripts listados están funcionales y configurados correctamente.

## 📝 Tests por Módulo

```powershell
# Tests de servicios
mvn test -Dtest="*Service*Test"

# Tests de controladores
mvn test -Dtest="*Controller*Test"

# Tests de email
mvn test -Dtest="*Email*Test"

# Tests con patrón específico
mvn test -Dtest="*TDDTest"
```

## 🛠️ Comandos de Desarrollo

```powershell
# Limpiar proyecto
mvn clean

# Compilar sin tests
mvn compile -DskipTests

# Verificar dependencias
mvn dependency:tree

# Información efectiva del POM
mvn help:effective-pom
```

## 📊 Análisis y Reportes

```powershell
# Solo generar reporte (tests ya ejecutados)
mvn jacoco:report

# Verificar logs detallados
mvn test -X

# Tests con profiles específicos
mvn test -Dspring.profiles.active=test
```

## 🔍 Diagnóstico

```powershell
# Verificar versiones
java -version
mvn -version

# Verificar compilación
mvn clean compile

# Tests ignorando fallos
mvn test -Dmaven.test.failure.ignore=true
```

## 📈 Estadísticas Actuales

```
✅ Tests Totales: 47
✅ Success Rate: 100%
📊 Cobertura: ~85% (JaCoCo)
⏱️ Tiempo Ejecución: ~10s
🏗️ Arquitectura: TDD + Spring Boot
🔧 Herramienta: Solo JaCoCo (PITest/Clover no configurados)
```

## 🎯 Casos de Uso Frecuentes

### Durante Desarrollo

```powershell
# Desarrollo iterativo
mvn test -Dtest="MiNuevoTest" && mvn test
```

### Antes de Commit

```powershell
# Verificación completa
mvn clean test jacoco:report
```

### Debugging

```powershell
# Test con logs detallados
mvn test -Dtest="ProblemaTest" -X
```

### CI/CD Local

```powershell
# Simular pipeline
mvn clean compile test jacoco:report
```

---

📖 **Para guía completa ver**: [TDD-GUIDE-ES.md](../TDD-GUIDE-ES.md)
