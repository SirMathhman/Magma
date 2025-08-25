# Recommended Features for Magma Interpreter

## Implemented so far
- Values and arithmetic
	- Integers; arithmetic +, -, * with correct precedence and left-associativity; unary minus.
	- Booleans; logical operators &&, ||, ! with precedence (and > or) and unary not.
	- Blocks as values and standalone statements; nested blocks; final expression returns value.
- Variables and scope
	- let and let mut; reassignment only when mutable.
	- Typed let declarations (let x : I32; ...) with assignments in branches or directly.
	- Block scoping: inner lets don’t leak; assignment inside standalone blocks can update outer variables.
	- Bare identifier as a top-level program resolves to current env value.
- Control flow and grammar
	- if (cond) then else as an expression (condition must be boolean).
	- while (...) { ... } and for (...) { ... } parsed as statements (not expressions).
	- Match expression over integers with wildcard `_`, braces and semicolons enforced; non-exhaustive without wildcard is an error.
- Functions (declarations and calls)
	- Parse fn name, parameter list with names and types, return type, and body value; duplicate function names rejected.
	- Duplicate parameter names rejected; arity checked at call sites; undefined functions rejected.
	- Current behavior: parameters are parsed and arity-checked, but not bound; body value is returned as-is.
- Struct declarations
	- Parse struct name and field list; duplicate field names rejected; duplicate struct names rejected per run.
- Errors
	- Contextual error messages for common parse/eval errors (no longer just “Undefined value”).

## Recommended next features

### Execution Semantics
- Execute while/for loop bodies (currently only parsed as statements).
- Complete arithmetic and comparisons: add division `/`, modulus `%`, comparisons `<, <=, >, >=, ==, !=`, and parentheses for grouping.

### Functions
- Evaluate body at call time with parameter binding; allow recursion.
- Block/function bodies with local scope and return statements; closures capturing outer variables.
- Type checking for parameters and return values.

### Types
- Enforce types for let/assign/if/match arms/function returns.
- Support additional basic types beyond I32/bool (e.g., I64, U32, strings, floats).
- Type inference and/or generics (future).

### Variables and Scope
- Clarify shadowing and redeclaration rules; ensure consistent let semantics across typed/untyped forms.

### Expressions and Grammar
- General statement lists at top-level (not only within let-flows before a final expression).
- Allow identifiers as first-class values inside expressions (not only as top-level program or final identifier).
- Allow keyword-without-space forms (e.g., if(...)) instead of requiring a space.
- Comments: // and /* */.

### Match Expressions
- Patterns beyond ints: booleans, identifiers, ranges, struct destructuring.
- Exhaustiveness checks and duplicate/unreachable arm detection.

### Structs
- Struct literals and field access/mutation: `S { a: 1 }`, `s.a`, `s.a = 2`.
- Method calls and associated functions; type checking of fields.

### Errors and Tooling
- Line/column diagnostics with spans; error codes.
- Lexer/AST parser refactor to simplify and avoid duplication.
- Persistent REPL state vs per-run reset toggle (current engine resets per run internally).

---
Priority suggestion: finish arithmetic/comparisons and implement loop execution semantics.
