# Modules Overview

This list summarizes the main modules of the project for quick reference.

- `magma.compiler` – entry point for compilation
  - `magma.compiler.Compiler` – minimal compiler skeleton
  - helper functions `c_type_of`, `bool_to_c`, and `emit_return` reduce
    duplicate type and return statement logic

See [compiler_features.md](compiler_features.md) for details on supported syntax.
