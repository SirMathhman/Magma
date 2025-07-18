# Modules Overview

This list summarizes the main modules of the project for quick reference.

- `magma.compiler` – entry point for compilation
- `magma.compiler.Compiler` – minimal compiler skeleton. Operates on input and
  output files; empty input results in `int main() {}` in the output. It also
  supports a basic function syntax `fn name() => {}` which becomes
  `void name() {}` in C.

- `.github/workflows/ci.yml` – GitHub Actions workflow that installs dependencies and runs `pytest`.
