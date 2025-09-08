<!-- Copilot/AI agent guidance for the Magma compiler workspace -->
# Magma — AI coding agent instructions

Purpose
- Help an automated coding assistant be productive quickly in the Magma compiler repository (a small experimental Magma -> C compiler).

High-level architecture (what to know immediately)
- This repo is an early-stage compiler frontend and backend: lexer, parser, typechecker, IR, and a C emitter (see `README.md`).
- Key docs to read first: `docs/spec.md` (language design) and `docs/quickstart.md` (example workflow).
- Typical pipeline to implement or modify: source (.mg) -> lexer -> parser -> AST -> typechecker -> IR -> C emitter -> compile with system C compiler.

Files and folders of interest
- `docs/spec.md` — authoritative language semantics and types (i32, i64, f32, f64, bool, ptr<T>, let/var). Use it as the spec for language features and tests.
- `docs/quickstart.md` — shows expected generated C patterns (e.g., `print` mapping to `printf`), and suggests a `magmac` tool to emit `.c` files.
- `README.md` — high-level goals, layout, and roadmap; helpful for PR descriptions and long-term design decisions.
- `tests/` — tests live here (add tests for codegen/lexer/parser). Example test found: `tests/test_exit5.py::test_exit5_stdout_and_code` (pytest cache present).
- `.github/prompts/plan.prompt.md` — contains agent-style prompts; follow its structure for large design or spec work.

Project-specific conventions & patterns
- Keep language semantics in `docs/spec.md`. New features must be matched against that document and examples in `docs/quickstart.md`.
- The code generation target is idiomatic C. Emit readable C (use `#include <stdio.h>` where `print` is used; follow example in `docs/quickstart.md`).
- No `src/` code yet — when adding modules, follow the planned compiler stages in the README and name directories clearly: `src/lexer`, `src/parser`, `src/typecheck`, `src/codegen`.

Developer workflows (commands & tests)
- Tests appear to use pytest (see `tests/` and `.pytest_cache`). Run via: `pytest -q` in the repository root.
- C output is expected to be compiled with common C toolchains. Example commands from `docs/quickstart.md`:
  - gcc (MinGW/Cygwin): `gcc -std=c11 -O2 -o hello hello.c` then `./hello`
  - MSVC (Developer Command Prompt): `cl /EHsc /O2 hello.c` then `hello.exe`

Integration points & external dependencies
- The main external integration is with system C compilers (gcc/clang/clang-cl/MSVC). Tests or example flows may invoke the toolchain.
- Future dependencies: when implemented, the `magmac` CLI will be an entrypoint that reads `.mg` and emits `.c`.

Examples from the codebase (copy or mirror these patterns)
- `docs/quickstart.md` demonstrates mapping `print(x)` in Magma to `printf("%d\n", x)` in C — follow this for codegen for integer printing.
- The language spec lists explicit types and `let` vs `var`. Implement parser/typechecker to require explicit types on variables and functions.

What to avoid / out-of-scope assumptions
- Don't assume threading, generics, or modules exist yet — the spec explicitly defers these.
- Don't invent a different runtime or garbage collector; the spec references manual alloc/free mappings to C's `malloc`/`free`.

When you need more context
- If code exists in `src/` files not present in the repo yet, read top-level README and `docs/` first and ask maintainers for intended module structure.
- For large design work, prefer generating proposals that update `docs/spec.md` and `docs/quickstart.md`, and create a `.github/prompts/plan.prompt.md`-style plan for acceptance criteria.

Edits and PR guidance for AI agents
- Keep commits small and focused by compiler stage (lexer, parser, typechecker, codegen).
- Include or update examples in `docs/quickstart.md` and add round-trip tests under `tests/` that: compile a small `.mg` example, run the emitted C, and assert stdout/exit code.
- Reference `docs/spec.md` in PR descriptions and add tests that assert the behavior described there.

Contact points / where to ask human maintainers
- Open issues and PR comments are the expected channel. Link to `docs/spec.md` lines when asking design questions.

If you add files
- New source code should live under `src/` with clear subdirectories per compiler stage. Add tests under `tests/` and examples under `examples/`.

Please review these instructions and tell me any unclear areas or missing details to iterate.
