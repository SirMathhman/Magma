## Magma compiler & contributor notes

This file documents a single, practical guideline for the assistant and contributors:

- When a class starts to accumulate many helper methods (or CPD starts flagging duplication), prefer extracting small, focused top-level classes rather than inlining methods into the same file.

Why:
- Splitting responsibilities into multiple classes keeps individual files short (helpful for reviewers and the CPD rules), reduces method-count violations, and makes it easier to test and reuse helpers without triggering duplication detectors.

How to apply:
- Create new top-level classes (for example `magma.CodeGen`, `magma.CompilerHelpers`) for code generation and small transformations instead of adding many private helpers to `Compiler` or `CompilerUtil`.
- Keep helpers pure where reasonable, and expose simple static entry points.
- Avoid creating many tiny methods inside a single class just to work around duplication rules; instead group related helpers into a small new class.

Note: this file intentionally keeps a single guideline to make it easy for the assistant to read and follow; more rules should be added to project CONTRIBUTING.md instead of here.

Additional testing guideline for the assistant:

- Tests should keep to a single assertion per test method when practical. This keeps failures focused and satisfies the project's style checks that count test method complexity. If multiple related checks are needed, create separate test methods (e.g., `parenthesizedLiteral`, `parenthesizedReadInt`, `parenthesizedReadIntPlusOne`) or use parameterized tests.