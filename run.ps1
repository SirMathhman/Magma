<#
Runs the Magma CLI runner using the project's classes.
Usage: .\run.ps1 [path-to-script]
#>
Param(
    [string]$ScriptPath = "src/main/magma/magma/Main.mgs"
)

Push-Location -Path $PSScriptRoot
try {
    mvn -q compile
    & java -cp "target/classes" magma.Main $ScriptPath
} finally {
    Pop-Location
}
