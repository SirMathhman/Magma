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

Function declarations

The interpreter supports simple function declarations and calls. Example:

    fn first(a : I32, b : I32) : I32 => { return a; } first(100, 200)

This evaluates to "100". Note: parameter lists are comma-separated; nested commas in
arguments are not currently supported.

Return type is optional

You can omit the return-type annotation. Examples:

    fn get() => { return 100; } get()

This evaluates to "100".

Compact expression body

You may also use a compact expression body after the arrow without braces or the `return` keyword. This is allowed for single-expression bodies. Example:

    fn get() => 100; get()

This also evaluates to "100". Note: the compact form only accepts a single expression after `=>` (up to the following semicolon).

Code style and banned APIs

- The project enforces a small set of Checkstyle rules located in `config/checkstyle/checkstyle.xml`.
- Usage of `java.util.regex` (both imports and fully-qualified references such as `java.util.regex.Pattern`) is banned by Checkstyle. This is intentional: prefer the project's parser utilities or a designated regex wrapper library that centralizes pattern usage and avoids ad-hoc regex in business logic.

- New: Checkstyle now enforces a maximum method name length of 20 characters. Long method names (21+ chars) will fail the build. The rule is configured in `config/checkstyle/checkstyle.xml`.
 
- New: Checkstyle now enforces a maximum number of parameters per method or constructor (3 parameters max). Methods or constructors with more than 3 parameters will fail the Checkstyle step. This rule is configured in `config/checkstyle/checkstyle.xml` under the `ParameterNumber` module.

Runtime values (in-progress refactor)
-------------------------------------

- Existing interpreter logic represents values as strings; complex values use internal prefixes:
    - Arrays: `@ARR:elem1|elem2|...`
    - Structs: `@STR:Type|field1=val1|field2=val2|...`
    - References: `@REF:name` and `@REFMUT:name`
- A sealed `Value` hierarchy (`src/main/java/magma/Value.java`) has been introduced to model values with proper types (Int, Bool, Array, Ref, Struct, Unit).
- `ValueCodec` (`src/main/java/magma/ValueCodec.java`) converts between the string encoding and `Value`. This lets us migrate internal interpreter code incrementally without breaking public behavior or tests.
