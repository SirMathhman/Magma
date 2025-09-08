param(
	[string]$JavaHome = $env:JAVA_HOME
)

Push-Location "$PSScriptRoot/.."
$projectRoot = (Resolve-Path .).Path
Write-Host "Project root: $projectRoot"

if (-not (Test-Path "$projectRoot/java/magma-core/pom.xml")) {
	Write-Error "Maven project not found under java/magma-core."
	exit 2
}

Write-Host "Running: mvn -f java/pom.xml -DskipTests=true package"
mvn -f java/pom.xml -DskipTests=true package
$rc = $LASTEXITCODE
if ($rc -ne 0) { Pop-Location; exit $rc }

$jar = Get-ChildItem -Path java\magma-core\target -Filter "magma-core-*.jar" | Select-Object -First 1
if (-not $jar) {
	Write-Error "Built jar not found"
	Pop-Location
	exit 3
}

Write-Host "Running stub CLI via java -cp $($jar.FullName) magma.CLI --help"
Start-Process -NoNewWindow -Wait -FilePath "java" -ArgumentList "-cp", "${jar.FullName}", "magma.CLI", "--help"
$rc = $LASTEXITCODE
Write-Host "CLI exit code: $rc"
Pop-Location
exit $rc
