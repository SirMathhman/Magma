# Format all Python files in the current directory using black
Get-ChildItem -Filter *.py | ForEach-Object { black $_.FullName }

# Check cyclomatic complexity (must be <= 10 for all functions)
$failed = $false
Get-ChildItem -Filter *.py | ForEach-Object {
    $result = radon cc -a -nc -s $_.FullName
    $lines = $result -split "`n"
    foreach ($line in $lines) {
        $line = $line.Trim()
        if ($line -match '^F\s+\d+:\d+\s+(\w+)\s+-\s+[A-Z]\s+\((\d+)\)') {
            $funcName = $Matches[1]
            $cc = [int]$Matches[2]
            if ($cc -gt 10) {
                Write-Host "ERROR: Function '$funcName' in $($_.Name) exceeds cyclomatic complexity 10 (actual: $cc)."
                $failed = $true
            }
        }
    }
}
if ($failed) { exit 1 }
