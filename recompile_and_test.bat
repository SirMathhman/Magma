@echo off
echo Recompiling test class and running tests...

REM Clean test-classes directory
echo Cleaning test-classes directory...
if exist target\test-classes (
    rmdir /s /q target\test-classes
)

REM Create fresh test-classes directory
mkdir target\test-classes

REM Compile MainTest.java with Main.class in classpath
echo Compiling MainTest.java...
javac -cp target\classes;lib\junit-platform-console-standalone-1.8.2.jar -d target\test-classes test\java\magma\MainTest.java
if %ERRORLEVEL% NEQ 0 (
    echo Failed to compile MainTest.java!
    exit /b %ERRORLEVEL%
)

REM Run the tests
echo Running tests...
java -jar lib\junit-platform-console-standalone-1.8.2.jar --class-path target\classes;target\test-classes --scan-class-path
if %ERRORLEVEL% EQU 0 (
    echo Tests executed successfully!
) else (
    echo Tests execution failed!
)