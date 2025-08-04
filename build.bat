@echo off
echo Cleaning target directory...
if exist target\classes (
    rmdir /s /q target\classes
)
mkdir target\classes

echo Building Magma project...
javac -d target\classes src\java\magma\*.java
if %ERRORLEVEL% EQU 0 (
    echo Build successful!
) else (
    echo Build failed!
)