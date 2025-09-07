Ban the use of the literal `null` across the codebase.

Rule:
- Do not use the `null` literal. Use `java.util.Optional` to represent absent values.

Rationale:
- `Optional` makes absence explicit and helps avoid NPEs.

Note: Checkstyle enforces this rule via a RegexpSinglelineJava rule that flags literal `null` tokens.
When updating code, avoid the token `null` even in string messages — prefer words like "absent" instead of "null".

Additional rule:
- Do not throw exceptions or use the `throws` declaration for control flow. Instead, return a `Result<T,E>` (Ok/Err) to represent success or failure.

Rationale:
- Exceptions hide control flow and make reasoning and static analysis harder. Returning an explicit Result value keeps error handling local and visible to callers.

Enforcement:
- Checkstyle includes rules that flag the token `throws` and patterns like `throw new` as errors. When updating code, convert methods that previously threw exceptions into Result-returning methods and handle errors explicitly.
