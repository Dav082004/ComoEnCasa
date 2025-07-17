@echo off
echo ==========================================
echo    ELIMINAR TAREAS PROGRAMADAS
echo ==========================================

:: Verificar permisos de administrador
net session >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] ❌ Se requieren permisos de administrador
    pause
    exit /b 1
)

echo [INFO] 🗑️ Eliminando todas las tareas programadas de Como En Casa...
echo.

:: Eliminar todas las tareas
schtasks /delete /tn "ComoEnCasa_Backup_Daily" /f >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [DELETED] ✅ ComoEnCasa_Backup_Daily
) else (
    echo [NOT_FOUND] ❌ ComoEnCasa_Backup_Daily no encontrada
)

schtasks /delete /tn "ComoEnCasa_Full_Backup_Weekly" /f >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [DELETED] ✅ ComoEnCasa_Full_Backup_Weekly
) else (
    echo [NOT_FOUND] ❌ ComoEnCasa_Full_Backup_Weekly no encontrada
)

schtasks /delete /tn "ComoEnCasa_Log_Cleanup" /f >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [DELETED] ✅ ComoEnCasa_Log_Cleanup
) else (
    echo [NOT_FOUND] ❌ ComoEnCasa_Log_Cleanup no encontrada
)

schtasks /delete /tn "ComoEnCasa_Performance_Analysis" /f >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [DELETED] ✅ ComoEnCasa_Performance_Analysis
) else (
    echo [NOT_FOUND] ❌ ComoEnCasa_Performance_Analysis no encontrada
)

echo.
echo [COMPLETE] 🎯 Proceso completado
echo [INFO] ℹ️ Verificando tareas restantes...

schtasks /query /tn "ComoEnCasa*" >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [SUCCESS] ✅ Todas las tareas de Como En Casa han sido eliminadas
) else (
    echo [WARNING] ⚠️ Algunas tareas pueden permanecer
    schtasks /query /tn "ComoEnCasa*"
)

echo.
pause
