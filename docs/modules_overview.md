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
    per line, each translated in the same manner.
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
    the C form `struct Name {int x;};`.

- `.github/workflows/ci.yml` – GitHub Actions workflow that installs dependencies and runs `pytest`.
