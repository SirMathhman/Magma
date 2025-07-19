# Modules Overview

This list summarizes the main modules of the project for quick reference.

- `magma.compiler` – entry point for compilation
  - `magma.compiler.Compiler` – minimal compiler skeleton. Operates on input and
    output files; empty input results in `int main() {}` in the output. It also
    supports a basic function syntax `fn name() => {}` which becomes
    `void name() {}` in C. The pattern tolerates arbitrary whitespace so that
    formatting differences – including newline and carriage return characters – do
    not affect compilation. An optional explicit
    return type such as `fn name(): Void => {}` is also recognized and produces
    the same C output. Functions may declare a boolean return with
    `fn name(): Bool => { return true; }` or `fn name(): Bool => { return false; }`.
    These yield `int name() { return 1; }` or `int name() { return 0; }` in C so
    that the output remains valid without extra headers. Numeric return types
    like `U8` or `I64` translate to plain C integers such as `unsigned char` or
    `long long` with a fixed body `return 0;`. Multiple functions can appear one
    per line, each translated in the same manner. Functions can also take
    parameters written as `name: Type` separated by commas, so
    `fn add(x: I32, y: I32)` yields `int add(int x, int y)` in the generated C.
    Function bodies may also contain simple variable declarations of the form
    `let name: I32 = 1;` which become `int name = 1;` in C. Only literal values
    are accepted so the regular-expression parser remains straightforward.
    Arrays can be declared with `let arr: [I32; 3] = [1, 2, 3];` resulting in
    `int arr[] = {1, 2, 3};`.
    When the type is omitted and the value is a boolean or integer literal, the
    compiler infers the type and still emits `int` in the generated C code.
    Assignment statements are supported when the variable is declared with
    `mut` and the new value matches the original type.  Reassignment is written
    simply as `name = 2;` and translates directly to the equivalent C statement.
    Structures can be declared with `struct Name {x : I32;}` and translate to
    the C form:
    
    ```c
    struct Name {
        int x;
    };
    ```
    Function and struct bodies are emitted with four-space indentation so the
    generated code is easier to inspect.
    Nested blocks written with `{` and `}` can be placed inside functions and
    may nest arbitrarily. Each level of braces increases the indentation in the
    generated C code.
    Simple `if` statements written as `if (condition) { ... }` are translated
    directly. Boolean literals in the condition become `1` or `0` to keep the
    generated C self-contained.
    Basic comparisons `<`, `<=`, `>`, `>=`, and `==` require both sides to have
    matching types; otherwise compilation fails.
    Function calls written as `foo(1, bar);` are copied directly after
    translating boolean literals to `1` or `0`. Each argument must either be a
    literal or a previously declared variable; otherwise compilation fails. When
    function parameters include numeric bounds such as `I32 > 10`, literal
    arguments are validated against the bound at compile time.
    Variables may also be initialized from other variables. Numeric
    declarations can include bounds like `let y: I32 > 10 = x;`. The initializer
    must provide at least the same bound; otherwise compilation fails.
    Array elements can be accessed with `arr[i]` when `i` is bounded by
    `arr.length` using a declaration like `let i: USize < arr.length = 0;`.
    Out-of-range constants are rejected at compile time.
    Expressions may be wrapped in any number of parentheses. The compiler
    removes matching outer pairs before processing so `( (x) )` is treated the
    same as `x`.
    Arithmetic expressions involving only numeric literals such as
    `1 + 2 * 3 - 4` are copied directly into the generated C code. They are
    evaluated only when needed for bound checks, keeping the parser minimal.

- `.github/workflows/ci.yml` – GitHub Actions workflow that installs dependencies and runs `pytest`.
