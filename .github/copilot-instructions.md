Compiler implementation notes

- Do not use regular expressions (regex) anywhere in `Compiler.java`.
- Use simple string scanning or character inspection for token detection.
- Keep the behavior minimal: detect bare `readInt` (identifier not followed by `(`) as a compile error, and detect `readInt()` as a call.
Run `mvn -q -DskipTests=false clean test` before and after making changes to ensure tests fail and then pass.

Note: PMD/CPD thresholds in this repository are intentionally conservative to promote emergence
and small, expressive duplications rather than aggressive wholesale removal. When refactoring to
address CPD findings, prefer extracting small, well-named helpers and preserving emergent
patterns that improve readability and domain expressiveness rather than mechanically removing
every repeated block.

Rarely modify Runner.java and Executor.java and related classes. Only modify these if you need too, usually Compiler.java and things that depend on Compiler.java are to be modified instead.

Avoid using regex-based transformations in compiler helpers. Prefer small, explicit parsing
or token-aware helper functions (like `CompilerUtil.removeExternDeclaration`,
`CompilerUtil.protectLetMut`, and `CompilerUtil.replaceLetWithConst`) so transformations are
predictable and don't accidentally change identifiers or comments.