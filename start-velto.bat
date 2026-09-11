@echo off
title VELTO - Ride Sharing System Launcher
color 0A
echo ==================================================================
echo               VELTO - RIDE SHARING SYSTEM LAUNCHER
echo ==================================================================
echo.

:: 1. Check Java 17
java -version >nul 2>&1
if %errorlevel% neq 0 (
    color 0C
    echo [ERROR] Java is not installed or not in your PATH!
    echo Please install Java 17 LTS to run VELTO.
    pause
    exit /b 1
)
echo [OK] Java detected.

:: 2. Check MongoDB Port 27017
powershell -Command "$t = Test-NetConnection -ComputerName localhost -Port 27017 -WarningAction SilentlyContinue; if ($t.TcpTestSucceeded) { exit 0 } else { exit 1 }" >nul 2>&1
if %errorlevel% neq 0 (
    color 0E
    echo [WARNING] MongoDB Community Server does not seem to be running on port 27017!
    echo Attempting to start MongoDB service or please ensure MongoDB service is active.
    echo.
) else (
    echo [OK] MongoDB is running on localhost:27017.
)

:: 3. Launch Spring Boot Backend in a new window
echo [INFO] Starting Spring Boot 3 Backend on http://localhost:8080...
start "VELTO Backend Service" cmd /k "cd /d "%~dp0backend" && mvn spring-boot:run"

:: 4. Wait 8 seconds for Spring Boot startup
echo [INFO] Waiting for backend initialization...
timeout /t 8 /nobreak >nul

:: 5. Open Frontend in default browser
echo [INFO] Opening VELTO Web Interface...
start "" "%~dp0frontend\index.html"

echo.
echo ==================================================================
echo  VELTO is now running!
echo  - Backend API : http://localhost:8080/api/health
echo  - Frontend    : file:///%~dp0frontend/index.html
echo  - Demo Admin  : admin@velto.com / Admin@123
echo  - Demo Driver : rajesh.driver@velto.com / Driver@123
echo  - Demo Rider  : priya.passenger@velto.com / Passenger@123
echo ==================================================================
pause
