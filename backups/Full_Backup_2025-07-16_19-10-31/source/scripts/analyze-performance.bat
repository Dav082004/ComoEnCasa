@echo off
echo ==========================================
echo   ANÁLISIS DE PERFORMANCE - COMO EN CASA
echo ==========================================

set LOG_DIR=logs
set REPORT_DIR=performance-reports

if not exist %REPORT_DIR% mkdir %REPORT_DIR%

echo.
echo 🔍 ANALIZANDO PERFORMANCE DEL SISTEMA...
echo.

set REPORT_FILE=%REPORT_DIR%\performance-report-%date:~10,4%-%date:~4,2%-%date:~7,2%.txt

echo REPORTE DE PERFORMANCE - COMO EN CASA > %REPORT_FILE%
echo Generado: %date% %time% >> %REPORT_FILE%
echo ========================================== >> %REPORT_FILE%
echo. >> %REPORT_FILE%

echo 📊 1. ANÁLISIS DE TIEMPO DE RESPUESTA
echo ========================================== >> %REPORT_FILE%

if exist %LOG_DIR%\comoencasa-http.log (
    echo [INFO] Analizando tiempos de respuesta...
    echo Analizando requests HTTP... >> %REPORT_FILE%
    
    powershell -Command "& {
        $content = Get-Content -Path '%LOG_DIR%\comoencasa-http.log' -ErrorAction SilentlyContinue
        if ($content) {
            $durations = $content | Where-Object { $_ -match 'Duration: (\d+)ms' } | ForEach-Object { 
                if ($_ -match 'Duration: (\d+)ms') { [int]$matches[1] }
            }
            if ($durations.Count -gt 0) {
                $avg = ($durations | Measure-Object -Average).Average
                $max = ($durations | Measure-Object -Maximum).Maximum
                $min = ($durations | Measure-Object -Minimum).Minimum
                Write-Output \"Tiempo promedio: $([math]::Round($avg, 2))ms\"
                Write-Output \"Tiempo máximo: ${max}ms\"
                Write-Output \"Tiempo mínimo: ${min}ms\"
                Write-Output \"Total de requests: $($durations.Count)\"
                $slow = ($durations | Where-Object { $_ -gt 1000 }).Count
                Write-Output \"Requests lentos (>1s): $slow\"
            } else {
                Write-Output \"No se encontraron datos de duración\"
            }
        } else {
            Write-Output \"No hay datos de HTTP disponibles\"
        }
    }" >> %REPORT_FILE%
) else (
    echo No hay datos de HTTP disponibles >> %REPORT_FILE%
)

echo. >> %REPORT_FILE%
echo 📊 2. ANÁLISIS DE ERRORES >> %REPORT_FILE%
echo ========================================== >> %REPORT_FILE%

if exist %LOG_DIR%\comoencasa-error.log (
    echo [INFO] Analizando errores...
    
    powershell -Command "& {
        $errorContent = Get-Content -Path '%LOG_DIR%\comoencasa-error.log' -ErrorAction SilentlyContinue
        if ($errorContent) {
            Write-Output \"Total de errores: $($errorContent.Count)\"
            
            # Agrupar errores por tipo
            $errorTypes = @{}
            $errorContent | ForEach-Object {
                if ($_ -match '(\w+Exception|\w+Error)') {
                    $errorType = $matches[1]
                    if ($errorTypes.ContainsKey($errorType)) {
                        $errorTypes[$errorType]++
                    } else {
                        $errorTypes[$errorType] = 1
                    }
                }
            }
            
            Write-Output \"\"
            Write-Output \"Errores por tipo:\"
            $errorTypes.GetEnumerator() | Sort-Object Value -Descending | ForEach-Object {
                Write-Output \"  $($_.Key): $($_.Value)\"
            }
        } else {
            Write-Output \"✅ No hay errores registrados\"
        }
    }" >> %REPORT_FILE%
) else (
    echo ✅ No hay errores registrados >> %REPORT_FILE%
)

echo. >> %REPORT_FILE%
echo 📊 3. ANÁLISIS DE MEMORIA Y RECURSOS >> %REPORT_FILE%
echo ========================================== >> %REPORT_FILE%

echo [INFO] Analizando recursos del sistema...

wmic process where "name='java.exe'" get processid,workingsetsize,pagefileusage /format:csv | findstr /v "Node" >> %REPORT_FILE%

echo. >> %REPORT_FILE%
echo Memoria del sistema: >> %REPORT_FILE%
wmic computersystem get TotalPhysicalMemory /value | findstr "TotalPhysicalMemory" >> %REPORT_FILE%
wmic OS get FreePhysicalMemory /value | findstr "FreePhysicalMemory" >> %REPORT_FILE%

echo. >> %REPORT_FILE%
echo 📊 4. ANÁLISIS DE BASE DE DATOS >> %REPORT_FILE%
echo ========================================== >> %REPORT_FILE%

if exist %LOG_DIR%\comoencasa-database.log (
    echo [INFO] Analizando operaciones de BD...
    
    powershell -Command "& {
        $dbContent = Get-Content -Path '%LOG_DIR%\comoencasa-database.log' -ErrorAction SilentlyContinue
        if ($dbContent) {
            Write-Output \"Total de operaciones BD: $($dbContent.Count)\"
            
            $writes = ($dbContent | Where-Object { $_ -match 'DB_WRITE' }).Count
            $errors = ($dbContent | Where-Object { $_ -match 'DB_ERROR' }).Count
            
            Write-Output \"Operaciones de escritura: $writes\"
            Write-Output \"Errores de BD: $errors\"
            
            if ($errors -gt 0) {
                Write-Output \"\"
                Write-Output \"Últimos errores de BD:\"
                $dbContent | Where-Object { $_ -match 'DB_ERROR' } | Select-Object -Last 5 | ForEach-Object {
                    Write-Output \"  $_\"
                }
            }
        } else {
            Write-Output \"No hay datos de BD disponibles\"
        }
    }" >> %REPORT_FILE%
) else (
    echo No hay datos de BD disponibles >> %REPORT_FILE%
)

echo. >> %REPORT_FILE%
echo 📊 5. ANÁLISIS DE SEGURIDAD >> %REPORT_FILE%
echo ========================================== >> %REPORT_FILE%

if exist %LOG_DIR%\comoencasa-audit.log (
    echo [INFO] Analizando eventos de seguridad...
    
    powershell -Command "& {
        $auditContent = Get-Content -Path '%LOG_DIR%\comoencasa-audit.log' -ErrorAction SilentlyContinue
        if ($auditContent) {
            Write-Output \"Total de eventos de auditoría: $($auditContent.Count)\"
            
            $logins = ($auditContent | Where-Object { $_ -match 'LOGIN|AUTH' }).Count
            $failures = ($auditContent | Where-Object { $_ -match 'FAIL|ERROR' }).Count
            
            Write-Output \"Intentos de login: $logins\"
            Write-Output \"Fallos de seguridad: $failures\"
            
            if ($failures -gt 0) {
                Write-Output \"\"
                Write-Output \"Últimos fallos de seguridad:\"
                $auditContent | Where-Object { $_ -match 'FAIL|ERROR' } | Select-Object -Last 3 | ForEach-Object {
                    Write-Output \"  $_\"
                }
            }
        } else {
            Write-Output \"No hay datos de auditoría disponibles\"
        }
    }" >> %REPORT_FILE%
) else (
    echo No hay datos de auditoría disponibles >> %REPORT_FILE%
)

echo. >> %REPORT_FILE%
echo 📊 6. RECOMENDACIONES >> %REPORT_FILE%
echo ========================================== >> %REPORT_FILE%

echo [INFO] Generando recomendaciones...

echo Recomendaciones de optimización: >> %REPORT_FILE%

if exist %LOG_DIR%\comoencasa-error.log (
    for /f %%i in ('type "%LOG_DIR%\comoencasa-error.log" 2^>nul ^| find /c /v ""') do set ERROR_COUNT=%%i
) else (
    set ERROR_COUNT=0
)

if %ERROR_COUNT% GTR 10 (
    echo ⚠️ Alto número de errores detectado: %ERROR_COUNT% >> %REPORT_FILE%
    echo   - Revisar logs de errores para identificar problemas recurrentes >> %REPORT_FILE%
    echo   - Implementar manejo de excepciones mejorado >> %REPORT_FILE%
)

echo ✅ Configurar rotación automática de logs >> %REPORT_FILE%
echo ✅ Implementar alertas en tiempo real >> %REPORT_FILE%
echo ✅ Monitorear métricas de JVM >> %REPORT_FILE%
echo ✅ Optimizar consultas de base de datos lentas >> %REPORT_FILE%

echo. >> %REPORT_FILE%
echo ========================================== >> %REPORT_FILE%
echo Fin del reporte - %date% %time% >> %REPORT_FILE%

echo [SUCCESS] ✅ Reporte de performance generado: %REPORT_FILE%
echo.
echo 📊 RESUMEN RÁPIDO:
echo ==========================================

if %ERROR_COUNT% GTR 0 (
    echo ❌ Errores detectados: %ERROR_COUNT%
) else (
    echo ✅ Sin errores detectados
)

if exist %LOG_DIR%\comoencasa-app.log (
    for /f %%i in ('type "%LOG_DIR%\comoencasa-app.log" 2^>nul ^| find /c /v ""') do echo 📄 Total logs aplicación: %%i
)

echo.
set /p OPEN="¿Desea abrir el reporte completo? (S/N): "
if /i "%OPEN%"=="S" start notepad %REPORT_FILE%

echo.
echo ==========================================
pause
