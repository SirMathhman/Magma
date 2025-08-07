@echo off
echo Building Magma project...

:: Create build directory if it doesn't exist
if not exist build mkdir build

:: Compile main code
echo Compiling main code...
javac -d build src\main\java\com\magma\*.java

:: Check if compilation was successful
if %ERRORLEVEL% NEQ 0 (
    echo Build failed!
    exit /b %ERRORLEVEL%
)

echo Build completed successfully!