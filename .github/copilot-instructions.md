# Linting & build constraints for the Magma project

Below are the concrete, machine-enforced constraints discovered in the repository's linting configuration. Use these when authoring or refactoring Java code for this project.

## Quick checklist

- [x] Create `.github/copilot-instructions.md` (this file).
- [x] Summarize Checkstyle rules and suppressions (`config/checkstyle/*`).
- [x] Summarize PMD rules and PMD plugin configuration (`config/pmd/*`, `pom.xml`).
- [x] Summarize custom Java-file-count check (`scripts/check_java_files.py` and exec plugin usage).
- [x] Note how violations fail the build and CI phases to watch.

## Environment & build behavior

- Java language target: Java 22 (maven.compiler.release=22). Tools are configured to parse modern Java (records, sealed types).
- CI/build tools that enforce rules:
  - `maven-checkstyle-plugin` (configured to fail on violations)
  - `maven-pmd-plugin` (executes `check` and `cpd-check` goals)
  - `exec-maven-plugin` invoking `scripts/check_java_files.py` during `validate` (non-zero exit fails the build)
- PMD and Checkstyle are pinned to versions that support Java 22 features (Checkstyle dependency 10.12.0; PMD dependencies v7.0.0 in the PMD plugin).
- Property: `<cpd.skip>false</cpd.skip>` — CPD is enabled by default unless overridden on the command line.

## Checkstyle constraints (config/checkstyle/checkstyle.xml)

- Suppressions file: `config/checkstyle/suppressions.xml` is applied via `SuppressionFilter`.
**Method count per class:**
  - `MethodCount` → `maxTotal = 10` (counts constructors by default) — classes with more than 10 methods will violate.
  - **Intent:** Keep the purpose of each class clear and focused, with good naming and separation of concerns. This helps avoid "god-classes" that try to do too much and become hard to maintain or understand.
  - **Recommended fix:** If a class exceeds the method limit, split it into smaller, well-named classes, each with a clear responsibility. For test classes, split into smaller test classes that each test a distinct feature or aspect of the program.
  - Illegal tokens enforced (via `IllegalToken`): the following tokens are flagged:
    - `LITERAL_NULL`: Banned to avoid NullPointerExceptions. **Recommended fix:** Use `Optional` or provide a safe default value instead of `null`.
  - `LITERAL_THROW`: Banned because throw causes challenges in determining control flow of errors. **Recommended fix:** Use a sealed interface `Result` with variants `Ok` and `Err` to represent success and error outcomes explicitly.
  - `LITERAL_CONTINUE`: Banned because `continue` keywords often challenge control flow within loops. **Intent:** Keep loops simple and predictable.
  - `LITERAL_BREAK`: Banned to fix control flow issues. **Recommended fix:** Extract the loop into a method and use a `return` statement instead.
  - `TYPECAST`: Casting is unsafe and can cause issues at runtime. **Recommended fix:** Use pattern matching, such as using `instanceof` and `switch`, instead of explicit casts.
- `LITERAL_VOID` is enforced by a separate `IllegalToken` rule so it can be selectively suppressed.
  - Illegal types:
    - `IllegalType` disallows use of `Object` and `java.lang.Object` as a type. **Intent:** Do not use generic `Object`, as this indicates abstraction issues. **Recommended fix:** Instead, make an interface (even a marker one) and implementations. The interface does not have to be sealed.
- Suppressions:
  - `config/checkstyle/suppressions.xml` suppresses the `IllegalToken` rule for test sources (regex matches `src/test/java/...`) — specifically to allow `void` usage in test code.
- Checkstyle plugin settings in `pom.xml`:
  - `configLocation` points to `config/checkstyle/checkstyle.xml`.
  - `includeTestSourceDirectory=true` (rules also apply to tests except where suppressed).
  - `failOnViolation=true` (violations will fail the build when Checkstyle runs).

## PMD constraints (config/pmd/ruleset.xml + plugin config)

- PMD ruleset: `config/pmd/ruleset.xml` contains at least one explicit rule:
  - `NoDeprecatedAnnotation` — XPath rule that flags usage of `@Deprecated` (message: "@Deprecated annotation is not allowed"). **Intent:** Deprecated code causes things to be outdated and should be removed or replaced.
- PMD plugin configuration in `pom.xml`:
  - `<targetJdk>22</targetJdk>` so PMD parses Java 22 syntax.
  - `<minimumTokens>30</minimumTokens>` (affects PMD's token-based rules/thresholds).
  - `<includeTests>true</includeTests>` — PMD also analyzes test sources.
  - PMD engine dependencies are pinned to `pmd-core:7.0.0` and `pmd-java:7.0.0` in the plugin's `<dependencies>`.
  - The PMD plugin runs in the `test` phase with `check` and `cpd-check` goals.
- Effect: any PMD rule violation reported by the plugin's `check` goal will cause the build to fail.

## Custom Java-file-count rule (scripts/check_java_files.py + exec plugin)

- Purpose: ensure no immediate directory under `src/main/java` (and the root `src/main/java` directory itself) contains more than N `.java` files. **Intent:** Prevent excessive classes from appearing in packages, and keep packages focused on a single intent. This enforces the Single Responsibility Principle (SRP) at the package level.
- Default configuration:
  - Base path: `src/main/java`.
  - Default limit: `10` `.java` files per directory.
  - Non-recursive: counts only files directly inside each immediate directory (does not recurse into subdirectories).
- CLI options the script supports:
  - `--limit` / `-l` to change the max allowed files.
  - `--path` / `-p` to change the base directory to check.
  - `--root-only` to check only the base directory and skip immediate subdirectories.
- How it fails CI:
  - The script prints a concise report and exits with status `1` when any directory exceeds the configured limit. The `exec-maven-plugin` runs this script during the `validate` phase; a non-zero exit will fail the Maven build.
- Suggestions produced by the script:
  - For offending directories, the script prints suggested PowerShell (`pwsh`) and POSIX commands to move files into a new subdirectory (e.g., `New-Item`, `Move-Item`, `mkdir -p`, `mv`). These are guidance only; CI still fails until resolved.
- Notes: the exec plugin configuration uses `python` as the executable; ensure `python` is available in PATH in CI.

## CPD / duplication checks

- CPD is invoked via the PMD plugin (`cpd-check` goal) and controlled by property `<cpd.skip>false</cpd.skip>` in `pom.xml`. **Intent:** Remove duplicate behavior and encourage architectural clarity and generalization, not just copy-paste removal. The low token threshold (`30`) is set so that the architecture is naturally revealed through refactoring and generalization.
- If you need to skip CPD in local runs, override via `-Dcpd.skip=true` on the Maven command line (CI may not set this).

## How violations affect the build and CI phases

- `scripts/check_java_files.py` runs in the `validate` phase (via `exec-maven-plugin`) — a non-zero exit code fails the build early.
- PMD `check` and `cpd-check` run in the `test` phase; PMD violations reported by the `check` goal fail the build.
- Checkstyle runs in the `test` phase and is configured with `failOnViolation=true`; Checkstyle violations will fail the build.
- Summary: failing any of these tools (script, Checkstyle, PMD/CPD) will cause `mvn` to exit non-zero in CI. **Intent:** The code is not considered "good" until it adheres to these constraints. This keeps the code maintainable. While this might seem pedantic for small codebases, as the codebase scales, these rules become increasingly more important.

## Quick authoring tips

- Keep classes under 11 methods (<=10 methods) or split/refactor into smaller classes.
- Avoid using `Object` / `java.lang.Object` as a declared type.
- Avoid `@Deprecated` on elements (the PMD XPath rule forbids it).
- Be cautious with null literals, throws, continue/break, and explicit casts — Checkstyle currently flags these token usages.
- Keep directories under `src/main/java` to 10 or fewer `.java` files (or run the script locally and refactor into subpackages/directories).
- Tests may allow `void` in places where production code would be flagged because of `suppressions.xml`.

## New: method parameter limit

- **Constraint:** No more than two parameters per method (Checkstyle `ParameterNumber`, `max = 2`).
- **Intent:** This is draconian by design: force extraction of helper classes, parameter objects, and move behavior into instance methods rather than sprawling static methods with many parameters. The goal is stronger adherence to SRP and clearer object design.
- **Recommended fix:** When a method needs more than two inputs, consider:
  - Extracting a small value object / parameter object that groups related parameters.
  - Turning related static helpers into instance methods on a focused class so state can be captured as fields.
  - Splitting the method into smaller responsibilities where appropriate.

Addendum: this limit is configurable via the Maven property `max.method.parameters` in `pom.xml`.

## Where these rules live (paths)

- `pom.xml` — plugin lifecycle and properties (Checkstyle, PMD, exec script, CPD property).
- `config/checkstyle/checkstyle.xml` — Checkstyle rules.
- `config/checkstyle/suppressions.xml` — Checkstyle suppressions (test-only for `IllegalToken`).
- `config/pmd/ruleset.xml` — PMD rules (e.g., ban `@Deprecated`).
- `scripts/check_java_files.py` — custom directory/file-count checks invoked during Maven `validate`.

## Verification & CI notes

- Ensure `python` is present in CI images (the exec plugin invokes `python scripts/check_java_files.py`).
- Running `mvn -e verify` locally should exercise Checkstyle and PMD as configured (tools run during `test` phase; the script earlier in `validate`).

---

If you want, I can also:

- Add a short GitHub Actions workflow that runs `mvn -e verify` to surface the same checks on PRs.
- Create a small README in `scripts/` demonstrating how to run `check_java_files.py` locally with PowerShell examples.

Requirements coverage: Created `.github/copilot-instructions.md` and documented the lint constraints discovered (Done).
