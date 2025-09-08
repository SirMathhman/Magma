# Research: Magma → C compiler (Phase 0)

## Unknowns extracted
- Magma language reference / formal semantics [NEEDS CLARIFICATION]
- Performance targets and scale [NEEDS CLARIFICATION]
- Policy for unmappable constructs and foreign/library bindings [NEEDS CLARIFICATION]

## Topics researched & decisions

### Parser choice
- Options considered: ANTLR, hand-written recursive-descent, or parser combinators
- Decision: ANTLR. Rationale: mature tooling, good Java support, generates robust parsers from grammar, reduces early parsing bugs.

### Code generation approach
- Options: template-based (StringTemplate), AST-walker with visitors, or intermediate IR then backend
- Decision: AST-walker + template snippets for boilerplate. Rationale: simpler for first pass, easier to inspect generated C, and suitable for incremental features.

### Mapping Magma → C
- Need to define mapping for Magma primitive types, memory model, and control structures.
- Decision: support a safe subset initially (numbers, booleans, strings via char* with simple allocation, functions, control flow). Documented in data-model.md.

### Debugging and source mapping
- Emit comments and optional mapping files that map Magma source lines to generated C lines when `--emit-mappings` is provided.

### Build & CI strategy for generated C
- Use system gcc/clang to compile generated C in integration tests. CI should run these compile-and-run checks for sample programs.

## Alternatives considered
- Full IR + LLVM backend: more powerful but larger scope; deferred to future phases if needed.

## Next actions
1. Create data-model.md describing AST shapes and codegen units.
2. Produce CLI contract and codegen API contract.
3. Add quickstart example and failing contract tests.

---
*End of research.md*
