param(
    [string]$FilePath
)

# Default file path if not provided
if (-not $FilePath) {
    $FilePath = "src/main/magma/magma/Interpreter.mgs"
}

Write-Host "Building project with Maven..."
mvn -q package
if ($LASTEXITCODE -ne 0) {
    Write-Error "Maven build failed"
    exit $LASTEXITCODE
}

Write-Host "Running interpreter on $FilePath"
java -cp target/classes;"target/dependency/*" magma.Main $FilePath
