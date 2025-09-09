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

Contract (inputs/outputs/errors)
--------------------------------
- Input: `source` string and `input` string (unused for these features).
- Success: `Result.Ok` with the stringified result (literal or computed sum).
- Failure: `Result.Err` for parse errors, type/width mismatches, unknown identifiers, or overflow.

Quality gates & status
----------------------
- Tests added and executed. All tests and Checkstyle/PMD checks pass in this workspace (`mvn -DskipTests=false test` -> BUILD SUCCESS).

Notes and next steps
--------------------
- Behavior is intentionally minimal and conservative. If you want more expressions or a richer type system, we should replace the ad-hoc parsing with a small expression parser and add focused tests.

Documentation: `docs/architecture.md` updated to reflect the final change.
