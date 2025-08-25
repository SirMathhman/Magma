# Recommended Features for Magma Interpreter

## Implemented so far
- Values and arithmetic
	- Integers; arithmetic +, -, * with correct precedence and left-associativity; unary minus.
	- Booleans; logical operators &&, ||, ! with precedence (and > or) and unary not.
	- Parentheses supported in control-flow conditions; arithmetic grouping parens not yet formalized.
	- Blocks as values and standalone statements; nested blocks; final expression returns value. Empty-value blocks are invalid.
- Variables and scope
	- let and let mut; reassignment only when mutable.
	- Typed let declarations (let x : I32; ...) with assignments in branches or directly.
	- Block scoping: inner lets don’t leak; assignment inside standalone blocks can update outer variables.
	- Top-level supports general statement lists followed by a final expression.
- Control flow and grammar
	- if (cond) then else as an expression (condition must be boolean).
	- while (...) { ... } and for (...) { ... } are parsed as statements (not expressions); missing parens/braces are rejected.
	- Match expression over integers with wildcard `_`; braces and semicolons enforced.
- Functions and closures
	- Named functions: parse fn name, parameters with names and types, optional return type, and expression body.
	- Arity checking at call sites; undefined functions rejected; duplicate function/parameter names rejected.
	- First-class functions: inline `fn` expressions and arrow closures `(a : T, ...) => expr` can be assigned to vars/fields and called.
	- this-object semantics: inside a function/closure body, `this` exposes parameters and locals as fields; returning `this` yields an object with those fields and any nested functions as methods. Field access via `obj.field` and method calls `obj.method(...)` are supported. Calling non-callable fields is invalid.
- Classes and methods
	- class fn declarations: `class fn Name(params) => { ... }` behave like constructors that return `this`; parameters become fields.
	- impl blocks attach methods to structs/classes: `impl Name { fn m(...) : T => expr; ... }` with grammar validations (unknown targets invalid; empty impl invalid; optional trailing semicolon allowed after the block).
- Structs
	- Struct declarations with named fields; duplicate field names rejected; duplicate struct names rejected per run.
	- Struct value construction syntax exists (e.g., `S {}` for empty); methods can be attached via `impl` and then invoked (`S {}.m()`).
- Arrays
	- Fixed-size array types `[T; N]`, array literals `[x, y, ...]`, and indexing `arr[i]` are supported.
- Errors
	- Contextual error messages for common parse/eval errors; many grammar errors detected with specific messages.

## Recommended next features

### Execution semantics
- Execute while/for loop bodies (currently only parsed as statements).
- Complete arithmetic and comparisons: add division `/`, modulus `%`, comparisons `<, <=, >, >=, ==, !=`, and parentheses for arithmetic grouping.

### Functions and closures
- Recursion and mutual recursion validation; recursion tests and tail positions.
- Block/function bodies with explicit return statements (in addition to expression bodies).
- Type checking for parameters and return values; function overloading rules (if any).

### Types
- Enforce types for let/assign/if/match arms/function returns.
- Extend primitives beyond I32/bool (e.g., I64, U32, strings, floats).
- Arrays: bounds checks, length mismatches on literals vs types, element type enforcement, and slice operations (future).
- Type inference and/or generics (future).

### Variables and scope
- Clarify shadowing and redeclaration rules; ensure consistent let semantics across typed/untyped forms.

### Expressions and grammar
- Arithmetic grouping parentheses and precedence table documentation/refinement.
- Comments: // and /* */.
- Keyword-without-space forms (e.g., `if(...)`) acceptance and lexer rules.

### Match expressions
- Patterns beyond ints: booleans, identifiers, ranges, struct destructuring.
- Exhaustiveness checks and duplicate/unreachable arm detection (beyond wildcard fallback).

### Structs and objects
- Full struct literals with field initializers: `S { a: 1 }`, `S { a, b }`.
- Field access/mutation for struct instances: `s.a`, `s.a = 2` with type checking.
- Interop between `this`-objects and declared structs (unify or clarify semantics).

### Errors and tooling
- Line/column diagnostics with spans; error codes.
- Lexer/AST parser refactor to simplify and avoid duplication.
- Persistent REPL state vs per-run reset toggle (engine currently resets per run).

---
Priority suggestion: finish arithmetic/comparisons and implement loop execution semantics; then add type checking and struct field access to solidify the core language.
