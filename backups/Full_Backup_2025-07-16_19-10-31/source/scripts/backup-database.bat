@echo off
echo ==========================================
echo     BACKUP BASE DE DATOS - COMO EN CASA
echo ==========================================

:: Configuración
set MYSQL_USER=root
set MYSQL_PASSWORD=
set DATABASE_NAME=comoencasa_db
set BACKUP_DIR=%~dp0..\backups\database
set DATE_TIME=%date:~10,4%-%date:~4,2%-%date:~7,2%_%time:~0,2%-%time:~3,2%-%time:~6,2%
set DATE_TIME=%DATE_TIME: =0%

:: Configuración de XAMPP MySQL
set XAMPP_PATH=C:\xampp\mysql\bin
set PATH=%XAMPP_PATH%;%PATH%

:: Crear directorio si no existe
if not exist "%BACKUP_DIR%" mkdir "%BACKUP_DIR%"

echo [INFO] 📅 Iniciando backup de la base de datos...
echo [INFO] 🗂️ Base de datos: %DATABASE_NAME%
echo [INFO] 📁 Directorio: %BACKUP_DIR%
echo [INFO] ⏰ Timestamp: %DATE_TIME%
echo.

:: Realizar backup
echo [PROGRESS] 💾 Creando backup...
echo [DEBUG] 🔍 Verificando conexión MySQL...
echo [DEBUG] 📍 Usando ruta: %XAMPP_PATH%
echo [DEBUG] 🔑 Usuario: %MYSQL_USER% (sin contraseña)
echo.

:: Comando mysqldump con contraseña vacía
mysqldump -u %MYSQL_USER% --password= %DATABASE_NAME% > "%BACKUP_DIR%\backup_%DATABASE_NAME%_%DATE_TIME%.sql" 2>error.log

if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] ✅ Backup creado exitosamente
    echo [INFO] 📄 Archivo: backup_%DATABASE_NAME%_%DATE_TIME%.sql
    
    :: Calcular tamaño del archivo
    for %%F in ("%BACKUP_DIR%\backup_%DATABASE_NAME%_%DATE_TIME%.sql") do (
        echo [INFO] 📊 Tamaño: %%~zF bytes
    )
    
    :: Limpiar archivo de error si existe
    if exist error.log del error.log
) else (
    echo [ERROR] ❌ Error al crear el backup
    echo [ERROR] 🔍 Verificar que MySQL esté ejecutándose
    echo [ERROR] 🔍 Verificar credenciales de conexión
    echo [ERROR] 🔍 Verificar que XAMPP esté instalado en C:\xampp\
    echo.
    if exist error.log (
        echo [DEBUG] 📜 Detalles del error:
        type error.log
        del error.log
    )
    pause
    exit /b 1
)

echo.
echo [MAINTENANCE] 🧹 Limpiando backups antiguos...
:: Mantener solo los últimos 7 backups
forfiles /p "%BACKUP_DIR%" /m backup_*.sql /d -7 /c "cmd /c del @path" 2>nul
if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] ✅ Backups antiguos eliminados (>7 días)
) else (
    echo [INFO] ℹ️ No hay backups antiguos para eliminar
)

echo.
echo [COMPLETE] 🎉 Proceso de backup completado
echo [NEXT] 📌 Próximo backup programado: mañana a las 2:00 AM
echo.
pause
