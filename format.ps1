# Format all Python files in the current directory using black
Get-ChildItem -Filter *.py | ForEach-Object { black $_.FullName }

# Check cyclomatic complexity (must be <= 10 for all functions)
$failed = $false
Get-ChildItem -Filter *.py | ForEach-Object {
    $result = radon cc -a -nc -s $_.FullName
    if ($result -match '[CF]') {
        Write-Host "ERROR: Function in $($_.Name) exceeds cyclomatic complexity 10."
        $failed = $true
    }
}
if ($failed) { exit 1 }
