# Coding Standards

This project favors clear string manipulation over complex regular expressions.
When parsing source text, prefer small, readable loops and `split` operations.
Long or intricate regex patterns can be hard to maintain and are discouraged.

Avoid Java-style exception handling. Do not use `throw`, `try`, or `catch`.
Instead, represent failure or missing values with a `Result` or `Option` object.
