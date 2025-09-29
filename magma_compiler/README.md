# Magma Compiler

Prototype compiler for the Magma programming language.

Features:

- Minimal lexer/tokenizer
- Basic AST skeleton
- CLI to print tokens

Usage:

Run tests:

    cargo test --manifest-path magma_compiler/Cargo.toml

Build and run:

    cargo run --manifest-path magma_compiler/Cargo.toml -- < input.mg
