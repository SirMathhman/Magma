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

## Developer Workflow

- **Regenerate C++**: Run the `main` method in `src/main/java/magma/Main.java`.
  - This will overwrite `src/main/java/magma/Main.cpp`.
- **Testing**: Currently, the primary test is whether `Main.java` can successfully compile itself and if the resulting C++ code is valid.

## Important Files

- `src/main/java/magma/Main.java`: The source of truth. Contains the compiler and the runtime library definitions.
- `src/main/java/magma/Main.cpp`: The generated output. Do not edit this manually; it is a build artifact.

## Coding Guidelines for Agents

- **Preserve Functional Style**: When adding logic, use the existing `Option`, `Result`, and `Iter` primitives. Do not introduce null checks if `Option` can be used.
- **AST Modifications**: If adding new language features, ensure you add corresponding nodes to both `J*` (Java AST) and `C*` (C++ AST) hierarchies and implement the transformation logic.
- **No External Dependencies**: The project appears to be zero-dependency. Do not add Maven/Gradle dependencies without explicit instruction.

### Platform Independence & JDK Usage

- **Avoid JDK Dependencies**: Do not use standard JDK classes (e.g., `java.util.*`, `java.io.*`) in the core logic or AST nodes. The code must be transpiled to C++, which does not have the JVM standard library.
- **`@Actual` Annotation**:
  - Use `@Actual` to mark classes or methods that are required for the Java bootstrap environment but should be **omitted** or **stubbed** in the C++ output.
  - **Classes**: If a class is annotated with `@Actual` (e.g., `JavaList` wrapping `ArrayList`), it is skipped entirely during transpilation.
  - **Methods**: If a method is annotated with `@Actual`, only a C++ function declaration (prototype) is generated, with no body. This allows defining interfaces in Java that map to native C++ implementations.
  - **Usage**: Use this pattern to bridge Java-specific implementations (like file I/O or collections) that allow the compiler to run on the JVM, while keeping the transpiled code platform-independent.
