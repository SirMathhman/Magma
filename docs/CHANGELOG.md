# Changelog

All notable changes to this project will be documented in this file.

## 2025-09-08 — Draft updates

- LANGUAGE: Added "Top-level expression programs" convenience form. A file containing a single top-level integer expression with `I32` suffix (for example `5I32`) is equivalent to a `fn main() -> int { return <expr>; }` and the returned `int` is used as the process exit code (backend-dependent mapping documented).
- IMPLEMENTATION: Documented how the C reference backend lowers top-level expression programs to a generated `main` and maps the returned `int32_t` to the process exit code using the low-order 8 bits.
 - LANGUAGE: Added normative wording for local `let` statements with explicit `I32` annotation and type-checking rules.
 - IMPLEMENTATION: Documented how the C reference backend lowers `let x : I32 = 0;` to an `int32_t` local and the requirement to type-check the initializer for `I32` compatibility.
 - LANGUAGE: Expanded integer width support to `U8|U16|U32|U64|I8|I16|I32|I64` and added rules for representability and type-checking.
 - IMPLEMENTATION: Documented mapping of the new annotations to C fixed-width types (`uintN_t`/`intN_t`) and lowering examples.
 - LANGUAGE: Specify the default integer type is `I32` for unannotated literals and inferred locals (for example `let x = 0;`).
 - IMPLEMENTATION: Clarified that unannotated integers lower to `int32_t` by default and that annotated initializers (for example `let x : U8 = 0;`) are interpreted in the annotated type and lowered to the corresponding `uintN_t`/`intN_t` with representability checks.
 - LANGUAGE: Added `Bool` type and boolean literals `true`/`false` with example usage.
 - IMPLEMENTATION: Documented lowering of `Bool` to C `bool`/`_Bool` and noted conversion/interop considerations.

## 2025-09-08 — Assistant tooling

- COPILOT-INSTRUCTIONS: Added `/spec` command behaviour — when a user message begins with `/spec ` the assistant will execute the instructions in `.github/prompts/spec.md` and treat that file as the authoritative prompt for `/spec` invocations. Repository editing steps requested by that prompt will follow the normal repo workflow and be documented in the changelog.

