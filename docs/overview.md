## Module Overview

The project is organized around a few small modules:

- `Main` – entry point orchestrating diagram and stub generation.
- `GenerateDiagram` – creates PlantUML diagrams from the sources.
- `TypeScriptStubs` – writes TypeScript stubs that mirror the Java classes.
- `JavaFile` and `Sources` – helpers for parsing Java source files.
- `PathLike`/`JVMPath` – thin wrappers around file system paths.
- `Option` (`Some`/`None`) – optional value type.
- `Result` (`Ok`/`Err`) – result type used for error handling.

These modules keep the codebase small and easy to navigate.
