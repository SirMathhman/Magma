# Magma Language Specification (Draft)

Status: Draft

This document specifies the Magma language at a level sufficient for implementing a compiler frontend and backends. Sections may be expanded as the language design matures. The wording uses normative language: "shall" for requirements, "should" for recommendations, and "will" for expected behaviors.

## 1. Goals and Design Principles

- Magma shall be a small, expressive language that prioritizes clarity and predictable semantics over syntactic brevity.
- The language shall be specified independently of any particular implementation strategy or backend. Implementations shall document backend-specific details (for example, code generation patterns and runtime requirements) in an implementation guide.

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

## Operators and comparisons

- Magma shall support the usual comparison and logical operators with the following symbols and semantics:

  - Equality: `==`, `!=` — test for equality / inequality.
  - Relational: `<`, `<=`, `>`, `>=` — ordered comparisons for numeric values.
  - Logical not: `!` — boolean negation.

- Semantics and typing rules:
  - `==` and `!=` shall be defined for values of the same type for primitive types (`I8..I64`, `U8..U64`, `float`, `Bool`) and shall compare numeric values by value and booleans by truth value.
  - Equality for `string` values shall compare string contents (implementations shall provide a runtime helper for content equality).
  - Relational operators `<`, `<=`, `>`, `>=` shall be defined only for numeric types (signed and unsigned integers and floats). Comparing values with incompatible numeric kinds (for example mixing signed and unsigned integer types without an explicit conversion) shall be a type-check error; implementations shall document any implicit promotions they perform.
  - The unary `!` operator shall accept and return `Bool` and shall evaluate to the boolean negation of its operand; applying `!` to non-boolean values shall be a type error unless the implementation defines a documented conversion.

- Operator precedence (informative):
  - Unary operators (including `!` and unary `-`) bind tighter than multiplicative/additive operators.
  - Relational operators bind below additive/multiplicative and above logical `&&` / `||`.

Implementations shall produce clear diagnostics when operators are applied to operands with incompatible types.

## 5. Types and Type System

- Magma shall have the following builtin types at MVP:
  - `int` — 32-bit signed integer
  - `float` — double-precision floating point
  - `bool` / `Bool` — boolean (literal forms: `true`, `false`)
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
- Closures and first-class functions shall be supported in a limited form in the MVP: function literals shall be allowed. How closures and function values are represented is a backend implementation concern; implementations shall document their chosen lowering and calling conventions.

ABI and calling convention:
- The specification does not mandate a particular ABI or calling convention; these are implementation details. Implementations shall document the conventions they use and any implications for interop.

## 7. Memory model and runtime

- The language's memory model shall distinguish between value and reference types. Strings, arrays, and other heap-allocated objects are conceptualized as heap values in the language.
- The specification does not mandate a particular allocation or memory-management strategy (manual, reference-counting, or garbage-collected). Runtime design choices are left to implementations; implementers shall document the runtime API and semantics for their backend.

Automatic memory management (reference counting or GC) will be evaluated after the MVP; the specification does not require a GC for the initial release.

## 8. Standard library (initial)

- The standard library shall include a small set of modules for:
  - `io` — printing and basic I/O
  - `math` — numeric helpers
  - `strings` — string utilities

The implementation language and packaging of the standard library are implementation concerns. The standard library shall provide a stable, documented API for user programs.

## 9. Error handling

- Magma shall use explicit error returns or a `Result<T, E>` style in a future revision; for the MVP, error handling shall be done via returned status codes and optional `Option`-like patterns implemented in the standard library.
- Compiler diagnostics shall include:
  - file path, line, and column
  - a short error code or label
  - a human-readable message
  - a code snippet with an arrow indicating the error position

## 10. Interoperability with foreign code

- Programs shall be able to call externally-provided functions and use externally-declared types. The exact foreign-function interface (FFI) syntax and semantics are implementation-defined and shall be documented by each backend.

Backends that target C or other native ABIs should document how `extern` declarations map to the target ABI.

## 11. Tooling and build

- The language specification does not mandate a particular implementation technology or build system for the compiler. Implementations may provide a CLI, IDE integrations, and build tooling as appropriate.

Typical CLI functionality includes specifying input source files, an output location, and options for choosing a backend or target artifact type; exact CLI syntax is implementation-defined.

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
      let x = 10; // x shall be inferred as I32 by default
      let y: int = 20;
      return add(x, y);
    }

Local `let` statement example (explicit width annotation):

    fn main() -> int {
      let x : I32 = 0;
      return x;
    }

## Local `let` statements (explicit width annotation)

- Local `let` statements shall allow an explicit integer width annotation. The supported annotations in the MVP are:

  - Unsigned: `U8`, `U16`, `U32`, `U64`
  - Signed: `I8`, `I16`, `I32`, `I64`

  Example:

      fn main() -> int {
        let a: U8 = 0;
        let b: I32 = 0;
        return b;
      }

  Notes:
  - An annotation such as `I32` shall indicate a 32-bit signed integer; `U32` shall indicate a 32-bit unsigned integer, and similarly for other widths. Implementations shall map these annotations to the platform-specific fixed-width integer types (for example `int32_t` / `uint32_t` in the C reference backend).
  - The default integer type for unannotated integer literals and inferred locals shall be `I32`. For example, `let x = 0;` shall infer `x` as `I32` unless another type is required by context.
  - If a `let` binding includes an explicit annotation (for example `let x : U8 = 0;`), the initializer shall be interpreted in the context of the annotated type: `0` becomes `U8` in that example (not `I32`).
  - If both an explicit annotation and an initializer are present, the compiler shall check that the initializer's value is representable in the annotated width and signedness and shall produce a type error on mismatch (for example, assigning `-1` to `U8` or assigning `256` to `U8` shall be an error).
  - Implementations shall document the precise semantics for out-of-range integer literals (for example, whether such cases are reported as parse-time errors or type-check errors).

If statements

- Magma shall support `if` statements with an optional `else` branch. The concrete syntax is:

      if ( <expression> ) <statement> [ else <statement> ]

- Semantics and typing:
  - The condition expression inside `if (...)` shall have type `Bool`. If the condition has a different type, the compiler shall produce a type error unless the implementation documents and performs a well-defined conversion.
  - Both the `then` and `else` branches are statements and may be either a single statement or a block (`{ ... }`).
  - An `if` without an `else` shall execute the `then` branch when the condition is true and do nothing when false.

- Definite assignment and example:
  - The compiler shall enforce definite assignment for local variables declared without an initializer: every possible control-flow path that leads to a use of the local must assign it a value first. For example, the following shall be valid because both branches assign `x` before any use:

      fn main() -> int {
        let x : I32;
        if (true) x = 3; else x = 5;
        return x;
      }

  - If a local may be used on some control-flow path without prior initialization, the compiler shall emit an error.

Top-level expression programs (convenience form):

    Magma implementations may accept a source file that consists of a single top-level expression followed by an optional type annotation suffix that indicates the intended integer width. For the MVP the supported integer suffix is `I32` which denotes a 32-bit signed integer literal result. When a source file is a single expression, it is semantically equivalent to providing an explicit `main` function that returns the value of that expression. Concretely:

    Source file:

        5I32

    Is equivalent to:

        fn main() -> int {
          return 5;
        }

    The returned integer value becomes the program's process exit code when targeting native executables or a corresponding platform-specific return value for other backends. Implementations shall document backend-specific limits for mapping integer return values to process exit codes (for example, truncation or sign behavior) but shall, by default, treat the `int` return value as a 32-bit signed value and use its low-order 8 bits as the POSIX exit code when producing native executables, unless the backend documents a different mapping.

  Boolean example:

      fn main() -> int {
        let b: Bool = true;
        if (b) {
          return 0;
        }
        return 1;
      }

## 13. Future work (non-normative)

- Generics / parametric polymorphism
- Pattern matching
- Advanced optimizer and SSA-based IR
- Native interop improvements and safer FFI

## 14. Conformance and testing

- The compiler shall include a suite of conformance tests derived from examples in this specification.

The test harness shall compile Magma programs and shall verify that produced artifacts behave as specified for the chosen backend. Implementations should include end-to-end tests that exercise the frontend, typechecker, codegen, and runtime.

## 15. Revision history

- 2025-09-08 — Add normative specification for local `let` statements and example `let x : I32 = 0;` — user

- 2025-09-08 — Add normative wording for `let` with explicit `I32` annotation and type-checking rules — assistant

- Draft created: 2025-09-08 — initial structure and normative language

- 2025-09-08 — Add `Bool` type and `true`/`false` boolean literals; example and semantics added — user

- Draft created: 2025-09-08 — initial structure and normative language

---

This specification is a working draft. Sections marked as future work shall be revised into normative requirements when implemented.
