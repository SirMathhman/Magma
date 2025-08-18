@echo off
REM Simple runner for Application.main
REM - If compiled classes are missing, run `mvn -DskipTests package` before running.

SETLOCAL
SET ROOT_DIR=%~dp0
nSET CLASSES_DIR=%ROOT_DIR%target\classes

nREM If Application.class is missing, build the project with Maven
IF NOT EXIST "%CLASSES_DIR%\Application.class" (
  echo Compiled classes not found. Running Maven package (skipping tests)...
  mvn -DskipTests package
  IF ERRORLEVEL 1 (
    echo Maven build failed. Aborting.
    EXIT /B 1
  )
)


echo Forwarding to the PowerShell runner (run.ps1). This keeps backward compatibility with callers using run.bat.

SET SCRIPT_DIR=%~dp0
powershell -NoProfile -ExecutionPolicy Bypass -File "%SCRIPT_DIR%run.ps1" -- %*
EXIT /B %ERRORLEVEL%
