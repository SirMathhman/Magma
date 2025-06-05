#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
OUT_DIR="$SCRIPT_DIR/out"

if [ ! -d "$OUT_DIR" ]; then
  "$SCRIPT_DIR/build.sh"
fi

java --enable-preview -cp "$OUT_DIR" magma.Main "$@"

"$SCRIPT_DIR/render-diagram.sh"

