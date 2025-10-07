# Magma

A Java-to-C++ transpiler and serialization framework with advanced type safety and validation.

## Overview

Magma is a compiler project that translates Java source code into C++. It features a custom lexer/parser, a powerful serialization/deserialization system based on algebraic data types (ADTs), and comprehensive validation to ensure data integrity during transformation.

**Key Features:**

- Java 24 language support with modern pattern matching
- Functional programming patterns using `Result<T, E>` and `Option<T>` types
- Field consumption validation ensuring 1:1 correspondence between data and types
- Type-safe AST representation using sealed interfaces and records
- Comprehensive error reporting with contextual information

## Quick Start

### Prerequisites

- Java 24 or later
- Maven 3.8.9+

### Building

```bash
# Compile the project
mvn compile

# Run tests
mvn test

# Build without tests
mvn -DskipTests package
```

### Running the Transpiler

```bash
# Transpile all Java files in src/main/java to C++ in src/main/windows
mvn exec:java
```

The transpiler reads Java files from `src/main/java/magma/` and generates corresponding C++ files in `src/main/windows/magma/`.

## Project Structure

```
Magma/
├── src/main/java/magma/          # Main source code
│   ├── compile/                   # Compiler infrastructure (AST, lexer, parser, serialization)
│   │   ├── Lang.java             # AST node definitions (JRoot, CRoot, records, interfaces)
│   │   ├── JavaSerializer.java   # Serialization/deserialization engine
│   │   ├── Node.java             # Intermediate representation for parsed data
│   │   ├── rule/                 # Parsing rules and grammar definitions
│   │   ├── context/              # Error context types
│   │   └── error/                # Error types (CompileError, ApplicationError)
│   ├── option/                    # Option<T> monad (Some, None)
│   ├── result/                    # Result<T, E> monad (Ok, Err)
│   ├── list/                      # List types including NonEmptyList<T>
│   ├── transform/                 # Java → C++ transformation logic
│   ├── Compiler.java             # Main compilation orchestration
│   └── Main.java                 # Entry point for transpilation
├── src/test/java/                # JUnit 5 test suite
├── src/main/windows/             # Generated C++ output (auto-generated, do not edit)
├── docs/                         # Detailed feature documentation
├── config/checkstyle/            # Checkstyle configuration
└── pom.xml                       # Maven build configuration
```

## Architecture

### Core Components

#### 1. Parsing & Lexing (`magma.compile`)

The compiler uses a custom recursive descent parser with rules defined in `Lang.java`. The lexer produces a `Node` tree representing the parsed structure.

**Key files:**

- `Lang.java` — AST definitions, parsing rules (`JRoot()`, `CRoot()`, `Method()`, etc.)
- `Node.java` — Intermediate tree representation with string, node, and node list fields
- `rule/` — Parsing primitives (Tag, First, Suffix, String, etc.)

#### 2. Serialization/Deserialization (`JavaSerializer.java`)

A reflection-based system that maps between `Node` trees and Java records/sealed interfaces.

**Features:**

- Automatic field mapping using Java reflection and record components
- Support for `Option<T>`, `List<T>`, `NonEmptyList<T>`, and nested records
- Sealed interface polymorphism via `@Tag` annotations
- Field consumption validation (see [Field Validation Feature](FIELD_VALIDATION_FEATURE.md))

**Example:**

```java
@Tag("Person")
public record Person(String name, Option<String> email) {}

Node node = new Node().retype("Person")
    .withString("name", "Alice")
    .withString("email", "alice@example.com");

Result<Person, CompileError> result = JavaSerializer.deserialize(Person.class, node);
// → Ok(Person("Alice", Some("alice@example.com")))
```

#### 3. Result & Option Types (`magma.result`, `magma.option`)

Functional error handling without exceptions.

- `Result<T, E>` — Either `Ok<T, E>(value)` or `Err<T, E>(error)`
- `Option<T>` — Either `Some<T>(value)` or `None<T>()`

**Pattern Matching:**

```java
if (result instanceof Ok<String, CompileError>(String value)) {
    System.out.println("Success: " + value);
} else if (result instanceof Err<String, CompileError>(CompileError err)) {
    System.err.println("Error: " + err);
}
```

#### 4. AST Nodes (`Lang.java`)

Sealed interfaces define the structure:

- `JavaRootSegment` — Top-level Java elements (import, package, class, interface, record)
- `CRootSegment` — Top-level C++ elements (struct, function)
- `JStructureSegment` — Class/interface/record body elements (field, method, nested class)
- `JMethodSegment` / `CFunctionSegment` — Method/function body statements
- `JExpression` / `CExpression` — Expressions

All AST nodes are immutable records with `@Tag` annotations for serialization.

#### 5. Transformation (`Transformer.java`)

Converts Java AST (`JRoot`) to C++ AST (`CRoot`).

- Maps Java classes → C++ structs
- Maps Java methods → C++ functions
- Handles type conversions (e.g., `String` → `char*`)

## Key Features

### Field Consumption Validation

Ensures every field in a `Node` is consumed during deserialization. Prevents silent data loss.

**Example:**

```java
@Tag("Person")
public record Person(String name) {}

Node node = new Node().retype("Person")
    .withString("name", "Alice")
    .withString("phone", "555-1234");  // ← Leftover field!

// Fails with: "Incomplete deserialization for 'Person': leftover fields [phone]"
```

📖 **See:** [FIELD_VALIDATION_FEATURE.md](FIELD_VALIDATION_FEATURE.md)

### Type Mismatch Validation

Detects when `Option<String>` encounters a node or list instead of a string.

```java
// Field expects Option<String> but Node has a list → Error
```

📖 **See:** [docs/TYPE_MISMATCH_VALIDATION.md](docs/TYPE_MISMATCH_VALIDATION.md)

### NonEmptyList Semantic Tightening

`NonEmptyList<T>` eliminates the invalid state of "present but empty."

- `Option<NonEmptyList<T>>` — Either absent or has ≥1 elements
- Compiler-enforced guarantee

📖 **See:** [docs/NONEMPTYLIST_REFACTORING.md](docs/NONEMPTYLIST_REFACTORING.md)

### Unknown Tag Validation

Detects when a `@Tag` value in a `Node` doesn't match any permitted subclass of a sealed interface.

📖 **See:** [docs/UNKNOWN_TAG_VALIDATION.md](docs/UNKNOWN_TAG_VALIDATION.md)

## Testing

Tests use JUnit 5 with pattern matching on `Result` and `Option` types.

```bash
# Run all tests
mvn test

# Run a specific test
mvn -Dtest=ComprehensiveFieldValidationTest test
```

**Key test files:**

- `ComprehensiveFieldValidationTest.java` — Field consumption validation
- `TypeMismatchValidationTest.java` — Type mismatch detection
- `SimpleClassWithMethodTest.java` — End-to-end class parsing
- `SerializeRoundtripTest.java` — Serialization ↔ deserialization roundtrips

## Code Style

The project uses Checkstyle with custom rules:

- **One loop per method** — Extract nested loops into helper methods
- Consistent formatting and naming conventions

Configuration: `config/checkstyle/checkstyle.xml`

Run checkstyle:

```bash
mvn checkstyle:check
```

## Documentation

### Feature Documentation (`docs/`)

Detailed writeups of specific features, fixes, and refactorings:

- [FIELD_VALIDATION_FEATURE.md](FIELD_VALIDATION_FEATURE.md) — Field consumption validation
- [docs/TYPE_MISMATCH_VALIDATION.md](docs/TYPE_MISMATCH_VALIDATION.md) — Type mismatch detection
- [docs/NONEMPTYLIST_REFACTORING.md](docs/NONEMPTYLIST_REFACTORING.md) — NonEmptyList introduction
- [docs/PLACEHOLDER_STATEMENT_FIX.md](docs/PLACEHOLDER_STATEMENT_FIX.md) — Function body architecture
- [docs/RESULT_TYPE_ERROR_HANDLING.md](docs/RESULT_TYPE_ERROR_HANDLING.md) — Result type patterns

See [docs/INDEX.md](docs/INDEX.md) for a full list.

## Contributing

When making changes:

1. **Run tests:** `mvn test` after every change
2. **Preserve APIs:** Update all call sites when changing `Result`/`Option` semantics
3. **Add tests:** Use JUnit 5 with explicit `instanceof Ok<?, ?>` / `instanceof Err<?, ?>` assertions
4. **Document:** Update or create markdown documentation for substantive changes (see [AI Instructions](.github/copilot-instructions.md))

## Common Tasks

### Adding a new AST node type

1. Add a record in `Lang.java` with `@Tag("yourTag")`
2. Add to the appropriate sealed interface permit list
3. Update parsing rules (e.g., `JRoot()`, `Method()`)
4. Add serialization support in `JavaSerializer.java` if needed
5. Update `Transformer.java` for Java → C++ mapping
6. Add tests in `src/test/java/`

### Debugging deserialization issues

1. Check `Node.toString()` output to see the parsed structure
2. Verify `@Tag` annotations match the node's type field
3. Check for leftover fields (field consumption validation)
4. Look for type mismatches (string vs node vs list)

### Fixing build errors

```bash
# Clean build
mvn clean compile

# Verbose output
mvn compile -X
```

## License

(Add license information here)

## Contact

(Add contact/maintainer information here)
