<!-- GitHub Copilot / Assistant instructions for contributors -->
# Copilot / Assistant Instructions

Please follow these guidelines when making changes in this repository.

- After completing any task that changes or adds functionality, always create or update documentation describing the change (for example: README, CHANGELOG, docs/ files, or inline code comments). Documentation should explain the what, why, and how to use the new or changed behavior.

- Keep changes minimal and focused. Run tests and linters locally before opening a pull request.

- When adding new public APIs or commands, include usage examples and any required configuration.

- If a change affects behavior or migrations, add upgrade notes and backwards-compatibility considerations.

- Do not add new tests unless explicitly instructed to do so. This project aims for 100% test coverage, which requires tests to cover all branches; only add tests when requested so contributors avoid introducing excessive or narrowly-scoped tests and instead focus on generalizable, well-covered implementations.

Thank you for helping keep the project well-documented.

## Repository static-analysis and build constraints (summary)

When contributing code or generating changes, follow these repository-specific constraints so automated checks (Checkstyle, PMD/CPD, JaCoCo) pass:

- Java language & build
  - Project targets Java 24 and enables preview features; compiler args include `--enable-preview`.
  - Tests and tooling expect a JDK 24 installation.

- Checkstyle (rules in `checkstyle.xml`)
  - The literal `null` is banned (use `Optional` or the repository `Result<T, E>` pattern instead).
  - `throw` / `throws` are banned; prefer returning `Result` error values instead of exceptions.
  - Explicit type casts are disallowed; prefer pattern matching or polymorphism.
  - Wildcard generics (e.g., `?`, `? extends`, `? super`) are banned; prefer explicit type parameters or bounded types.
  - Literal uses of `Object` are banned; prefer proper generics/polymorphism.
  - Use of `java.util.regex` (regex API/imports) is disallowed by policy.
  - Cyclomatic complexity per method is limited to 15.
  - Maximum number of parameters per method is 3.
  - Checkstyle is run during the `test` phase and configured to fail the build on violations; it also checks test sources.

- PMD / CPD (configured in `pom.xml`)
  - Copy/Paste detection (CPD) token threshold: minimum 50 tokens.
  - Literal tokens (string/number) are ignored when detecting duplication.
  - CPD runs on tests and main sources; violations are configured to fail the build.

- JaCoCo (coverage)
  - Coverage is enforced: 100% minimum for INSTRUCTION, BRANCH, LINE, METHOD, and CLASS counters (reports generated during test/verify phases).

Practical notes for assistants and contributors
  - Avoid using `null`, `java.util.regex`, raw `Object`, wildcard generics, casts, and explicit throw/throws.
  - Keep methods small/simple to satisfy cyclomatic complexity and parameter count rules; if logic grows, extract helpers (but keep helpers simple as well).
    - Run `mvn test` locally with JDK 24 to verify Checkstyle, CPD, and JaCoCo checks before opening PRs.
    - Always run `mvn clean test -q` at the start and at the end of your changes to ensure tests and static checks are green and to minimize feedback loops.
