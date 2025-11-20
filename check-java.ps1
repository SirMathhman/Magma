Write-Output "Checking Java version and environment..."
java --version
Write-Output "Checking javac version..."
javac --version
Write-Output "Checking Maven (optional)..."
mvn -v 2>$null
if ($LASTEXITCODE -ne 0) {
    Write-Output "Maven not detected. If you want to run unit tests use Maven or configure your IDE to run them." 
} else {
    Write-Output "Maven detected - you can run: mvn -q test"
}
