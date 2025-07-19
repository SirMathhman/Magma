# Project Roadmap

The roadmap tracks upcoming milestones. Items checked are completed.

- [ ] Set up project scaffolding and initial tests
- [x] Implement a minimal compiler skeleton
- [ ] Expand the language grammar
  - [x] Support `fn name() => {}` to `void name() {}`
  - [x] Handle multiple `fn` declarations in a single file
  - [x] Allow optional `Void` return type with `fn name(): Void => {}`
  - [x] Support boolean return with `fn name(): Bool => { return true; }` or
    `fn name(): Bool => { return false; }` generating
    `int name() { return 1; }` or `int name() { return 0; }` in C
  - [x] Accept extra whitespace in function declarations, including newlines
    and carriage returns
  - [x] Map numeric return types `U8`, `U16`, `U32`, `U64`, `I8`, `I16`, `I32`,
    `I64` to standard C integers
  - [x] Support simple `let` statements within functions
  - [x] Support array `let` statements like `let nums: [I32; 3] = [1, 2, 3];`
  - [x] Allow assignment statements with `mut` declarations
  - [x] Support `struct` declarations like `struct Point {x : I32;}`
  - [x] Allow function parameters with `fn add(x: I32, y: I32)` syntax
  - [x] Handle nested braces within function bodies
  - [x] Support `if` statements written as `if (condition) { ... }`
  - [x] Validate comparison operators `<`, `<=`, `>`, `>=`, and `==` in `if`
    conditions
  - [x] Check array indexing against compile-time bounds
  - [x] Allow optional parentheses around expressions
- [ ] Build a self-hosted version of Magma

- [x] Set up CI/CD pipeline for running tests
