# Magma Language Specification (Draft)

This document is a living draft of the Magma language specification. It contains the current assumptions, syntax sketches (EBNF), core types, semantics, examples, and notes for future extension. This is a stubbed, pragmatic starting point to make design and implementation decisions.

## Overview

Magma is a small, statically-typed language designed to be compiled into portable C (C11). It aims to combine imperative and functional features, with an emphasis on clarity of code generation and predictable runtime behavior.

Key characteristics:
- Statically typed with local type inference (let-bindings)
- Functions are first-class (support for higher-order functions)
- Algebraic data types (ADTs) / sum types (optional MVP feature)
- Records (struct-like types) and arrays
- Simple module system (one file = one module)

This document defines a minimal core suitable for an MVP compiler. Optional features are marked.

## Lexical structure

- Line comments start with `//` and continue to end of line
- Block comments `/* ... */` allowed and nestable (implementation note)
- Identifiers: `[A-Za-z_][A-Za-z0-9_]*`
- Keywords (non-exhaustive): `let`, `fn`, `if`, `else`, `match`, `return`, `type`, `import`, `module`

## Grammar (EBNF - draft)

Note: This is a compact EBNF for the core; whitespace and precedence rules are described after.

program        ::= { top_level }
top_level      ::= function_decl | type_decl | import_decl | stmt
import_decl    ::= 'import' string_literal
type_decl      ::= 'type' Identifier '=' type_expr
function_decl  ::= 'fn' Identifier '(' [ param_list ] ')' [ '->' type_expr ] block
param_list     ::= param { ',' param }
param          ::= Identifier ':' type_expr
type_expr      ::= Identifier | 'fn' '(' [ type_list ] ')' '->' type_expr | record_type | array_type
type_list      ::= type_expr { ',' type_expr }
record_type    ::= '{' field_list '}'
field_list     ::= field { ',' field }
field          ::= Identifier ':' type_expr
array_type     ::= type_expr '[' ']'

stmt           ::= let_stmt | expr_stmt | return_stmt | if_stmt | while_stmt | block
let_stmt       ::= 'let' Identifier [ ':' type_expr ] '=' expr
return_stmt    ::= 'return' [ expr ]
if_stmt        ::= 'if' '(' expr ')' stmt [ 'else' stmt ]
while_stmt     ::= 'while' '(' expr ')' stmt
block          ::= '{' { stmt } '}'

expr           ::= assignment
assignment     ::= logical_or [ '=' assignment ]
logical_or     ::= logical_and { '||' logical_and }
logical_and    ::= equality { '&&' equality }
equality       ::= comparison { ( '==' | '!=' ) comparison }
comparison     ::= term { ( '<' | '>' | '<=' | '>=' ) term }
term           ::= factor { ( '+' | '-' ) factor }
factor         ::= unary { ( '*' | '/' | '%' ) unary }
unary          ::= ( '!' | '-' ) unary | primary
primary        ::= literal | Identifier | fn_literal | '(' expr ')' | call | array_literal | record_literal
call           ::= primary '(' [ arg_list ] ')'
arg_list       ::= expr { ',' expr }
fn_literal     ::= 'fn' '(' [ param_list_no_names ] ')' [ '->' type_expr ] block
param_list_no_names ::= type_expr { ',' type_expr }

literal        ::= integer_literal | float_literal | string_literal | boolean_literal

Identifier     ::= /* as above */

This grammar intentionally avoids complex precedence features like pattern matching in expressions — `match` or `switch` can be added as top-level or expression-level forms.

## Types

Core types:
- i32, i64 (signed integers)
- f32, f64 (floating point)
- bool
- char
- string (immutable reference to UTF-8 data)
- arrays (T[])
- records (struct-like)
- function types (fn(...)->T)

Type system rules (draft):

- Variables declared with `let` have either explicit annotation or inferred from initializer.
- Functions must declare return types for clarity in codegen (inferred return may be allowed later).
- Subtyping is minimal / structural for records (MVP: no subtyping).

## Semantics (high-level)

- Evaluation order: eager, left-to-right for function arguments
- Integer overflow: defined as wrap-around for i32/i64 (matches C behavior unless we add sanitizer)
- Memory: heap allocations via runtime helpers (malloc/wrapped alloc) for arrays and records. Manual free or runtime-managed via reference counting depending on runtime choice.

## Standard library (stub)

Add as `magma.runtime` (implemented in C for the target runtime):
- print, println
- memory allocation helpers: mg_alloc(size), mg_free(ptr)
- basic I/O and runtime asserts

## Examples

Hello world

```
fn main() {
  print("Hello, Magma\n");
}
```

Factorial (recursive)

```
fn fact(n: i32) -> i32 {
  if (n <= 1) {
    return 1;
  }
  return n * fact(n - 1);
}

fn main() {
  let x: i32 = fact(10);
  println(x);
}
```

Higher-order function

```
fn applyTwice(f: fn(i32) -> i32, x: i32) -> i32 {
  return f(f(x));
}

fn inc(a: i32) -> i32 { return a + 1; }

fn main() { println(applyTwice(inc, 3)); }
```

## Error reporting

- Compiler will report errors with file/line/column and a short message.
- Diagnostics will include a snippet and an arrow pointing to the relevant code (MVP: best-effort).

## Interop and C generation considerations

- Name Mangling: Map Magma identifiers to C-safe identifiers; avoid collisions for generated symbols.
- Calling convention: Use C calling conventions; functions without closures map to direct C functions.
- Closures: Lower closures to structs capturing environment + function pointer (MVP: limit closure capture to values; no GC assumptions).

## Future work / optional features

- Pattern matching (`match`) with exhaustiveness checking
- Algebraic Data Types (ADTs) and tagged unions
- Macros or compile-time functions
- Optimization passes and an SSA IR
- Better memory management (refcounting or GC)

---

This file is intentionally pragmatic and minimal to accelerate the compiler MVP. The next step is to stabilize the syntax choices, pick a parser strategy (ANTLR vs hand-rolled), and expand the type system and runtime model in detail.
