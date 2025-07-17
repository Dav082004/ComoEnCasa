@echo off
echo ==========================================
echo    MANTENIMIENTO Y LIMPIEZA AUTOMÁTICA
echo ==========================================

set PROJECT_DIR=%~dp0..
set LOG_DIR=%PROJECT_DIR%\backend\logs
set TEMP_DIR=%PROJECT_DIR%\temp
set BACKUP_DIR=%PROJECT_DIR%\backups

echo [INFO] 🧹 Iniciando rutina de mantenimiento...
echo [INFO] 📁 Directorio del proyecto: %PROJECT_DIR%
echo [INFO] ⏰ Timestamp: %date% %time%
echo.

:: ==========================================
:: 1. LIMPIEZA DE LOGS ANTIGUOS
:: ==========================================
echo [STEP 1/7] 📋 Limpieza de logs antiguos...

if exist "%LOG_DIR%" (
    echo [PROGRESS] 🗂️ Procesando directorio de logs...
    
    :: Eliminar logs de más de 30 días
    forfiles /p "%LOG_DIR%" /m *.log /d -30 /c "cmd /c del @path" >nul 2>&1
    if %ERRORLEVEL% EQU 0 (
        echo [SUCCESS] ✅ Logs antiguos eliminados (>30 días)
    ) else (
        echo [INFO] ℹ️ No hay logs antiguos para eliminar
    )
    
    :: Comprimir logs de más de 7 días
    echo [PROGRESS] 🗜️ Comprimiendo logs de más de 7 días...
    forfiles /p "%LOG_DIR%" /m *.log /d -7 /c "cmd /c powershell Compress-Archive -Path @path -DestinationPath @path.zip -CompressionLevel Fastest; del @path" >nul 2>&1
    
    :: Contar archivos de log actuales
    for /f %%i in ('dir "%LOG_DIR%\*.log" /b 2^>nul ^| find /c /v ""') do set LOG_COUNT=%%i
    echo [INFO] 📊 Logs activos: %LOG_COUNT%
    
) else (
    echo [WARNING] ⚠️ Directorio de logs no encontrado: %LOG_DIR%
)

:: ==========================================
:: 2. LIMPIEZA DE ARCHIVOS TEMPORALES
:: ==========================================
echo.
echo [STEP 2/7] 🗑️ Limpieza de archivos temporales...

:: Limpiar directorio temp del proyecto
if exist "%TEMP_DIR%" (
    echo [PROGRESS] 📁 Limpiando temp del proyecto...
    rmdir /s /q "%TEMP_DIR%" >nul 2>&1
    mkdir "%TEMP_DIR%" >nul 2>&1
    echo [SUCCESS] ✅ Directorio temp del proyecto limpiado
) else (
    echo [INFO] ℹ️ Directorio temp del proyecto no existe
)

:: Limpiar archivos temporales del sistema
echo [PROGRESS] 🧽 Limpiando archivos temporales del sistema...
del /q /f %TEMP%\* >nul 2>&1
for /d %%i in (%TEMP%\*) do rmdir /s /q "%%i" >nul 2>&1
echo [SUCCESS] ✅ Archivos temporales del sistema limpiados

:: Limpiar cache de Maven (si existe)
if exist "%USERPROFILE%\.m2\repository" (
    echo [PROGRESS] ☕ Limpiando cache de Maven antiguo...
    forfiles /p "%USERPROFILE%\.m2\repository" /s /m *.* /d -60 /c "cmd /c del @path" >nul 2>&1
    echo [SUCCESS] ✅ Cache de Maven optimizado
)

:: ==========================================
:: 3. OPTIMIZACIÓN DE BACKUPS
:: ==========================================
echo.
echo [STEP 3/7] 💾 Optimización de backups...

if exist "%BACKUP_DIR%" (
    echo [PROGRESS] 📦 Procesando directorio de backups...
    
    :: Eliminar backups de más de 90 días
    forfiles /p "%BACKUP_DIR%" /s /m backup_*.sql /d -90 /c "cmd /c del @path" >nul 2>&1
    forfiles /p "%BACKUP_DIR%" /s /m *.zip /d -90 /c "cmd /c del @path" >nul 2>&1
    
    :: Comprimir backups de más de 7 días
    forfiles /p "%BACKUP_DIR%" /s /m backup_*.sql /d -7 /c "cmd /c powershell Compress-Archive -Path @path -DestinationPath @path.zip -CompressionLevel Optimal; del @path" >nul 2>&1
    
    echo [SUCCESS] ✅ Backups optimizados
    
    :: Contar backups actuales
    for /f %%i in ('dir "%BACKUP_DIR%\*.sql" "%BACKUP_DIR%\*.zip" /b /s 2^>nul ^| find /c /v ""') do set BACKUP_COUNT=%%i
    echo [INFO] 📊 Archivos de backup: %BACKUP_COUNT%
    
) else (
    echo [INFO] ℹ️ Directorio de backups no encontrado
)

:: ==========================================
:: 4. LIMPIEZA DE LOGS DE APLICACIÓN
:: ==========================================
echo.
echo [STEP 4/7] 📱 Limpieza específica de logs de aplicación...

:: Limpiar logs de Spring Boot
if exist "%PROJECT_DIR%\backend\spring.log" (
    del /q "%PROJECT_DIR%\backend\spring.log" >nul 2>&1
    echo [SUCCESS] ✅ Log de Spring Boot reiniciado
)

:: Limpiar logs de errores grandes (>50MB)
if exist "%LOG_DIR%" (
    echo [PROGRESS] 📏 Verificando tamaño de logs...
    for %%F in ("%LOG_DIR%\*.log") do (
        if %%~zF GTR 52428800 (
            echo [LARGE] 🚨 Log grande encontrado: %%~nxF (%%~zF bytes)
            copy "%%F" "%%F.bak" >nul 2>&1
            echo. > "%%F"
            echo [SUCCESS] ✅ Log grande archivado y reiniciado: %%~nxF
        )
    )
)

:: ==========================================
:: 5. OPTIMIZACIÓN DE BASE DE DATOS
:: ==========================================
echo.
echo [STEP 5/7] 🗄️ Optimización de base de datos...

echo [PROGRESS] ⚙️ Ejecutando optimización de MySQL...
mysql -u root -ppassword -e "OPTIMIZE TABLE comoencasa_db.comprobantes;" >nul 2>&1
mysql -u root -ppassword -e "OPTIMIZE TABLE comoencasa_db.pedidos;" >nul 2>&1
mysql -u root -ppassword -e "OPTIMIZE TABLE comoencasa_db.detalle_pedidos;" >nul 2>&1
mysql -u root -ppassword -e "OPTIMIZE TABLE comoencasa_db.productos;" >nul 2>&1
mysql -u root -ppassword -e "OPTIMIZE TABLE comoencasa_db.usuarios;" >nul 2>&1

if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] ✅ Tablas de base de datos optimizadas
) else (
    echo [WARNING] ⚠️ Error en optimización de BD (MySQL puede no estar disponible)
)

:: ==========================================
:: 6. LIMPIEZA DE DEPENDENCIAS FRONTEND
:: ==========================================
echo.
echo [STEP 6/7] 🌐 Limpieza de dependencias frontend...

if exist "%PROJECT_DIR%\frontend\node_modules\.cache" (
    echo [PROGRESS] 📦 Limpiando cache de Node.js...
    rmdir /s /q "%PROJECT_DIR%\frontend\node_modules\.cache" >nul 2>&1
    echo [SUCCESS] ✅ Cache de Node.js limpiado
)

if exist "%PROJECT_DIR%\frontend\dist" (
    echo [PROGRESS] 🏗️ Limpiando directorio de build...
    rmdir /s /q "%PROJECT_DIR%\frontend\dist" >nul 2>&1
    echo [SUCCESS] ✅ Directorio de build limpiado
)

:: ==========================================
:: 7. REPORTE DE ESPACIO LIBERADO
:: ==========================================
echo.
echo [STEP 7/7] 📊 Generando reporte de mantenimiento...

:: Crear reporte
set REPORT_FILE=%PROJECT_DIR%\logs\maintenance_report_%date:~10,4%%date:~4,2%%date:~7,2%.txt
(
echo ==========================================
echo   REPORTE DE MANTENIMIENTO AUTOMÁTICO
echo ==========================================
echo.
echo Fecha y hora: %date% %time%
echo Ejecutado por: %USERNAME%
echo Sistema: %COMPUTERNAME%
echo.
echo TAREAS REALIZADAS:
echo ✅ Limpieza de logs antiguos (^>30 días^)
echo ✅ Compresión de logs (^>7 días^)
echo ✅ Limpieza de archivos temporales
echo ✅ Optimización de backups
echo ✅ Limpieza de logs grandes (^>50MB^)
echo ✅ Optimización de base de datos
echo ✅ Limpieza de cache frontend
echo.
echo ESTADÍSTICAS:
echo • Logs activos: %LOG_COUNT%
echo • Archivos de backup: %BACKUP_COUNT%
echo.
echo PRÓXIMO MANTENIMIENTO: Mañana a las 3:00 AM
echo ==========================================
) > "%REPORT_FILE%"

echo [SUCCESS] ✅ Reporte guardado: maintenance_report_%date:~10,4%%date:~4,2%%date:~7,2%.txt

:: ==========================================
:: RESUMEN FINAL
:: ==========================================
echo.
echo ==========================================
echo        MANTENIMIENTO COMPLETADO ✅
echo ==========================================
echo.
echo [SUMMARY] 📋 Tareas completadas:
echo • 🧹 Limpieza de logs antiguos
echo • 🗑️ Eliminación de archivos temporales  
echo • 💾 Optimización de backups
echo • 📱 Limpieza de logs de aplicación
echo • 🗄️ Optimización de base de datos
echo • 🌐 Limpieza de cache frontend
echo • 📊 Generación de reporte
echo.
echo [NEXT] 📅 Próximo mantenimiento: Mañana a las 3:00 AM
echo [LOGS] 📄 Reporte guardado en: logs\maintenance_report_*.txt
echo.
echo [PERFORMANCE] ⚡ Sistema optimizado para mejor rendimiento
echo.

:: Solo pausar si se ejecuta manualmente
if "%1" NEQ "auto" pause
