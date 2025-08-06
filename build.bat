@echo off
echo Building and running Magma Hello World application...

REM Create build directories if they don't exist
if not exist build\classes mkdir build\classes
if not exist build\test-classes mkdir build\test-classes
if not exist lib mkdir lib

REM Download JUnit 5 dependencies if they don't exist
if not exist lib\junit-platform-console-standalone-1.9.2.jar (
    echo Downloading JUnit 5 dependencies...
    powershell -Command "& {Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.9.2/junit-platform-console-standalone-1.9.2.jar' -OutFile 'lib\junit-platform-console-standalone-1.9.2.jar'}"
    if %ERRORLEVEL% neq 0 (
        echo Failed to download JUnit dependencies!
        exit /b %ERRORLEVEL%
    )
)

REM Compile the main Java source files
echo Compiling main Java source files...
javac -d build\classes src\main\java\*.java
if %ERRORLEVEL% neq 0 (
    echo Main compilation failed!
    exit /b %ERRORLEVEL%
)

REM Compile the test Java source files
echo Compiling test Java source files...
javac -d build\test-classes -cp build\classes;lib\junit-platform-console-standalone-1.9.2.jar src\test\java\MainTest.java src\test\java\ArrayTest.java src\test\java\TypeInferenceTest.java src\test\java\CharTypeTest.java
if %ERRORLEVEL% neq 0 (
    echo Test compilation failed!
    exit /b %ERRORLEVEL%
)

REM Run the tests
echo Running tests...
java -jar lib\junit-platform-console-standalone-1.9.2.jar --class-path build\classes;build\test-classes --scan-class-path
if %ERRORLEVEL% neq 0 (
    echo Tests failed!
    exit /b %ERRORLEVEL%
)

REM Run the compiled program
echo Running the application...
java -cp build\classes Main

echo Done.