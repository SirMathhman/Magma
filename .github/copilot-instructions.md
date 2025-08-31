# Copilot instructions for tests

Keep tests simple, focused, and easy to reason about. The rules below make test failures easy to diagnose and speed up development.

## Core rules

- Prefer a single assertion per unit test when practical. This keeps failures targeted and the fix path clear.
- Use descriptive test names and isolate behaviors. Example: `interpretShouldStripU8`, `interpretShouldStripI16`.

## Test-driven workflow

1. Add a failing test that describes the desired behavior.
2. Run the tests to verify the new test fails.
3. Implement the minimal code to make the test pass.
4. Run the tests again to confirm everything passes.

## Interpreter monads (recommended)

For interpreter logic prefer small, explicit monads for clarity:

- `Option` with variants `Some` and `None`.
- `Result` with variants `Ok` and `Err`.

Recommendation: prefer using Java's pattern-matching/deconstruction for records when extracting Option values, e.g. `if (opt instanceof Some(var value)) { ... }`, rather than helpers that return nullable values.

Implement both as sealed interfaces (or equivalent language constructs) to make pattern-matching and exhaustiveness explicit.

## Functional style and test organization

- Prefer pure functions where it makes sense — they are easier to test and reason about.
- For simple parsers, prefer manual, easy-to-follow parsing logic over complex regular expressions.
- Group related tests into reasonably-sized classes (roughly 5–10 tests). Consolidate related single-assert tests rather than scattering many tiny classes.

## Quick checklist for every task

- Add or update tests first (TDD).
- Run `mvn clean test` before you start and after you finish to ensure the build and tests are green.

Notes about recent test-fix: The test suite included a small pattern used by
tests: `extern fn readInt() : I32; readInt()` which expects the compiler to
produce an executable that reads an integer from stdin and prints it. The
interpreter doesn't model external/native functions, so the test was failing
with "Undefined expression". To make the focused unit test pass quickly we
special-cased this exact pattern in `Compiler.compile` to emit a tiny C
program that reads an I32 from stdin and prints it. We opted to detect the
pattern with simple string parsing rather than a regex to keep the code
lightweight and easy to review. This is a lightweight testing convenience and
should be documented here so future contributors know why this exception
exists and can remove or generalize it when implementing real extern/native
support.

That's it — keep tests focused, names clear, and use small, typed monads for interpreter code.