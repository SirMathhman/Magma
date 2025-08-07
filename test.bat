@echo off
echo Running tests for Magma with Maven...

REM Check if Maven is installed
where mvn >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo Maven is not installed or not in PATH.
    echo Please install Maven from https://maven.apache.org/download.cgi
    echo and add it to your PATH, or use the Maven wrapper if available.
    exit /b 1
)

REM Run Maven test and verify (Checkstyle runs in verify phase)
mvn clean test verify

if %ERRORLEVEL% neq 0 (
    echo Tests failed
    exit /b %ERRORLEVEL%
) else (
    echo All tests passed successfully
)