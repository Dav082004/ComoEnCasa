@echo off
echo ==========================================
echo   RESTAURAR BASE DE DATOS - COMO EN CASA
echo ==========================================

:: Configuración
set MYSQL_USER=root
set MYSQL_PASSWORD=password
set DATABASE_NAME=comoencasa_db
set BACKUP_DIR=%~dp0..\backups\database

echo [INFO] 🔍 Buscando archivos de backup disponibles...
echo.

if not exist "%BACKUP_DIR%" (
    echo [ERROR] ❌ Directorio de backups no encontrado: %BACKUP_DIR%
    echo [SOLUTION] 💡 Ejecute primero backup-database.bat
    pause
    exit /b 1
)

echo [AVAILABLE] 📁 Archivos de backup disponibles:
echo ==========================================
dir "%BACKUP_DIR%\backup_*.sql" /b /od 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] ❌ No se encontraron archivos de backup
    echo [SOLUTION] 💡 Ejecute primero backup-database.bat
    pause
    exit /b 1
)

echo.
echo ==========================================
set /p BACKUP_FILE="📝 Ingrese el nombre completo del archivo: "

if not exist "%BACKUP_DIR%\%BACKUP_FILE%" (
    echo [ERROR] ❌ Archivo no encontrado: %BACKUP_FILE%
    echo [LOCATION] 📁 Verifique que esté en: %BACKUP_DIR%
    pause
    exit /b 1
)

echo.
echo [WARNING] ⚠️ ADVERTENCIA IMPORTANTE ⚠️
echo ==========================================
echo Esta operación:
echo • Sobrescribirá TODOS los datos actuales
echo • Eliminará información no respaldada
echo • No se puede deshacer fácilmente
echo.
echo [DATABASE] 🗄️ Base de datos: %DATABASE_NAME%
echo [BACKUP] 📄 Archivo: %BACKUP_FILE%
echo.

:CONFIRM
set /p CONFIRM="❓ ¿Desea continuar? (SI/NO): "
if /i "%CONFIRM%"=="SI" goto RESTORE
if /i "%CONFIRM%"=="NO" goto CANCEL
echo [ERROR] ❌ Respuesta inválida. Escriba SI o NO
goto CONFIRM

:RESTORE
echo.
echo [PROGRESS] 🔄 Iniciando restauración...
echo [STEP 1/3] 🗑️ Eliminando base de datos actual...
mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% -e "DROP DATABASE IF EXISTS %DATABASE_NAME%;" 2>nul

echo [STEP 2/3] 🏗️ Creando nueva base de datos...
mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% -e "CREATE DATABASE %DATABASE_NAME%;" 2>nul

echo [STEP 3/3] 📥 Restaurando datos desde backup...
mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% %DATABASE_NAME% < "%BACKUP_DIR%\%BACKUP_FILE%" 2>nul

if %ERRORLEVEL% EQU 0 (
    echo.
    echo [SUCCESS] ✅ Base de datos restaurada exitosamente
    echo [INFO] 🎯 Datos restaurados desde: %BACKUP_FILE%
    echo [INFO] 📊 Verifique la integridad de los datos en la aplicación
) else (
    echo.
    echo [ERROR] ❌ Error durante la restauración
    echo [TROUBLESHOOT] 🔧 Posibles causas:
    echo • MySQL no está ejecutándose
    echo • Credenciales incorrectas
    echo • Archivo de backup corrupto
    echo • Permisos insuficientes
)

goto END

:CANCEL
echo.
echo [CANCELLED] ❌ Operación cancelada por el usuario
echo [INFO] ℹ️ No se realizaron cambios en la base de datos

:END
echo.
pause
