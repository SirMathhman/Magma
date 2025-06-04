#!/bin/bash
set -e

# Compile sources
mkdir -p out
# Compile all source files under src/
javac -d out $(find src -name '*.java')

# Run the program
java -cp out magma.GenerateDiagram
