@echo off
REM Script para ejecutar la aplicación Spring Boot en modo desarrollo
echo Iniciando aplicación Como en Casa en modo desarrollo...
echo.

REM Navegar al directorio del proyecto
cd /d "%~dp0"

REM Establecer el perfil de Spring Boot
set SPRING_PROFILES_ACTIVE=dev

REM Ejecutar la aplicación saltando los tests
mvn spring-boot:run -Dmaven.test.skip=true

pause
