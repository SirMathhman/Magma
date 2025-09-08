<!-- GitHub Copilot / Assistant instructions for contributors -->
# Copilot / Assistant Instructions

These guidelines help contributors make changes that keep the repository build and QA checks green.

High-level rules

- After changing or adding functionality, update relevant documentation (README, CHANGELOG, docs/, or inline comments). Explain what changed, why, and how to use it.
- Keep changes small and focused. Run tests and linters locally before opening a pull request.
- When adding public APIs or commands, include usage examples and any required configuration.
- If behavior or migrations change, add upgrade notes and compatibility guidance.
- Do not add tests unless asked. This project enforces 100% coverage; contributors should avoid adding narrowly-scoped tests that complicate maintenance.

Thanks for helping keep the project well-documented.

## Build and static-analysis constraints (summary)

Follow these repository constraints so automated checks (Checkstyle, PMD/CPD, JaCoCo) pass.

Java build

- Targets Java 24 and enables preview features (compiler args include `--enable-preview`).
- Tests and tooling expect JDK 24.

Checkstyle (see `checkstyle.xml`)

- Avoid the literal `null`. Use `Optional` or the repository `Result<T, E>` pattern.
- Avoid `throw` / `throws`; prefer returning `Result` error values.
- Avoid explicit casts; prefer pattern matching or polymorphism.
- Do not use wildcard generics (e.g., `?`, `? extends`, `? super`); use explicit type parameters or bounded types.
- Do not use raw `Object` types; prefer proper generics or polymorphism.
- Do not import or use `java.util.regex`.
- Keep cyclomatic complexity per method <= 15.
- Limit method parameter count to 3.
- Checkstyle runs during the `test` phase and fails the build on violations (including test sources).

PMD / CPD (configured in `pom.xml`)

- CPD token threshold: minimum 50 tokens.
- Literal tokens (string/number) are ignored when detecting duplication.
- CPD checks both main and test sources and fails the build on violations.

JaCoCo (coverage)

- Enforced coverage: 100% minimum for INSTRUCTION, BRANCH, LINE, METHOD, and CLASS counters (reported during test/verify phases).

Practical notes

- Avoid `null`, `java.util.regex`, raw `Object`, wildcard generics, explicit casts, and `throw`/`throws`.
- Keep methods short and simple to meet complexity and parameter limits; extract small helpers when necessary.
- Run `mvn test` locally with JDK 24 to verify Checkstyle, CPD, and JaCoCo checks before opening PRs.
- Run `mvn clean test -q` at the start and end of changes to keep feedback loops short.

When in doubt, prefer small, well-documented changes and ask maintainers for guidance.
