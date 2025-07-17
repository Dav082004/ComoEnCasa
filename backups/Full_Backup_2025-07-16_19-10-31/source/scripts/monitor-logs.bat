@echo off
echo ==========================================
echo    MONITOR DE LOGS - COMO EN CASA
echo ==========================================

set LOG_DIR=logs
set BACKUP_DIR=backup-logs

echo [INFO] Verificando directorio de logs: %LOG_DIR%
if not exist %LOG_DIR% (
    echo [WARNING] No existe el directorio de logs: %LOG_DIR%
    echo [INFO] Creando directorio de logs...
    mkdir %LOG_DIR%
    mkdir %LOG_DIR%\archived
)

echo.
echo 📊 OPCIONES DE MONITOREO:
echo.
echo 1. Ver logs en tiempo real (aplicación)
echo 2. Ver logs de errores
echo 3. Ver logs de auditoría 
echo 4. Ver logs HTTP
echo 5. Ver logs de base de datos
echo 6. Generar reporte de logs
echo 7. Limpiar logs antiguos
echo 8. Backup de logs
echo 9. Ver métricas del sistema
echo 0. Salir
echo.

set /p OPTION="Seleccione una opción (0-9): "

if "%OPTION%"=="1" goto VIEW_APP_LOGS
if "%OPTION%"=="2" goto VIEW_ERROR_LOGS
if "%OPTION%"=="3" goto VIEW_AUDIT_LOGS
if "%OPTION%"=="4" goto VIEW_HTTP_LOGS
if "%OPTION%"=="5" goto VIEW_DB_LOGS
if "%OPTION%"=="6" goto GENERATE_REPORT
if "%OPTION%"=="7" goto CLEANUP_LOGS
if "%OPTION%"=="8" goto BACKUP_LOGS
if "%OPTION%"=="9" goto VIEW_METRICS
if "%OPTION%"=="0" goto EXIT

echo [ERROR] Opción inválida: %OPTION%
pause
goto START

:VIEW_APP_LOGS
echo.
echo 📄 LOGS DE APLICACIÓN EN TIEMPO REAL
echo Presione Ctrl+C para salir
echo ==========================================
if exist %LOG_DIR%\comoencasa-app.log (
    powershell Get-Content -Path "%LOG_DIR%\comoencasa-app.log" -Wait -Tail 50
) else (
    echo [WARNING] No existe el archivo de log: %LOG_DIR%\comoencasa-app.log
    echo [INFO] Inicie la aplicación para generar logs
)
goto END

:VIEW_ERROR_LOGS
echo.
echo ❌ LOGS DE ERRORES
echo ==========================================
if exist %LOG_DIR%\comoencasa-error.log (
    powershell Get-Content -Path "%LOG_DIR%\comoencasa-error.log" -Tail 30
) else (
    echo [INFO] ✅ No hay errores registrados
)
goto END

:VIEW_AUDIT_LOGS
echo.
echo 🔐 LOGS DE AUDITORÍA
echo ==========================================
if exist %LOG_DIR%\comoencasa-audit.log (
    powershell Get-Content -Path "%LOG_DIR%\comoencasa-audit.log" -Tail 30
) else (
    echo [INFO] No hay eventos de auditoría registrados
)
goto END

:VIEW_HTTP_LOGS
echo.
echo 🌐 LOGS DE REQUESTS HTTP
echo ==========================================
if exist %LOG_DIR%\comoencasa-http.log (
    powershell Get-Content -Path "%LOG_DIR%\comoencasa-http.log" -Tail 30
) else (
    echo [INFO] No hay requests HTTP registrados
)
goto END

:VIEW_DB_LOGS
echo.
echo 💾 LOGS DE BASE DE DATOS
echo ==========================================
if exist %LOG_DIR%\comoencasa-database.log (
    powershell Get-Content -Path "%LOG_DIR%\comoencasa-database.log" -Tail 30
) else (
    echo [INFO] No hay operaciones de BD registradas
)
goto END

:GENERATE_REPORT
echo.
echo 📊 GENERANDO REPORTE DE LOGS...
echo ==========================================

set REPORT_FILE=log-report-%date:~10,4%-%date:~4,2%-%date:~7,2%.txt
echo REPORTE DE LOGS - COMO EN CASA > %REPORT_FILE%
echo Generado: %date% %time% >> %REPORT_FILE%
echo ========================================== >> %REPORT_FILE%
echo. >> %REPORT_FILE%

echo 📁 ARCHIVOS DE LOG DISPONIBLES: >> %REPORT_FILE%
if exist %LOG_DIR% (
    dir %LOG_DIR%\*.log /B >> %REPORT_FILE%
) else (
    echo   No hay archivos de log >> %REPORT_FILE%
)

echo. >> %REPORT_FILE%
echo 📊 ESTADÍSTICAS: >> %REPORT_FILE%

if exist %LOG_DIR%\comoencasa-error.log (
    for /f %%i in ('type "%LOG_DIR%\comoencasa-error.log" ^| find /c /v ""') do set ERROR_COUNT=%%i
    echo   Errores registrados: %ERROR_COUNT% >> %REPORT_FILE%
) else (
    echo   Errores registrados: 0 >> %REPORT_FILE%
)

if exist %LOG_DIR%\comoencasa-audit.log (
    for /f %%i in ('type "%LOG_DIR%\comoencasa-audit.log" ^| find /c /v ""') do set AUDIT_COUNT=%%i
    echo   Eventos de auditoría: %AUDIT_COUNT% >> %REPORT_FILE%
) else (
    echo   Eventos de auditoría: 0 >> %REPORT_FILE%
)

echo. >> %REPORT_FILE%
echo 🔍 ÚLTIMOS 10 ERRORES: >> %REPORT_FILE%
if exist %LOG_DIR%\comoencasa-error.log (
    powershell Get-Content -Path "%LOG_DIR%\comoencasa-error.log" -Tail 10 >> %REPORT_FILE%
) else (
    echo   No hay errores recientes >> %REPORT_FILE%
)

echo [SUCCESS] Reporte generado: %REPORT_FILE%
echo [INFO] Abriendo reporte...
start notepad %REPORT_FILE%
goto END

:CLEANUP_LOGS
echo.
echo 🧹 LIMPIEZA DE LOGS ANTIGUOS
echo ==========================================

set /p DAYS="¿Eliminar logs de más de cuántos días? (7): "
if "%DAYS%"=="" set DAYS=7

echo [INFO] Eliminando logs de más de %DAYS% días...

if exist %LOG_DIR%\archived (
    forfiles /p %LOG_DIR%\archived /m *.log* /d -%DAYS% /c "cmd /c del @path" 2>nul
    echo [SUCCESS] Logs antiguos eliminados
) else (
    echo [INFO] No hay directorio de archivos para limpiar
)

echo [INFO] Tamaño actual del directorio de logs:
if exist %LOG_DIR% (
    powershell "'{0:N2} MB' -f ((Get-ChildItem -Path '%LOG_DIR%' -Recurse | Measure-Object -Property Length -Sum).Sum / 1MB)"
)
goto END

:BACKUP_LOGS
echo.
echo 💾 BACKUP DE LOGS
echo ==========================================

if not exist %BACKUP_DIR% mkdir %BACKUP_DIR%

set DATE_TIME=%date:~10,4%-%date:~4,2%-%date:~7,2%_%time:~0,2%-%time:~3,2%-%time:~6,2%
set DATE_TIME=%DATE_TIME: =0%
set BACKUP_FILE=%BACKUP_DIR%\logs-backup-%DATE_TIME%.zip

echo [INFO] Creando backup de logs...
powershell Compress-Archive -Path '%LOG_DIR%\*' -DestinationPath '%BACKUP_FILE%' -Force

if exist %BACKUP_FILE% (
    echo [SUCCESS] Backup creado: %BACKUP_FILE%
    echo [INFO] Limpiando backups antiguos (manteniendo últimos 5)...
    for /f "skip=5 delims=" %%i in ('dir /b /o-d %BACKUP_DIR%\logs-backup-*.zip') do del "%BACKUP_DIR%\%%i"
) else (
    echo [ERROR] Error al crear backup
)
goto END

:VIEW_METRICS
echo.
echo 📊 MÉTRICAS DEL SISTEMA
echo ==========================================

echo [INFO] CPU y Memoria:
wmic cpu get loadpercentage /value | findstr "LoadPercentage"
wmic OS get TotalVisibleMemorySize,FreePhysicalMemory /value | findstr "="

echo.
echo [INFO] Espacio en disco:
wmic logicaldisk get caption,size,freespace /value | findstr "="

echo.
echo [INFO] Procesos Java activos:
tasklist /fi "imagename eq java.exe" /fo table

echo.
echo [INFO] Conectividad de red:
ping -n 1 google.com > nul && echo ✅ Conexión a Internet: OK || echo ❌ Conexión a Internet: ERROR

echo.
echo [INFO] Uso de puertos:
netstat -an | findstr ":8081" && echo ✅ Puerto 8081: EN USO || echo ⚠️ Puerto 8081: LIBRE
netstat -an | findstr ":3306" && echo ✅ Puerto 3306 (MySQL): EN USO || echo ⚠️ Puerto 3306 (MySQL): LIBRE

goto END

:END
echo.
echo ==========================================
pause

:EXIT
echo.
echo 👋 Hasta luego!
exit /b 0
