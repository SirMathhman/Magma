# Coding Standards

This project favors clear string manipulation over complex regular expressions.
When parsing source text, prefer small, readable loops and `split` operations.
Long or intricate regex patterns can be hard to maintain and are discouraged.

Abstract classes tend to complicate the design and are avoided in this codebase.
Favor composition of small collaborating objects over inheritance whenever
possible.
