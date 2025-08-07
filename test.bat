@echo off
echo Running Magma tests...

:: First run the build script
echo Building project before testing...
call build.bat

:: Check if build was successful
if %ERRORLEVEL% NEQ 0 (
    echo Tests cannot run because build failed!
    exit /b %ERRORLEVEL%
)

:: Create test build directory if it doesn't exist
if not exist build\test mkdir build\test

:: Set classpath for JUnit 5
set JUNIT_CP=lib\junit-jupiter-api-5.8.1.jar;lib\junit-platform-commons-1.8.1.jar;lib\opentest4j-1.2.0.jar;lib\apiguardian-api-1.1.2.jar;lib\junit-jupiter-params-5.8.1.jar

:: Compile tests
echo Compiling tests...
javac -cp build;%JUNIT_CP% -d build\test src\test\java\com\magma\*.java

:: Check if test compilation was successful
if %ERRORLEVEL% NEQ 0 (
    echo Test compilation failed!
    exit /b %ERRORLEVEL%
)

:: Download JUnit Platform Console Launcher if not exists
if not exist lib\junit-platform-console-standalone-1.8.1.jar (
    echo Downloading JUnit Platform Console Launcher...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.8.1/junit-platform-console-standalone-1.8.1.jar' -OutFile 'lib\junit-platform-console-standalone-1.8.1.jar'"
)

:: Run tests using JUnit Platform
echo Running tests...
java -jar lib\junit-platform-console-standalone-1.8.1.jar --class-path build;build\test --scan-class-path

:: Check if tests were successful
if %ERRORLEVEL% NEQ 0 (
    echo Tests failed!
    exit /b %ERRORLEVEL%
)

echo All tests passed successfully!