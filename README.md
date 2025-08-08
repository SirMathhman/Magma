# Magma magma.magma.Compiler

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

## Language Features

The Magma compiler supports the following language features:

### Variable Declarations and Types
- Variable declarations with `let` and `let mut` for mutable variables
- Type annotations (U8, U16, U32, U64, I8, I16, I32, I64, F32, F64, Bool)
- Type suffixes for numeric literals
- Arrays and 2D arrays
- Pointers and dereferencing

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