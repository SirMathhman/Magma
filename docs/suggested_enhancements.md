# Suggested Enhancements

This page tracks ideas for improving the Magma compiler. Update this list whenever a new feature or refactoring idea comes up.

- Static analysis to detect unreachable code
- Integration with a simple build system for multi-file projects
- Command-line flag to output intermediate representations for debugging
- Nested object declarations compile to standalone singletons
- String slice `&Str` for passing text values
- Arrays of `&Str` allowed in variables and function parameters
- Central `value_info` helper for all value expressions
- `type_info` helper centralizes parsing for pointers and arrays
- Allow `Any` parameters on extern functions for simplified FFI hooks
- Extern functions may use generics with variadic or fixed-size arrays
