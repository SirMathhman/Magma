clang++ -o test_compile.exe test_compile.cpp
if ($LASTEXITCODE -eq 0) {
	./test_compile.exe
} else {
	Write-Host "Build failed. Tests not run."
}
