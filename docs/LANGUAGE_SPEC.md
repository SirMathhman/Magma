# Magma Language Specification (Draft)

Status: Draft

This document specifies the Magma language at a level sufficient for implementing a compiler frontend and a reference code generator to C. Sections may be expanded as the language design matures. The wording uses normative language: "shall" for requirements, "should" for recommendations, and "will" for expected behaviors.

## 1. Goals and Design Principles

- Magma shall be a small, expressive language designed to be compiled to portable C.
- Magma shall prioritize clarity and predictable semantics over syntactic brevity.
- The compiler shall aim to generate readable C that mirrors the structure of the original Magma source for debugging.

## 2. File & Module Model

- A Magma source file shall have the extension `.mg` or `.magma`.
- One source file shall correspond to one top-level module unless the module system is explicitly declared.
- Importing another module shall be done with an `import` declaration at file scope.

Example:

    import io

## 3. Lexical grammar

- Magma shall use UTF-8 encoded source text.
- Identifiers shall begin with an ASCII letter or underscore and may contain letters, digits, or underscores. Identifiers are case-sensitive.
- Numeric literals shall include integer and floating-point forms. Integer literals shall be decimal only in the MVP.
- String literals shall be enclosed in double quotes and shall support C-style escape sequences (e.g., `\n`, `\t`, `\\`, `\"`).
- Comments shall be supported in two forms:
  - Line comments starting with `//` until end-of-line.
  - Block comments enclosed between `/*` and `*/`, which shall not be nestable in the MVP.

## 4. Syntax (EBNF, draft)

The grammar below is a simplified EBNF sketch for the initial language surface. This grammar shall be refined into a full ANTLR grammar or equivalent.

    program        ::= { declaration }
    declaration    ::= function_decl | type_decl | import_decl | global_var_decl
    import_decl    ::= 'import' identifier ';'
    type_decl      ::= 'type' identifier '{' field_list '}'
    field_list     ::= [ field { ',' field } ]
    field          ::= identifier ':' type
    global_var_decl::= 'var' identifier ':' type [ '=' expression ] ';'

    function_decl  ::= 'fn' identifier '(' [ param_list ] ')' [ '->' type ] block
    param_list     ::= param { ',' param }
    param          ::= identifier ':' type

    block          ::= '{' { statement } '}'
    statement      ::= local_var_decl | expression_stmt | return_stmt | if_stmt | while_stmt
    local_var_decl ::= 'let' identifier [ ':' type ] [ '=' expression ] ';'
    expression_stmt::= expression ';'
    return_stmt    ::= 'return' [ expression ] ';'
    if_stmt        ::= 'if' '(' expression ')' block [ 'else' block ]
    while_stmt     ::= 'while' '(' expression ')' block

    expression     ::= assignment
    assignment     ::= logical_or [ '=' assignment ]
    logical_or     ::= logical_and { '||' logical_and }
    logical_and    ::= equality { '&&' equality }
    equality       ::= relational { ( '==' | '!=' ) relational }
    relational     ::= additive { ( '<' | '>' | '<=' | '>=' ) additive }
    additive       ::= multiplicative { ( '+' | '-' ) multiplicative }
    multiplicative ::= unary { ( '*' | '/' | '%' ) unary }
    unary          ::= ( '!' | '-' ) unary | primary
    primary        ::= literal | identifier | '(' expression ')' | function_literal

    function_literal ::= 'fn' '(' [ param_list ] ')' [ '->' type ] block

    literal        ::= integer | float | string | 'true' | 'false' | 'null'

Notes:
- The assignment operator `=` shall be right-associative for chained assignments.
- The grammar shall be designed to produce meaningful parse errors with source locations.

## 5. Types and Type System

- Magma shall have the following builtin types at MVP:
  - `int` — 32-bit signed integer
  - `float` — double-precision floating point
  - `bool` — boolean
  - `string` — UTF-8 string (heap-allocated)
  - `void` — used for functions that return no value
  - Struct types defined via `type` declarations

- Type inference shall be supported for local `let` bindings where an initializer is present (the compiler shall infer the declared type from the initializer expression).
- Function parameter and return types shall be explicitly declared in the MVP (type annotations shall be required for public APIs).
- The type system shall be statically checked at compile time and shall produce clear diagnostics for mismatches.

Type equivalence rules:
- Structural typing for record/struct types shall not be supported in the initial release; user-defined types shall be nominal.

Generics:
- Generics shall be considered future work and shall not be required for the MVP. If generics are added, they will be documented in a later revision.

## 6. Functions and calling conventions

- Functions shall be declared with `fn name(params) -> type { ... }`.
- Closures and first-class functions shall be supported in a limited form in the MVP: function literals shall be allowed, but capturing closures that capture mutable environments shall be lowered to helper structs in the generated C and shall be explicitly documented.
- The compiler shall map Magma function calls to C functions. When closures are used, the compiler shall generate wrapper structs and function pointers as needed.

ABI and calling convention:
- The generated C shall use the platform default calling convention for the target C compiler.

## 7. Memory model and runtime

- The Magma runtime shall provide allocation primitives for heap-allocated objects (strings, structs, arrays) and shall be provided as C source packaged with the compiler.
- Initially, the runtime shall rely on manual allocation and deallocation functions (malloc/free wrappers). The runtime should provide helper functions such as `magma_alloc`, `magma_free`, `magma_string_new`, and `magma_string_free`.
- Automatic memory management (reference counting or GC) will be evaluated after the MVP; the specification does not mandate a GC for the initial release.

## 8. Standard library (initial)

- The standard library shall include a small set of modules for:
  - `io` — printing and basic I/O
  - `math` — numeric helpers
  - `strings` — string utilities

- The standard library shall be implemented in portable C and shipped with the compiler as source files and headers.

## 9. Error handling

- Magma shall use explicit error returns or a `Result<T, E>` style in a future revision; for the MVP, error handling shall be done via returned status codes and optional `Option`-like patterns implemented in the standard library.
- Compiler diagnostics shall include:
  - file path, line, and column
  - a short error code or label
  - a human-readable message
  - a code snippet with an arrow indicating the error position

## 10. Interoperability with C

- Generated C shall be valid ISO C11 (or a widely-supported subset) and shall compile with `gcc` and `clang`.
- The compiler shall provide a way to declare `extern` C functions and types to call into hand-written C libraries.

Example extern declaration (draft syntax):

    extern c: {
      fn printf(format: string, ...) -> int;
    }

The exact syntax shall be defined in a later revision.

## 11. Tooling and build

- The Magma compiler shall be implemented as a Maven Java project and shall provide a CLI JAR.
- The CLI shall provide commands to compile Magma to C source files and optionally to invoke the system C compiler to produce an executable.

CLI semantics:
- `magma compile -o outdir src1.mg src2.mg` shall produce `outdir` containing generated `.c` and `.h` files and a small build manifest.

## 12. Examples

Hello World (Magma source):

    fn main() -> int {
      io::println("Hello, Magma!");
      return 0;
    }

This shall generate a C program that, when compiled and executed, prints "Hello, Magma!".

Function example with types:

    fn add(a: int, b: int) -> int {
      return a + b;
    }

Local inference example:

    fn main() -> int {
      let x = 10; // x shall be inferred as int
      let y: int = 20;
      return add(x, y);
    }

## 13. Future work (non-normative)

- Generics / parametric polymorphism
- Pattern matching
- Advanced optimizer and SSA-based IR
- Native interop improvements and safer FFI

## 14. Conformance and testing

- The compiler shall include a suite of conformance tests derived from examples in this specification.
- The test harness shall compile Magma programs and shall verify that generated C compiles and the produced executables behave as specified.

## 15. Revision history

- Draft created: 2025-09-08 — initial structure and normative language

---

This specification is a working draft. Sections marked as future work shall be revised into normative requirements when implemented.
