@echo off
chcp 65001 >nul
echo ==========================================
echo   RESTAURAR BASE DE DATOS - COMO EN CASA
echo ==========================================

:: Configuración
set MYSQL_USER=root
set DATABASE_NAME=comoencasa_db
set BACKUP_DIR=%~dp0..\backups\database
set XAMPP_PATH=C:\xampp\mysql\bin
set PATH=%XAMPP_PATH%;%PATH%

echo [INFO] Buscando archivos de backup disponibles...
echo.

if not exist "%BACKUP_DIR%" (
    echo [ERROR] Directorio de backups no encontrado: %BACKUP_DIR%
    echo [SOLUTION] Ejecute primero backup-database.bat
    pause
    exit /b 1
)

echo [AVAILABLE] Archivos de backup disponibles:
echo ==========================================
dir "%BACKUP_DIR%\backup_*.sql" /b /od 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] No se encontraron archivos de backup
    echo [SOLUTION] Ejecute primero backup-database.bat
    pause
    exit /b 1
)

echo.
echo ==========================================
echo [HELP] Opciones:
echo   1. Usar el backup mas reciente (RECOMENDADO)
echo   2. Seleccionar archivo manualmente
echo   3. Cancelar operacion
echo.

:MENU_SELECT
set /p "CHOICE=Seleccione una opcion (1, 2 o 3): "

if "%CHOICE%"=="1" goto USE_LATEST
if "%CHOICE%"=="2" goto MANUAL_SELECT
if "%CHOICE%"=="3" goto CANCEL
echo [ERROR] Opcion invalida. Use 1, 2 o 3
goto MENU_SELECT

:USE_LATEST
echo.
echo [AUTO] Seleccionando backup mas reciente...
for /f "delims=" %%i in ('dir "%BACKUP_DIR%\backup_*.sql" /b /od 2^>nul') do set BACKUP_FILE=%%i
if not defined BACKUP_FILE (
    echo [ERROR] No se pudo encontrar backup mas reciente
    goto MANUAL_SELECT
)
echo [SELECTED] %BACKUP_FILE%
goto CONFIRM_RESTORE

:MANUAL_SELECT
echo.
echo ==========================================
echo [INPUT] Ingrese el nombre EXACTO del archivo:
echo.
set /p "BACKUP_FILE=Nombre del archivo: "

:: Limpiar entrada de caracteres extraños
set "BACKUP_FILE=%BACKUP_FILE:´╗┐=%"
set "BACKUP_FILE=%BACKUP_FILE: =%"

if "%BACKUP_FILE%"=="" (
    echo [ERROR] Nombre vacio
    goto MANUAL_SELECT
)

if /i "%BACKUP_FILE%"=="cancel" goto CANCEL
if /i "%BACKUP_FILE%"=="exit" goto CANCEL

if not exist "%BACKUP_DIR%\%BACKUP_FILE%" (
    echo [ERROR] Archivo no encontrado: %BACKUP_FILE%
    echo [HELP] Asegurese de escribir el nombre EXACTO mostrado arriba
    echo [RETRY] Intentando nuevamente...
    goto MANUAL_SELECT
)

:CONFIRM_RESTORE
echo.
echo [WARNING] ADVERTENCIA IMPORTANTE
echo ==========================================
echo Esta operacion:
echo • Eliminara TODOS los datos actuales
echo • Restaurara desde: %BACKUP_FILE%
echo • NO se puede deshacer
echo.

:CONFIRM_LOOP
set /p "CONFIRM=Continuar? (S=Si, N=No): "

if /i "%CONFIRM%"=="S" goto RESTORE
if /i "%CONFIRM%"=="SI" goto RESTORE
if /i "%CONFIRM%"=="Y" goto RESTORE
if /i "%CONFIRM%"=="YES" goto RESTORE
if /i "%CONFIRM%"=="N" goto CANCEL
if /i "%CONFIRM%"=="NO" goto CANCEL

echo [ERROR] Respuesta invalida. Use S para Si o N para No
goto CONFIRM_LOOP

:RESTORE
echo.
echo [PROGRESS] Iniciando restauracion...
echo [STEP 1/3] Eliminando base de datos actual...
mysql -u %MYSQL_USER% --password= -e "DROP DATABASE IF EXISTS %DATABASE_NAME%;" 2>nul

echo [STEP 2/3] Creando nueva base de datos...
mysql -u %MYSQL_USER% --password= -e "CREATE DATABASE %DATABASE_NAME%;" 2>nul

echo [STEP 3/3] Restaurando datos desde backup...
mysql -u %MYSQL_USER% --password= %DATABASE_NAME% < "%BACKUP_DIR%\%BACKUP_FILE%" 2>nul

if %ERRORLEVEL% EQU 0 (
    echo.
    echo [SUCCESS] Base de datos restaurada exitosamente
    echo [INFO] Datos restaurados desde: %BACKUP_FILE%
    echo [INFO] Verifique la aplicacion para confirmar
) else (
    echo.
    echo [ERROR] Error durante la restauracion
    echo [TROUBLESHOOT] Posibles causas:
    echo • MySQL no esta ejecutandose
    echo • Archivo de backup corrupto
    echo • Permisos insuficientes
)

goto END

:CANCEL
echo.
echo [CANCELLED] Operacion cancelada
echo [INFO] No se realizaron cambios

:END
echo.
pause
