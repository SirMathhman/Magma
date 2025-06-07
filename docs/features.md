# Java to TypeScript Feature Map

This document summarises which Java language features are currently handled by the Magma compiler and which remain to be implemented.

## Implemented Features

- Parses classes, interfaces and records from the Java sources.
- Preserves `extends` and `implements` relationships in the generated TypeScript.
- Converts fields and methods with their parameter and return types.
- Supports generic type parameters on classes and methods.
- Preserves the `static` modifier on methods.
- Maps primitive types (`byte`, `short`, `int`, `long`, `float`, `double`) to `number`, `boolean` to `boolean` and `char`/`String` to `string` via the `NumberType`, `BooleanType` and `StringType` nodes.
- Converts Java functional interfaces (`Function`, `BiFunction`, `Supplier`) to arrow function types.
- Resolves type parameters within record constructors and fields.
- Compiles all Java files under `src/` to matching paths under `src-web/`.
- Includes a `check-ts.sh` utility to type-check the generated stubs.

## Missing Features

The TypeScript output still uses placeholders for several Java constructs:

- Lambda expressions, `switch` statements and `instanceof` checks are emitted as `0`.
- `if`, `for` and `while` headers are not parsed and always generate `if (true)`.
- `case` statements inside `switch` blocks are ignored.
- Variadic type arguments and some complex type forms are replaced with the symbol `?`.

Completing these items is required before the compiler can bootstrap itself purely from the generated TypeScript.
