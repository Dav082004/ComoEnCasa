@echo off
echo Verificando estado de MySQL...

echo.
echo 1. Verificando servicios MySQL:
sc query MySQL
net start | findstr MySQL

echo.
echo 2. Verificando conexión a MySQL:
mysql -u root -p -e "SELECT 'MySQL funcionando correctamente' as status;"

echo.
echo 3. Verificando variables de timeout:
mysql -u root -p -e "SHOW VARIABLES LIKE '%timeout%';"

echo.
echo 4. Verificando conexiones activas:
mysql -u root -p -e "SHOW PROCESSLIST;"

echo.
echo 5. Verificando configuración de wait_timeout:
mysql -u root -p -e "SET GLOBAL wait_timeout=28800; SET GLOBAL interactive_timeout=28800; SHOW VARIABLES LIKE '%timeout%';"

pause
