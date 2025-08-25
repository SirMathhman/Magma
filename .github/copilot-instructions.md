# Copilot instructions for tests

Each unit test should contain a single assertion where practical. This keeps tests focused and the failure signal clear.

## Test Driven Design

Use test driven design when implementing new features or fixing bugs:
- Write a failing test for the desired behavior.
- Run the test to confirm it fails.
- Implement the code to make the test pass.
- Run the test again to confirm it passes.

## Interpreter Monad Usage

Prefer to use a custom `Option` and `Result` monad for interpreter logic and related code. 
- `Option` should have variants `Some` and `None`.
- `Result` should have variants `Ok` and `Err`.
- Both should be implemented as sealed interfaces.

This approach improves type safety and clarity in error handling and optional values.

## Functional Programming

Prefer pure functions when possible. Pure functions are easier to test, reason about, and maintain.
- If multiple related behaviors must be validated, use separate tests with clear names (e.g., `interpretShouldStripU8`, `interpretShouldStripI16`).

- Prefer manual parsing over heavy use of regular expressions for simple interpreters when practical.

- Prefer grouping related tests into classes of a reasonable size: aim for 5–10 tests per test class. Avoid creating many tiny test classes with only one or two assertions; instead, consolidate related single-assert tests into the same class with clear, focused test names.

- Always run `mvn clean test` at the start and end of each task.