@echo off
echo Running CheckStyle for Magma with Maven...

REM Check if Maven is installed
where mvn >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo Maven is not installed or not in PATH.
    echo Please install Maven from https://maven.apache.org/download.cgi
    echo and add it to your PATH, or use the Maven wrapper if available.
    exit /b 1
)

REM Run Maven CheckStyle
mvn checkstyle:check

if %ERRORLEVEL% neq 0 (
    echo CheckStyle found issues in the code
    exit /b %ERRORLEVEL%
) else (
    echo CheckStyle completed successfully - no issues found
)