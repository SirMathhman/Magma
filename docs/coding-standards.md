# Coding Standards

This project favors clear string manipulation over complex regular expressions.
When parsing source text, prefer small, readable loops and `split` operations.
Long or intricate regex patterns can be hard to maintain and are discouraged.
Functions should contain at most **one** loop and indentation should never
nest more than two levels deep.

Avoid Java-style exception handling. Do not use `throw`, `try`, or `catch`.
Instead, represent failure or missing values with a `Result` or `Option` object.

Abstract classes tend to complicate the design and are avoided in this codebase.
Favor composition of small collaborating objects over inheritance whenever
possible.

TypeScript output follows a minimal style. Variable declarations keep spaces
around the colon so `let` statements read `let x : number` rather than
`let x: number`.
