@echo off
echo Building Magma with Maven...

REM Check if Maven is installed
where mvn >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo Maven is not installed or not in PATH.
    echo Please install Maven from https://maven.apache.org/download.cgi
    echo and add it to your PATH, or use the Maven wrapper if available.
    exit /b 1
)

REM Run Maven compile (with validate phase which includes Checkstyle)
mvn clean validate compile

if %ERRORLEVEL% neq 0 (
    echo Build failed
    exit /b %ERRORLEVEL%
) else (
    echo Build completed successfully
)