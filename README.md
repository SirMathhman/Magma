# Magma Self-Hosted Compiler

This repository contains a single monolithic file `src/magma/Main.java`. The program is intentionally self-contained and compiles itself from Java to C. The design keeps all logic in one place so the generated C code mirrors the original source.

## Error Handling

Earlier versions inserted placeholder comments whenever the compiler encountered unsupported syntax. Placeholders have been removed in favor of explicit errors. The compiler now throws `CompilationError` when it cannot handle an input fragment.

## Key Components

- **Option** and **List** – lightweight collection abstractions.
- **Result** – represents success or `IOError` from file operations.
- **JavaType**, **Node**, **CDefinition** – models of the generated C code.

