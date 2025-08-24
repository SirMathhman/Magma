# Copilot instructions for tests

- Each unit test should contain a single assertion where practical. This keeps tests focused and the failure signal clear.
- If multiple related behaviors must be validated, use separate tests with clear names (e.g., `interpretShouldStripU8`, `interpretShouldStripI16`).

- Prefer manual parsing over heavy use of regular expressions for simple interpreters when practical.

- Prefer grouping related tests into classes of a reasonable size: aim for 5–10 tests per test class. Avoid creating many tiny test classes with only one or two assertions; instead, consolidate related single-assert tests into the same class with clear, focused test names.
