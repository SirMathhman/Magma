@echo off
setlocal enabledelayedexpansion

echo Running Magma tests...

:: First, build the project
call build.bat

if %ERRORLEVEL% neq 0 (
    echo Build failed, cannot run tests
    exit /b %ERRORLEVEL%
)

:: Set classpath for running tests
set CLASSPATH=build\classes\main;build\classes\test
for %%f in (lib\*.jar) do (
    set CLASSPATH=!CLASSPATH!;%%f
)

echo Running tests...

:: Run tests using JUnit Platform Console Launcher
java -cp %CLASSPATH% org.junit.platform.console.ConsoleLauncher --scan-classpath --reports-dir=build\test-reports

if %ERRORLEVEL% neq 0 (
    echo Tests failed
    exit /b %ERRORLEVEL%
)

echo All tests passed successfully.