#!/usr/bin/env bash
set -euo pipefail

OUT_DIR="out"

mkdir -p "$OUT_DIR"
javac --release 21 --enable-preview -d "$OUT_DIR" $(find src/java -name '*.java')

