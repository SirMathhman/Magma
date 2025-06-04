#!/usr/bin/env bash
set -euo pipefail

OUT_DIR="out"

mkdir -p "$OUT_DIR"

# Match the major version of the installed JDK when compiling. Older
# environments may not support the latest ``--release`` value, so we
# derive it from ``javac -version``.
JAVA_VERSION=$(javac -version 2>&1 | sed -E 's/.* ([0-9]+).*/\1/')

# ``--enable-preview`` is harmless even if no preview features are used.
javac --release "$JAVA_VERSION" --enable-preview \
    -d "$OUT_DIR" $(find src/java -name '*.java')

