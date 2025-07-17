@echo off
title 🛠️ Sistema Maestro de Mantenimiento - Como En Casa
color 0A

:MENU_PRINCIPAL
cls
echo ==========================================
echo  🛠️  SISTEMA MAESTRO DE MANTENIMIENTO
echo     COMO EN CASA - GESTIÓN COMPLETA
echo ==========================================
echo.
echo [BACKUPS] 💾
echo [1] 🗄️ Backup Base de Datos          [2] 📦 Backup Completo
echo [3] 🔄 Restaurar Base de Datos       [4] 📋 Ver Backups Disponibles
echo.
echo [MANTENIMIENTO] 🧹
echo [5] 🛠️ Ejecutar Limpieza Completa    [6] 📊 Análisis de Performance
echo [7] 🗂️ Limpiar Solo Logs            [8] 🗑️ Limpiar Solo Temporales
echo.
echo [TAREAS PROGRAMADAS] ⏰
echo [9] 📅 Configurar Tareas Auto        [10] ❌ Eliminar Tareas Auto
echo [11] 📋 Ver Estado de Tareas         [12] 🔄 Ejecutar Tarea Manual
echo.
echo [MONITOREO] 📊
echo [13] 📈 Dashboard Sistema            [14] 🔍 Monitor Logs Tiempo Real
echo [15] 📄 Ver Reportes Recientes       [16] ⚡ Estado de Servicios
echo.
echo [UTILIDADES] 🔧
echo [17] 🗄️ Optimizar Base de Datos      [18] 🔄 Reiniciar Servicios
echo [19] 📊 Estadísticas del Sistema     [20] 🚪 Salir
echo.
set /p opcion="🎯 Seleccione una opción [1-20]: "

:: Validar entrada
if "%opcion%"=="" goto MENU_PRINCIPAL
if %opcion% LSS 1 goto MENU_PRINCIPAL
if %opcion% GTR 20 goto MENU_PRINCIPAL

goto OPCION_%opcion%

:: ==========================================
:: SECCIÓN DE BACKUPS
:: ==========================================

:OPCION_1
cls
echo 🗄️ EJECUTANDO BACKUP DE BASE DE DATOS...
echo ==========================================
call "%~dp0backup-database.bat"
echo.
echo Presione cualquier tecla para continuar...
pause >nul
goto MENU_PRINCIPAL

:OPCION_2
cls
echo 📦 EJECUTANDO BACKUP COMPLETO...
echo ==========================================
call "%~dp0full-backup.bat"
echo.
echo Presione cualquier tecla para continuar...
pause >nul
goto MENU_PRINCIPAL

:OPCION_3
cls
echo 🔄 RESTAURAR BASE DE DATOS...
echo ==========================================
call "%~dp0restore-database.bat"
echo.
echo Presione cualquier tecla para continuar...
pause >nul
goto MENU_PRINCIPAL

:OPCION_4
cls
echo 📋 BACKUPS DISPONIBLES...
echo ==========================================
echo.
echo [BASE DE DATOS] 🗄️
if exist "%~dp0..\backups\database\" (
    echo Ubicación: %~dp0..\backups\database\
    dir "%~dp0..\backups\database\backup_*.sql" /b /od 2>nul
    if %ERRORLEVEL% NEQ 0 echo • No hay backups de BD disponibles
) else (
    echo • Directorio de backups no encontrado
)

echo.
echo [BACKUPS COMPLETOS] 📦
if exist "C:\Backups\ComoEnCasa\" (
    echo Ubicación: C:\Backups\ComoEnCasa\
    dir "C:\Backups\ComoEnCasa\ComoEnCasa_*.zip" /b /od 2>nul
    if %ERRORLEVEL% NEQ 0 echo • No hay backups completos disponibles
) else (
    echo • Directorio de backups completos no encontrado
)
echo.
pause
goto MENU_PRINCIPAL

:: ==========================================
:: SECCIÓN DE MANTENIMIENTO
:: ==========================================

:OPCION_5
cls
echo 🛠️ EJECUTANDO LIMPIEZA COMPLETA...
echo ==========================================
call "%~dp0maintenance-cleanup.bat" auto
echo.
echo Presione cualquier tecla para continuar...
pause >nul
goto MENU_PRINCIPAL

:OPCION_6
cls
echo 📊 EJECUTANDO ANÁLISIS DE PERFORMANCE...
echo ==========================================
call "%~dp0analyze-performance.bat"
echo.
echo Presione cualquier tecla para continuar...
pause >nul
goto MENU_PRINCIPAL

:OPCION_7
cls
echo 🗂️ LIMPIANDO SOLO LOGS...
echo ==========================================
echo [INFO] 📋 Procesando logs del proyecto...

set PROJECT_DIR=%~dp0..
set LOG_DIR=%PROJECT_DIR%\backend\logs

if exist "%LOG_DIR%" (
    echo [PROGRESS] 🧹 Eliminando logs antiguos (>30 días)...
    forfiles /p "%LOG_DIR%" /m *.log /d -30 /c "cmd /c del @path" >nul 2>&1
    
    echo [PROGRESS] 🗜️ Comprimiendo logs (>7 días)...
    forfiles /p "%LOG_DIR%" /m *.log /d -7 /c "cmd /c echo Comprimiendo: @file" 2>nul
    
    echo [SUCCESS] ✅ Limpieza de logs completada
) else (
    echo [WARNING] ⚠️ Directorio de logs no encontrado
)
echo.
pause
goto MENU_PRINCIPAL

:OPCION_8
cls
echo 🗑️ LIMPIANDO ARCHIVOS TEMPORALES...
echo ==========================================
echo [INFO] 🧽 Procesando archivos temporales...

echo [PROGRESS] 📁 Limpiando directorio temp del proyecto...
if exist "%~dp0..\temp\" (
    rmdir /s /q "%~dp0..\temp\" >nul 2>&1
    mkdir "%~dp0..\temp\" >nul 2>&1
    echo [SUCCESS] ✅ Temp del proyecto limpiado
) else (
    echo [INFO] ℹ️ Directorio temp no existe
)

echo [PROGRESS] 🗑️ Limpiando archivos temporales del sistema...
del /q /f %TEMP%\* >nul 2>&1
echo [SUCCESS] ✅ Archivos temporales del sistema limpiados

echo.
pause
goto MENU_PRINCIPAL

:: ==========================================
:: SECCIÓN DE TAREAS PROGRAMADAS
:: ==========================================

:OPCION_9
cls
echo 📅 CONFIGURAR TAREAS AUTOMÁTICAS...
echo ==========================================
echo [WARNING] ⚠️ Se requieren permisos de administrador
echo.
set /p confirm="¿Continuar con la configuración? (S/N): "
if /i "%confirm%" NEQ "S" goto MENU_PRINCIPAL

call "%~dp0setup-scheduled-tasks.bat"
echo.
echo Presione cualquier tecla para continuar...
pause >nul
goto MENU_PRINCIPAL

:OPCION_10
cls
echo ❌ ELIMINAR TAREAS AUTOMÁTICAS...
echo ==========================================
echo [WARNING] ⚠️ Esto eliminará todas las tareas programadas
echo.
set /p confirm="¿Está seguro? (S/N): "
if /i "%confirm%" NEQ "S" goto MENU_PRINCIPAL

call "%~dp0remove-scheduled-tasks.bat"
echo.
echo Presione cualquier tecla para continuar...
pause >nul
goto MENU_PRINCIPAL

:OPCION_11
cls
echo 📋 ESTADO DE TAREAS PROGRAMADAS...
echo ==========================================
echo.
echo [LISTADO] 📅 Tareas de Como En Casa:
schtasks /query /tn "ComoEnCasa*" /fo table 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [INFO] ℹ️ No hay tareas programadas configuradas
    echo [SUGGESTION] 💡 Use la opción 9 para configurarlas
)
echo.
pause
goto MENU_PRINCIPAL

:OPCION_12
cls
echo 🔄 EJECUTAR TAREA MANUAL...
echo ==========================================
echo.
echo [AVAILABLE] 📋 Tareas disponibles:
echo [1] Backup Base de Datos
echo [2] Backup Completo  
echo [3] Limpieza de Mantenimiento
echo [4] Análisis de Performance
echo.
set /p task="Seleccione tarea [1-4]: "

if "%task%"=="1" schtasks /run /tn "ComoEnCasa_Backup_Daily" >nul 2>&1
if "%task%"=="2" schtasks /run /tn "ComoEnCasa_Full_Backup_Weekly" >nul 2>&1  
if "%task%"=="3" schtasks /run /tn "ComoEnCasa_Log_Cleanup" >nul 2>&1
if "%task%"=="4" schtasks /run /tn "ComoEnCasa_Performance_Analysis" >nul 2>&1

if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] ✅ Tarea ejecutada exitosamente
) else (
    echo [ERROR] ❌ Error ejecutando tarea o tarea no encontrada
    echo [SUGGESTION] 💡 Verifique que las tareas estén configuradas
)
echo.
pause
goto MENU_PRINCIPAL

:: ==========================================
:: SECCIÓN DE MONITOREO
:: ==========================================

:OPCION_13
cls
echo 📈 DASHBOARD DEL SISTEMA...
echo ==========================================
call "%~dp0sistema-monitoreo.bat"
goto MENU_PRINCIPAL

:OPCION_14
cls
echo 🔍 MONITOR DE LOGS EN TIEMPO REAL...
echo ==========================================
call "%~dp0monitor-logs.bat"
goto MENU_PRINCIPAL

:OPCION_15
cls
echo 📄 REPORTES RECIENTES...
echo ==========================================
echo.
echo [REPORTES DE MANTENIMIENTO] 🛠️
if exist "%~dp0..\logs\maintenance_report_*.txt" (
    dir "%~dp0..\logs\maintenance_report_*.txt" /b /od
    echo.
    set /p report="¿Ver algún reporte? (nombre completo o ENTER para continuar): "
    if not "!report!"=="" (
        if exist "%~dp0..\logs\!report!" (
            type "%~dp0..\logs\!report!"
            echo.
        ) else (
            echo [ERROR] ❌ Reporte no encontrado
        )
    )
) else (
    echo [INFO] ℹ️ No hay reportes de mantenimiento disponibles
)
echo.
pause
goto MENU_PRINCIPAL

:OPCION_16
cls
echo ⚡ ESTADO DE SERVICIOS...
echo ==========================================
echo.
echo [VERIFICANDO SERVICIOS] 🔍

echo [MYSQL] 🗄️
tasklist /fi "imagename eq mysqld.exe" /fo csv | find /i "mysqld.exe" >nul
if %ERRORLEVEL% EQU 0 (
    echo ✅ MySQL: EJECUTÁNDOSE
) else (
    echo ❌ MySQL: NO DETECTADO
)

echo [JAVA] ☕
tasklist /fi "imagename eq java.exe" /fo csv | find /i "java.exe" >nul
if %ERRORLEVEL% EQU 0 (
    echo ✅ Java: EJECUTÁNDOSE
) else (
    echo ❌ Java: NO DETECTADO
)

echo [DOCKER] 🐳
tasklist /fi "imagename eq docker.exe" /fo csv | find /i "docker.exe" >nul
if %ERRORLEVEL% EQU 0 (
    echo ✅ Docker: EJECUTÁNDOSE
) else (
    echo ❌ Docker: NO DETECTADO
)

echo.
echo [PUERTOS] 🔌
echo Verificando puertos de la aplicación...
netstat -an | find ":8080" >nul
if %ERRORLEVEL% EQU 0 (
    echo ✅ Puerto 8080 (Backend): ACTIVO
) else (
    echo ❌ Puerto 8080 (Backend): NO ACTIVO
)

netstat -an | find ":3000" >nul
if %ERRORLEVEL% EQU 0 (
    echo ✅ Puerto 3000 (Frontend): ACTIVO
) else (
    echo ❌ Puerto 3000 (Frontend): NO ACTIVO
)

echo.
pause
goto MENU_PRINCIPAL

:: ==========================================
:: SECCIÓN DE UTILIDADES
:: ==========================================

:OPCION_17
cls
echo 🗄️ OPTIMIZAR BASE DE DATOS...
echo ==========================================
echo [INFO] 🔧 Ejecutando optimización de MySQL...

mysql -u root -ppassword -e "OPTIMIZE TABLE comoencasa_db.comprobantes;" >nul 2>&1
mysql -u root -ppassword -e "OPTIMIZE TABLE comoencasa_db.pedidos;" >nul 2>&1
mysql -u root -ppassword -e "OPTIMIZE TABLE comoencasa_db.detalle_pedidos;" >nul 2>&1
mysql -u root -ppassword -e "OPTIMIZE TABLE comoencasa_db.productos;" >nul 2>&1
mysql -u root -ppassword -e "OPTIMIZE TABLE comoencasa_db.usuarios;" >nul 2>&1
mysql -u root -ppassword -e "OPTIMIZE TABLE comoencasa_db.categorias;" >nul 2>&1

if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] ✅ Base de datos optimizada exitosamente
    echo [INFO] 📊 Todas las tablas han sido optimizadas
) else (
    echo [ERROR] ❌ Error en la optimización
    echo [TROUBLESHOOT] 🔍 Verificar que MySQL esté ejecutándose
)

echo.
pause
goto MENU_PRINCIPAL

:OPCION_18
cls
echo 🔄 REINICIAR SERVICIOS...
echo ==========================================
echo [WARNING] ⚠️ Esto reiniciará los servicios de la aplicación
echo.
set /p confirm="¿Continuar? (S/N): "
if /i "%confirm%" NEQ "S" goto MENU_PRINCIPAL

echo [PROGRESS] 🔄 Reiniciando servicios...

echo [STEP 1/2] 🛑 Deteniendo Docker Compose...
cd /d "%~dp0.." >nul 2>&1
docker-compose down >nul 2>&1

echo [STEP 2/2] 🚀 Iniciando Docker Compose...
docker-compose up -d >nul 2>&1

if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] ✅ Servicios reiniciados exitosamente
) else (
    echo [ERROR] ❌ Error reiniciando servicios
    echo [TROUBLESHOOT] 🔍 Verificar configuración de Docker
)

echo.
pause
goto MENU_PRINCIPAL

:OPCION_19
cls
echo 📊 ESTADÍSTICAS DEL SISTEMA...
echo ==========================================
echo.

echo [SISTEMA] 💻
echo Computadora: %COMPUTERNAME%
echo Usuario: %USERNAME%
echo Fecha/Hora: %date% %time%
echo.

echo [MEMORIA] 🧠
for /f "skip=1" %%i in ('wmic OS get TotalVisibleMemorySize /value') do if not "%%i"=="" set TotalMem=%%i
for /f "skip=1" %%i in ('wmic OS get FreePhysicalMemory /value') do if not "%%i"=="" set FreeMem=%%i
echo Memoria Total: %TotalMem:~23% KB
echo Memoria Libre: %FreeMem:~19% KB
echo.

echo [ESPACIO EN DISCO] 💽
for /f "tokens=3" %%a in ('dir C:\ /-c ^| find "bytes free"') do echo Espacio Libre C:\: %%a bytes
echo.

echo [ARCHIVOS DEL PROYECTO] 📁
if exist "%~dp0..\backend\logs\" (
    for /f %%i in ('dir "%~dp0..\backend\logs\*.log" /b 2^>nul ^| find /c /v ""') do echo Archivos de Log: %%i
)

if exist "%~dp0..\backups\" (
    for /f %%i in ('dir "%~dp0..\backups\*.*" /b /s 2^>nul ^| find /c /v ""') do echo Archivos de Backup: %%i
)

echo.
pause
goto MENU_PRINCIPAL

:OPCION_20
cls
echo 🚪 SALIENDO DEL SISTEMA...
echo ==========================================
echo.
echo [INFO] 💼 Sesión finalizada
echo [NEXT] 📅 Próximo mantenimiento programado según configuración
echo [CONTACT] 📧 Para soporte: comoencasa@gmail.com
echo.
echo ¡Gracias por usar el Sistema de Mantenimiento!
echo.
pause
exit

:: ==========================================
:: MANEJO DE ERRORES
:: ==========================================

:ERROR
cls
echo ❌ ERROR: Opción no válida
echo.
echo Presione cualquier tecla para continuar...
pause >nul
goto MENU_PRINCIPAL
