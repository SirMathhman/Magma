# Magma (minimal Maven project)

This is a minimal Maven project scaffolded to match the installed Java version on this machine (OpenJDK 24).

Files added:

- `pom.xml` - Maven project file targeting Java 24
- `src/main/java/magma/App.java` - Simple Hello World application
- `README.md` - This file

Additional files:

- `src/main/java/magma/Interpreter.java` - Empty placeholder class for future interpreter features

Interpreter API

- `src/main/java/magma/Interpreter.java` now provides a method `interpret(String source, String input)` that will eventually interpret source code and return a result string. Currently it is a stub and always throws `InterpretException`.
- As of this change, `Interpreter.interpret` recognizes simple integer literal programs; e.g. `interpret("5", "")` now returns `Result.Ok("5")`.
- The interpreter also accepts typed integer suffixes. For example `interpret("5I32", "")` will return `Result.Ok("5")` (the suffix is ignored for now).
- `src/main/java/magma/InterpretException.java` defines `InterpretException` which carries both an error message and the offending source string. The exception message is formatted as `message + ": " + source` and you can retrieve the raw source via `getSource()`.

Build & run

If you have Maven installed, build with:

    mvn -DskipTests package

Then run the JAR from the `target` directory (artifactId-version.jar):

    java -jar target/magma-0.1.0-SNAPSHOT.jar

If Maven is not installed, you can compile and run directly with javac/java:

    javac -d out src/main/java/magma/App.java
    java -cp out magma.App
