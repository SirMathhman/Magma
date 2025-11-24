# PowerShell build script for Magma C++ project
param(
    [string]$BuildType = "Release",
    [string]$Generator = "",
    [switch]$Clean,
    [switch]$Run
)

$ErrorActionPreference = "Stop"

$buildDir = "build"

# Clean build directory if requested
if ($Clean -and (Test-Path $buildDir)) {
    Write-Host "Cleaning build directory..." -ForegroundColor Yellow
    Remove-Item -Recurse -Force $buildDir
}

# Create build directory
if (-not (Test-Path $buildDir)) {
    New-Item -ItemType Directory -Path $buildDir | Out-Null
}

# Configure CMake
Write-Host "Configuring CMake..." -ForegroundColor Cyan
Push-Location $buildDir

try {
    $cmakeArgs = @("..", "-DCMAKE_BUILD_TYPE=$BuildType")
    
    if ($Generator) {
        $cmakeArgs += "-G", $Generator
    }
    
    & cmake @cmakeArgs
    
    if ($LASTEXITCODE -ne 0) {
        throw "CMake configuration failed"
    }
    
    # Build
    Write-Host "Building project..." -ForegroundColor Cyan
    & cmake --build . --config $BuildType
    
    if ($LASTEXITCODE -ne 0) {
        throw "Build failed"
    }
    
    Write-Host "`nBuild successful!" -ForegroundColor Green
    Write-Host "Executable location: dist\windows\magma.exe" -ForegroundColor Green
    
    # Run if requested
    if ($Run) {
        Write-Host "\nRunning magma..." -ForegroundColor Cyan
        Pop-Location
        $exePath = "dist\windows\magma.exe"
        if (Test-Path $exePath) {
            & $exePath
            Push-Location $buildDir
        } else {
            # Try alternative path for multi-config generators
            $exePath = "dist\windows\$BuildType\magma.exe"
            if (Test-Path $exePath) {
                & $exePath
                Push-Location $buildDir
            } else {
                Write-Host "Error: Could not find executable" -ForegroundColor Red
                Push-Location $buildDir
            }
        }
    }
} finally {
    Pop-Location
}
