# Magma Self-Hosted Compiler

This repository contains a single monolithic file `src/magma/Main.java`. The program is intentionally self-contained and compiles itself from Java to C. The design keeps all logic in one place so the generated C code mirrors the original source.

## Error Handling

Earlier versions inserted placeholder comments whenever the compiler encountered unsupported syntax. Placeholders have been removed in favor of explicit errors. The compiler now reports failures using the checked `CompileException` which surfaces through the `Result` API.

## Key Components

- **Option** and **List** – lightweight collection abstractions.
- **Result** – generic wrapper that yields `Ok` or `Err` for either file IO or compilation.
- **CompileException** – checked exception signalling unsupported Java constructs.
- **JavaType**, **Node**, **CDefinition** – models of the generated C code.

### Main Structures

- `Main` – entry point and home of the compiler logic.
- `Path`/`IOError` – minimal file abstraction.
- `Option`, `List`, `Result` – simple collections and sum types providing functional style utilities.

