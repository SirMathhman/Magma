# Magma Compiler – Complete Implementation Plan

## Overview

Build a production-quality compiler for **Magma** entirely in **Rust** with three independent backends: **TypeScript**, **JavaScript**, and **LLVM IR**. All output is human-readable. Complete all three backends fully in Rust (Months 0–9), then systematically rewrite the entire compiler in Magma itself (Months 9–18) with all three backends functioning. No performance optimization required; correctness and readability prioritized.

---

## Magma Language Specification (Frozen)

- **Functions:** `fn name(param : Type) : ReturnType => expr;`
- **Structs:** `struct Point {x : I32, y : I32}`
- **Classes:** `class fn Point(x : I32, y : I32) => {}`
- **Ownership:** `&T` (immutable borrow), `&mut T` (mutable borrow), `let mut x` for mutation
- **Generics:** Monomorphization (compile-time specialization per concrete type instantiation)
- **Error Handling:** `Result<T, E>` types
- **Immutability:** Default immutable; explicit `let mut` for mutability
- **Modules:** Java-like package system, file-to-module mapping
- **Type Annotations:** Required for function signatures and parameters; inferred for local variables
- **Backends:** All three (TS, JS, LLVM) must compile identical Magma source identically

---

## Compiler Architecture (Rust)

```
Magma Source (.mg files)
    ↓
[Lexer] → Token Stream
    ↓
[Parser] → AST (immutable)
    ↓
[Semantic Analyzer]
├─ Symbol Table (module/package scoping)
├─ Type Checker (bidirectional inference)
├─ Borrow Checker (ownership verification)
└─ Monomorphization Collector (gather generic specializations)
    ↓
[HIR] (ownership-verified, type-annotated, desugared)
    ↓
Codegen Dispatch
├─→ [TypeScript Backend] → .ts files + .d.ts declarations
├─→ [JavaScript Backend] → .js files
└─→ [LLVM IR Backend] → .ll files (human-readable LLVM IR)
    ↓
[Post-Processing]
├─ TypeScript: ready for `tsc`
├─ JavaScript: ready for Node.js/bundlers
└─ LLVM: ready for `llvm-as` + linker → native executable
```

---

## Implementation Steps

### Phase 0: Project Initialization (Week 0)

1. **Project setup and specification lock**
   - Create Cargo workspace with modules: `lexer/`, `parser/`, `ast/`, `semantic/`, `codegen_ts/`, `codegen_js/`, `codegen_llvm/`, `driver/`, `diagnostics/`
   - Add dependencies: `nom` (parser combinators), `inkwell` (LLVM IR), `serde` (serialization), `quote` + `proc-macro2` (code generation), `miette` (diagnostics), `clap` (CLI)
   - Freeze Magma grammar in BNF/EBNF form
   - Document all language semantics in `LANGUAGE.md`

#### Recommended Crates for Accelerated Development

**Core Dependencies (Priority 1 - Immediate Impact):**

- **`quote` + `proc-macro2` (Code Generation):** Accelerates backend code generation by 40-50%. The `quote!` macro dramatically simplifies emitting readable TypeScript, JavaScript, and LLVM IR. Eliminates manual string concatenation; type-safe code emission. Widely trusted in Rust ecosystem.
- **`miette` (Diagnostics):** Professional-grade error reporting with rich terminal output, source spans, nested diagnostics, and code highlighting. Reduces manual error handling code by ~70%. Integrates seamlessly with `nom` errors.
- **`nom` (Parser Combinators):** Already planned; zero-copy, composable parsing with excellent error recovery. Chosen over `lalrpop` to preserve full control over custom parsing logic and simplify eventual self-hosting.

**Supporting Dependencies (Priority 2 - Recommended):**

- **`clap` (CLI Argument Parsing):** Professional CLI framework with auto-generated help, subcommands, and argument validation. Significantly faster than manual parsing.
- **`serde` + `serde_json` (Serialization):** Serialize HIR/AST for debugging, testing, and intermediate representation exchange between compiler stages.
- **`tempfile` (Testing):** For generating temporary output files during backend tests without manual cleanup.

**LLVM & Runtime (Already Planned):**

- **`inkwell` (LLVM IR):** Safe Rust wrapper; use over raw `llvm-sys` for reliability and ease of use.

**Why Not `lalrpop`?**
While `lalrpop` (LR(1) parser generator) could reduce grammar boilerplate, `nom` is preferred:

1. Magma's custom error recovery and lookahead needs are better served by combinators
2. Handwritten parsers simplify self-hosting (fewer bootstrapping dependencies)
3. `nom` has proven track record in major Rust compiler projects (rust-analyzer ecosystem)
4. Better alignment with eventual Magma rewrite (combinators are easier to reimplement in Magma)

**Implementation Sequencing:**

1. **Week 0:** Add all crates to `Cargo.toml`; set up `miette` error infrastructure
2. **Weeks 1-2:** Lexer/parser with `nom` + `miette` error handling
3. **Weeks 5-6:** Integrate `quote` + `proc-macro2` into TypeScript backend (major speedup)
4. **Week 7:** JavaScript backend benefits from same `quote` infrastructure
5. **Weeks 8-9:** LLVM backend uses `inkwell` for IR generation
6. **Week 10:** Add `clap`-based CLI driver with subcommands
7. **Weeks 11-13:** Add `serde` serialization as needed for testing/debugging

**Expected Time Savings:** 2-4 weeks across the entire project (primarily in backend code generation and error handling).

### Phase 1: Frontend Implementation (Weeks 1–4, Month 1)

2. **Implement Rust lexer (Week 1)** ✅ COMPLETE

   - Hand-written tokenizer producing `Token` stream with source spans
   - Handle all token types: keywords, operators, identifiers, integer/string literals, punctuation
   - Generate rich error diagnostics with source context
   - Create standalone CLI tool: `magma-lex <file.mg>`
   - Comprehensive unit tests covering all token types
   - **Status**: All 16 tests passing. Lexer handles all token types including keywords, operators, literals, comments.
   - **Deliverables**:
     - `src/token.rs` - Token and Span definitions
     - `src/lexer.rs` - Lexer implementation with 16 unit tests
     - `src/bin/lex.rs` - CLI tool
     - `test_sample.mg` - Sample Magma file

3. **Implement Rust parser (Week 2)** ✅ COMPLETE

   - Hand-written recursive descent parser using precedence climbing
   - Emit immutable AST capturing all syntax with source locations
   - Handle operator precedence and associativity correctly
   - Implement error recovery for better diagnostics
   - Create parser CLI tool: `magma-parse <file.mg>`
   - Exhaustive tests covering entire grammar
   - **Status**: All 42 tests passing. Parser handles all expressions, operators, function/struct definitions
   - **Features**:
     - Proper operator precedence (multiplication before addition, etc.)
     - Unary operators (negation, logical not, references, dereference)
     - Binary operators (arithmetic, comparison, logical, bitwise, shifts)
     - Postfix operations (function calls, field access, array indexing)
     - If expressions
     - Function and struct definitions with type annotations
   - **Deliverables**:
     - `src/parser.rs` - Parser implementation with 42 unit tests
     - `src/bin/parse.rs` - CLI tool
     - Enhanced `src/ast.rs` with Display trait and extended expressions

4. **Implement Rust semantic analyzer (Weeks 3–4)** ⏳ NEXT
   - Build symbol table with package/module resolution
   - Implement bidirectional type checker:
     - Required annotations for function signatures/parameters
     - Type inference for local variables
     - Unification algorithm for constraint solving
   - Implement borrow checker verifying ownership rules:
     - Borrowing and lifetime tracking
     - Move semantics enforcement
     - Mutation restrictions
   - Collect monomorphization sites (all concrete type instantiations)
   - Lower to ownership-verified HIR
   - Extensive test suite for type checking and borrowing

### Phase 2: Backend Implementation (Weeks 5–9, Months 2–3)

5. **Implement TypeScript backend (Weeks 5–6)**

   - Lower HIR to readable TypeScript code via string generation or AST builder
   - Emit idiomatic TypeScript:
     - Proper indentation and formatting
     - Preserved variable/function names
     - Semantic structure clarity
   - Generate `.d.ts` type declaration files for type safety
   - Include source maps for debugging
   - Output ready for `tsc` compilation
   - Comprehensive codegen tests

6. **Implement JavaScript backend (Week 7)**

   - Lower HIR to readable JavaScript code (nearly identical to TypeScript output but with JS runtime semantics)
   - Emit `.js` files ready for Node.js and bundlers
   - Handle ownership semantics as runtime values (GC-compatible)
   - Include source maps for debugging
   - Comprehensive tests

7. **Implement LLVM IR backend (Weeks 8–9)**
   - Lower HIR to unoptimized, readable LLVM IR via `inkwell`
   - Map ownership info to LLVM constructs:
     - Owned values → stack allocation via `alloca` or heap allocation via `malloc`
     - Borrows → LLVM pointer types with `noalias` attributes
     - Mutability → memory access rules
   - Emit `.ll` text files (human-readable, not binary)
   - Generate proper function signatures, control flow, and metadata
   - Include source location metadata
   - Comprehensive tests

### Phase 3: Integration & Stabilization (Weeks 10–13, Months 3–4)

8. **Unify all backends and establish full pipeline (Week 10)**

   - Create CLI driver supporting flags: `--target ts`, `--target js`, `--target llvm`
   - Compile same Magma program to all three targets
   - Verify semantic equivalence (same program behaves identically across all backends)
   - Create integration tests for cross-target verification
   - Establish baseline compilation times (performance not critical)

9. **Expand language feature set (Weeks 11–13, Months 2–3)**

   - Implement all core features:
     - Control flow: `if/else`, loops, pattern matching
     - Collections: arrays, basic container types
     - Method calls and field access
     - Ownership/borrowing expressions
     - Generic monomorphization in all backends
     - `Result` type propagation and error handling
     - Struct and class instantiation
   - Ensure all features work identically across all three backends
   - Comprehensive test matrix: (feature × backend)

10. **Stabilize compiler and documentation (Month 4)**
    - Fix bugs across all backends
    - Improve error messages and diagnostics
    - Document codegen strategy for each backend
    - Create example programs demonstrating language features
    - Establish compiler as stable baseline
    - Create contribution guidelines

---

## Self-Hosting Phases (Months 5–9: Rewrite Compiler in Magma)

### Strategy

Rewrite the entire Rust compiler in Magma, targeting all three backends simultaneously. Each component is ported, verified for equivalence, and integrated incrementally.

### Phase 4: Self-hosting Lexer/Parser (Months 5–6)

11. **Self-host lexer in Magma**

    - Write Magma lexer in Magma targeting all three backends simultaneously
    - Compile using Rust compiler
    - Verify generated lexer (in TS, JS, and LLVM IR) produces identical token streams to original
    - Integrate into build pipeline

12. **Self-host parser in Magma**
    - Write Magma parser targeting all three backends
    - Compile using Rust compiler
    - Verify AST equivalence across all outputs
    - Integrate with self-hosted lexer

### Phase 5: Self-hosting Semantic Analyzer (Months 6–7)

13. **Self-host type checker and borrow checker in Magma**
    - Write type checker in Magma targeting all three backends
    - Write borrow checker in Magma
    - Compile with Rust compiler
    - Create test suite verifying output equivalence:
      - Rust analyzer output ≡ self-hosted Magma analyzer compiled to TS/JS/LLVM
    - Achieve full semantic analysis self-hosted

### Phase 6: Self-hosting Codegen (Month 8–9)

14. **Self-host TypeScript backend in Magma**

    - Write TypeScript codegen in Magma
    - Compile to TS/JS/LLVM via Rust compiler
    - Verify output equivalence

15. **Self-host JavaScript backend in Magma**

    - Write JavaScript codegen in Magma
    - Compile to TS/JS/LLVM
    - Verify output equivalence

16. **Self-host LLVM backend in Magma**

    - Write LLVM codegen in Magma
    - Compile to TS/JS/LLVM
    - Verify output equivalence
    - Achieve complete self-hosting

17. **Full self-hosting verification (Month 9)**
    - Compile Magma compiler source with self-hosted compiler compiled to TypeScript/JavaScript/LLVM IR
    - Verify self-compiled compiler produces identical output to Rust-built version for all three targets
    - Test self-compiled compiler compiling itself again (three-level bootstrap)
    - Document bootstrap process

---

## Production Hardening Phase (Months 10–18)

18. **Comprehensive testing and stabilization**

    - Stress tests with large programs
    - Compile large real-world projects
    - Fix edge cases
    - Improve error diagnostics with rich context

19. **Optional performance optimization**

    - If needed, add unoptimized-to-optimized pipeline (but not required by spec)
    - Profile generated code
    - Optimize hotspots
    - Measure compile times

20. **IDE and tooling development**

    - Implement LSP server for IDE support (written in Magma)
    - Create code formatter for Magma
    - Build REPL for interactive exploration
    - Create web-based playground
    - Integrate with VS Code, IntelliJ

21. **Documentation and examples**

    - Write comprehensive language documentation
    - Create tutorials and guides
    - Build example projects
    - Establish best practices guide
    - Create standard library overview

22. **Production release**
    - Declare Magma compiler production-ready (v1.0)
    - Tag release, publish artifacts
    - Establish support/maintenance process

---

## Key Design Principles

- **Single codebase:** One Rust compiler managing all three backends; no forking or duplication
- **Semantic equivalence guaranteed:** Same Magma program produces identical behavior across TS/JS/LLVM (within language semantics)
- **Human-readable output mandatory:** All generated code is indented, named, and structured for human understanding
- **Unoptimized IR acceptable:** No performance optimization required; focus on correctness and clarity
- **Clean self-hosting path:** Systematically port each component to Magma; verify equivalence at each step
- **No performance requirements:** Compiler speed is secondary to correctness and maintainability

---

## Testing Matrix

| Component          | Unit Tests | Integration Tests        | Backend Coverage |
| ------------------ | ---------- | ------------------------ | ---------------- |
| Lexer              | ✓          | ✓ (per backend)          | TS, JS, LLVM     |
| Parser             | ✓          | ✓ (per backend)          | TS, JS, LLVM     |
| Type Checker       | ✓          | ✓ (semantic equivalence) | TS, JS, LLVM     |
| Borrow Checker     | ✓          | ✓ (ownership validation) | TS, JS, LLVM     |
| TypeScript Backend | ✓          | ✓                        | TypeScript       |
| JavaScript Backend | ✓          | ✓                        | JavaScript       |
| LLVM Backend       | ✓          | ✓                        | LLVM IR          |
| End-to-End         | -          | ✓ (cross-target)         | TS, JS, LLVM     |

---

## Risk Mitigation

| Risk                                                  | Mitigation                                                                           |
| ----------------------------------------------------- | ------------------------------------------------------------------------------------ |
| Self-hosting three backends simultaneously is complex | Implement all backends in Rust first; port one at a time to Magma                    |
| LLVM integration unfamiliar                           | Use `inkwell` (safe Rust wrapper); study existing compiler backends                  |
| Semantic divergence between targets                   | Extensive cross-target equivalence tests; same HIR for all backends                  |
| 15–18 month timeline aggressive                       | Leverage crate ecosystem: `quote`, `miette`, `nom`, `clap` save 2-4 weeks            |
| Borrow checker complexity                             | Start simple (affine types); expand gradually; extensive testing                     |
| Backend code generation error-prone                   | Use `quote!` macro for type-safe code emission; eliminates string concatenation bugs |
| Manual error reporting tedious                        | Use `miette` for rich diagnostics; reduces manual formatting by ~70%                 |

---

## Success Criteria

1. ✓ End-to-end Magma compiler in Rust compiling Magma → TS, JS, LLVM (Month 4)
2. ✓ All three backends produce human-readable output (Month 4)
3. ✓ Self-hosted compiler (Magma source) compiled by Rust compiler → TS, JS, LLVM (Month 9)
4. ✓ Self-hosted compiler compiles itself (three-level bootstrap working) (Month 9)
5. ✓ Production-ready compiler with all features, tooling, documentation (Month 18)

---

## Timeline Summary

| Phase                  | Duration                   | Milestone                                          |
| ---------------------- | -------------------------- | -------------------------------------------------- |
| 0: Setup & Spec        | Week 0                     | Project initialized, language specification locked |
| 1: Frontend            | Weeks 1–4                  | Lexer, parser, semantic analyzer complete in Rust  |
| 2: Backends            | Weeks 5–9                  | TS, JS, LLVM backends complete and verified        |
| 3: Integration         | Weeks 10–13                | Full pipeline working; all features implemented    |
| 4: Self-host Frontend  | Weeks 14–17 (Months 5–6)   | Lexer/parser self-hosted, verified                 |
| 5: Self-host Analyzer  | Weeks 18–21 (Months 6–7)   | Type/borrow checker self-hosted, verified          |
| 6: Self-host Codegen   | Weeks 22–26 (Months 8–9)   | All codegens self-hosted; three-level bootstrap    |
| 7: Hardening & Tooling | Weeks 27–39 (Months 10–18) | Optimization, IDE, docs, production release        |

---

## Next Steps

1. Initialize Cargo project with proper module structure
2. Begin Week 1: Implement Rust lexer
3. Set up CI/CD pipeline (GitHub Actions)
4. Create comprehensive test harness
5. Document each component as it's built
6. Weekly progress reviews against timeline
