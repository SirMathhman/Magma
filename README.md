# Magma Compiler

## Build System

The Magma compiler project has transitioned to Maven as its build system. Maven provides dependency management, standardized build lifecycle, and support for code quality tools like CheckStyle.

### Prerequisites

To build and test the project, you need:

- Java Development Kit (JDK) 11 or later
- Apache Maven 3.6.0 or later

### Installing Maven

1. Download Maven from [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi)
2. Extract the archive to a directory of your choice
3. Add the `bin` directory to your system PATH
4. Verify installation by running `mvn -version` in a command prompt

### Building the Project

To build the project:

```
build-maven.bat
```

Or directly with Maven:

```
mvn compile
```

### Running Tests

To run the tests:

```
test-maven.bat
```

Or directly with Maven:

```
mvn test
```

### Running CheckStyle

To check code style compliance:

```
checkstyle-maven.bat
```

Or directly with Maven:

```
mvn checkstyle:check
```

The project uses Google's Java style guide for CheckStyle rules.

### Legacy Build System

The legacy build system (using `build.bat` and `test.bat`) is still available but will be deprecated in future releases. It is recommended to migrate to the Maven-based build system.

## Project Structure

- `src/main/java`: Main source code
- `src/test/java`: Test source code
- `target/`: Maven build output (generated)

### Component Architecture

The compiler is designed using composition and separation of concerns:

- `Compiler`: Main entry point that coordinates the compilation process
- `TypeMapper`: Handles mapping between TypeScript/JavaScript types and C types
- `ValueProcessor`: Processes value expressions and references
- `DeclarationProcessor`: Processes variable declarations with different syntax forms
- `DeclarationContext`: Stores context information for declarations being processed
- `VariableDeclaration`: Represents a variable declaration with name and value
- `TypeCheckParams`: Parameters for type compatibility checks
- `TypeScriptAnnotationParams`: Parameters for processing TypeScript annotations
- `DeclarationConfig`: Configuration record for bundling dependencies
- `CompileException`: Exception thrown for compilation errors

This architecture follows good object-oriented design principles, keeping classes focused on single responsibilities and using composition instead of inheritance.

## Language Features

The Magma compiler supports the following language features:

### Variable Declarations and Types
- Variable declarations with `let` and `let mut` for mutable variables
- Type annotations (U8, U16, U32, U64, I8, I16, I32, I64, F32, F64, Bool)
- Type suffixes for numeric literals
- Arrays and 2D arrays
- Pointers and dereferencing

### Type Safety
- Type compatibility checking between variable declarations and values
- CompileException thrown for incompatible type assignments (e.g., `let x : I32 = 0U64;`)
- Informative error messages for type mismatch errors

### Operators
- Arithmetic operators (+, -, *, /, %)
- Comparison operators (==, !=, <, >, <=, >=)
- Logical operators (&&, ||, !)
- Ternary operator (condition ? true_value : false_value)

### Control Flow
- If statements with the following requirements:
  - Parentheses around the condition are required
  - Braces around the body are required
  - The condition must be a boolean expression (boolean literal, variable, comparison, or logical operation)

## Supported Types

Magma supports the following primitive types:

### Integer Types
- `I8`: 8-bit signed integer (maps to C's `int8_t`)
- `I16`: 16-bit signed integer (maps to C's `int16_t`)
- `I32`: 32-bit signed integer (maps to C's `int32_t`) - default integer type
- `I64`: 64-bit signed integer (maps to C's `int64_t`)
- `U8`: 8-bit unsigned integer (maps to C's `uint8_t`)
- `U16`: 16-bit unsigned integer (maps to C's `uint16_t`)
- `U32`: 32-bit unsigned integer (maps to C's `uint32_t`)
- `U64`: 64-bit unsigned integer (maps to C's `uint64_t`)

### Floating-Point Types
- `F32`: 32-bit floating-point (maps to C's `float`) - default for decimal literals
- `F64`: 64-bit floating-point (maps to C's `double`)

### Boolean Type
- `Bool`: Boolean type (maps to C's `bool`)

## Type Declaration Syntax

Magma supports two different ways to declare variables with specific types:

### Type Annotation
```
let x : I32 = 42;
let pi : F32 = 3.14159;
let temperature : F64 = 98.6F64;
```

### Type Suffix
```
let x = 42I32;
let pi = 3.14159F32;
let temperature = 98.6F64;
```

## Type Inference

Magma automatically infers types when no explicit type is provided:
- Decimal literals are inferred as `F32`
- Integer literals are inferred as `I32`
- Type inference for variable references uses the type of the referenced variable

## Mutable Variables

Variables can be declared as mutable using the `mut` keyword:
```
let mut count : I32 = 0;
count = count + 1;

let mut temperature = 98.6F64;
temperature = 99.1F64;
```