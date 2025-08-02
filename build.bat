@echo off
echo Building Magma project...

:: Create build directories if they don't exist
if not exist "build\classes" mkdir build\classes
if not exist "build\test-classes" mkdir build\test-classes
if not exist "build\lib" mkdir build\lib

:: Download JUnit 5 dependencies if they don't exist
echo Downloading JUnit 5 dependencies...

:: Download each dependency individually to ensure all are downloaded
if not exist "build\lib\junit-jupiter-api-5.10.0.jar" (
    powershell -Command "& {Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-api/5.10.0/junit-jupiter-api-5.10.0.jar' -OutFile 'build\lib\junit-jupiter-api-5.10.0.jar'}"
)
if not exist "build\lib\junit-jupiter-engine-5.10.0.jar" (
    powershell -Command "& {Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-engine/5.10.0/junit-jupiter-engine-5.10.0.jar' -OutFile 'build\lib\junit-jupiter-engine-5.10.0.jar'}"
)
if not exist "build\lib\junit-platform-commons-1.10.0.jar" (
    powershell -Command "& {Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/junit/platform/junit-platform-commons/1.10.0/junit-platform-commons-1.10.0.jar' -OutFile 'build\lib\junit-platform-commons-1.10.0.jar'}"
)
if not exist "build\lib\junit-platform-engine-1.10.0.jar" (
    powershell -Command "& {Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/junit/platform/junit-platform-engine/1.10.0/junit-platform-engine-1.10.0.jar' -OutFile 'build\lib\junit-platform-engine-1.10.0.jar'}"
)
if not exist "build\lib\junit-platform-launcher-1.10.0.jar" (
    powershell -Command "& {Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/junit/platform/junit-platform-launcher/1.10.0/junit-platform-launcher-1.10.0.jar' -OutFile 'build\lib\junit-platform-launcher-1.10.0.jar'}"
)
if not exist "build\lib\junit-platform-console-1.10.0.jar" (
    powershell -Command "& {Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console/1.10.0/junit-platform-console-1.10.0.jar' -OutFile 'build\lib\junit-platform-console-1.10.0.jar'}"
)
if not exist "build\lib\junit-platform-reporting-1.10.0.jar" (
    powershell -Command "& {Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/junit/platform/junit-platform-reporting/1.10.0/junit-platform-reporting-1.10.0.jar' -OutFile 'build\lib\junit-platform-reporting-1.10.0.jar'}"
)
if not exist "build\lib\opentest4j-1.3.0.jar" (
    powershell -Command "& {Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/opentest4j/opentest4j/1.3.0/opentest4j-1.3.0.jar' -OutFile 'build\lib\opentest4j-1.3.0.jar'}"
)
if not exist "build\lib\apiguardian-api-1.1.2.jar" (
    powershell -Command "& {Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/apiguardian/apiguardian-api/1.1.2/apiguardian-api-1.1.2.jar' -OutFile 'build\lib\apiguardian-api-1.1.2.jar'}"
)

:: Set classpath for JUnit
set JUNIT_CP=build\lib\junit-jupiter-api-5.10.0.jar;build\lib\junit-jupiter-engine-5.10.0.jar;build\lib\junit-platform-commons-1.10.0.jar;build\lib\junit-platform-engine-1.10.0.jar;build\lib\junit-platform-launcher-1.10.0.jar;build\lib\junit-platform-console-1.10.0.jar;build\lib\junit-platform-reporting-1.10.0.jar;build\lib\opentest4j-1.3.0.jar;build\lib\apiguardian-api-1.1.2.jar

:: Compile main source code
echo Compiling main source code...
javac -d build\classes src\java\magma\*.java

:: Check if compilation was successful
if %ERRORLEVEL% neq 0 (
    echo Error compiling main source code
    exit /b %ERRORLEVEL%
)

:: Compile test source code
echo Compiling test source code...
javac -cp build\classes;%JUNIT_CP% -d build\test-classes test\java\magma\*.java

:: Check if compilation was successful
if %ERRORLEVEL% neq 0 (
    echo Error compiling test source code
    exit /b %ERRORLEVEL%
)

echo Build completed successfully.