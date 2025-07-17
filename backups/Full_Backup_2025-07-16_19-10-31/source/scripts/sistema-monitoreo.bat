@echo off
title SISTEMA DE MONITOREO - COMO EN CASA
color 0A

:MAIN_MENU
cls
echo.
echo     ╔═══════════════════════════════════════════════╗
echo     ║           🏢 COMO EN CASA - MONITOREO         ║
echo     ║              Sistema de Logs v2.0            ║
echo     ╚═══════════════════════════════════════════════╝
echo.
echo     📊 ESTADO DEL SISTEMA:
echo     ────────────────────────────────────────────────

REM Verificar si la aplicación está corriendo
tasklist /fi "imagename eq java.exe" | findstr "java.exe" >nul
if %errorlevel%==0 (
    echo     🟢 Aplicación Java: ACTIVA
) else (
    echo     🔴 Aplicación Java: INACTIVA
)

REM Verificar MySQL
netstat -an | findstr ":3306" >nul
if %errorlevel%==0 (
    echo     🟢 MySQL: CONECTADO
) else (
    echo     🔴 MySQL: DESCONECTADO
)

REM Verificar logs
if exist logs\comoencasa-app.log (
    echo     🟢 Sistema de Logs: ACTIVO
) else (
    echo     🔴 Sistema de Logs: INACTIVO
)

echo     ────────────────────────────────────────────────
echo.
echo     📋 OPCIONES DISPONIBLES:
echo.
echo     🔍 MONITOREO:
echo       1. Monitor de logs en tiempo real
echo       2. Dashboard de métricas
echo       3. Análisis de performance
echo       4. Estado del sistema
echo.
echo     📊 REPORTES:
echo       5. Reporte de errores
echo       6. Reporte de auditoría
echo       7. Estadísticas de uso
echo       8. Reporte completo
echo.
echo     🛠️ MANTENIMIENTO:
echo       9. Backup de logs
echo      10. Limpiar logs antiguos
echo      11. Reiniciar logs
echo      12. Configurar alertas
echo.
echo     ⚙️ UTILIDADES:
echo      13. Abrir directorio de logs
echo      14. Ver logs de base de datos
echo      15. Logs de seguridad
echo       0. Salir
echo.
echo     ────────────────────────────────────────────────

set /p choice="     Seleccione una opción (0-15): "

if "%choice%"=="1" goto MONITOR_REAL_TIME
if "%choice%"=="2" goto DASHBOARD
if "%choice%"=="3" goto PERFORMANCE_ANALYSIS
if "%choice%"=="4" goto SYSTEM_STATUS
if "%choice%"=="5" goto ERROR_REPORT
if "%choice%"=="6" goto AUDIT_REPORT
if "%choice%"=="7" goto USAGE_STATS
if "%choice%"=="8" goto FULL_REPORT
if "%choice%"=="9" goto BACKUP_LOGS
if "%choice%"=="10" goto CLEANUP_LOGS
if "%choice%"=="11" goto RESTART_LOGS
if "%choice%"=="12" goto CONFIGURE_ALERTS
if "%choice%"=="13" goto OPEN_LOG_DIR
if "%choice%"=="14" goto DB_LOGS
if "%choice%"=="15" goto SECURITY_LOGS
if "%choice%"=="0" goto EXIT

echo     ❌ Opción inválida. Presione cualquier tecla para continuar...
pause >nul
goto MAIN_MENU

:MONITOR_REAL_TIME
cls
echo.
echo     🔍 MONITOR EN TIEMPO REAL
echo     ═══════════════════════════════════════════════
echo.
echo     Seleccione el tipo de log a monitorear:
echo.
echo     1. Logs de aplicación
echo     2. Logs de errores
echo     3. Logs HTTP
echo     4. Logs de auditoría
echo     5. Todos los logs (múltiples ventanas)
echo     0. Volver al menú principal
echo.

set /p log_type="     Opción: "

if "%log_type%"=="1" goto MONITOR_APP
if "%log_type%"=="2" goto MONITOR_ERROR
if "%log_type%"=="3" goto MONITOR_HTTP
if "%log_type%"=="4" goto MONITOR_AUDIT
if "%log_type%"=="5" goto MONITOR_ALL
if "%log_type%"=="0" goto MAIN_MENU

goto MONITOR_REAL_TIME

:MONITOR_APP
cls
echo     📄 MONITOREANDO LOGS DE APLICACIÓN
echo     Presione Ctrl+C para salir
echo     ═══════════════════════════════════════════════
if exist logs\comoencasa-app.log (
    powershell Get-Content -Path "logs\comoencasa-app.log" -Wait -Tail 20
) else (
    echo     ⚠️ No existe el archivo de log de aplicación
    pause
)
goto MAIN_MENU

:MONITOR_ERROR
cls
echo     ❌ MONITOREANDO LOGS DE ERRORES
echo     Presione Ctrl+C para salir
echo     ═══════════════════════════════════════════════
if exist logs\comoencasa-error.log (
    powershell Get-Content -Path "logs\comoencasa-error.log" -Wait -Tail 20
) else (
    echo     ✅ No hay errores registrados
    pause
)
goto MAIN_MENU

:DASHBOARD
cls
echo.
echo     📊 DASHBOARD DE MÉTRICAS EN TIEMPO REAL
echo     ═══════════════════════════════════════════════

:DASHBOARD_LOOP
cls
echo     📊 DASHBOARD - %date% %time%
echo     ═══════════════════════════════════════════════
echo.

REM Contar logs
if exist logs\comoencasa-app.log (
    for /f %%i in ('type "logs\comoencasa-app.log" 2^>nul ^| find /c /v ""') do set APP_LOGS=%%i
) else (
    set APP_LOGS=0
)

if exist logs\comoencasa-error.log (
    for /f %%i in ('type "logs\comoencasa-error.log" 2^>nul ^| find /c /v ""') do set ERROR_LOGS=%%i
) else (
    set ERROR_LOGS=0
)

if exist logs\comoencasa-audit.log (
    for /f %%i in ('type "logs\comoencasa-audit.log" 2^>nul ^| find /c /v ""') do set AUDIT_LOGS=%%i
) else (
    set AUDIT_LOGS=0
)

echo     📈 ESTADÍSTICAS:
echo       📄 Logs de aplicación: %APP_LOGS%
echo       ❌ Logs de errores: %ERROR_LOGS%
echo       🔐 Logs de auditoría: %AUDIT_LOGS%
echo.

REM Memoria del sistema
echo     💾 MEMORIA:
wmic OS get FreePhysicalMemory,TotalVisibleMemorySize /value 2>nul | findstr "=" | for /f "tokens=2 delims==" %%a in ('more') do echo       %%a KB

echo.
echo     🔄 CPU:
wmic cpu get loadpercentage /value 2>nul | findstr "LoadPercentage"

echo.
echo     🌐 CONECTIVIDAD:
ping -n 1 127.0.0.1 >nul && echo       ✅ Localhost: OK || echo       ❌ Localhost: ERROR
ping -n 1 google.com >nul && echo       ✅ Internet: OK || echo       ❌ Internet: ERROR

echo.
echo     ⚠️ ÚLTIMOS ERRORES:
if exist logs\comoencasa-error.log (
    powershell Get-Content -Path "logs\comoencasa-error.log" -Tail 3 2>nul | findstr /v "^$"
) else (
    echo       ✅ Sin errores recientes
)

echo.
echo     ────────────────────────────────────────────────
echo     Presione 'R' para actualizar, 'Q' para salir

choice /c RQ /n /t 10 /d R >nul
if errorlevel 2 goto MAIN_MENU
goto DASHBOARD_LOOP

:PERFORMANCE_ANALYSIS
cls
echo.
echo     📊 INICIANDO ANÁLISIS DE PERFORMANCE...
echo.
call scripts\analyze-performance.bat
pause
goto MAIN_MENU

:SYSTEM_STATUS
cls
echo.
echo     🖥️ ESTADO DETALLADO DEL SISTEMA
echo     ═══════════════════════════════════════════════
echo.

echo     📋 PROCESOS JAVA:
tasklist /fi "imagename eq java.exe" /fo table

echo.
echo     🌐 PUERTOS EN USO:
netstat -an | findstr ":8081\|:3306\|:80\|:443"

echo.
echo     💾 ESPACIO EN DISCO:
wmic logicaldisk get caption,size,freespace /format:table

echo.
echo     📁 ARCHIVOS DE LOG:
if exist logs (
    dir logs\*.log /s
) else (
    echo     ⚠️ Directorio de logs no encontrado
)

echo.
pause
goto MAIN_MENU

:ERROR_REPORT
cls
echo.
echo     📋 GENERANDO REPORTE DE ERRORES...
echo.

set ERROR_REPORT=reports\error-report-%date:~10,4%-%date:~4,2%-%date:~7,2%.txt
if not exist reports mkdir reports

echo REPORTE DE ERRORES - COMO EN CASA > %ERROR_REPORT%
echo Generado: %date% %time% >> %ERROR_REPORT%
echo ============================================ >> %ERROR_REPORT%
echo. >> %ERROR_REPORT%

if exist logs\comoencasa-error.log (
    echo TOTAL DE ERRORES: >> %ERROR_REPORT%
    for /f %%i in ('type "logs\comoencasa-error.log" ^| find /c /v ""') do echo %%i errores registrados >> %ERROR_REPORT%
    
    echo. >> %ERROR_REPORT%
    echo ÚLTIMOS 20 ERRORES: >> %ERROR_REPORT%
    echo ---------------------------------------- >> %ERROR_REPORT%
    powershell Get-Content -Path "logs\comoencasa-error.log" -Tail 20 >> %ERROR_REPORT%
) else (
    echo ✅ NO HAY ERRORES REGISTRADOS >> %ERROR_REPORT%
)

echo.
echo     ✅ Reporte generado: %ERROR_REPORT%
start notepad %ERROR_REPORT%
pause
goto MAIN_MENU

:BACKUP_LOGS
cls
echo.
echo     💾 BACKUP DE LOGS
echo     ═══════════════════════════════════════════════
echo.

call scripts\backup-logs.bat
pause
goto MAIN_MENU

:CLEANUP_LOGS
cls
echo.
echo     🧹 LIMPIEZA DE LOGS
echo     ═══════════════════════════════════════════════
echo.

set /p days="     ¿Eliminar logs de más de cuántos días? (7): "
if "%days%"=="" set days=7

echo     [INFO] Eliminando logs de más de %days% días...

if exist logs\archived (
    forfiles /p logs\archived /m *.log* /d -%days% /c "cmd /c del @path" 2>nul
    echo     ✅ Logs antiguos eliminados
) else (
    echo     ⚠️ No hay directorio de archivos para limpiar
)

echo.
pause
goto MAIN_MENU

:OPEN_LOG_DIR
start explorer logs
goto MAIN_MENU

:EXIT
cls
echo.
echo     👋 Cerrando Sistema de Monitoreo...
echo.
echo     Gracias por usar el sistema de monitoreo de Como en Casa
echo.
timeout /t 2 >nul
exit /b 0

REM Funciones adicionales...
:AUDIT_REPORT
cls
echo.
echo     🔐 REPORTE DE AUDITORÍA
echo.
if exist logs\comoencasa-audit.log (
    powershell Get-Content -Path "logs\comoencasa-audit.log" -Tail 50
) else (
    echo     📝 No hay eventos de auditoría registrados
)
pause
goto MAIN_MENU

:USAGE_STATS
cls
echo.
echo     📊 ESTADÍSTICAS DE USO
echo.
echo     [INFO] Calculando estadísticas...
echo.

REM Contar diferentes tipos de eventos
if exist logs\comoencasa-http.log (
    powershell -Command "& {
        $content = Get-Content -Path 'logs\comoencasa-http.log' -ErrorAction SilentlyContinue
        if ($content) {
            $total = $content.Count
            $gets = ($content | Where-Object { $_ -match 'GET' }).Count
            $posts = ($content | Where-Object { $_ -match 'POST' }).Count
            Write-Output \"     📊 HTTP Requests totales: $total\"
            Write-Output \"       📄 GET: $gets\"
            Write-Output \"       📝 POST: $posts\"
        }
    }"
)

echo.
pause
goto MAIN_MENU

:FULL_REPORT
call scripts\generate-full-report.bat
pause
goto MAIN_MENU

:RESTART_LOGS
echo.
echo     🔄 REINICIANDO SISTEMA DE LOGS...
echo.
echo     ⚠️ Esta acción moverá los logs actuales a backup
echo.
set /p confirm="     ¿Continuar? (S/N): "
if /i not "%confirm%"=="S" goto MAIN_MENU

if not exist logs-backup mkdir logs-backup
if exist logs (
    move logs logs-backup\logs-%date:~10,4%-%date:~4,2%-%date:~7,2%
)
mkdir logs
mkdir logs\archived

echo     ✅ Sistema de logs reiniciado
pause
goto MAIN_MENU

:CONFIGURE_ALERTS
echo.
echo     🚨 CONFIGURACIÓN DE ALERTAS
echo.
echo     Esta funcionalidad permite configurar alertas automáticas
echo     cuando se detecten errores críticos o problemas de rendimiento.
echo.
echo     [INFO] Esta característica estará disponible en una próxima versión
pause
goto MAIN_MENU

:DB_LOGS
cls
echo.
echo     💾 LOGS DE BASE DE DATOS
echo.
if exist logs\comoencasa-database.log (
    powershell Get-Content -Path "logs\comoencasa-database.log" -Tail 30
) else (
    echo     📝 No hay logs de base de datos
)
pause
goto MAIN_MENU

:SECURITY_LOGS
cls
echo.
echo     🔒 LOGS DE SEGURIDAD
echo.
if exist logs\comoencasa-audit.log (
    echo     Buscando eventos de seguridad...
    powershell Get-Content -Path "logs\comoencasa-audit.log" | Where-Object { $_ -match "LOGIN|AUTH|FAIL|SECURITY" } | Select-Object -Last 20
) else (
    echo     📝 No hay logs de seguridad
)
pause
goto MAIN_MENU

:MONITOR_ALL
echo.
echo     🚀 ABRIENDO MÚLTIPLES MONITORES...
echo.
if exist logs\comoencasa-app.log (
    start "Monitor App" cmd /k "echo Logs de Aplicación && powershell Get-Content -Path logs\comoencasa-app.log -Wait -Tail 10"
)
if exist logs\comoencasa-error.log (
    start "Monitor Error" cmd /k "echo Logs de Errores && powershell Get-Content -Path logs\comoencasa-error.log -Wait -Tail 10"
)
if exist logs\comoencasa-http.log (
    start "Monitor HTTP" cmd /k "echo Logs HTTP && powershell Get-Content -Path logs\comoencasa-http.log -Wait -Tail 10"
)
echo     ✅ Monitores abiertos en ventanas separadas
pause
goto MAIN_MENU

:MONITOR_HTTP
cls
echo     🌐 MONITOREANDO REQUESTS HTTP
echo     Presione Ctrl+C para salir
echo     ═══════════════════════════════════════════════
if exist logs\comoencasa-http.log (
    powershell Get-Content -Path "logs\comoencasa-http.log" -Wait -Tail 20
) else (
    echo     ⚠️ No hay logs HTTP disponibles
    pause
)
goto MAIN_MENU

:MONITOR_AUDIT
cls
echo     🔐 MONITOREANDO AUDITORÍA
echo     Presione Ctrl+C para salir
echo     ═══════════════════════════════════════════════
if exist logs\comoencasa-audit.log (
    powershell Get-Content -Path "logs\comoencasa-audit.log" -Wait -Tail 20
) else (
    echo     ⚠️ No hay logs de auditoría disponibles
    pause
)
goto MAIN_MENU
