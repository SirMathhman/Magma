# Magma Project Instructions

## Project Overview

Magma is a self-hosting Java-to-C++ transpiler. The entire compiler (parser, AST, code generator, and runtime library definitions) is contained within the single file `src/main/java/magma/Main.java`, which reads its own source code and compiles it into `src/main/windows/magma/Main.cpp`.

**Critical constraint**: This is a **zero-dependency project** — the compiler has no external Maven dependencies and the generated C++ is platform-independent. Do not add dependencies without explicit approval.

## Architecture

- **Single-File Compiler**: All ~2750 lines of compiler logic live in `Main.java`. The `main()` method calls `run()`, which reads `src/main/java/magma/Main.java` and writes to `src/main/windows/magma/Main.cpp` and `target/classes/windows/magma/Main.cpp`.
- **Self-Hosting Pipeline**:
  1.  **Lexing/Parsing**: Custom ad-hoc parsing using `State`, `Folder`, and `divide` methods (no parser generator). Key methods: `foldStatement`, `EscapedFolder`, `ValueFolder`.
  2.  **AST Construction**: Parses Java into `JType`, `JExpression`, `JDeclaration` nodes.
  3.  **Transformation**: Converts Java AST to C++ AST (`CType`, `CExpression`, `CDeclaration`) via `transformType`, `transformExpression`, `transformInvocation`.
  4.  **Code Generation**: Calls `generate()` on C++ nodes. Output accumulates in instance fields (`structures`, `functions`, `globals`, `functionDeclarations`, `structureForwardDeclarations`).

## Key Conventions & Patterns

### Functional Programming

The codebase relies heavily on functional programming patterns, implementing its own functional primitives **instead of Java Streams** or standard collections.

- **`Option<T>`**: Used for nullable values (variants: `Some`, `None`). **Never use `null`** — CheckStyle enforces this (`config/checkstyle/checkstyle.xml` bans `LITERAL_NULL`).
- **`Result<T, X>`**: Used for operations that can fail (variants: `Ok`, `Err`). Example: `Path.readString()` returns `Result<String, IOError>`.
- **`Iter<T>`**: Custom iterator with lazy evaluation. Use `.map()`, `.filter()`, `.fold()`, `.collect()` (not Java Stream).
- **`List<T>`**: Immutable-style list interface. Factory: `Lists.empty()`, `Lists.of(...)`. Implemented by `@Actual` class `JavaList` (JVM only).

**Example pattern** (from `run()` method):

```java
switch (input) {
    case Err<String, IOError> v -> new Some<IOError>(v.error);
    case Ok<String, IOError> v -> { /* use v.value */ }
}
```

### AST Structure

- **Sealed Interfaces**: Define Algebraic Data Types (ADTs) for AST nodes.
  - Example: `sealed interface JType permits Identifier, JArrayType, JGenericType, JPrimitiveType, ...`
  - Example: `sealed interface CType permits Identifier, CPointerType, CPrimitiveType, CTemplateType, ...`
- **Records**: Immutable data holders for AST nodes.
  - Example: `record JDeclaration(List<String> annotations, List<String> typeParameters, Option<String> maybeBeforeType, JType type, String name, ...)`
  - Example: `record CStructure(List<String> typeParameters, String name, List<CDefinable> fields) implements CRootSegment`

### Transpilation Logic

- **Type Mapping** (`transformType` method):
  - Java `interface` → C++ `struct` with function pointers (vtable-like).
  - Java `record` → C++ `struct`.
  - Java `sealed interface` → C++ `struct` containing a `union` (data) and `enum` (tag).
  - Java primitives: `int`/`boolean` → `int`, `void` → `void`, `char` → `char`.
  - Java `String` (built-in `JRecursiveType`) → `char*`.
- **Memory Management**: Generated C++ uses `_ref` pointer conventions. Generated code is stack-based with manual memory control.

## Developer workflow (quick)

- **Java tool build (recommended)**: use the project's wrapper and a Java 24 JDK.

  - Local build: `./mvnw -DskipTests package` (Windows: `.\mvnw.cmd -DskipTests package`).
  - Run the compiler directly: `./mvnw exec:java` (or `java -cp target/classes magma.Main`).

- **Regenerate C++**: Run the `main` method in `src/main/java/magma/Main.java`.

  - This writes `src/main/windows/magma/Main.cpp` — **do not edit generated `Main.cpp` by hand**.
  - Also writes to `target/classes/windows/magma/Main.cpp` (Maven output).

- **Try compiling the generated C++**: Use CMake (recommended) or compiler directly:

  - **CMake (Windows)**: `.\build.ps1` (Release), `.\build.ps1 -BuildType Debug` (Debug), `.\build.ps1 -Run` (build & run).
  - **CMake (Unix)**: `./build.sh` (Release), `./build.sh Debug`, `./build.sh --run`.
  - **Direct compilation**: `clang++ -std=c++20 src/main/windows/magma/Main.cpp -O2 -o magma` (or `g++` or `cl /std:c++20`).
  - Note: generated C++ can be very large (~MB scale); compilation may require `/bigobj` (MSVC) or significant memory/time.

- **Testing & Validation**: the canonical check is `Main.java` successfully self-transpiles and the produced C++ compiles; there are **no automated unit tests** in the repo.

## Important files and hotspots (where to change behavior)

- `src/main/java/magma/Main.java` — single-file compiler (parser, AST, transformation, codegen). This is the only place you normally edit to change the transpiler.
- `src/main/windows/magma/Main.cpp` — generated C++. Overwritten by `Main.java::run()`.
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
