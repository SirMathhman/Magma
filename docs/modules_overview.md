# Modules Overview

This list summarizes the main modules of the project for quick reference.

- `magma.compiler` – entry point for compilation
  - `magma.compiler.Compiler` – minimal compiler skeleton
  - internal tables like `struct_fields` and `func_sigs` now live on the
    instance so helpers access shared state without long parameter lists
  - helper functions `c_type_of`, `bool_to_c`, `type_info`, `emit_return`,
    `analyze_expr`, `value_info`, `handle_conditional`, and `handle_let` reduce duplicate type,
    expression, and return logic. `handle_conditional` parses both `if` and
    `while` blocks so the main loop stays compact
  - `build_env_init` gathers lines for captured environments to avoid
    repeating struct setup
  - `process_callable` handles functions, classes, and their generic forms
- `magma.numbers` – numeric type mapping and range helpers
- `tests.utils` – helper used by tests for compiling snippet strings

Running ``python src/magma/__init__.py`` directly compiles the example under
``working/``.  The module adjusts ``sys.path`` at runtime so it functions both
as a package and as a standalone script.

See [compiler_features.md](compiler_features.md) for details on supported syntax.
