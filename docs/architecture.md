# Architecture change: support Bool type annotations in let-declarations

Goal
----
Allow let-declarations to be annotated with the `Bool` type and accept boolean
initializers such as `let x : Bool = true; x` which should evaluate to `true`.
Additionally, unannotated let-bindings initialized with boolean literals
should be accepted, e.g. `let x = true; x` => `"true"`.

Files / Modules affected
------------------------
- `src/main/java/magma/Interpreter.java` — extend type checking for annotated
  suffixes to accept `Bool` and validate boolean initializers.
- `src/test/java/magma/InterpreterLetBindingTest.java` — add a failing test for
  the new behavior.

Inputs / Outputs / Errors (contract)
-----------------------------------
- Input: source program string containing a semicolon-separated sequence with
  a `let` declaration annotated with `: Bool` and a boolean initializer.
- Output: on success, interpreter returns `Result.Ok` with the string
  representation of the boolean value (`"true"` or `"false"`).
- Error modes: if the annotated type is `Bool` but the initializer is not a
  boolean literal, the interpreter should return `Result.Err` with a
  descriptive `InterpretError`.

Migration / Compatibility
-------------------------
This introduces `Bool` as a recognized annotation for variables. Existing
integer typed suffixes (e.g., `U8`, `I32`) are unchanged and continue to be
validated as before.

Tests to add
------------
- `src/test/java/magma/InterpreterLetBindingTest.java`
  - `annotatedLetBoolLiteral_evaluates_true` — asserts
    `interpret("let x : Bool = true; x", "")` yields `Ok("true")`.

Quality gates
-------------
- Add failing test (red)
- Implement change and run `mvn -DskipTests=false test` until green
- Ensure `mvn -DskipTests=false package` succeeds

Notes
-----
This change is intentionally small: it treats `Bool` as a special case of an
annotation (no width) and validates that the initializer is `true` or
`false`. No other language changes are introduced.
 
## Architecture change: compound assignment (+=)

Goal
----
Support compound assignment `+=` for mutable numeric variables. Example:
`let mut x = 0; x += 1; x` should evaluate to `"1"`.

Files / Modules affected
------------------------
- `src/main/java/magma/Interpreter.java` — add handling for `+=` in statement
  position, share validation/evaluation logic with existing `=` handling.
- `src/test/java/magma/InterpreterFeatureTest.java` — add acceptance test
  `compAssign` verifying `let mut x = 0; x += 1; x` => `"1"`.

Inputs / Outputs / Errors (contract)
-----------------------------------
- Input: source program string containing a mutable variable and a `+=`
  operation.
- Output: on success, interpreter returns `Result.Ok` with the string
  representation of the final variable value.
- Error modes: assignment to immutable variable, unknown identifier,
  read of uninitialized variable, invalid numeric RHS, or type mismatch
  against an annotated/inferred type.

Migration / Compatibility
-------------------------
This is a backwards-compatible extension for mutable variables only and
reuses existing assignment validation rules. No change to existing public
APIs.

Tests added
-----------
- `src/test/java/magma/InterpreterFeatureTest.java::compAssign` — asserts
  `let mut x = 0; x += 1; x` evaluates to `"1"`.

- Invalid case to cover: `let mut x = true; x += 1; x` — compound numeric
  assignment applied to a Bool-inferred or annotated variable should be
  rejected. Planned test: `src/test/java/magma/InterpreterFeatureTest.java::compAssignBoolErr`.

## Architecture change: while-loops

Goal
----
Add support for `while` loops in statement position. Example:
`let mut sum = 0; let mut counter = 0; while (counter < 4) { sum += counter; counter += 1; } sum` should evaluate to `"6"`.

Files / Modules affected
------------------------
- `src/main/java/magma/Interpreter.java` — add parsing & execution for `while` statements in `handleStatement`.
- `src/test/java/magma/InterpreterFeatureTest.java` — add a failing acceptance test `whileLoopSum`.

Inputs / Outputs / Errors (contract)
-----------------------------------
- Input: source program string containing a top-level `while` statement (in a sequence).
- Output: on success, the interpreter continues executing subsequent parts; final expression should reflect loop effects.
- Errors: invalid while syntax (missing parentheses/body), errors evaluating the condition, or errors from statements inside the loop (assignment errors, type errors, etc.).

Migration / Compatibility
-------------------------
This is an additive feature. `while` is only allowed in statement position (not as the final expression). Existing code paths for blocks and statements are reused to minimize changes.

Tests to add
------------
- `src/test/java/magma/InterpreterFeatureTest.java::whileLoopSum` — asserts the example above returns `"6"`.

Quality gates
-------------
- Add failing test (red)
- Implement `while` and run `mvn -DskipTests=false test` until green
- Ensure `mvn -DskipTests=false package` succeeds and Checkstyle/PMD stay clean


Quality gates
-------------
- Failing test added first (red), implemented feature, ran `mvn -DskipTests=false test` until green.

Documentation: updated this `docs/architecture.md` entry to describe the change.
Title: Enforce typed-literal vs annotated type compatibility

Goal
----
Make assignments with an explicit type annotation reject RHS typed literals that have a different kind (U vs I) or width (8/16/32/64), even when the numeric value would fit the annotated type. Example: `let x : U8 = 3I32; x` should be invalid.

Files/Modules affected
----------------------
- `src/main/java/magma/Interpreter.java` - add a compatibility check in let-binding handling to reject mismatched typed RHS literals.
- `src/test/java/magma/InterpreterFeatureTest.java` - add a failing acceptance test `letTypedMismatchErr` that asserts the behavior.

Inputs/Outputs/Errors (contract)
--------------------------------
- Input: source code string containing a `let` binding with an annotated type and an RHS expression.
- Output: Interpretation Result. If RHS is a typed literal (has a suffix like `I32` or `U8`) and the annotated type suffix differs (case-insensitive mismatch in kind or width), interpretation must fail with an interpret error.
- Error modes: invalid typed literal, mismatched typed literal vs annotated type, value not fitting annotated type.

Migration / Compatibility notes
------------------------------
This change tightens type compatibility for let-bindings only when the RHS is a typed literal. Untyped literals and expressions retain existing coercion rules (e.g., untyped literal fits into annotated typed variable if numeric range allows). This is a minor behavioral change that may make previously-accepted programs invalid when they mix explicit type suffixes in RHS with a different annotated type.

Tests to add
------------
- `src/test/java/magma/InterpreterFeatureTest.java::letTypedMismatchErr` - asserts `Interpreter.interpret("let x : U8 = 3I32; x")` is invalid.
 - `src/test/java/magma/InterpreterFeatureTest.java::assignImmutableErr` - asserts `Interpreter.interpret("let x = 0; x = 100; x")` is invalid.
 - `src/test/java/magma/InterpreterFeatureTest.java::declareReadInvalid` - asserts `Interpreter.interpret("let x : I32; x")` is invalid (reading an uninitialized declaration).

Quality gates
-------------
- Build: `mvn package` must succeed
- Tests: new and existing tests must pass
- Lint: keep changes minimal to avoid checkstyle violations

Notes
-----
This is a small, localized change: detect typed suffix on RHS text during let-binding handling and compare to annotation. If RHS is not a literal (has no suffix), existing numeric-range coercion continues to apply.
## Interpreter: typed integer literals, addition, and let bindings (compact)

Goal
-----
Add small, well-scoped features: typed integer literals (existing), two-integer addition, and simple `let` bindings with optional type annotations. Example: `let x : I32 = 3; x` => `"3"`.

What changed
------------
- `Interpreter.interpret` now supports semicolon-separated sequences. Intermediate statements must be `let` declarations; the final fragment is evaluated as an expression.
- `let` form: `let <ident> (':' <I|U><width>)? '=' <expr>` — the annotated type (e.g., `I32`, `U8`) is validated against the initializer.
- Expressions supported: integer literals (typed/untyped), variable references, and simple `<int> + <int>` addition (existing behavior).

Files touched
------------
- `src/main/java/magma/Interpreter.java` — sequence eval, let parsing, variable env, and helper functions.
- `src/test/java/magma/InterpreterFeatureTest.java` — added feature tests (let-binding + existing addition tests).
 - `src/test/java/magma/InterpreterLetBindingTest.java` — new unit test asserting unannotated let binding works: `let x = 3; x` => `"3"`.

Contract (inputs/outputs/errors)
--------------------------------
- Input: `source` string and `input` string (unused for these features).
- Success: `Result.Ok` with the stringified result (literal or computed sum).
- Failure: `Result.Err` for parse errors, type/width mismatches, unknown identifiers, or overflow.

Acceptance tests added
-----------------------
- `src/test/java/magma/InterpreterLetBindingTest.java`: verifies that an unannotated `let` binding followed by a variable lookup evaluates to the RHS value. Example: `let x = 3; x` should produce `"3"`.
 - `src/test/java/magma/InterpreterFeatureTest.java::literalTrue` - verifies that boolean literal `true` is accepted and returns `"true"`.

Quality gates & status
----------------------
- Tests added and executed. All tests and Checkstyle/PMD checks pass in this workspace (`mvn -DskipTests=false test` -> BUILD SUCCESS).

Notes and next steps
--------------------
- Behavior is intentionally minimal and conservative. If you want more expressions or a richer type system, we should replace the ad-hoc parsing with a small expression parser and add focused tests.

Documentation: `docs/architecture.md` updated to reflect the final change.

## Architecture change: multi-parameter function declarations and calls

Goal
----
Support function declarations with multiple parameters and function calls that pass multiple arguments. Example:
`fn first(a : I32, b : I32) : I32 => { return a; } first(100, 200)` should evaluate to `"100"`.

Files / Modules affected
------------------------
- `src/main/java/magma/Interpreter.java` — extend function parsing to accept comma-separated parameter lists and update call evaluation to bind multiple params.
- `src/test/java/magma/InterpreterFeatureTest.java` — add `fnTwoParamCall` acceptance test.

Design notes
------------
- Parameter lists are stored in `Env.fnParamNames` and `Env.fnParamTypes` as lists keyed by function name.
- `handleFnDecl` parses comma-separated parameters and records their names/types.
- `tryEvalFunctionCall` splits call arguments on commas, evaluates each argument in the caller environment, checks arity, creates a shallow copy of the caller `Env`, binds parameters into the function-local env, and evaluates the function body expression.
- Limitations: splitting on commas is simplistic and does not support nested comma-containing expressions in arguments or parameter defaults.

Tests added
-----------
- `src/test/java/magma/InterpreterFeatureTest.java::fnTwoParamCall` — verifies two-argument function declaration and call returns expected value.

Quality gates
-------------
- All tests must pass (`mvn test`) and Checkstyle must have 0 violations.

Documentation changes
---------------------
- README updated with a short example (see commit) describing `fn` syntax and the current limitation (no nested-commas in args).

