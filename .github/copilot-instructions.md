# Copilot instructions for tests

- Each unit test should contain a single assertion where practical. This keeps tests focused and the failure signal clear.
- If multiple related behaviors must be validated, use separate tests with clear names (e.g., `interpretShouldStripU8`, `interpretShouldStripI16`).
