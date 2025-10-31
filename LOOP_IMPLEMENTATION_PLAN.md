# Loop Implementation Plan - TDD Approach

## Current Status: 🔴 RED PHASE COMPLETE

Created 11 comprehensive loop support tests in `tests/loop_support.rs`. All tests fail (expected):

### Test Breakdown

- **While loop basics**: 3 tests (simple, with break, with continue)
- **For loop basics**: 3 tests (range, with break, with continue)
- **Advanced loops**: 5 tests (nested, variable scope, function calls, control flow, while true)

### Test Results

```
0 passed; 11 failed
```

All failures are due to parser not recognizing loop syntax:

- Parser sees `while` keyword but doesn't know how to parse while expressions
- Parser sees `for` keyword but doesn't know how to parse for expressions
- Parser sees `break`/`continue` as unexpected tokens

## Implementation Roadmap (GREEN Phase)

To make tests pass, we need to implement in this order:

### 1. Add AST Expression Types (`src/ast.rs`)

```rust
// Add to Expression enum:
WhileLoop {
    condition: Box<Expression>,
    body: Box<Expression>,
    span: Span,
}

ForLoop {
    variable: String,
    range_start: Box<Expression>,
    range_end: Box<Expression>,
    body: Box<Expression>,
    span: Span,
}

Break {
    span: Span,
}

Continue {
    span: Span,
}
```

### 2. Add Parser Support (`src/parser.rs`)

- Add `parse_while_loop()` method
- Add `parse_for_loop()` method
- Add `parse_break()` method
- Add `parse_continue()` method
- Update `parse_primary()` to recognize these keywords

### 3. Add Semantic Analysis (`src/semantic.rs`)

- Type check loop conditions (must be Bool)
- Handle loop variable scoping (for loop)
- Validate break/continue only appear inside loops
- Ensure loop body returns compatible type

### 4. Add HIR Lowering (`src/hir_lowering.rs`)

- Lower WhileLoop to HIR equivalent
- Lower ForLoop to HIR equivalent
- Handle break/continue in HIR

### 5. Add Backend Support (All Three)

- **TypeScript** (`src/codegen_typescript.rs`): Generate `while` and `for` loops
- **JavaScript** (`src/codegen_javascript.rs`): Generate `while` and `for` loops
- **LLVM** (`src/codegen_llvm.rs`): Generate branch-based loop structure

## Effort Estimate

- Parser: ~150 lines (30 min)
- Semantic: ~100 lines (20 min)
- HIR: ~50 lines (15 min)
- TypeScript Backend: ~80 lines (20 min)
- JavaScript Backend: ~80 lines (20 min)
- LLVM Backend: ~100 lines (30 min)

**Total: ~560 lines, ~2.5 hours work**

## Next Steps

1. ✅ RED phase complete (tests written and failing)
2. ⏳ GREEN phase: Implement loop support in all components
3. ⏳ REFACTOR phase: Optimize, simplify code
4. ⏳ Create 20+ cross-backend equivalence tests for loops

---

**Ready to proceed with GREEN phase?** Let me know if you'd like me to start implementing or want to adjust the approach!
