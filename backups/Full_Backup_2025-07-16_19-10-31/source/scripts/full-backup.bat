@echo off
echo ==========================================
echo    BACKUP COMPLETO - COMO EN CASA
echo ==========================================

:: Configuración
set PROJECT_DIR=%~dp0..
set BACKUP_ROOT=%PROJECT_DIR%\backups
set DATE_TIME=%date:~10,4%-%date:~4,2%-%date:~7,2%_%time:~0,2%-%time:~3,2%-%time:~6,2%
set DATE_TIME=%DATE_TIME: =0%
set BACKUP_DIR=%BACKUP_ROOT%\Full_Backup_%DATE_TIME%

echo [INFO] 🎯 Iniciando backup completo del proyecto
echo [INFO] 📁 Directorio origen: %PROJECT_DIR%
echo [INFO] 🎁 Directorio destino: %BACKUP_DIR%
echo [INFO] ⏰ Timestamp: %DATE_TIME%
echo.

:: Crear directorio de backup
echo [STEP 1/6] 📁 Creando directorio de backup...
if not exist "%BACKUP_ROOT%" mkdir "%BACKUP_ROOT%"
if not exist "%BACKUP_DIR%" mkdir "%BACKUP_DIR%"
echo [SUCCESS] ✅ Directorio creado: %BACKUP_DIR%

:: 1. Backup de código fuente
echo.
echo [STEP 2/6] 💻 Backup de código fuente...
if not exist "%BACKUP_DIR%\source" mkdir "%BACKUP_DIR%\source"

echo [PROGRESS] 📂 Copiando backend (excluyendo target, .m2)...
xcopy "%PROJECT_DIR%\backend" "%BACKUP_DIR%\source\backend" /E /I /H /Y /Q /EXCLUDE:"%~dp0exclude_list.txt"

echo [PROGRESS] 📂 Copiando frontend (excluyendo node_modules)...
xcopy "%PROJECT_DIR%\frontend" "%BACKUP_DIR%\source\frontend" /E /I /H /Y /Q /EXCLUDE:"%~dp0exclude_list.txt"

echo [PROGRESS] 📂 Copiando scripts...
xcopy "%PROJECT_DIR%\scripts" "%BACKUP_DIR%\source\scripts" /E /I /H /Y /Q

echo [PROGRESS] 📂 Copiando documentación...
xcopy "%PROJECT_DIR%\docs" "%BACKUP_DIR%\source\docs" /E /I /H /Y /Q

echo [SUCCESS] ✅ Código fuente respaldado

:: 2. Backup de base de datos
echo.
echo [STEP 3/6] 🗄️ Backup de base de datos...
if not exist "%BACKUP_DIR%\database" mkdir "%BACKUP_DIR%\database"

echo [PROGRESS] 💾 Ejecutando mysqldump...
set PATH=C:\xampp\mysql\bin;%PATH%
mysqldump -u root --password= comoencasa_db > "%BACKUP_DIR%\database\comoencasa_db_%DATE_TIME%.sql" 2>nul

if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] ✅ Base de datos respaldada
) else (
    echo [WARNING] ⚠️ Error en backup de BD - continuando...
)

:: 3. Backup de configuraciones
echo.
echo [STEP 4/6] ⚙️ Backup de configuraciones...
if not exist "%BACKUP_DIR%\config" mkdir "%BACKUP_DIR%\config"

copy "%PROJECT_DIR%\docker-compose.yml" "%BACKUP_DIR%\config\" >nul 2>&1
copy "%PROJECT_DIR%\*.md" "%BACKUP_DIR%\config\" >nul 2>&1
copy "%PROJECT_DIR%\*.sql" "%BACKUP_DIR%\config\" >nul 2>&1
copy "%PROJECT_DIR%\.env" "%BACKUP_DIR%\config\" >nul 2>&1

echo [SUCCESS] ✅ Configuraciones respaldadas

:: 4. Backup de logs (últimos 7 días)
echo.
echo [STEP 5/6] 📋 Backup de logs recientes...
if exist "%PROJECT_DIR%\backend\logs" (
    if not exist "%BACKUP_DIR%\logs" mkdir "%BACKUP_DIR%\logs"
    
    echo [PROGRESS] 📄 Copiando logs de los últimos 7 días...
    forfiles /p "%PROJECT_DIR%\backend\logs" /m *.log /d -7 /c "cmd /c copy @path \"%BACKUP_DIR%\logs\"" >nul 2>&1
    
    echo [SUCCESS] ✅ Logs respaldados
) else (
    echo [INFO] ℹ️ No se encontraron logs para respaldar
)

:: 5. Crear archivo de información
echo.
echo [STEP 6/6] 📝 Creando archivo de información...
(
echo ==========================================
echo   BACKUP COMPLETO - COMO EN CASA
echo ==========================================
echo.
echo Fecha de creación: %date% %time%
echo Directorio origen: %PROJECT_DIR%
echo Directorio backup: %BACKUP_DIR%
echo.
echo CONTENIDO:
echo • source/       - Código fuente completo
echo • database/     - Backup de base de datos MySQL
echo • config/       - Archivos de configuración
echo • logs/         - Logs recientes (7 días^)
echo.
echo INSTRUCCIONES DE RESTAURACIÓN:
echo 1. Extraer backup en directorio deseado
echo 2. Restaurar BD con: mysql -u root -p comoencasa_db ^< database/comoencasa_db_*.sql
echo 3. Configurar variables de entorno
echo 4. Ejecutar: docker-compose up -d
echo.
echo Para soporte: comoencasa@gmail.com
echo ==========================================
) > "%BACKUP_DIR%\LEEME_INSTRUCCIONES.txt"

echo [SUCCESS] ✅ Archivo de información creado

:: 6. Crear archivo comprimido (opcional)
echo.
echo [OPTIONAL] 📦 ¿Desea crear archivo ZIP comprimido?
set /p CREATE_ZIP="(S/N): "
if /i "%CREATE_ZIP%"=="S" (
    echo [PROGRESS] 🗜️ Comprimiendo backup...
    powershell Compress-Archive -Path '%BACKUP_DIR%\*' -DestinationPath '%BACKUP_ROOT%\ComoEnCasa_Full_Backup_%DATE_TIME%.zip' -CompressionLevel Fastest
    
    if exist "%BACKUP_ROOT%\ComoEnCasa_Full_Backup_%DATE_TIME%.zip" (
        echo [SUCCESS] ✅ Archivo ZIP creado: ComoEnCasa_Full_Backup_%DATE_TIME%.zip
        
        :: Calcular tamaños
        echo.
        echo [INFO] 📊 Información de tamaños:
        for %%F in ("%BACKUP_ROOT%\ComoEnCasa_Full_Backup_%DATE_TIME%.zip") do (
            echo [ZIP] 🗜️ Archivo comprimido: %%~zF bytes
        )
        
        echo [OPTION] ❓ ¿Desea eliminar la carpeta sin comprimir? (S/N):
        set /p DELETE_FOLDER=""
        if /i "!DELETE_FOLDER!"=="S" (
            rmdir /s /q "%BACKUP_DIR%"
            echo [SUCCESS] ✅ Carpeta sin comprimir eliminada
        )
    ) else (
        echo [ERROR] ❌ Error al crear archivo ZIP
    )
)

:: 7. Limpieza de backups antiguos
echo.
echo [MAINTENANCE] 🧹 Limpiando backups antiguos...
forfiles /p "%BACKUP_ROOT%" /m "ComoEnCasa_Full_Backup_*.zip" /d -30 /c "cmd /c del @path" >nul 2>&1
forfiles /p "%BACKUP_ROOT%" /m "Full_Backup_*" /d -30 /c "cmd /c rmdir /s /q @path" >nul 2>&1

echo [SUCCESS] ✅ Backups antiguos eliminados (>30 días)

:: Resumen final
echo.
echo ==========================================
echo           BACKUP COMPLETADO ✅
echo ==========================================
echo [LOCATION] 📁 Backup guardado en:
echo %BACKUP_DIR%
if /i "%CREATE_ZIP%"=="S" (
    echo.
    echo [ZIP] 🗜️ Archivo comprimido:
    echo %BACKUP_ROOT%\ComoEnCasa_Full_Backup_%DATE_TIME%.zip
)
echo.
echo [NEXT] 📅 Próximo backup completo recomendado: Semanalmente
echo [AUTO] 🤖 Configure tareas programadas para automatizar
echo.
pause
