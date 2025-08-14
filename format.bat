@echo off
REM Format all Python files in the current directory using black
for %%f in (*.py) do "black" "%%f"
REM Check cyclomatic complexity (must be <= 10 for all functions)
for %%f in (*.py) do radon cc -a -nc -s "%%f" | findstr /R /C:"[CF]" >nul && (
	echo ERROR: Function in %%f exceeds cyclomatic complexity 10.
	exit /b 1
)
