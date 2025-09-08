# Magma — a simple compiler from Magma language to C

Magma is an experimental programming language and a small compiler that translates Magma source programs into C. The goal of this repository is to incrementally build a reliable frontend (lexer, parser, typechecker) and a backend that emits readable, idiomatic C code.

This repository is in an early stage. The primary purpose of the current files is to capture the language design, provide a quickstart for contributors, and outline the roadmap for implementing the compiler.

## Goals

- Define a compact, statically typed language (Magma) with a minimal standard library.
- Implement a modular compiler pipeline: lexing, parsing, semantic analysis (type checking), IR generation, and C code emission.
- Produce readable C output that can be compiled by common C compilers (gcc/clang/msvc).
- Provide tests and examples to validate language features and code generation.

## Project layout

- `docs/` — language specification and quickstart guides.
- `src/` — compiler source code (not yet present).
- `examples/` — example Magma programs and their generated C output (to be added).
- `tests/` — test-suite (to be added).

## Roadmap (high-level)

1. Write initial language spec and example programs.
2. Implement lexer and parser, and a minimal AST.
3. Implement type checker and semantic validations.
4. Implement a simple IR and a C code emitter for a subset of the language.
5. Expand language features and test coverage, improve emitted C quality.

## How to contribute

1. Open an issue describing the feature or bug.
2. Fork and create topic branches for changes.
3. Add tests for new features and run the test-suite.
4. Send a pull request with clear justification and tests.

## Next steps in this repo

- Add `docs/spec.md` with the initial language design.
- Add `docs/quickstart.md` with examples and a simple build workflow.
- Start implementing the lexer and parser in `src/`.

---

If you're the language designer, please see `docs/spec.md` for the spec and answer the 5 design questions in the project's root README or in a reply to guide implementation.
