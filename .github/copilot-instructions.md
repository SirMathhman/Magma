# Magma Project Instructions

## Project Overview

Magma is a self-hosting Java-to-C++ transpiler. The core logic is contained within `src/main/java/magma/Main.java`, which reads its own source code and compiles it into `src/main/java/magma/Main.cpp`.

## Architecture

- **Single-File Compiler**: The entire compiler (parser, AST, code generator, and runtime library definitions) is primarily contained in `Main.java`.
- **Self-Hosting**: The project is designed to compile itself. The `run()` method in `Main.java` reads `Main.java` and outputs `Main.cpp`.
- **Pipeline**:
  1.  **Lexing/Parsing**: Custom ad-hoc parsing using `State`, `Folder`, and `divide` methods. It does not use a standard parser generator.
  2.  **AST Construction**: Parses Java code into internal Java AST nodes (`JType`, `JExpression`, `JDeclaration`).
  3.  **Transformation**: Converts Java AST nodes into C++ AST nodes (`CType`, `CExpression`, `CDeclaration`).
  4.  **Code Generation**: Calls `generate()` on C++ AST nodes to produce C++ source code.

## Key Conventions & Patterns

### Functional Programming

The codebase relies heavily on functional programming patterns, implementing its own functional primitives instead of using Java Streams exclusively.

- **`Option<T>`**: Used for nullable values (variants: `Some`, `None`).
- **`Result<T, X>`**: Used for operations that can fail (variants: `Ok`, `Err`).
- **`Iter<T>`**: A custom iterator implementation used for collections and lazy evaluation.
- **`List<T>`**: A custom immutable-style list interface (implemented by `JavaList` wrapping `java.util.ArrayList`).

### AST Structure

- **Sealed Interfaces**: Used extensively to define Algebraic Data Types (ADTs) for AST nodes.
  - Example: `sealed interface JType permits Identifier, JArrayType, ...`
- **Records**: Used for immutable data holding for AST nodes.
  - Example: `record JDeclaration(...)`

### Transpilation Logic

- **Mapping**:
  - Java `interface` -> C++ `struct` with function pointers (vtable-like).
  - Java `record` -> C++ `struct`.
  - Java `sealed interface` -> C++ `struct` containing a `union` (data) and `enum` (tag).
- **Memory Management**: The generated C++ code uses a specific style of memory management (often passing `_ref` pointers).

## Developer workflow (quick)

- **Java tool build (recommended)**: use the project's wrapper and a Java 24 JDK.

  - Local build: `./mvnw -DskipTests package` (Windows: `mvnw.cmd -DskipTests package`).
  - Run the compiler directly: `./mvnw -DskipTests exec:java` (or `java -cp target/classes magma.Main`).

- **Regenerate C++**: Run the `main` method in `src/main/java/magma/Main.java`.

  - This writes `src/main/java/magma/Main.cpp` and `target/classes/magma/Main.cpp` — do not edit generated `Main.cpp` by hand.

- **Try compiling the generated C++**: example (may need platform tweak):

  - `clang++ -std=c++20 src/main/java/magma/Main.cpp -O2 -o magma_generated` or
  - `g++ -std=c++20 src/main/java/magma/Main.cpp -O2 -o magma_generated`
  - Note: generated C++ can be large and platform-dependent; compilation may require additional flags.

- **Testing & Validation**: the canonical check is `Main.java` successfully self-transpiles and the produced C++ compiles; there are no automated unit tests in the repo.

## Important files and hotspots (where to change behavior)

- `src/main/java/magma/Main.java` — single-file compiler (parser, AST, transformation, codegen). This is the only place you normally edit to change the transpiler.
- `src/main/java/magma/Main.cpp` — generated C++. Overwritten by `Main.java::run()`.
- `pom.xml` — Maven build and `exec-maven-plugin` config; project uses Java 24 (`maven.compiler.release=24`).

Hotspots inside `Main.java` (examples of where to update when adding language features):

- Parsing and splitting: `divide`, `foldStatement`, `EscapedFolder`, `ValueFolder`.
- AST types and transforms: add new `J*` nodes, `C*` nodes and implement conversion via `transformType`, `transformExpression`, `transformInvocation`.
- Object/method handling: `parseObject`, `transformObject`, `completeMethodProto`, `computeMethodBody`.

## Coding guidelines (project-specific)

- Prefer the project's functional primitives (`Option`, `Result`, `Iter`, `List`) instead of `null` or raw Java collections in compiler core logic.
- Keep changes inside `Main.java` for transpiler logic — this file contains the full pipeline: parsing → AST (`J*` types) → C AST (`C*` types) → codegen.
- When adding features:
  - Add new `J*` and `C*` types (records/sealed interfaces) and a matching `generate()` or `toCType()` where appropriate.
  - Update transformation helpers: `transformType`, `transformExpression`, `transformInvocation`, `transformMethodDeclaration`.
  - Update `compileRootSegment`/`compileStatements` if you add new top-level constructs.
- Do not add runtime or core libraries that rely on JVM-only classes (e.g., `java.io.*`, `java.util.*`) into core AST nodes — use `@Actual` to provide JVM-only helpers (these are omitted or stubbed in C++ output).
- Avoid adding Maven/Gradle dependencies without explicit approval — the project is intentionally zero-dependency to keep the generated C++ independent.

### Platform-specific notes and `@Actual`

- The transpiled code is intended to be platform-independent C++; any Java-only helpers should be marked `@Actual`.
  - `@Actual` classes are used during Java bootstrap (e.g., `JavaList`, `JavaPath`) but are omitted from the C++ generator output.
  - `@Actual` methods are emitted as prototypes only in the generated C++ (no body) — this is the pattern to provide native implementations later.

Key constraints:

- Compiler core logic should avoid depending on JVM-only libs; use `@Actual` for any build-time helpers.
- Make incremental, small changes and regenerate `Main.cpp` often to keep transformations visible and debuggable.
