# Modules Overview

This list summarizes the main modules of the project for quick reference.

- `magma.compiler` – entry point for compilation
  - `magma.compiler.Compiler` – minimal compiler skeleton
  - helper functions `c_type_of`, `bool_to_c`, `emit_return`, and
    `analyze_expr` reduce duplicate type, expression, and return logic
  - `build_env_init` gathers lines for captured environments to avoid
    repeating struct setup
  - `process_callable` handles functions, classes, and their generic forms
- `magma.numbers` – numeric type mapping and range helpers
- `tests.utils` – helper used by tests for compiling snippet strings

Running ``python src/magma/__init__.py`` directly compiles the example under
``working/``.  The module adjusts ``sys.path`` at runtime so it functions both
as a package and as a standalone script.

See [compiler_features.md](compiler_features.md) for details on supported syntax.
