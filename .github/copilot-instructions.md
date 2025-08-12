# Copilot Instructions for Magma

## Project Overview
- Magma is a custom language compiler that translates simple assignment statements from a custom syntax (e.g., `let x : I32 = 0I32;`) to C syntax (e.g., `int32_t x = 0;`).
- The main logic resides in `src/lib.rs`, specifically the `compile` function.
- Supported types: U8, U16, U32, U64, I8, I16, I32, I64. Type suffixes on literals (e.g., `0I32`) must match the declared type.

## Architecture & Data Flow
- Input: Source code string in custom syntax.
- Processing: Regex-based parsing in `compile` extracts variable name, type, and value, validates type suffixes, and maps to C types.
- Output: C assignment statement as a string.
- All logic is currently in a single file (`src/lib.rs`).

## Developer Workflows
## Developer Workflows (Test-Driven Design)
 - **Write a failing test first:** Add a new test case in the `tests` module in `src/lib.rs` for the desired feature or bug fix.
 - **Confirm the test fails:** Run the test with `cargo test` (e.g., `cargo test --lib -- tests::<test_name> --exact --show-output`) and ensure it fails as expected.
 - **Implement the feature or fix:** Update the code (usually in `compile` in `src/lib.rs`) to make the test pass.
 - **Test everything again:** Run all tests with `cargo test` to confirm correctness and avoid regressions.
 - **Refactor:** Remove duplicate code, choose better names, and improve structure. Always rerun tests after refactoring.
 - **Repeat:** Continue this cycle for every new feature or fix.

## Project-Specific Patterns
- All parsing is done via a single regex. To extend syntax, update the regex in `compile`.
- Type suffixes on literals are optional but, if present, must match the declared type.
- Error handling: Only specific patterns are accepted; all other inputs return a clear error string.
- Tests use `assert_compile(input, expected)` for positive cases and direct error checks for negative cases.

## Integration Points & Dependencies
- Uses the `regex` crate for parsing. Ensure it is present in `Cargo.toml`.
- No external services or cross-component communication; all logic is local.

## Examples
- Valid: `let x : I32 = 0I32;` → `int32_t x = 0;`
- Invalid: `let x : I32 = 0U32;` → Error: Type suffix does not match declared type
- Valid: `let a : U8 = 1;` → `uint8_t a = 1;`

## Key Files
- `src/lib.rs`: Main compiler logic and all tests
- `Cargo.toml`: Dependency management

---

For unclear patterns or missing conventions, ask the user for clarification or examples from their workflow.
