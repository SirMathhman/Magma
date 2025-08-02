@echo off
echo Running Magma tests...

:: Check if build directories exist, if not run build script
if not exist "build\classes" (
    echo Build directories not found. Running build script first...
    call build.bat
    if %ERRORLEVEL% neq 0 (
        echo Build failed. Cannot run tests.
        exit /b %ERRORLEVEL%
    )
)

:: Set classpath for JUnit
set JUNIT_CP=build\lib\junit-jupiter-api-5.10.0.jar;build\lib\junit-jupiter-engine-5.10.0.jar;build\lib\junit-platform-commons-1.10.0.jar;build\lib\junit-platform-engine-1.10.0.jar;build\lib\junit-platform-launcher-1.10.0.jar;build\lib\junit-platform-console-1.10.0.jar;build\lib\junit-platform-reporting-1.10.0.jar;build\lib\opentest4j-1.3.0.jar;build\lib\apiguardian-api-1.1.2.jar

:: Run tests using JUnit Platform Console Launcher
echo Running JUnit tests...
java -cp build\classes;build\test-classes;%JUNIT_CP% org.junit.platform.console.ConsoleLauncher --scan-classpath --reports-dir=build\test-reports

:: Check if tests were successful
if %ERRORLEVEL% neq 0 (
    echo Tests failed with errors.
    exit /b %ERRORLEVEL%
) else (
    echo All tests passed successfully.
)