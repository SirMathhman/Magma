# Magma (minimal Maven project)

This is a minimal Maven project scaffolded to match the installed Java version on this machine (OpenJDK 24).

Files added:

- `pom.xml` - Maven project file targeting Java 24
- `src/main/java/magma/App.java` - Simple Hello World application
- `README.md` - This file

Additional files:

- `src/main/java/magma/Interpreter.java` - Empty placeholder class for future interpreter features

Interpreter API

- `src/main/java/magma/Interpreter.java` provides `interpret(String source, String input)` which evaluates small programs and returns a `Result<String, InterpretError>`.
- The interpreter supports:
    - integer literals (e.g. `"5"` -> `"5"`),
    - typed integer literals with suffixes like `U8`, `I32` (e.g. `"255U8"` -> `"255"`),
    - simple addition of two integers (with optional compatible typed suffixes), and
    - semicolon-separated sequences with `let` bindings. Example: `let x : U8 = 3U8; x` evaluates to `"3"`.

Tests include `src/test/java/magma/InterpreterLetBindingTest.java` which verifies both unannotated and annotated `let` bindings.

Build & run

If you have Maven installed, build with:

    mvn -DskipTests package

Then run the JAR from the `target` directory (artifactId-version.jar):

    java -jar target/magma-0.1.0-SNAPSHOT.jar

If Maven is not installed, you can compile and run directly with javac/java:

    javac -d out src/main/java/magma/App.java
    java -cp out magma.App

Code style and banned APIs

- The project enforces a small set of Checkstyle rules located in `config/checkstyle/checkstyle.xml`.
- Usage of `java.util.regex` (both imports and fully-qualified references such as `java.util.regex.Pattern`) is banned by Checkstyle. This is intentional: prefer the project's parser utilities or a designated regex wrapper library that centralizes pattern usage and avoids ad-hoc regex in business logic.

- New: Checkstyle now enforces a maximum method name length of 20 characters. Long method names (21+ chars) will fail the build. The rule is configured in `config/checkstyle/checkstyle.xml`.
 
- New: Checkstyle now enforces a maximum number of parameters per method or constructor (3 parameters max). Methods or constructors with more than 3 parameters will fail the Checkstyle step. This rule is configured in `config/checkstyle/checkstyle.xml` under the `ParameterNumber` module.
