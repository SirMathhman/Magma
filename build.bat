@echo off
REM Build script for Magma project using clang++
setlocal
set CLANG_BIN="C:\Program Files\LLVM\bin"
set PATH=%CLANG_BIN%;%PATH%

clang++ -std=c++17 -o test_alwaysThrows.exe test_alwaysThrows.cpp alwaysThrows.cpp
if %ERRORLEVEL% neq 0 (
    echo Build failed.
    exit /b %ERRORLEVEL%
)

echo Build succeeded.

test_alwaysThrows.exe
endlocal
