#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Generate TypeScript stubs if they do not exist
if [ ! -d "$SCRIPT_DIR/src/node" ]; then
  "$SCRIPT_DIR/run.sh" >/dev/null
fi

# Type check the generated TypeScript
exec tsc --noEmit -p "$SCRIPT_DIR/tsconfig.json"
