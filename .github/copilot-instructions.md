# Magma - Java-to-C++ Transpiler

## Project Overview

Magma is a **Java-to-C++ transpiler** that reads Java source code and generates equivalent C++ code. The entire transpiler is implemented as a single monolithic Java file (`App.java`, ~2800 lines) that compiles itself into C++ (`App.cpp`).

**Key Concept**: This project transpiles Java code to C++ while using a self-hosted approach - the transpiler itself is written in Java and can be transpiled to C++.

## Architecture

### Self-Contained Design

- **Everything in one file**: All classes, interfaces, records, and enums are nested within `App.java`
- **No external dependencies**: Only uses JDK standard library (`java.io`, `java.nio.file`, `java.util`)
- **Functional-style programming**: Heavy use of streams, options, and immutable data structures implemented from scratch

### Core Components

1. **Custom Collections Framework** (lines 1-800)

   - `Option<T>` (Some/None) - null-safe optional values
   - `Stream<T>` with `Head<T>` interface - lazy stream processing
   - `ArrayList<T>` - wrapper around `java.util.ArrayList`
   - `HashMap<K,V>` - wrapper around `java.util.HashMap`
   - Custom collectors: `ListCollector`, `MapCollector`, `Joiner`

2. **C Type System** (lines 400-700)

   - `CType` - sealed interface for C++ types
   - Implementations: `CPrimitiveType`, `CIdentifier`, `CPointerType`, `CTemplateType`, `CStructureType`, `CFunctionType`
   - All types implement `replaceIdentifiersWithMapping()` for template instantiation

3. **AST Nodes** (lines 700-1100)

   - `CExpression` - sealed interface for expressions (`CIdentifier`, `CFieldAccess`, `CInvocation`, `CReference`, etc.)
   - `CStructureMember` - class members (fields, methods)
   - `CDefinition` - variable/parameter declarations with type information
   - `CStructureHeader` - struct/class headers with type parameters

4. **Compilation State** (lines 1100-1400)

   - `Frames` - manages nested scopes during compilation
   - `Frame` - single scope with definitions and structure types
   - Tracks: type parameters, variable definitions, structure types

5. **Parser & Compiler** (lines 1400-2800)
   - String-based parsing using `divide()` and `State` machine
   - Methods like `parseStructure()`, `compileExpression()`, `compileMethodSegment()`
   - Generates: forward declarations, structures, sealed structures (sum types), globals, functions

## Build & Run Workflow

### Compilation Chain

```bash
# Compile Java code
mvn compile

# Run Java transpiler (generates App.cpp)
java -cp target/classes magma.App

# Compile generated C++ (requires clang)
clang src/main/java/magma/App.cpp -o main.exe
```

The transpiler:

1. Reads `src/main/java/magma/App.java`
2. Parses Java syntax using custom string-based parser
3. Generates `src/main/java/magma/App.cpp`
4. Optionally compiles C++ output using clang

### Testing

- Tests located in `target/surefire-reports/`
- Run with: `mvn test`
- 3 tests in `MagmaLibraryTest`

## Code Patterns & Conventions

### Sealed Types → C++ Tagged Unions

Java sealed interfaces become C++ tagged unions:

```java
sealed interface Option<T> permits None, Some
```

Generates:

```cpp
enum OptionTag { NoneType, SomeType };
union OptionData<T> { None<T> none; Some<T> some; };
struct Option<T> { OptionTag tag; OptionData<T> data; };
```

### Records → C++ Structs

Java records become C++ structs with constructors:

```java
record CPointerType(CType type) implements CType
```

Generates:

```cpp
struct CPointerType { CType type; };
CPointerType new_CPointerType(CType type) { ... }
```

### Methods → Free Functions

Instance methods become free functions with `void* _ref` as first parameter:

```java
public String generate() { return this.value; }
```

Becomes:

```cpp
char* generate_CIdentifier(void* _ref) {
    CIdentifier _this = *((CIdentifier*) _ref);
    return _this.value;
}
```

### Type Inference

Variables declared as `var` trigger type inference using `resolveExpression()`:

```java
var result = parseExpression(input);
```

The transpiler infers the type by analyzing the RHS expression.

### String Parsing Pattern

The `divide()` method splits input using a `State` machine that tracks:

- Current depth (for nested braces/parens)
- Buffer for current segment
- Escape sequence handling for strings and chars

## Critical Gotchas

1. **No Standard Library**: All collections (`Option`, `Stream`, `ArrayList`) are custom implementations - don't assume Java standard library semantics

2. **Everything is Nested**: All types are nested within `App` class - fully qualified names get very long

3. **Placeholder Types**: When type resolution fails, `Placeholder` types are generated with wrapped comments: `/*placeholder text*/`

4. **Manual Type Tracking**: The `Frames` stack manually tracks all variable definitions and type parameters - similar to a symbol table

5. **Template Instantiation**: C++ templates are instantiated by replacing type parameters using `replaceIdentifiersWithMapping()`

6. **No AST Classes**: Parser directly generates C++ code strings - no intermediate AST representation

## Development Tips

- **Search for patterns**: Use `grep_search` to find examples of similar transpilation cases
- **Follow the pipeline**: Input → `divide()` → `compile*()` → String output
- **Check `Frames`**: Always ensure variable definitions are registered in frames before resolving
- **Lambda numbering**: Lambdas are numbered sequentially (`_lambda1_`, `_lambda2_`) using the `counter` field
- **Type resolution**: Use `resolveExpression()` to determine the C++ type of any Java expression

## File Organization

```
src/main/java/magma/
├── App.java      # Entire transpiler (2800+ lines)
├── App.cpp       # Generated C++ output
├── Native.h      # C++ helper declarations
└── Native.cpp    # C++ helper implementations
```

## Common Tasks

**Add new Java construct support**: Find the relevant `compile*()` or `parse*()` method, add pattern matching, generate corresponding C++ string

**Debug type resolution**: Check `Frames.resolve()`, `Frames.findStructure()`, and `resolveExpression()`

**Fix C++ generation**: Look at the final string concatenation in methods like `generateMethod()`, `generateHeaderWithParameters()`

**Handle new operator**: Add to `parseExpression()` or `compileOperator()` chain
