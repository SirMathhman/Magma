#!/usr/bin/env bash
set -euo pipefail

# Compile Java sources
javac --release 21 --enable-preview $(find src -name "*.java")

# Generate TypeScript output
java --enable-preview -cp src magma.Main
