#!/bin/bash
set -e

# Build the project by compiling Java sources into the out/ directory
mkdir -p out
javac -d out src/magma/Result.java src/magma/GenerateDiagram.java
