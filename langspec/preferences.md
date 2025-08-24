# Magma language — chosen syntax & core design (snapshot)

This document captures the syntax-focused design choices made interactively.

## High-level

- Goal: General-purpose language (hybrid Rust / TypeScript inspiration)
- File extension: `.mgs`
- Output targets (initial): C (simple C backend) and JavaScript
- Program entry point: No implicit `main`; programs have no default entry point.
- Focus: syntax and type system; standard library decisions deferred

- Packaging/distribution: Use a package manifest format for releases/libraries (manifest file declares name, version, dependencies).

- Package manifest format chosen: TOML (Cargo.toml-style).

## Typing & ownership

- Typing: Static with a mix of explicit annotations and local inference; explicit generics required for polymorphism.
- Mutability: Immutable by default; explicit mutable bindings with `let mut name: Type = ...;` (Rust-style)
- Ownership/Borrowing: Ownership + borrowing model (Rust-like). Lifetimes follow Rust-style elision rules; lifetime syntax uses tick names (e.g., `'a`).
- Unsafe: No unsafe escape hatch — always safe.
- Nulls: Null is prohibited; use `Option<T>` for nullable values.

- Integer types (fixed-size): unsigned U8, U16, U32, U64, signed I8, I16, I32, I64, and pointer-sized USize/usize. (Monomorphic fixed-size integers only as chosen.)

- Integer literal syntax: use explicit suffixes to indicate type, e.g. `5I32`, `10U8`.
- Default numeric types: default integer type is `I32`; default float type is `F32`.

## Generics, traits & dispatch

- Generics: Monomorphization (compile-time specialization).
- Generics syntax: Angle-brackets with inline bounds: `fn id<T>(x: T) -> T` and `fn parse<T: Parser>(...)`.
- Polymorphism/ad-hoc: Nominal traits (explicit `trait` + `impl` model) with explicit impls.
- Trait objects / dynamic dispatch: Allowed, but trait objects must be stored behind explicit pointer wrappers (syntax does not mandate the wrappers themselves; they are library types).
- Explicit generics are required on functions and exported APIs (no full HM inference for exported generics).
- Coherence: free impls allowed anywhere (no orphan restriction).

## Error handling

- Result/Option monads for errors; `?` propagation operator supported.

## Refinement types and verification

- Refinements supported; statically enforced.
- Decision: Built-in verifier using a decidable fragment — Presburger (linear integer arithmetic) chosen for the built-in fragment to maximize compile-time proofs while keeping verification fast.
- Refinements are fully statically checked by the built-in verifier (no SMT integration for now).
- Example syntax: `let x: I32 > 10;` and singleton literal refinements like `let x: 5I32 = 5I32;`.
- Integer overflow behavior is governed by refinement checks rather than implicit wrapping/trapping.

## Syntax style & core constructs

- Surface syntax: C-style braces and semicolon conventions.
- Expression-oriented language: `if`, `match`, `loop` are expressions that yield values.
- Last-expression return: implicit last-expression return chosen (final expression in a block is the block's value, no explicit `return` required).
- Pattern matching: Rust-style `enum` + `match` with exhaustive checking and guards. Wildcard uses underscore `_`.
- Matching semantics: match binds by move by default (moves unless matched by reference).
- Arrays/slices: support fixed-size arrays (`[T; N]`) and dynamic slices/arrays (`[]T`).
 - Array literal syntax: use `[1, 2, 3]` for list literals and `[]` for an empty array.
 - Tuples: tuple literals use square-bracket syntax `[]` as well (same visual form as arrays/ slices).

- Tuple/array disambiguation: there is no syntax distinction between single-element tuples and single-element arrays — they are the same at runtime.
- String literals: double-quoted escaped strings (e.g., "hello\n") only for now.

- Comments: C-style `//` single-line and `/* ... */` block comments.

- Semicolons: require semicolons after statements/expressions, except the final expression in a block may omit the trailing semicolon to return its value (omit semicolon to return, Rust-style).

## Declarations, classes, and modules

- Types / classes: class-style type declarations allowed inside a single declaration, e.g.:

  ```mgs
  class fn Point(x: I32, y: I32) => {
      fn add(*this, other: Point) -> Point { ... }
  }
  ```

- Methods: explicit receiver required (e.g., `*this`, `*mut this`, `&this`), and method definitions are declared inside the type/class body.
- Inheritance: No subclassing / inheritance.
- Interfaces: Classes declare implemented interfaces inline using `with InterfaceName;` inside the class body.

- Visibility & modules:
  - Top-level module resolution: `import parent.Child` resolves to `(<project source root>)/parent.mgs`.
  - No Java-style `package` statements.
  - Explicit `export` keyword required for symbols to be importable from other modules (top-level exports are not implicit).
  - Import aliases supported: `import parent.Child as Thing;`.
  - Re-exporting: disallowed — modules may not re-export symbols they import. All exported symbols must be declared with `export` in the original defining module.
  - Parameterized modules: allow top-level parameter declarations and instantiation syntax. Example:
    - top-level parameter declaration: `require(x: I32);`
    - instantiate/import with parameter: `import parent.Child(123);`
    - parameter kinds: module parameters may be both types and values. Example syntax: `require <T>(value: T);` and `import M(123);` or `import M(MyType);`.
    - evaluation time: module parameters are runtime values provided at import/instantiation time (e.g. `import M(123);`).
      - implication: because these parameters are only available at runtime they are not usable for compile-time decisions such as monomorphization, refinement checking, or other static specialization.
      - note: when a "type" is passed as a module parameter it is represented as a runtime type descriptor (a reflective runtime value) rather than a compile-time-only type; it cannot trigger code specialization.
  - allowed values: no restrictions — module parameters may be any runtime value (primitives, structs/objects, closures, runtime type descriptors, etc.).

  - Circular imports: disallowed — import cycles are a compile-time error; the import graph must be acyclic.

  - Module instantiation identity: each distinct set of runtime arguments produces a fresh module instance with its own identity and state (separate from other instantiations of the same module path with different args).
  - Import evaluation location: imports are evaluated at the location of the `import` statement and need not be top-level. An `import` may appear anywhere a statement is allowed; the module body runs (per the instance policy) at that point.
  - Instantiation timing: module instances are evaluated eagerly at the import location (the module body runs immediately when the `import` statement executes).

  - Top-level mutable state: allowed — modules may declare `let mut` bindings at top level. Each module instance (per argument set) has its own independent top-level mutable state.
    - follow-up: whether other modules may mutate an exported top-level `let mut` variable is undecided (question pending).

## Concurrency & other runtime

- Concurrency: not defined in the language surface syntax (left to libraries / future design).
- Floating point: C-like IEEE-754 floats (+NaN, +Inf, -Inf, signed zero). Refinements can assert non-NaN/finite where needed.

## Metaprogramming & operators

- Metaprogramming: none for now (no macros / compile-time plugins).
- Operator overloading: disallowed — prefer explicit function/method names.

## Misc

- Pattern examples:
  - `let result = match value { 1 => a, 2 => b, _ => default };`
- Source root: imports resolve from the project source root by module-path names (e.g. `import parent.Child` -> `<source_root>/parent.mgs`).

---

If you want, I can:
- Commit this file to `langspec/preferences.md` (done).  
- Produce a concise one-page printed summary (README-style) or a JSON version for machine consumption.  
- Start a grammar sketch (BNF) or small example programs showing these syntax choices.

Status: `langspec/preferences.md` created with the above content.

- Import policy: explicit imports required; no implicit prelude. Note: this language is intended to support bare-metal environments (minimal or no runtime).
