# Magma — Work Breakdown Structure (WBS)

Purpose
- Provide a prioritized, actionable roadmap to implement the Magma language spec in `langspec/preferences.md`.
- Each item has a short description, acceptance criteria, and priority (High/Med/Low).

Notes
- This WBS adopts a test-driven development (TDD) stance: tests drive design and the system architecture will emerge from passing tests rather than being rigidly pre-planned.
- Do not assume any fixed runtime/ABI/architecture up-front; prefer small, language-agnostic test artifacts and executable examples that demonstrate behavior.
- The immediate goal is a minimally useful prototype validated by tests (lexer/parser/type-checker/runtime behavior tests). Larger architecture decisions are deferred until driven by test needs.

1. Project setup (High)
- 1.1 Create repository layout
  - tasks: add `langspec/`, `compiler/`, `runtime/` (placeholder), `examples/`, `tests/` folders; add README, LICENSE
  - acceptance: repo contains folders and basic README describing how to run tests locally
- 1.2 TDD-first tooling decision
  - tasks: decide the first test harness/tooling (e.g., Python + pytest, TypeScript + Jest, or simple shell-based tests). Keep tooling minimal and replaceable.
  - acceptance: documented choice and one working test that fails (red) to start the TDD cycle

2. Spec finalization (High)
- 2.1 Resolve underspecified decisions (short list)
  - borrow/move rules & Copy types
  - trait-object/vtable layout
  - memory model / runtime allocation strategy
  - panic/unwind policy
  - TOML manifest schema
  - acceptance: `langspec/preferences.md` updated with decisions or marked "deferred with rationale"

3. Surface grammar & parser (High)
- 3.1 Grammar sketch (BNF)
  - tasks: produce a concise grammar covering modules, imports, require, declarations, expressions, patterns, and top-level items
  - acceptance: grammar file `langspec/grammar.md` with examples
- 3.2 Lexer implementation
  - tasks: token definitions, comment/string handling, numeric suffixes
  - acceptance: lexer unit tests for tokens
- 3.3 Parser implementation
  - tasks: parser that emits AST for top-level items and expressions (recursive-descent or parser-generator)
  - acceptance: parser tests that parse example `.mgs` files and produce expected AST

4. AST, name resolution & module loader (High)
- 4.1 AST design
  - tasks: canonical AST nodes for modules, items, expressions, patterns, types
  - acceptance: documentation + reference printer
- 4.2 Name resolution & scope rules
  - tasks: implement symbol tables, imports resolution from source root, enforce `export` rules, disallow cycles
  - acceptance: tests for imports/exports, import location semantics (imports run where they appear)
- 4.3 Module loader / instantiation
  - tasks: eager instantiation at import location, instance identity per-args, runtime parameter passing
  - acceptance: runtime demonstrates two instances with separate state

5. Type system & semantics (High)
- 5.1 Type representation
  - tasks: implement primitive types, fixed-size ints, type descriptors for runtime type params
  - acceptance: types serialized in AST and visible to type-checker
- 5.2 Type checker (static)
  - tasks: local inference + required explicit generic annotations, monomorphization plan documented (but not used for runtime params)
  - acceptance: type-checker passes on core examples
- 5.3 Ownership & borrow checker
  - tasks: implement move semantics, `let mut`, borrow rules with elision heuristics; identify Copy types
  - acceptance: borrow-checker unit tests similar to Rust litmus cases
- 5.4 Trait/typeclass support
  - tasks: nominal traits, impl resolution, trait bounds/checking
  - acceptance: trait impl tests and trait-object typing
- 5.5 Refinement verifier (Presburger)
  - tasks: implement a small Presburger decision procedure or use a library to verify linear integer constraints
  - acceptance: examples with simple refinement proofs compile or error as appropriate

6. Runtime & codegen (High)
- 6.1 Runtime primitives
  - tasks: define runtime layout for type descriptors, module instances, boxed values, and heap/stack discipline
  - acceptance: minimal runtime library for C/JS backend
- 6.2 C backend (emit C)
  - tasks: lower AST to simple C constructs, map ownership to explicit allocations, implement runtime helpers
  - acceptance: emit compilable C for small examples and produce working binaries
- 6.3 JS backend (emit JS)
  - tasks: lower to JS (ES modules), implement runtime descriptors in JS
  - acceptance: node.js runs example programs

7. Standard library & examples (Med)
- 7.1 Minimal stdlib
  - tasks: basic types and collections (Vec/list), basic I/O, Result/Option helpers
  - acceptance: examples using stdlib compile and run
- 7.2 Example programs
  - tasks: several small examples (hello, counter module, simple server skeleton)
  - acceptance: example runs on C or JS backend

8. Packaging & build system (Med)
- 8.1 TOML manifest schema
  - tasks: define required fields, dependency resolution algorithm, and layout conventions
  - acceptance: sample `Magma.toml` and resolver tests
- 8.2 Build tool (magma build)
  - tasks: implement a simple build tool to compile modules, manage caching, and run examples
  - acceptance: `magma build` compiles an example project

9. Tooling, tests & CI (Med)
- 9.1 Unit & integration tests
  - tasks: set up test harness, unit tests for lexer/parser/typechecker/runtime
  - acceptance: test suite runs locally
- 9.2 Linting & formatting
  - tasks: implement a minimal formatter and linter rules (e.g., discourage top-level `let mut` if desired)
  - acceptance: `magma fmt` or linter run
- 9.3 CI configuration
  - tasks: add GitHub Actions for builds/tests
  - acceptance: CI runs and reports green on merge to master

10. Documentation & onboarding (Low)
- 10.1 Language reference and tutorial
  - tasks: write README, small tutorial, and design rationale
  - acceptance: new users can follow tutorial to run examples
- 10.2 Contributor guide
  - tasks: contribution rules, how to implement features
  - acceptance: documented CONTRIBUTING.md

Milestones & timeline (suggested, TDD-oriented)
- Sprint length: 1 week crystalline TDD cycles (small tests -> implementation -> refactor)
- M0 (Sprint 0): Project skeleton + start TDD harness (one failing test)
- M1 (Sprints 1-2): Lexer (tests for tokenization) + Parser stub (parse small examples) + AST printer
- M2 (Sprints 3-4): Name resolution + module loader semantics (tests for imports, eager instantiation, no cycles)
- M3 (Sprints 5-7): Basic type-checker (primitive types, signatures) driven by test-cases
- M4 (Sprints 8-11): Ownership/borrow checker and traits introduced progressively via tests
- M5 (Sprints 12-16): Simple backend(s) or interpreter covering language semantics required by tests; runtime specifics emerge as needed

Risks & mitigations
- Presburger verifier complexity: keep refinement fragment small; fallback to runtime checks when undecidable
- Borrow checker complexity: start with a conservative checker, iterate towards ergonomics
- Runtime/ABI mismatch between C/JS: isolate runtime-specific code behind a small portable runtime interface

Next immediate steps (TDD-first)
- A: Write the first failing behavior test (for example: a `.mgs` file with `require`/`import` semantics that should execute) — I can create the test scaffold now.
- B: Produce a concise BNF/grammar sketch to clarify parsing expectations (useful after a red test exists).
- C: Implement a tiny Python parser prototype for `require` and `import` to make the first test pass.


---

Status: WBS created. Update or triangulate this document as the spec evolves.
