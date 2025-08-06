@echo off
setlocal enabledelayedexpansion

echo Building Magma compiler...

:: Create directories if they don't exist
if not exist "build" mkdir build
if not exist "build\classes" mkdir build\classes
if not exist "build\classes\main" mkdir build\classes\main
if not exist "build\classes\test" mkdir build\classes\test
if not exist "lib" mkdir lib

:: Download JUnit 5 dependencies if they don't exist
set JUNIT_PLATFORM_VERSION=1.9.2
set JUNIT_JUPITER_VERSION=5.9.2
set JUNIT_VINTAGE_VERSION=5.9.2
set OPENTEST4J_VERSION=1.2.0
set APIGUARDIAN_VERSION=1.1.2

set JUNIT_PLATFORM_ENGINE=junit-platform-engine-%JUNIT_PLATFORM_VERSION%.jar
set JUNIT_PLATFORM_COMMONS=junit-platform-commons-%JUNIT_PLATFORM_VERSION%.jar
set JUNIT_JUPITER_API=junit-jupiter-api-%JUNIT_JUPITER_VERSION%.jar
set JUNIT_JUPITER_ENGINE=junit-jupiter-engine-%JUNIT_JUPITER_VERSION%.jar
set JUNIT_JUPITER_PARAMS=junit-jupiter-params-%JUNIT_JUPITER_VERSION%.jar
set OPENTEST4J=opentest4j-%OPENTEST4J_VERSION%.jar
set APIGUARDIAN=apiguardian-api-%APIGUARDIAN_VERSION%.jar

set JUNIT_REPO=https://repo1.maven.org/maven2/org/junit

if not exist "lib\%JUNIT_PLATFORM_ENGINE%" (
    echo Downloading JUnit dependencies...
    powershell -Command "& {Invoke-WebRequest -Uri '%JUNIT_REPO%/platform/junit-platform-engine/%JUNIT_PLATFORM_VERSION%/%JUNIT_PLATFORM_ENGINE%' -OutFile 'lib\%JUNIT_PLATFORM_ENGINE%'}"
    powershell -Command "& {Invoke-WebRequest -Uri '%JUNIT_REPO%/platform/junit-platform-commons/%JUNIT_PLATFORM_VERSION%/%JUNIT_PLATFORM_COMMONS%' -OutFile 'lib\%JUNIT_PLATFORM_COMMONS%'}"
    powershell -Command "& {Invoke-WebRequest -Uri '%JUNIT_REPO%/jupiter/junit-jupiter-api/%JUNIT_JUPITER_VERSION%/%JUNIT_JUPITER_API%' -OutFile 'lib\%JUNIT_JUPITER_API%'}"
    powershell -Command "& {Invoke-WebRequest -Uri '%JUNIT_REPO%/jupiter/junit-jupiter-engine/%JUNIT_JUPITER_VERSION%/%JUNIT_JUPITER_ENGINE%' -OutFile 'lib\%JUNIT_JUPITER_ENGINE%'}"
    powershell -Command "& {Invoke-WebRequest -Uri '%JUNIT_REPO%/jupiter/junit-jupiter-params/%JUNIT_JUPITER_VERSION%/%JUNIT_JUPITER_PARAMS%' -OutFile 'lib\%JUNIT_JUPITER_PARAMS%'}"
    powershell -Command "& {Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/opentest4j/opentest4j/%OPENTEST4J_VERSION%/%OPENTEST4J%' -OutFile 'lib\%OPENTEST4J%'}"
    powershell -Command "& {Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/apiguardian/apiguardian-api/%APIGUARDIAN_VERSION%/%APIGUARDIAN%' -OutFile 'lib\%APIGUARDIAN%'}"
)

:: Set classpath for compilation
set CLASSPATH=.
for %%f in (lib\*.jar) do (
    set CLASSPATH=!CLASSPATH!;%%f
)

:: Compile main sources
echo Compiling main sources...
javac -d build\classes\main -cp %CLASSPATH% src\main\java\com\magma\compiler\lexer\*.java src\main\java\com\magma\compiler\parser\*.java src\main\java\com\magma\compiler\ast\*.java src\main\java\com\magma\compiler\*.java

if %ERRORLEVEL% neq 0 (
    echo Error compiling main sources
    exit /b %ERRORLEVEL%
)

:: Compile test sources
echo Compiling test sources...
javac -d build\classes\test -cp %CLASSPATH%;build\classes\main src\test\java\com\magma\compiler\lexer\*.java src\test\java\com\magma\compiler\parser\*.java src\test\java\com\magma\compiler\ast\*.java

if %ERRORLEVEL% neq 0 (
    echo Error compiling test sources
    exit /b %ERRORLEVEL%
)

echo Build completed successfully.