Ban the use of the literal `null` across the codebase.

Rule:
- Do not use the `null` literal. Use `java.util.Optional` to represent absent values.

Rationale:
- `Optional` makes absence explicit and helps avoid NPEs.

Note: Checkstyle enforces this rule via a RegexpSinglelineJava rule that flags literal `null` tokens.
When updating code, avoid the token `null` even in string messages — prefer words like "absent" instead of "null".
