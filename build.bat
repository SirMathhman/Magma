@echo off
echo Building Magma...

REM Create output directories if they don't exist
if not exist "out\main" mkdir out\main
if not exist "out\test" mkdir out\test

REM Compile main source files
javac -d out\main -cp src\main\java src\main\java\magma\*.java
if %ERRORLEVEL% neq 0 (
    echo Build failed: Error compiling main source files
    exit /b %ERRORLEVEL%
)

REM Compile test source files
javac -d out\test -cp out\main;src\test\java;lib\* src\test\java\magma\*.java
if %ERRORLEVEL% neq 0 (
    echo Build failed: Error compiling test source files
    exit /b %ERRORLEVEL%
)

echo Build completed successfully