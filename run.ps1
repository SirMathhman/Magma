param(
  [Parameter(ValueFromRemainingArguments=$true)]
  [string[]] $RemainingArgs
)

# Simple PowerShell runner for Application.main
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
$classesDir = Join-Path $scriptDir "target\classes"

$applicationClass = Join-Path $classesDir "Application.class"
if (-not (Test-Path $applicationClass)) {
    Write-Host "Compiled classes not found. Running Maven package (skipping tests)..."
    & mvn -DskipTests package
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Maven build failed. Aborting."
        exit $LASTEXITCODE
    }
}

Write-Host "Running Application.main..."
& java -cp $classesDir Application @RemainingArgs
exit $LASTEXITCODE
