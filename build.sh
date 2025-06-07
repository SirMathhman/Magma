#!/usr/bin/env bash
set -euo pipefail

# Compile Java sources
javac $(find src -name "*.java")

# Generate TypeScript output
java -cp src magma.Main
