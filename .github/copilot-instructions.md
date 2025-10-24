# Magma Project - AI Coding Agent Instructions

## Project Overview

Magma is a **Java-to-C++ transpiler and functional library** written in Java 24. It implements functional programming abstractions (Option, Result, Stream, etc.) and transpiles itself from Java source code to C++ header/source files.

## Core Architecture

### Dual Implementation Pattern

- **Java source**: `src/main/java/magma/` - Functional library + transpiler
- **C++ target**: `src/main/windows/magma/` - Generated C++ code (DO NOT EDIT)
- Files in `src/main/windows/` are auto-generated with comment: `// File generated from '...' This is not source code!`

### Platform-Specific Code: `@Actual` Annotation

- Methods/classes marked `@Actual` have platform-specific implementations
- Java implementations in `JavaImpl.java` use standard Java libraries
- C++ implementations in `Actual.cpp` use platform APIs
- Example: `JIOError`, `JavaPath` are marked `@Actual` for platform I/O

### Transpilation Pipeline

1. `Main.main()` walks `src/main/java/` for `.java` files
2. Transpiles each to `.h` and `.cpp` in `src/main/windows/magma/`
3. Generates `build.bat` with clang commands
4. Executes build to create `magmac.exe`

## Key Components

### Functional Library Primitives

- `Option<T>` (sealed: `Some`, `None`) - Optional values with monadic operations
- `Result<T, X>` (sealed: `Ok`, `Err`) - Error handling without exceptions
- `Stream<T>` - Lazy functional streams (not Java stdlib streams)
- `ArrayList<T>` - Custom growable array (uses `MemUtils.malloc`)
- `ListMap<K, V>` - Map backed by ArrayList of tuples
- `Tuple<A, B>` - Immutable pairs in `Utils.java`

### Transpiler Implementation (`Main.java`)

- `compile()` - Main transpilation entry point
- `divide()` - String segmentation with depth tracking for nested structures
- `DivideState` - Stateful parser for Java syntax (tracks `{}/()/<>` depth)
- `ParseState` - Accumulates C++ output (includes, structs, functions, dependencies)
- Sealed interfaces (`Definable`, `CExpression`, `CRootSegment`) represent C++ AST nodes

### Build System

- **Maven**: Java compilation with JDK 24 (`mvn compile`, `mvn test`)
- **Build script**: Auto-generated `build.bat` uses clang to compile C++
- **No build.gradle**: Pure Maven project

## Development Workflows

### Testing Changes

```bash
# Test Java library
mvn test

# Full transpile and C++ build (Windows)
mvn compile
cd target/classes
java magma.Main
# Runs build.bat in src/main/windows/
```

### Adding New Library Classes

1. Create Java class in `src/main/java/magma/`
2. Use existing patterns: sealed interfaces for ADTs, custom collections
3. Mark platform-dependent code with `@Actual`
4. Run transpiler to generate C++ equivalents
5. Implement `@Actual` methods in `src/main/windows/magma/Actual.cpp`

### Debugging Transpiler

- `ParseState` tracks all compilation state (forward declarations, dependencies, etc.)
- `wrap()` creates C++ comments for unconverted Java code: `/*unconverted*/`
- Check generated `.h` files for struct forward declarations and dependency order
- `computeStructOrder()` resolves struct dependency graph to prevent C++ forward reference errors

## Critical Patterns

### Sealed Types Map to C++ Unions + Enums

```java
sealed interface Result<T, X> permits Ok, Err
```

Generates:

```cpp
enum ResultTag { OkType, ErrType };
union ResultData { Ok ok; Err err; };
struct Result { ResultTag tag; ResultData data; };
```

### Interface Methods → VTable Pattern

Interfaces generate separate VTable structs with function pointers:

```cpp
struct InterfaceVTable { /* function pointers */ };
struct Interface { void* data; InterfaceVTable vtable; };
```

### Stream Processing Uses Suppliers

`Stream<T>` wraps `Supplier<Option<T>>` for lazy head evaluation - not Java stdlib streams

### Memory Management

- `MemUtils.malloc()` for C++ allocation (defined in `MemUtils.java`)
- No automatic garbage collection in C++ output
- `Array<T>` wraps raw pointers with capacity tracking

## Testing Conventions

- Tests in `src/test/java/magma/` use JUnit 5
- `MagmaLibraryTest` verifies core classes are available
- Test custom collections (`ArrayList`, `ListMap`) not Java stdlib

## Common Pitfalls

- **Don't edit generated C++ files** - they're regenerated on every transpile
- **`@Actual` methods need dual implementation** - Java in `JavaImpl.java`, C++ in `Actual.cpp`
- **Use `magma.` collections**, not `java.util.*` when writing library code
- **Struct dependency order matters** - transpiler auto-resolves via `computeStructOrder()`
- **Sealed types require `permits` clause** - used to generate union variants

## Project-Specific Commands

```bash
# Compile Java library
mvn compile

# Run tests
mvn test

# Transpile and build C++ (from target/classes after mvn compile)
java magma.Main
```
