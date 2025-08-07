@echo off
if "%1"=="" (
    echo Usage: run_test_class.bat TestClassName
    exit /b 1
)

echo Compiling Java files...
if not exist build\classes mkdir build\classes
javac -d build\classes -cp "lib\junit-platform-console-standalone-1.9.2.jar" src\main\java\*.java src\test\java\*.java

echo Running test class: %1
java -cp "lib\junit-platform-console-standalone-1.9.2.jar;build\classes" org.junit.platform.console.ConsoleLauncher --select-class=%1 --details=verbose

echo Test execution completed.