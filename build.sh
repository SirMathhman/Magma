#!/usr/bin/env bash
set -euo pipefail

OUT_DIR="out"

mkdir -p "$OUT_DIR"
javac --release 21 --enable-preview -d "$OUT_DIR" $(find packages/compiler-java/src/main/java -name '*.java')

