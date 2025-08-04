@echo off
echo Running HelloWorld program...
java -cp target\classes HelloWorld
if %ERRORLEVEL% EQU 0 (
    echo Program executed successfully!
) else (
    echo Program execution failed!
)