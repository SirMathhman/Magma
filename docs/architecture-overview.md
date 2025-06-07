# Architecture Overview

This transpiler is intentionally small. The code favors plain string
processing over heavy parsing frameworks so that it can run without the
full Java standard library.  Each helper focuses on one task and
contains at most a single loop.  Complex regular expressions are avoided
in favor of simple scans so the logic stays maintainable.

The build and test scripts are kept as short shell wrappers. They can be
invoked through `bash` on Windows so the same commands work on both
platforms.

## Main Modules

- `magma.app.Transpiler` – orchestrates the conversion to TypeScript
- `ImportHelper` – rewrites package declarations and import lines
- `MethodStubber` – replaces method bodies with `// TODO` stubs and
  walks expressions using `parseValue`
- `FieldTranspiler` – converts Java field definitions
- `ArrowHelper` – rewrites lambda expressions to arrow functions
- `TypeMapper` – maps primitive and generic types and leaves unknown
  identifiers unchanged so the output stays close to the source
- `magma.Main` – CLI entry point
- `magma.result.Result` and `magma.option.Option` – lightweight
  replacements for exceptions

The `parseValue` routine incrementally scans characters.  It recognizes
member access, method calls, literals and the logical not operator.
Arguments inside method calls default to `/* TODO */` unless they are
simple literals or identifiers. Negated method calls keep their callee
name so boolean checks remain readable.
