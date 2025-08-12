# Copilot Instructions for Magma

## Project Overview
- Magma is a custom programming language project implemented in Java.
- The main logic is in `src/main/java/com/example/`, with `Compiler.java` as the entry point for compilation and `CompileException.java` for error handling.
- Language specification is detailed in `language_specification.md`.

## Architecture & Data Flow
- The `Compiler` class exposes a `compile(String sourceCode)` method. It returns an empty string for empty/null input, otherwise throws a `CompileException`.
- All compilation errors are handled via the custom `CompileException` class.
- Tests are located in `src/test/java/com/example/CompilerTest.java` and use JUnit 5.

## Developer Workflows
- **Build:** Use Maven (`mvn clean install`) to build the project. The build is configured in `pom.xml`.
- **Test:** Run tests with `mvn test`. JUnit 5 is used for unit testing.
- **Lint:** Code style is enforced with Checkstyle (`mvn checkstyle:check`). Rules are in `checkstyle.xml`.
- **Debug:** Standard Java debugging applies. The main entry for logic is the `Compiler` class.

## Conventions & Patterns
- All source code is in `src/main/java/com/example/`. Tests mirror this structure in `src/test/java/com/example/`.
- Compilation logic is centralized in the `Compiler` class. Extend this class for new compilation features.
- Error handling is always done via `CompileException`.
- Strings are treated as empty if null or empty, following the language spec.
- Follow the language specification in `language_specification.md` for syntax and type rules.

## Integration Points
- No external service integrations; all logic is local Java.
- Maven dependencies are managed in `pom.xml`.
- Checkstyle and JUnit are the main external tools.

## Examples
- To add a new language feature, update `Compiler.java` and document in `language_specification.md`.
- To add a test, create a new method in `CompilerTest.java` using JUnit 5 assertions.

## Key Files
- `src/main/java/com/example/Compiler.java`: Main compiler logic
- `src/main/java/com/example/CompileException.java`: Error handling
- `src/test/java/com/example/CompilerTest.java`: Unit tests
- `language_specification.md`: Language rules/spec
- `pom.xml`: Build and dependency management
- `checkstyle.xml`: Code style rules

---
If any conventions or workflows are unclear, please provide feedback so this guide can be improved.
