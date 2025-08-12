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

### Recommended Development Process
- Write a failing test for the desired feature or bug fix
- Execute tests to verify the test fails
- Implement the feature or fix so the test passes
- Run tests again to confirm success
- Refactor code, remove semantic duplicates, and perform renaming as needed
- Update documentation to reflect changes

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
