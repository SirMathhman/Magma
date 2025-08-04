@echo off
echo Running Magma program...
java -cp target\classes magma.Main
if %ERRORLEVEL% EQU 0 (
    echo Program executed successfully!
) else (
    echo Program execution failed!
)