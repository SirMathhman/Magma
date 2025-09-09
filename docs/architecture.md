# Architecture: Interpreter "literal integer" behavior

----
Make `Interpreter.interpret` return an Ok result for simple numeric literal programs so that `Interpreter.interpret("5", "")` yields `Result.Ok("5")`.
Title: Typed integer literal support (U8..U64, I8..I64)

Goal
----
Extend `Interpreter.interpret` so that it recognizes typed integer literals with suffixes `U8`, `U16`, `U32`, `U64` (unsigned) and `I8`, `I16`, `I32`, `I64` (signed). The interpreter should:

- Accept numeric literals with optional sign and an optional type suffix (e.g. `123`, `-5I8`, `255U8`).
- For typed suffixes, validate the numeric range for the specified width. Return `Result.Ok` with the base integer text when valid, and `Result.Err` when invalid (out-of-range or negative unsigned).

This change is intentionally small and keeps the public `Interpreter.interpret(String,String)` contract.

Files/Modules affected

Contract (inputs/outputs/errors)
  - Success: `Result.Ok<String,String>` containing the program output (here, the literal's text)
  - Failure: `Result.Err<String,String>` containing an error message or offending source

Migration / Compatibility
This change is additive and minimal; existing behavior for non-literal sources stays as Err (preserving backwards compatibility).

- `src/test/java/magma/InterpreterTypedLiteralTest.java` — added tests for:
  - valid max/min values for U8/I8 and U64/I64
  - unsigned negative numbers should produce Err
  - overflow values should produce Err
- `src/test/java/magma/InterpreterTypedLiteralTest.java` — asserts `interpret("5I32", "")` returns Ok("5") (support typed integer suffixes like `I32`).

Quality gates
-------------
- `mvn -DskipTests=false test` should run and initially show the new test failing.
- After implementation, `mvn -DskipTests=false package` should succeed and all tests pass.

Notes
-----
If more literal types are desired later (strings, booleans), extend the parser conservatively and add corresponding tests.
