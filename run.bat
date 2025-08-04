@echo off
echo Running Main program...
java -cp target\classes Main
if %ERRORLEVEL% EQU 0 (
    echo Program executed successfully!
) else (
    echo Program execution failed!
)