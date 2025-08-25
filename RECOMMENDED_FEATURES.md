# Recommended Features for Magma Interpreter

## Execution Semantics
- Execute while/for loop bodies (currently only parsed).
- Arithmetic and comparisons: +, -, *, /, %, unary -, <, <=, >, >=, ==, !=, with precedence and parentheses.
- Boolean operations: &&, ||, !.

## Functions
- Evaluate body at call time; bind parameters; allow recursion.
- Block bodies with local scope; return statements; closures capturing outer variables.
- Type checking for parameters and return values.

## Types
- Enforce types for let/assign/if/match arms/function returns.
- Support basic types beyond I32/bool (e.g., I64, U32, strings, floats).
- Type inference and/or generics (future).

## Variables and Scope
- Consistent let semantics (typed/untyped, mutability, shadowing).
- Decide whether inner-block assignment can mutate outer variables (current model isolates).

## Expressions and Grammar
- General statement lists at top-level (not only after let-typed branch).
- Identifiers as values (not just top-level or final expression).
- Allow keyword-without-space forms (e.g., if(...) vs requiring a space).
- Comments: // and /* */.

## Match Expressions
- Patterns beyond ints: booleans, identifiers, ranges, struct destructuring.
- Exhaustiveness checks; duplicate/unreachable arm detection.

## Structs
- Struct literals and field access/mutation: S { a: 1 }, s.a, s.a = 2.
- Method calls and associated functions; type checking of fields.

## Errors and Tooling
- Line/column diagnostics with spans; error codes.
- Lexer/AST parser refactor to simplify and avoid duplication.
- Persistent REPL state vs per-run reset toggle.

---
If you want to prioritize, arithmetic/comparisons and loop execution are foundational for many later features.
