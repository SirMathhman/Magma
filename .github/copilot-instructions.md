## Magma compiler & contributor notes

This document collects concise implementation and refactoring guidance for contributors
working on the Magma compiler and related code. Keep changes small and explicit; prefer
clarity and predictability over cleverness.

### Compiler implementation rules

- Do not use regular expressions (regex) anywhere in `magma.Compiler.java`.
- Use simple string scanning or character inspection for token detection.
- Keep the compiler behavior minimal and explicit. For example, treat the bare identifier
	`readInt` (not followed by `(`) as a compile error, and treat `readInt()` as a call.

### Refactoring guidance

- Avoid regex-based transformations in compiler helpers. Prefer small, explicit parsing or
	token-aware helper functions so transformations are predictable and don't accidentally
	change identifiers, comments, or whitespace (examples: `CompilerUtil.removeExternDeclaration`,
	`CompilerUtil.protectLetMut`, `CompilerUtil.replaceLetWithConst`).
- When a class grows large (for example `Compiler`), prefer splitting it into small,
	focused, top-level helper classes (for example `CompilerUtil`) to reduce method count,
	improve testability, and keep responsibilities explicit.
- Avoid inner (non-static nested) and local classes; prefer top-level helper classes to
	simplify testing, keep responsibilities explicit, and avoid classloading or visibility issues.
- Rarely modify `magma.run.Runner.java`, `magma.run.Executor.java`, and related runner
	classes; only change them when strictly necessary. Most changes should be implemented in
	`magma.Compiler.java` or helpers that `Compiler` depends on.

### PMD / CPD and style considerations

- PMD/CPD thresholds in this repository are intentionally conservative to encourage small,
	expressive duplications rather than aggressive wholesale removal. When addressing CPD
	findings, prefer extracting small, well-named helpers and preserving emergent patterns
	that improve readability and domain expressiveness rather than mechanically removing
	repeated blocks.

### Tooling & tests

- Run the test suite locally before and after substantive changes to validate behaviour:

```pwsh
mvn -q -DskipTests=false clean test
```

### Configuration files

- CheckStyle: `config/checkstyle/checkstyle.xml`
- PMD/CPD rules and thresholds: files under `config/pmd/` (review these for rule and threshold settings)

If you'd like, I can also add a short quick-reference for running CheckStyle and PMD locally
or wire simple validation tasks into the project `pom.xml` as follow-ups.