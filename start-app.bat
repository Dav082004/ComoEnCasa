@echo off
echo 🚀 Iniciando aplicación Como en Casa...
echo.

echo 📦 Iniciando Backend (Spring Boot)...
cd /d "c:\Users\User\Desktop\ComoEnCasa\backend"
start "Backend - Como en Casa" cmd /k "mvn spring-boot:run"

echo ⏳ Esperando 10 segundos para que el backend inicie...
timeout /t 10 /nobreak >nul

echo 🌐 Iniciando Frontend (React)...
cd /d "c:\Users\User\Desktop\ComoEnCasa\frontend"
start "Frontend - Como en Casa" cmd /k "npm start"

echo.
echo ✅ Aplicación iniciada!
echo 📍 Backend: http://localhost:8081
echo 📍 Frontend: http://localhost:3000
echo.
echo Presiona cualquier tecla para cerrar esta ventana...
pause >nul
