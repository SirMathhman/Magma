## Quick orientation for AI coding agents working on Magma

This file contains targeted, actionable guidance so an AI assistant can be immediately productive in this repository. Keep answers concise, reference files, and follow the project's existing conventions.

What this project is

- Java library/project named `Magma` (root `pom.xml`).
- Primary code under `src/main/java/magma` (compiler/serialization utilities). Tests are under `src/test/java`.
- There are also generated/Windows C++ translations under `src/main/windows` and `target/production/Magma` — these are not the primary Java sources.

Big-picture architecture (how pieces fit together)

- `magma.compile` contains the compile-time AST and deserialization logic. Key files:
  - `magma/compile/Serialize.java` — central deserialization entrypoint used by tests.
  - `magma/compile/Node.java` and `Lang.java` — nodes and language parsing/lexing helpers.
  - `magma/compile/rule/*` — parsing/validation rules.
- `magma.option` and `magma.result` implement small functional types used across the codebase (`Option`, `Some`, `None`, `Result`, `Ok`, `Err`). Tests and code use pattern matching on these types.
- `FIELD_VALIDATION_FEATURE.md` documents a recent feature (field-consumption validation). Tests under `src/test/java` exercise that feature — they are a good source of expected behaviors.

Developer workflows (essential commands)

- Build and run tests (Maven):
  - mvn test
  - mvn -DskipTests package (build without tests)
- Run a single test class (useful during development):
  - mvn -Dtest=ComprehensiveFieldValidationTest test
- IDE: project uses Java 24 in `pom.xml` properties. Configure your IDE accordingly.

Project-specific conventions and patterns

- Result/Option types: code commonly uses pattern matching on records, e.g. `if (result instanceof Ok<T, E>(value)) { ... }` and `if (result instanceof Err<T, E>(err)) { ... }`. Preserve this style when modifying logic or tests.
- Tests historically were handwritten `main` programs that print status; recent migration adds JUnit 5 tests (see `src/test/java/*`). When writing tests, prefer JUnit 5 (`org.junit.jupiter.api`) and assertions like `assertTrue(result instanceof Ok<?, ?>, ...)` to match project style.
- Serialization/deserialization: use `Serialize.deserialize(TargetClass.class, node)` — it returns `Result<T, CompileError>`. Tests inspect Ok vs Err to assert behavior.

Integration points & external dependencies

- Maven dependencies and build are centralized in `pom.xml`. There are no external network calls in tests. Watch for the checkstyle plugin configuration (project uses `config/checkstyle/checkstyle.xml`).
- Parsing/lexing infra: `magma.compile.Lang` provides lexers/parsers used by tests (see `DeserializationDebugTest`). Be cautious: some inputs in tests imply stateful lexers—follow existing patterns.

Files to inspect for context when tackling a change

- `README.md` — project overview, quick start, and feature summary. Start here for project understanding.
- `docs/ARCHITECTURE.md` — comprehensive system architecture, components, and data flow. Read when understanding how pieces fit together.
- `docs/DEVELOPER_GUIDE.md` — practical how-to guide for common development tasks (adding nodes, debugging, testing).
- `docs/INDEX.md` — organized catalog of all feature documentation with reading guides by audience.
- `pom.xml` — Java version and test framework configuration (JUnit added). Update here to add dependencies or plugin tweaks.
- `FIELD_VALIDATION_FEATURE.md` — feature spec and expected behavior for field-consumption validation.
- `src/main/java/magma/compile/JavaSerializer.java` (formerly Serialize.java) — core behavior for deserialization; test failures often trace back here.
- `src/main/java/magma/result/` and `src/main/java/magma/option/` — small ADTs used pervasively; changing their APIs affects many call sites.
- `src/test/java/*` — canonical examples/tests for the library's expected behavior.

How to make safe changes

- Run `mvn test` after edits. Use `-Dtest=...` to limit runs.
- Preserve public APIs in `magma.*` packages unless you update all call sites and tests.
- If you need to change `Result`/`Option` semantics, update tests accordingly and add targeted unit tests in `src/test/java`.

Examples of common tasks and where to start

- **Getting oriented**: Read `README.md`, then `docs/ARCHITECTURE.md` for system understanding.
- **Adding a new AST node**: See step-by-step guide in `docs/DEVELOPER_GUIDE.md` → "Adding a New AST Node Type"
- **Fix failing deserialization**: reproduce failing test, open `JavaSerializer.deserialize` and the relevant `rule` classes that consume fields.
- **Debugging issues**: See `docs/DEVELOPER_GUIDE.md` → "Debugging Techniques" for practical tips.
- **Understanding validation**: Read `FIELD_VALIDATION_FEATURE.md`, then `docs/TYPE_MISMATCH_VALIDATION.md` and related docs.

Notes & gotchas

- Some earlier tests were written as standalone `main` methods and later migrated to JUnit; you may find both styles—prefer the JUnit tests.
- The project compiles against Java 24 — ensure runtime/IDE compatibility.

If you modify or add tests, run them locally and keep assertions explicit about Ok vs Err forms (use `instanceof Ok<?, ?>` / `instanceof Err<?, ?>`).

<!-- Mandatory: require creating/updating documentation -->

IMPORTANT: After any substantive code or behavioral change (new feature, public API change, bugfix that affects users or tests), the agent MUST create or update documentation. This can be:

- a new markdown file under `docs/` describing the change and rationale (follow format in existing docs),
- an update to `README.md` when public APIs or usage change,
- an update to `docs/ARCHITECTURE.md` when system structure changes,
- an update to `docs/DEVELOPER_GUIDE.md` when new development patterns are introduced,
- an entry in `docs/INDEX.md` categorizing the new documentation,
- an update to feature-specific docs (e.g., `FIELD_VALIDATION_FEATURE.md`), or
- an updated test or developer note in `src/test/java/` explaining the expected behavior.

The documentation change should include: what changed, why, and how to verify (commands to run and expected results). Keep the documentation concise (3–8 sentences per section) and add file references where appropriate. Follow the format in `docs/DOCUMENTATION_ORGANIZATION.md` for guidance on documentation standards.
