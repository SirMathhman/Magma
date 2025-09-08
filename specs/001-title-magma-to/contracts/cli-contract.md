# CLI Contract

## Purpose
Define the CLI behavior for the Magma → C compiler prototype.

## Required flags
- `--input` (one or more source files)
- `--out-dir` (directory to write generated C)
- `--help` (prints usage)
- `--verbose` (increase logging verbosity)
- `--emit-mappings` (optional: write a mappings file linking Magma lines -> C lines)
- `--single-translation-unit` (optional: emit a single C file instead of one per module)

## Expected behavior
- Exit code 0 on success and produced C files.
- Non-zero exit code and printed diagnostics on error. Errors go to stderr.
