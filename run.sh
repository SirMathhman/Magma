#!/bin/bash
set -e

# Compile sources
mkdir -p out
javac -d out src/magma/Result.java src/magma/GenerateDiagram.java

# Run the program
java -cp out magma.GenerateDiagram
