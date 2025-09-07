#!/usr/bin/env sh
# Runs the Magma CLI runner. Usage: ./run.sh [path-to-script]
SCRIPT_PATH=${1:-src/main/magma/magma/Main.mgs}

mvn -q compile || exit 1
java --enable-preview -cp "target/classes" magma.Main "$SCRIPT_PATH"
