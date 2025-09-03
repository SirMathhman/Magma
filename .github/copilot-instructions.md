# Linting & build constraints for the Magma project

Below are the concrete, machine-enforced constraints discovered in the repository's linting configuration. Use these when authoring or refactoring Java code for this project.

## At-a-glance checklist

- [x] Document Checkstyle rules and suppressions (`config/checkstyle/*`).
- [x] Document PMD rules and PMD plugin configuration (`config/pmd/*`, `pom.xml`).
- [x] Document custom Java-file-count check (`scripts/check_java_files.py` and exec plugin usage).
- [x] Show how violations fail the build and CI phases to watch.

## Overview & intent

This project enforces a strict set of static-analysis and build-time checks to keep the codebase maintainable as it grows. The rules are intentionally prescriptive in places (for example: limiting method parameters, banning `null`/`throw`, and enforcing low CPD tokens) to force clearer abstractions, stronger separation of concerns, and explicit error handling.

Key goals:
- Encourage Single Responsibility Principle (SRP) at class and package levels.
- Make error and control flow explicit and easy to reason about.
- Surface duplicate behavior early so it can be refactored into reusable abstractions.

## Where these rules live (paths)

- `pom.xml` — plugin lifecycle and properties (Checkstyle, PMD, exec script, CPD property).
- `config/checkstyle/checkstyle.xml` — Checkstyle rules.
- `config/checkstyle/suppressions.xml` — Checkstyle suppressions (test-only for `IllegalToken`).
- `config/pmd/ruleset.xml` — PMD rules (e.g., ban `@Deprecated`).
- `scripts/check_java_files.py` — custom directory/file-count checks invoked during Maven `validate`.

## Environment & build behavior

- Java language target: Java 22 (maven.compiler.release=22). Tools are configured to parse modern Java (records, sealed types).
- CI/build tools that enforce rules:
  - `maven-checkstyle-plugin` (configured to fail on violations)
  - `maven-pmd-plugin` (executes `check` and `cpd-check` goals)
  - `exec-maven-plugin` invoking `scripts/check_java_files.py` during `validate` (non-zero exit fails the build)
- PMD and Checkstyle are pinned to versions that support Java 22 features (Checkstyle dependency 10.12.0; PMD dependencies v7.0.0 in the PMD plugin).
- Property: `<cpd.skip>false</cpd.skip>` — CPD is enabled by default unless overridden on the command line.

## Constraints (what is enforced, why, and how to fix violations)

### Checkstyle (config/checkstyle/checkstyle.xml)

- Suppressions file: `config/checkstyle/suppressions.xml` is applied via `SuppressionFilter`.

#### Method count per class
- Constraint: `MethodCount` → `maxTotal = 10` (counts constructors by default).
- Intent: Keep the purpose of each class clear and focused, with good naming and separation of concerns. Avoid "god-classes" that try to do too much and become hard to maintain.
- Recommended fix: Split large classes into smaller, well-named classes with a clear responsibility. For tests, split into smaller test classes that each test a distinct feature.

#### Illegal tokens
- `LITERAL_NULL`
  - Intent: Avoid NullPointerExceptions.
  - Recommended fix: Use `Optional` or provide a safe default value instead of `null`.

- `LITERAL_THROW`
  - Intent: Explicit error control — `throw` makes it harder to reason about error flow.
  - Recommended fix: Use a sealed `Result` interface with `Ok` and `Err` variants to represent success/error outcomes explicitly.

- `LITERAL_CONTINUE`
  - Intent: `continue` often complicates loop control flow.
  - Recommended fix: Refactor to keep loop bodies simple, or extract the loop into a method.

- `LITERAL_BREAK`
  - Intent: `break` can obscure control flow.
  - Recommended fix: Extract the loop into a method and use `return` to exit early instead.

- `TYPECAST`
  - Intent: Explicit casts are fragile and can fail at runtime.
  - Recommended fix: Use pattern matching (for example `instanceof` with binding, or `switch` patterns) or redesign APIs to avoid needing casts.

- `LITERAL_VOID`
  - This is enforced in an isolated `IllegalToken` rule so tests can selectively allow `void` usage via `suppressions.xml`.

#### Illegal types
- `IllegalType` disallows use of `Object` and `java.lang.Object` as a declared type.
  - Intent: Using raw `Object` indicates an abstraction problem.
  - Recommended fix: Define an interface (even a marker interface) and concrete implementations instead of using `Object`.

Checkstyle plugin settings in `pom.xml`:
- `configLocation` points to `config/checkstyle/checkstyle.xml`.
- `includeTestSourceDirectory=true` (rules also apply to tests except where suppressed).
- `failOnViolation=true` (violations will fail the build when Checkstyle runs).

### PMD (config/pmd/ruleset.xml)

- PMD ruleset includes:
  - `NoDeprecatedAnnotation` — XPath rule that flags `@Deprecated` usage.
    - Intent: Prevent deprecated code from lingering; prefer removal or replacement.

PMD plugin configuration in `pom.xml`:
- `<targetJdk>22</targetJdk>` so PMD parses Java 22 syntax.
- `<minimumTokens>30</minimumTokens>` (affects PMD's token-based thresholds).
- `<includeTests>true</includeTests>` — PMD also analyzes test sources.
- The PMD plugin runs in the `test` phase with `check` and `cpd-check` goals.

Any PMD `check` violation will cause the build to fail.

### Custom Java-file-count rule (`scripts/check_java_files.py`)

- Constraint: No more than 10 `.java` files directly inside any immediate directory under `src/main/java` (default limit).
- Intent: Prevent excessive classes in a package and keep packages focused on a single intent (SRP at the package level).
- Recommended fix: Move files into a new subpackage/directory; consider combining related classes or extracting responsibilities into a clearer package structure.
- CLI options: `--limit` / `-l`, `--path` / `-p`, and `--root-only`.
- How it fails CI: The script runs in Maven `validate` via `exec-maven-plugin` and exits non-zero when violations are found; this fails the build early.

### CPD / duplication checks

- CPD is invoked via the PMD plugin (`cpd-check`) and controlled by `<cpd.skip>false</cpd.skip>`.
- Intent: Remove duplicate behavior and encourage architectural clarity and generalization, not just copy-paste removal.

Note: avoid relying on regular expressions for core lexing or splitting tasks that may be reused across the codebase. If two similar regexes are later duplicated, CPD will flag them and it's harder to refactor shared lexing behavior. Prefer small helper methods (like `splitLines`) that centralize low-level parsing logic so they can be reused and tested.
- Note: The token threshold is intentionally low (`30`) so that refactoring is favored over leaving duplicated blocks of logic.

### New: method parameter limit

- Constraint: No more than two parameters per method (Checkstyle `ParameterNumber`, `max = 2`). The limit is configurable via the Maven property `max.method.parameters` in `pom.xml`.
- Intent: Force extraction of helper/parameter objects, convert helpers to instance methods holding state, or split responsibilities.
- Recommended fix: Create small value/parameter objects, convert helpers to instance methods holding state, or split responsibilities.

## How violations affect the build and CI phases

- `scripts/check_java_files.py` runs in `validate` and exits non-zero on failure (early build stop).
- PMD (`check`, `cpd-check`) runs in `test` phase and fails the build on violations.
- Checkstyle runs in `test` phase with `failOnViolation=true` and will fail the build on violations.

Summary: if any of these tools report a violation, `mvn` will exit non-zero in CI. Intent: the code is not considered "good" until it adheres to these constraints to keep the codebase maintainable and scalable.

## Quick authoring tips

- Keep classes under 11 methods (<=10 methods) or split/refactor into smaller classes.
- Prefer explicit types and avoid `Object` / `java.lang.Object` as a declared type.
- Avoid `@Deprecated`; remove or replace deprecated code.
- Avoid `null` literals — use `Optional` or safe defaults.
- Avoid `throw` for control-flow; prefer explicit result types (sealed `Result` with `Ok`/`Err`).
- Keep loop control simple; prefer method extraction and `return` over `break`/`continue`.
- Avoid explicit casts; prefer pattern matching or improved type design.
- Keep directories under `src/main/java` to 10 or fewer `.java` files (or run `scripts/check_java_files.py` locally and refactor into subpackages/directories).
- Tests may allow `void` in places where production code would be flagged because of `suppressions.xml`.

## Verification & CI notes

- Ensure `python` is present in CI images (the exec plugin invokes `python scripts/check_java_files.py`).
- Running `mvn -e verify` locally should exercise Checkstyle and PMD as configured (tools run during `test` phase; the script runs earlier in `validate`).

---

## Pattern matching examples

Small, copy-friendly examples showing pattern matching that avoids explicit casts.

- instanceof binding

  Use `instanceof` with a binding pattern to avoid explicit casts:

  ```java
  record Some<T>(T value) {
  }

  if (obj instanceof Some(var value)) {
    // do something
  }
  ```

- switch pattern

  Modern `switch` pattern matching (Java 17+ / preview) avoids casts and is concise:

  ```java
  sealed interface Shape permits Circle, Rectangle {
  }

  record Circle(double radius) implements Shape {}
  record Rectangle(double width, double height) implements Shape {}

  switch (shape) {
    case Circle(var radius) -> handleCircle(radius);
    case Rectangle(var width, var height) -> handleRectangle(width, height);
    //  We don't need a default case here
  }
  ```

If you want, I can also:

- Add a short GitHub Actions workflow that runs `mvn -e verify` to surface the same checks on PRs.
- Create a small README in `scripts/` demonstrating how to run `check_java_files.py` locally with PowerShell examples.

Requirements coverage: Created and updated `.github/copilot-instructions.md` to document lint constraints, intents, and recommended fixes.
