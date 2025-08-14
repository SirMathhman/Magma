@echo off
REM Format all Python files in the current directory using black
for %%f in (*.py) do "black" "%%f"
