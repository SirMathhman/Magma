Write-Output "Running Maven CPD (PMD) with minimumTokens=5..."
Write-Output "You can update token size in pom.xml plugin configuration (minimumTokens)."
mvn -q pmd:cpd-check
if ($LASTEXITCODE -ne 0) {
    Write-Output "CPD found issues or the plugin encountered an error (check output above)."
} else {
    Write-Output "CPD completed with no failing violations (or failOnViolation=false)."
}
