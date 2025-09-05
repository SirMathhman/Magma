$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Definition
Set-Location $projectRoot

Write-Host "Compiling Java sources..."
if (Test-Path out) { Remove-Item -Recurse -Force out }
mkdir out | Out-Null

try {
  javac -d out src/main/java/*.java
} catch {
  Write-Error "Compilation failed: $_"
  exit 2
}

if ($LASTEXITCODE -ne 0) {
  Write-Error "javac returned non-zero exit code: $LASTEXITCODE"
  exit $LASTEXITCODE
}

Write-Host "Running Main..."
try {
  java -cp out Main
} catch {
  Write-Error "Execution failed: $_"
  exit 3
}
