@echo off
echo Building and running Magma Hello World application...

REM Create build directory if it doesn't exist
if not exist build\classes mkdir build\classes

REM Compile the Java source file
echo Compiling Java source files...
javac -d build\classes src\main\java\Main.java
if %ERRORLEVEL% neq 0 (
    echo Compilation failed!
    exit /b %ERRORLEVEL%
)

REM Run the compiled program
echo Running the application...
java -cp build\classes Main

echo Done.