@echo off
echo Building Magma project and tests...

REM Clean target directories if they exist
echo Cleaning target directories...
if exist target\classes (
    rmdir /s /q target\classes
)
if exist target\test-classes (
    rmdir /s /q target\test-classes
)

REM Create fresh target directories
mkdir target\classes
mkdir target\test-classes

REM Create lib directory if it doesn't exist
if not exist lib (
    echo Creating lib directory...
    mkdir lib
)

REM Check if JUnit 5 standalone console jar exists, and if not, download it
set JUNIT_JAR=lib\junit-platform-console-standalone-1.8.2.jar
if not exist %JUNIT_JAR% (
    echo Downloading JUnit 5 standalone console jar...
    powershell -Command "(New-Object Net.WebClient).DownloadFile('https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.8.2/junit-platform-console-standalone-1.8.2.jar', '%JUNIT_JAR%')"
    if %ERRORLEVEL% NEQ 0 (
        echo Failed to download JUnit 5 standalone console jar!
        exit /b %ERRORLEVEL%
    )
)

REM Compile all source files
javac -d target\classes src\java\magma\*.java
if %ERRORLEVEL% NEQ 0 (
    echo Failed to compile Magma source files!
    exit /b %ERRORLEVEL%
)

REM Compile all test files
javac -cp target\classes;lib\junit-platform-console-standalone-1.8.2.jar -d target\test-classes test\java\magma\*.java
if %ERRORLEVEL% NEQ 0 (
    echo Failed to compile test files!
    exit /b %ERRORLEVEL%
)

echo Running tests in parallel...
java -jar lib\junit-platform-console-standalone-1.8.2.jar --class-path target\classes;target\test-classes --scan-class-path --config="junit.jupiter.execution.parallel.enabled=true" --config="junit.jupiter.execution.parallel.mode.default=concurrent" --config="junit.jupiter.execution.parallel.config.strategy=dynamic"
if %ERRORLEVEL% EQU 0 (
    echo Tests executed successfully!
) else (
    echo Tests execution failed!
)