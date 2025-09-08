# Magma language specification (initial)

This document captures the initial language design for Magma and outlines semantics that the compiler should implement. It is intentionally small and opinionated so the compiler can be built incrementally.

## Language goals

- Statically-typed, concise syntax.
- Predictable memory model (manual allocation or explicit ownership rules).
- Easy interop with C (straightforward mapping to C types and calling conventions).

## Lexical structure

- ASCII-based source files.
- Line comments start with `//` and block comments use `/* ... */`.

## Basic types

- `i32` — 32-bit signed integer.
- `i64` — 64-bit signed integer.
- `f32` — 32-bit float.
- `f64` — 64-bit float.
- `bool` — boolean type (`true` / `false`).
- `void` — no value (for functions).
- `ptr<T>` — raw pointer to `T` (maps to `T*` in C).

Type syntax is explicit: variables and function parameters must be declared with a type.

## Variables and assignments

- Immutable binding with `let` (read-only after initialization).
- Mutable binding with `var`.
- Example:

    let x: i32 = 10;
    var y: i32 = x + 5;

## Expressions and operators

- Standard arithmetic: `+`, `-`, `*`, `/`, `%`.
- Comparison: `==`, `!=`, `<`, `<=`, `>`, `>=`.
- Logical: `&&`, `||`, `!`.

Operator semantics follow usual C-like precedence and short-circuiting for `&&`/`||`.

## Control flow

- `if` / `else` expressions with required boolean condition.
- `while` loops.
- `for` loops are not included initially (can be desugared to `while`).

Example:

    if x > 0 {
        y = y - 1;
    } else {
        y = y + 1;
    }

## Functions

- Declaration syntax:

    fn add(a: i32, b: i32) -> i32 {
        return a + b;
    }

- Functions must declare parameter types and return type. `void` for no return value.
- No overloading initially.

## Memory model

- Provide `alloc<T>(n: i64) -> ptr<T>` and `free<T>(p: ptr<T>)` in the standard library (these map to `malloc`/`free`).
- `ptr<T>` is an unsafe raw pointer; the compiler will not track ownership.

## Standard library

- Minimal stdlib: memory alloc/free, simple I/O (`print` for strings and numeric types), and basic math.

## Error handling

- No exceptions initially. Functions can return `i32` error codes or `Option<T>` (a simple enum) in the future.

## Example: Magma -> C translation

Magma source:

    fn add(a: i32, b: i32) -> i32 {
        return a + b;
    }

    fn main() -> i32 {
        let x: i32 = add(2, 3);
        print(x);
        return 0;
    }

Generated C (illustrative):

    #include <stdio.h>

    int add(int a, int b) {
        return a + b;
    }

    int main(void) {
        int x = add(2, 3);
        printf("%d\n", x);
        return 0;
    }

## Notes and open questions

- This spec is intentionally small. We need to decide on generics, modules, concurrency, and ownership model next.

---

Update this file as the language design evolves. Once these basics are stable, the compiler pipeline (lexer -> parser -> typechecker -> codegen) can be implemented to match.
