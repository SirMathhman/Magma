@echo off
echo Recompiling test class and running tests...

REM Clean test-classes directory
echo Cleaning test-classes directory...
if exist target\test-classes (
    rmdir /s /q target\test-classes
)

REM Create fresh test-classes directory
mkdir target\test-classes

REM Compile all test files
echo Compiling test files...
javac -cp target\classes;lib\junit-platform-console-standalone-1.8.2.jar -d target\test-classes test\java\magma\*.java
if %ERRORLEVEL% NEQ 0 (
    echo Failed to compile test files!
    exit /b %ERRORLEVEL%
)

REM Run the tests in parallel
echo Running tests in parallel...
java -jar lib\junit-platform-console-standalone-1.8.2.jar --class-path target\classes;target\test-classes --scan-class-path --config="junit.jupiter.execution.parallel.enabled=true" --config="junit.jupiter.execution.parallel.mode.default=concurrent" --config="junit.jupiter.execution.parallel.config.strategy=dynamic"
if %ERRORLEVEL% EQU 0 (
    echo Tests executed successfully!
) else (
    echo Tests execution failed!
)