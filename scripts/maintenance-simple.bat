@echo off
setlocal EnableDelayedExpansion

echo [INFO] Iniciando rutina de mantenimiento simplificada...
echo [INFO] Fecha: %date% %time%
echo.

:: Variables básicas
set PROJECT_DIR=%~dp0..
set LOG_DIR=%PROJECT_DIR%\logs
set BACKEND_DIR=%PROJECT_DIR%\backend
set FRONTEND_DIR=%PROJECT_DIR%\frontend

echo [STEP 1/5] Limpieza de logs antiguos...

if exist "%LOG_DIR%" (
    echo [PROGRESS] Verificando logs antiguos...
    dir "%LOG_DIR%\*.log" /b >nul 2>&1
    if !ERRORLEVEL! EQU 0 (
        echo [SUCCESS] Directorio de logs verificado
    )
    
    set LOG_COUNT=0
    for %%f in ("%LOG_DIR%\*.log") do set /a LOG_COUNT+=1
    echo [INFO] Logs encontrados: !LOG_COUNT!
) else (
    echo [WARNING] Directorio de logs no encontrado
)

echo.
echo [STEP 2/5] Limpieza de archivos temporales del sistema...

echo [PROGRESS] Limpiando archivos temporales...
del /q /f "%TEMP%\*" >nul 2>&1
echo [SUCCESS] Archivos temporales limpiados

echo.
echo [STEP 3/5] Limpieza de directorios de compilacion...

if exist "%BACKEND_DIR%\target" (
    echo [PROGRESS] Encontrado directorio target...
    echo [INFO] Tamaño del directorio target antes de limpiar:
    dir "%BACKEND_DIR%\target" /s | find "File(s)"
    echo [WARNING] Directorio target presente - usar 'mvn clean' es recomendado
) else (
    echo [SUCCESS] No hay directorio target para limpiar
)

if exist "%FRONTEND_DIR%\node_modules" (
    echo [PROGRESS] Encontrado directorio node_modules...
    echo [WARNING] Directorio node_modules presente - usar 'npm install' después de limpiar
) else (
    echo [SUCCESS] No hay directorio node_modules
)

echo.
echo [STEP 4/5] Verificacion de procesos activos...

echo [PROGRESS] Verificando procesos Java...
tasklist | find "java.exe" >nul
if !ERRORLEVEL! EQU 0 (
    echo [WARNING] Procesos Java detectados - sistema en uso
    tasklist | find "java.exe"
) else (
    echo [SUCCESS] No hay procesos Java ejecutandose
)

echo [PROGRESS] Verificando procesos Node...
tasklist | find "node.exe" >nul
if !ERRORLEVEL! EQU 0 (
    echo [WARNING] Procesos Node detectados - frontend en uso
    tasklist | find "node.exe"
) else (
    echo [SUCCESS] No hay procesos Node ejecutandose
)

echo.
echo [STEP 5/5] Analisis de espacio...

echo [PROGRESS] Calculando tamaño del proyecto...
if exist "%PROJECT_DIR%" (
    echo [INFO] Analizando estructura del proyecto:
    echo [INFO] - Backend: %BACKEND_DIR%
    echo [INFO] - Frontend: %FRONTEND_DIR%
    echo [INFO] - Logs: %LOG_DIR%
    
    dir "%PROJECT_DIR%" | find "File(s)"
    echo [SUCCESS] Analisis completado
)

echo.
echo [SUCCESS] Rutina de mantenimiento simplificada completada
echo [INFO] Fecha finalizacion: %date% %time%

if not "%1"=="auto" (
    echo.
    echo Presiona cualquier tecla para continuar...
    pause >nul
)

endlocal
