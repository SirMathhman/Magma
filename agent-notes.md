```markdown
Agent reference — how the codebase works
======================================

Purpose
-------
- A short, living reference the assistant and contributors can read before touching code.
- Not a commit log — focus on architecture, conventions, important entry points, and common pitfalls.

High-level architecture
-----------------------
- `magma.compiler` — compiler pipeline: parsing, simple statement splitting, basic semantic checks, and type alias handling.
- `magma.parser` / `magma.ast` — AST shapes and parsing helpers (small, explicit parsing; avoid regex in core compiler files).
- `magma.emit` — code generation targets (JS/C emitters and helpers).
- `magma.run` / test harness — runner and executor used by integration tests.

Key responsibilities by package
-------------------------------
- `compiler`: orchestrates parsing, alias registration, semantic checks, and var/func/struct handling. Keep logic explicit and small helpers testable.
- `parser` / `ast`: canonical representations (records) for parsed constructs; prefer clear small functions.
- `emit`: translate AST / semantic results to target languages; keep per-target emitters isolated.
- `diagnostics`: compile-time error/report types.

Important files to know
----------------------
- `src/main/java/magma/compiler/Compiler.java` — central compiler logic and alias registry. Rules: do not use regex here; prefer string scanning and small helper functions.

Note for Copilot: avoid using regular expressions in `Compiler.java` for parsing. Regexes conflate tokenization and grammar, hide parsing structure, and prevent correct generalization of parsing logic — prefer explicit token scanning and small parser helpers so the AST and semantics emerge clearly.
- `src/main/java/magma/compiler/CompilerUtil.java` — extraction of parsing helpers (preferred place for pure helpers).
- `src/main/java/magma/emit/*` — emitter implementations.
- `src/test/java/magma/feature/*` — fast feature tests using `TestUtils.assertAllValidWithPrelude` and related helpers.

Conventions and coding rules
----------------------------
- No regex inside `Compiler.java`. Token detection must use character inspection or small parsing helpers.
- Prefer small, pure helper functions (return results instead of mutating global state when possible).
- Error handling: prefer `Result<T, E>` for operations that can fail rather than throwing; use `Optional<Error>` for void-like operations that may fail.
- Use clear, descriptive compile error messages for user-facing diagnostics.

Testing and verification
------------------------
- Run full test suite and quality checks with:
  ```pwsh
  mvn -q -DskipTests=false clean test
  ```
- Unit-location: `src/test/java` (feature tests under `magma.feature`). Test helpers in `magma.TestUtils`.
- The build runs CPD/PMD and Checkstyle; the project treats CPD defensively — prefer small refactors to reduce duplication where it helps readability.

Common pitfalls
---------------
- Misnamed test files (public class name must match filename) cause test compile failures on CI — run `mvn test` locally after renaming.
- Avoid changing `magma.run.Runner`/`Executor` unless strictly necessary; most changes belong in `Compiler` or helpers.
- When adding type alias logic: enforce naming conventions and resolve alias chains consistently (see existing `Compiler` alias-resolve code paths).

How to make safe edits
----------------------
1. Run tests locally before and after changes.
2. Keep changes small and add focused unit tests where behaviour changes.
3. If changing `Compiler`, prefer extracting helpers to `CompilerUtil` and add unit tests there.

How this file should be used
---------------------------
- Read before starting a task to get context and conventions.
- Update with short, actionable insights (not logs). Keep entries concise and latest-first if you add notes.

```
Agent notes (breadcrumbs)
=========================

Purpose:
- A compact breadcrumb file the assistant reads before each task and updates after each task.

Design goals:
- Keep entries short and actionable — think "breadcrumbs", not a full timeline.
- Prefer latest-first, terse bullets with a one-line summary and optional 1-2 follow-up items.
- Avoid sensitive data or long dumps of code; link to files or classes instead of pasting contents.

Usage:
- The assistant will read this file before starting any task and append a new breadcrumb after completing the task.
- Contributors may edit the file, but please keep edits concise and factual. Do not remove or rename the file

Recent assistant actions (latest first):