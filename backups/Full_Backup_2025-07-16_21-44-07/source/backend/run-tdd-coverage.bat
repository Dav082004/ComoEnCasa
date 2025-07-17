@echo off
echo ================================================
echo    TDD + COVERAGE PARA COMOENCASA - JAVA 24
echo ================================================
echo.

REM Configurar el entorno
set JAVA_OPTS=-XX:+EnableDynamicAgentLoading --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/sun.nio.ch=ALL-UNNAMED
set MAVEN_OPTS=%JAVA_OPTS%

echo [1/5] Limpiando proyecto anterior...
call mvn clean
if %ERRORLEVEL% neq 0 (
    echo ERROR: Falló la limpieza del proyecto
    pause
    exit /b 1
)

echo.
echo [2/5] Compilando proyecto...
call mvn compile
if %ERRORLEVEL% neq 0 (
    echo ERROR: Falló la compilación
    pause
    exit /b 1
)

echo.
echo [3/5] Ejecutando tests TDD con coverage...
call mvn test
if %ERRORLEVEL% neq 0 (
    echo ERROR: Algunos tests fallaron
    pause
    exit /b 1
)

echo.
echo [4/5] Generando reportes de coverage...
call mvn jacoco:report
if %ERRORLEVEL% neq 0 (
    echo ADVERTENCIA: Error generando reporte JaCoCo, intentando método alternativo...
    call mvn site:site
)

echo.
echo [5/5] Ejecutando análisis de código con SpotBugs...
call mvn spotbugs:check
if %ERRORLEVEL% neq 0 (
    echo ADVERTENCIA: SpotBugs encontró algunos problemas potenciales
)

echo.
echo ================================================
echo             REPORTE COMPLETADO
echo ================================================
echo.
echo UBICACIONES DE REPORTES:
echo - Coverage HTML: target\site\jacoco\index.html
echo - Coverage XML:  target\site\jacoco\jacoco.xml  
echo - SpotBugs:      target\site\spotbugs.html
echo - Tests:         target\site\surefire-report.html
echo.

REM Intentar abrir el reporte principal si existe
if exist "target\site\jacoco\index.html" (
    echo Abriendo reporte de coverage...
    start "" "target\site\jacoco\index.html"
) else (
    echo Reporte de coverage no encontrado. Revisando alternativas...
    if exist "target\site\index.html" (
        echo Abriendo reporte general del sitio...
        start "" "target\site\index.html"
    )
)

echo.
echo Presiona cualquier tecla para continuar...
pause > nul
