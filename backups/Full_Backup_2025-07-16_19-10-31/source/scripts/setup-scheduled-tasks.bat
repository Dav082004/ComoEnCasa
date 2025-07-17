@echo off
echo ==========================================
echo   CONFIGURAR TAREAS PROGRAMADAS
echo ==========================================
echo ADVERTENCIA: Ejecutar como ADMINISTRADOR
echo.

:: Verificar permisos de administrador
net session >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] ❌ Se requieren permisos de administrador
    echo [SOLUTION] 💡 Ejecute este script como administrador
    pause
    exit /b 1
)

set SCRIPT_DIR=%~dp0
echo [INFO] 📁 Directorio de scripts: %SCRIPT_DIR%
echo.

echo ==========================================
echo    CONFIGURANDO TAREAS AUTOMÁTICAS
echo ==========================================

:: 1. Backup diario de base de datos
echo [TASK 1/4] 💾 Configurando backup diario de BD...
schtasks /create /tn "ComoEnCasa_Backup_Daily" /tr "\"%SCRIPT_DIR%backup-database.bat\"" /sc daily /st 02:00 /ru SYSTEM /f >nul 2>&1

if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] ✅ Backup diario programado: 2:00 AM
) else (
    echo [ERROR] ❌ Error configurando backup diario
)

:: 2. Backup completo semanal
echo [TASK 2/4] 📦 Configurando backup completo semanal...
schtasks /create /tn "ComoEnCasa_Full_Backup_Weekly" /tr "\"%SCRIPT_DIR%full-backup.bat\"" /sc weekly /d SUN /st 01:00 /ru SYSTEM /f >nul 2>&1

if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] ✅ Backup semanal programado: Domingos 1:00 AM
) else (
    echo [ERROR] ❌ Error configurando backup semanal
)

:: 3. Mantenimiento de logs
echo [TASK 3/4] 🧹 Configurando limpieza de logs...
schtasks /create /tn "ComoEnCasa_Log_Cleanup" /tr "\"%SCRIPT_DIR%maintenance-cleanup.bat\"" /sc daily /st 03:00 /ru SYSTEM /f >nul 2>&1

if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] ✅ Limpieza de logs programada: 3:00 AM
) else (
    echo [ERROR] ❌ Error configurando limpieza de logs
)

:: 4. Monitoreo de performance
echo [TASK 4/4] 📊 Configurando análisis de performance...
schtasks /create /tn "ComoEnCasa_Performance_Analysis" /tr "\"%SCRIPT_DIR%analyze-performance.bat\"" /sc daily /st 23:00 /ru SYSTEM /f >nul 2>&1

if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] ✅ Análisis de performance programado: 11:00 PM
) else (
    echo [ERROR] ❌ Error configurando análisis de performance
)

echo.
echo ==========================================
echo         RESUMEN DE TAREAS CREADAS
echo ==========================================
echo.
echo [LISTADO] 📋 Tareas programadas:
schtasks /query /tn "ComoEnCasa*" /fo table

echo.
echo ==========================================
echo           COMANDOS ÚTILES
echo ==========================================
echo.
echo Para gestionar las tareas:
echo • Ver todas las tareas: schtasks /query /tn "ComoEnCasa*"
echo • Ejecutar manualmente: schtasks /run /tn "ComoEnCasa_Backup_Daily"
echo • Eliminar tarea: schtasks /delete /tn "ComoEnCasa_Backup_Daily" /f
echo • Deshabilitar tarea: schtasks /change /tn "ComoEnCasa_Backup_Daily" /disable
echo.
echo [SUCCESS] 🎉 Configuración de tareas completada
echo [NOTE] 📝 Las tareas se ejecutarán automáticamente según programación
echo.
pause

:: restore-database.bat
@echo off
set MYSQL_USER=root
set MYSQL_PASSWORD=tu_password
set DATABASE_NAME=comoencasa_db
set BACKUP_FILE=C:\backups\database\comoencasa_db_2025-07-16_02-00.sql

echo Restaurando base de datos...
mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% -e "DROP DATABASE IF EXISTS %DATABASE_NAME%;"
mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% -e "CREATE DATABASE %DATABASE_NAME%;"
mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% %DATABASE_NAME% < "%BACKUP_FILE%"
echo Restauración completada.


// Archivo: SystemMonitoringService.java
@Service
@Slf4j
public class SystemMonitoringService {

    @Scheduled(fixedRate = 300000) // cada 5 minutos
    public void reportMetrics() {
        // Lógica para obtener métricas y registrarlas en logs
        log.info("Métricas recopiladas: CPU={}, MEM={}", cpuLoad, memoryUsage);
    }

    @Scheduled(cron = "0 0 2 * * *") // 2:00 AM diario
    public void scheduleAutomaticDatabaseBackup() {
        // Lanza script batch para backup de base de datos
        log.info("Iniciando backup automático de base de datos...");
    }
}
