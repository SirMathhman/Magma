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
- `MethodStubber` – replaces method bodies with `// TODO` stubs.
  Each helper scans once so every function contains at most a single loop
  and indentation never exceeds two levels. Expressions are walked using
  `parseValue`.
- Nested `if` and `while` blocks are parsed recursively so statements
  inside them are handled just like top-level code.
- `FieldTranspiler` – converts Java field definitions
- `ArrowHelper` – rewrites lambda expressions to arrow functions
  - `TypeMapper` – maps primitive, boxed, and generic types and leaves unknown
    identifiers unchanged so the output stays close to the source
  - `java.util.function` interfaces map to arrow function types
- `magma.Main` – CLI entry point
 - `magma.result.Result` and `magma.option.Option` – lightweight
   replacements for exceptions. `Option` values can convert to a generic
   `Iter` so optional results compose with iterator helpers
- `magma.path.PathLike` and `magma.path.NioPath` – small wrapper around
  `java.nio.file.Path` so other classes don't depend on NIO directly.
  `NioPath` also provides helpers for reading and writing files so
  callers never touch `java.nio.file.Files`.
  These helpers now return `Result` or `Option` values rather than
  throwing `IOException` and are defined on `PathLike`.
- `PathLike.walk` – lists files without exposing `Files.walk` or throwing
  `IOException`
- `magma.list.ListLike` and `magma.list.JdkList` – simple list wrapper so
    code avoids a hard dependency on `java.util.List`. Iteration uses a
    lightweight `Iter` interface and a `ListIter` specialization instead of `java.lang.Iterable`.
    The iterator now exposes `map`, `fold`, and `flatMap` to keep loops out of callers.
    `flatMap` takes a function returning another iterator so nested lists can
    be flattened without revealing the underlying list implementation.

The `parseValue` routine incrementally scans characters.  It recognizes
member access, method calls, literals and the logical not operator.
Arguments inside method calls default to `/* TODO */` unless they are
simple literals or identifiers. Negated method calls keep their callee
name so boolean checks remain readable.

Recent tests stress nested and chained invocations to ensure this simple
scanner handles complex expressions without adding another parsing
framework. Member access after calls now composes with nested argument
lists so the design stays minimal while covering common Java idioms.

## Generated Output

The transpiler writes TypeScript files under `src/main/node`. These files are
checked into version control so we can observe how the generated code evolves.
They should never be edited directly.
