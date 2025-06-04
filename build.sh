#!/bin/bash
set -e

# Build the project by compiling Java sources into the out/ directory
mkdir -p out
# Compile all Java source files under the src/ directory
javac -d out $(find src -name '*.java')

# Generate diagram and TypeScript stubs
java -cp out magma.GenerateDiagram
