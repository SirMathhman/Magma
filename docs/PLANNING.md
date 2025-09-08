# Magma -> C Cross-Compiler — Planning Document

This document outlines the plan to build a cross-compiler written in Java (Maven-based) that compiles the Magma language to C. It covers goals, architecture, milestones, technical contracts, risks, testing, CI, and a suggested timeline for an MVP and subsequent releases.

## Goals

- Build a compiler implemented in Java and packaged via Maven
- Source language: Magma (a small, statically-typed, imperative/functional hybrid — spec to be defined)
- Target language: Portable ISO C (C11 as baseline)
- Implementation guidance: see `docs/IMPLEMENTATION.md` for a reference C backend guide and runtime suggestions. The language spec (`docs/LANGUAGE_SPEC.md`) remains implementation-agnostic.
- Deliverables: compiler CLI, library artifacts, runtime C support, tests, and documentation

## High-level architecture

Components:

- Frontend
  - Lexer
  - Parser (produces AST)
  - Name resolution & semantic checks (type checking, scope analysis)

- Middle-end
  - AST transformations (desugaring)
  - Optional IR(s) for optimization and lowering

- Backend
  - Code generator mapping AST/IR to C source files
  - Runtime library (C) for any needed primitives

- Tooling
  - CLI (javac-based jar), Maven modules, unit and integration tests
  - CI workflows to build, test, and cross-compile generated C

## Technical contracts

- Input: .magma source files and optional project descriptors
- Output: C source files (.c/.h) and optional compiled artifacts (via invoking a system C compiler in tests)
- Error modes: compilation should fail with readable errors (file/line/column), and include non-zero exit codes for CLI
- Performance: initial focus on correctness and readable generated C, not on heavy optimization

## Language design assumptions (to be finalized)

- Statically typed with type inference for local variables
- First-class functions and closures (initially restricted; may be lowered to structs + function pointers)
- Heap-allocated composite types (records/arrays)
- Simple module system (one file = one module)

Note: These are assumptions for planning. The definitive language spec will be created as a follow-up document (`docs/LANGUAGE_SPEC.md`).

## Detailed design decisions (options & recommendation)

- Parsing
  - Option A: Use ANTLR4 with a grammar and generate lexer/parser in Java — recommended for faster development and less parser-bug churn.
  - Option B: Hand-written recursive-descent parser — more control, simpler error messages for small grammars.

- IR strategy
  - Option A: Single AST lowered directly to C (simplest; fewer moving parts).
  - Option B: Create a small SSA-like IR for optimizations and easier codegen — more investment; postpone until after MVP.

- Memory management
  - Option A: Rely on C malloc/free and expose manual memory management in Magma runtime primitives.
  - Option B: Implement a simple reference-counting runtime in C for automatic memory management (easier to implement than a GC, but has cyclic-collection issues).
  - Recommendation: Start with manual/malloc-based and provide a standard library with allocation helpers; upgrade later to refcounting if needed.

## Maven project layout

- magma/ (root)
  - pom.xml (parent)
  - magma-compiler/ (module)
    - src/main/java/... compiler implementation
    - src/test/... unit tests
  - magma-runtime-c/ (module)
    - src/main/resources runtime C sources and headers packaged into artifact
  - magma-cli/ (module)
    - small launcher that invokes the compiler module

Packaging:
- Java artifacts published to Maven Central (or GitHub Packages)
- Runtime C packaged inside an artifact or published as a tarball

## CLI & UX

- Commands:
  - magma compile -o outdir src1.mg src2.mg
  - magma init (create project scaffold)

- Exit codes and diagnostics:
  - 0 success
  - non-zero on parse/semantic/codegen errors
  - compiler should print human-friendly errors with source snippets and arrows

## Testing strategy

- Unit tests for individual modules (lexer, parser, typechecker, codegen)
- Golden tests: compile Magma program -> generated C source compared against approved snapshot
- End-to-end tests: compile Magma -> C -> compile C with system C compiler (gcc/clang) -> run produced binary and assert outputs
- Property-based tests for parsing/unparsing round-trips (optional)

Automation notes:
- For CI, use Docker images with gcc and clang to build generated C and run tests across platforms.

## Continuous Integration

- Use GitHub Actions (template):
  - Build Maven modules, run Java unit tests
  - For each golden/e2e test: run compiler, compile generated C (gcc/clang), run binary
  - Publish artifacts on release

## Security considerations

- Generated C code must be sanitized for undefined behavior where possible.
- Be explicit about trusting Magma source when generating and compiling C: compilation and execution will run arbitrary native code.

## Milestones & timeline (suggested)

- Milestone 0 — Project setup (1 week)
  - Create Maven multi-module structure, CI skeleton, README, basic CLI

- Milestone 1 — Frontend & parsing MVP (2-3 weeks)
  - Implement grammar in ANTLR or hand-written parser
  - AST model and basic pretty-printer
  - Unit tests for parser

- Milestone 2 — Semantic analysis & typechecker (2-3 weeks)
  - Name resolution, scoping, type rules, error reporting
  - Tests with expected error messages

- Milestone 3 — Codegen MVP to C (2-3 weeks)
  - Map core language to C, produce compilable C for simple programs
  - Add runtime C helpers and packaging
  - End-to-end tests: compile & run generated C

- Milestone 4 — Polish & release v0.1 (2 weeks)
  - Documentation, examples, packaging, CI hardening

Total: ~9-12 weeks for a conservative MVP depending on team size and parallelization.

## Risks & mitigations

- Risk: C runtime semantics mismatch (undefined behavior)
  - Mitigation: Generate simple, clear C code, prefer well-defined constructs, and run on gcc/clang early

- Risk: Memory management complexity
  - Mitigation: Start with manual allocation model and limit features that create complex cyclic graphs; document clearly

- Risk: Performance surprises
  - Mitigation: Prioritize correctness and readable generated C; profile and optimize later

## Quality gates

- Build: Maven - clean package passes on main branch
- Lint/Format: Configure Spotless or similar for Java
- Tests: Unit + integration passing in CI
- Smoke: End-to-end generated C compiled and executed in CI

## Deliverables for MVP

- Java-based compiler packaged as Maven artifact
- CLI jar to compile Magma to C
- Runtime C sources and headers
- Documentation: README, PLANNING.md, LANGUAGE_SPEC.md (draft)
- Example programs and tests

## Next steps (immediate)

1. Create `docs/LANGUAGE_SPEC.md` draft (syntax + examples)
2. Choose parsing approach (ANTLR vs hand-rolled) and add to decision log
3. Initialize Maven multi-module skeleton

---

Appendix: Acceptance criteria checklist

- [ ] `magma-compiler` builds without errors
- [ ] CLI can parse a simple "Hello world" Magma program and emit C
- [ ] CI runs unit tests and one end-to-end example

This document will be iterated as the project progresses. See `docs/LANGUAGE_SPEC.md` for the language details (next task).
