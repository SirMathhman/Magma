# Modules Overview

This list summarizes the main modules of the project for quick reference.

- `magma.compiler` – entry point for compilation
  - `magma.compiler.Compiler` – minimal compiler skeleton
  - helper functions `c_type_of`, `bool_to_c`, `emit_return`, and
    `analyze_expr` reduce duplicate type, expression, and return logic
  - `process_callable` handles functions, classes, and their generic forms;
    the main compile loop now reuses it for top-level definitions
- `magma.numbers` – numeric type mapping and range helpers
- `tests.utils` – helper used by tests for compiling snippet strings; its
  `compile_source` function enforces a three-second timeout to catch infinite
  loops when the platform supports `signal.SIGALRM`. On systems without it the
  helper simply runs without a timeout

See [compiler_features.md](compiler_features.md) for details on supported syntax.
