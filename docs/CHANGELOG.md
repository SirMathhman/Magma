# Changelog

All notable changes to this project will be documented in this file.

## 2025-09-08 — Draft updates

- LANGUAGE_SPEC: Added "Top-level expression programs" convenience form. A file containing a single top-level integer expression with `I32` suffix (for example `5I32`) is equivalent to a `fn main() -> int { return <expr>; }` and the returned `int` is used as the process exit code (backend-dependent mapping documented).
- IMPLEMENTATION: Documented how the C reference backend lowers top-level expression programs to a generated `main` and maps the returned `int32_t` to the process exit code using the low-order 8 bits.

