@echo off
echo Building HelloWorld.java...
javac -d target\classes src\java\HelloWorld.java
if %ERRORLEVEL% EQU 0 (
    echo Build successful!
) else (
    echo Build failed!
)