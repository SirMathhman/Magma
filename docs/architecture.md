# Architecture: Interpreter "literal integer" behavior

Goal
----
Make `Interpreter.interpret` return an Ok result for simple numeric literal programs so that `Interpreter.interpret("5", "")` yields `Result.Ok("5")`.

Files/Modules affected
----------------------
- `src/main/java/magma/Interpreter.java` — implement minimal logic for numeric literals
- `src/test/java/magma/InterpreterFeatureTest.java` — new failing test encoding the acceptance criterion

Contract (inputs/outputs/errors)
-------------------------------
- Input: source (String), input (String)
- Output: `Result<String,String>` where
  - Success: `Result.Ok<String,String>` containing the program output (here, the literal's text)
  - Failure: `Result.Err<String,String>` containing an error message or offending source

Migration / Compatibility
-------------------------
This change is additive and minimal; existing behavior for non-literal sources stays as Err (preserving backwards compatibility).

Tests to add
------------
- `src/test/java/magma/InterpreterFeatureTest.java` — asserts `interpret("5", "")` returns Ok("5").

Quality gates
-------------
- `mvn -DskipTests=false test` should run and initially show the new test failing.
- After implementation, `mvn -DskipTests=false package` should succeed and all tests pass.

Notes
-----
If more literal types are desired later (strings, booleans), extend the parser conservatively and add corresponding tests.
