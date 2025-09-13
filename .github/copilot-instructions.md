<!--
Repository: Magma
Purpose: Guidance for AI coding agents (Copilot, assistants) to be productive in this small Bun+TypeScript project.
-->

# Copilot / AI agent instructions — Magma

This repository is a tiny TypeScript project built to run with Bun. The key goal for an AI coding agent is to make small, correct edits (implementation, tests) and keep builds/tests passing.

Keep edits small and verifiable. Preferred workflow:
- Run `bun install` to install deps.
- Run tests with Bun's test runner (tests import from `bun:test`). Example: `bun test` or `bun` where appropriate. See README.

Quick facts
- Runtime: Bun (see `README.md`).
- Entrypoint: `index.ts` (prints a hello message).
- Primary logic: `interpret.ts` exports `interpret(sourceCode: string): [number, string]`.
- Tests: `interpret.test.ts` uses `bun:test` and asserts behavior for `interpret`.

Coding patterns and conventions to follow
- TypeScript, ES modules, and Bun-specific flags are expected. `tsconfig.json` uses `module: "Preserve"` and `noEmit: true`.
- Keep exports minimal and typed. `interpret.ts` currently exports a tuple [number, string]. Maintain that shape unless modifying the public contract and updating tests.
- Tests target behavior, not implementation details. When changing `interpret` behavior, update `interpret.test.ts` accordingly.

Build/test/debug commands (discoverable in repo)
- Install dependencies: `bun install`.
- Run the app: `bun run index.ts` (recommended for manual verification).
- Run tests: `bun test` or `bun` with a test script if added to package.json. Tests import `bun:test` so they run under Bun's test runner.

Files to inspect for most changes
- `interpret.ts` — core function to implement/change.
- `interpret.test.ts` — tests that define expected behavior.
- `index.ts` — tiny runner; useful for quick manual verification.
- `tsconfig.json` — TypeScript compiler flags; avoid emitting JS when making TypeScript-only edits.

Advice for AI edits
- Small, focused PRs: implement one behavior or fix one failing test at a time.
- Run tests locally after edits. If you can't run Bun, prefer changes that keep type signatures and tests minimal.
- When adding new tests, use the existing pattern with `import { expect, test } from "bun:test";` and keep tests deterministic.
- Preserve repository's minimal dependency footprint. Do not add new runtime dependencies without justification.

Examples from this repo
- The test `interpret.test.ts` expects:
  - `interpret("")` returns `[0, ""]`.
  - `interpret("test")` throws.

If you change the API
- Update both `interpret.ts` and `interpret.test.ts` together.
- Update this file with a short note describing the API change.

When unsure, ask the maintainer for guidance about intended semantics for `interpret` before large refactors.

Contact/notes
- Maintainer: repository owner (see repo settings). Use PRs and small commits.
