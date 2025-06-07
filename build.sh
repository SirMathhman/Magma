#!/usr/bin/env bash
set -euo pipefail

# Compile Java sources with preview features enabled. Use the
# installed JDK version rather than requiring a specific release
# to avoid errors on environments without Java 21.
javac --source 21 --enable-preview $(find src -name "*.java")

# Generate TypeScript output
java --enable-preview -cp src magma.Main
