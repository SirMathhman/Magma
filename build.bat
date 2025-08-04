@echo off
echo Building Main.java...
javac -d target\classes src\java\Main.java
if %ERRORLEVEL% EQU 0 (
    echo Build successful!
) else (
    echo Build failed!
)