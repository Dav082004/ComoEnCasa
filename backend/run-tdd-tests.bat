@echo off
echo ========================================
echo     EJECUTANDO TESTS TDD - ComoEnCasa
echo ========================================
echo.

echo [1/4] Limpiando proyecto...
call mvn clean

echo.
echo [2/4] Compilando codigo fuente...
call mvn compile

echo.
echo [3/4] Ejecutando tests unitarios (TDD)...
call mvn test

echo.
echo [4/4] Generando reporte de coverage...
call mvn jacoco:report

echo.
echo ========================================
echo     RESULTADOS DISPONIBLES EN:
echo ========================================
echo Tests: target/surefire-reports/
echo Coverage HTML: target/site/jacoco/index.html
echo Coverage CSV: target/site/jacoco/jacoco.csv
echo.

echo Abriendo reporte de coverage en el navegador...
start target\site\jacoco\index.html

pause
