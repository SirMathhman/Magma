@echo off
echo Running tests for Magma...

REM First, execute the build script
call build.bat
if %ERRORLEVEL% neq 0 (
    echo Tests aborted: Build failed
    exit /b %ERRORLEVEL%
)

echo.
echo Running tests...

REM Run JUnit tests using the JUnit platform console launcher
java -jar lib\junit-platform-console-standalone-1.8.1.jar --class-path out\main;out\test --scan-class-path --reports-dir=test-reports

if %ERRORLEVEL% neq 0 (
    echo Tests failed
    exit /b %ERRORLEVEL%
) else (
    echo All tests passed successfully
)