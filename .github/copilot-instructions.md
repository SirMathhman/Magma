# Copilot instructions for Magma

Short, focused guidance for AI coding agents working in this repository.

- Big picture
  - Magma is a minimal Java (Maven) project that demonstrates a tiny execution API under package `magma`.
  - Core classes live in `src/main/java/magma`:
    - `Executor` — single static `execute(String)` method that returns a `Result<T,E>`.
    - `Result` — a sealed interface with two records: `Ok` and `Err` used to model success/error.
    - `ExecutionException` — a simple checked exception class (currently unused by `Executor`).
  - Tests are under `src/test/java/magma` (JUnit 5).

- What to change and why
  - Prefer small, focused edits that preserve the project's minimal API surface.
  - Respect the sealed `Result` type and existing tests' expectations when changing `Executor` behavior.
  - If adding new public API surface, update `pom.xml` or the manifest `mainClass` only when creating runnable jars.

- Build, test, and lint commands
  - Build and run tests: `mvn package` (or `mvn test`).
  - Run the jar produced: `java -jar target/magma-0.1.0-SNAPSHOT.jar`.
  - Checkstyle runs in the `test` phase and will fail the build on violations: `mvn test` or `mvn checkstyle:check -DskipTests=true`.
  - Note: project uses Java release 24 (see `pom.xml`), so ensure the agent targets that language level for code examples.

- Project-specific patterns and conventions
  - Use the project's `magma.Option<T>` sealed interface for simple null/empty checks, as shown in `Executor.execute`.
  - Tests use JUnit 5 and assert instance types (`instanceof Result.Ok`) and then cast to access record components.
  - Keep implementations concise and idiomatic for modern Java (records, sealed interfaces, `var`).

- Integration points and external dependencies
  - No external services. Only runtime dependency is the JDK and test dependencies (JUnit 5).
  - Checkstyle is configured in `config/checkstyle/checkstyle.xml` and is applied to tests; follow its rules when editing code.

- Examples (use these to guide edits)
  - To return an empty Ok for null/empty input (existing behavior):
    - Use the `Some<T>` and `None<T>` variants directly for presence/absence checks. For example, construct `new Some<>(value)` or `new None<>()`, and use pattern matching (`instanceof`) to inspect.
    - `if (valueIsMissing) return new Result.Ok<>("");`
  - To add a new `Result`-returning method, follow the same patterns (use `Result.Ok`/`Result.Err` records).

- Casting, pattern matching, and style
  - Do NOT use explicit casts (for example `(Circle) shape`) in new code. Casting is banned in this repository's guidance because pattern matching is safer and clearer.
  - Prefer Java pattern matching for `instanceof` and `switch` wherever applicable. Example using `instanceof` pattern matching:
    - ```
    if (shape instanceof Circle(var radius)) {
        return Math.PI * radius * radius;
    }
    ```
  - Example using pattern-matching `switch` expression:
    - ```
    return switch (shape) {
        case Circle(var radius) -> Math.PI * radius * radius;
        case Rectangle(var width, var height) -> width * height;
        case Triangle(var base, var height) -> 0.5 * base * height;
    };
    ```

- Files to reference for most changes
  - `pom.xml` — build, JDK level, plugin config (checkstyle, jar manifest)
  - `src/main/java/magma/Executor.java`
  - `src/main/java/magma/Result.java`
  - `src/test/java/magma/ExecutorTest.java`
  - `config/checkstyle/checkstyle.xml` — coding style rules

- When in doubt
  - Run `mvn test` locally to validate behavior and checkstyle.
  - Preserve existing tests' behavior unless the change intentionally updates tests and documentation.

  You MUST always run `mvn test` or some other variant at the end of your task! Do not use the test runner, because it will skip mandatory linting checks! You MUST resolve all linting errors.