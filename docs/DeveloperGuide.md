# Magma Developer Guide

This guide provides detailed information for developers who want to use or contribute to the Magma code generation system.

## Table of Contents

1. [Introduction](#introduction)
2. [Architecture](#architecture)
3. [Core Components](#core-components)
4. [Code Generation Process](#code-generation-process)
5. [Error Handling](#error-handling)
6. [Extending Magma](#extending-magma)
7. [Best Practices](#best-practices)

## Introduction

Magma is a code generation and transpilation tool that can process input code and transform it into different output formats. It uses a functional programming approach with robust error handling patterns.

The system is designed to be flexible and extensible, allowing for various input formats and output targets. The core functionality is implemented in Java, with support for generating both Java and C code.

## Architecture

Magma follows a pipeline architecture for code generation:

1. **Input Reading**: Read input code from files or strings
2. **Parsing**: Divide input into segments and parse them
3. **Transformation**: Transform parsed segments into an intermediate representation
4. **Code Generation**: Generate output code from the intermediate representation
5. **Output Writing**: Write the generated code to files or return as strings

This pipeline is implemented in the `Main` class, which orchestrates the entire process.

## Core Components

### DivideState

The `DivideState` class is a state machine for parsing and tokenizing input text. It maintains:

- A buffer for accumulating characters
- A collection of segments (parsed chunks)
- The input text being processed
- A depth counter (for tracking nested structures)
- An index for the current position in the input

Example usage:

```java
DivideState state = new DivideState(input);
while (true) {
    final var popped = state.pop().toTuple(new Tuple<>(state, '\0'));
    if (!popped.left()) break;

    final var tuple = popped.right();
    state = foldDecorated(folder, tuple.left(), tuple.right());
}
return state.advance().stream().toList();
```

### Node Hierarchy

Magma uses a hierarchy of node classes to represent code elements:

- `JavaParameter`: Base interface for all nodes
- `JavaMethodHeader`: Interface for method headers
- `CDefinition`: Represents C-style definitions
- `JavaConstructor`: Represents Java constructors
- `Placeholder`: Represents comments or placeholders

Example usage:

```java
JavaParameter param = new CDefinition("int", "count");
String code = param.generate();  // "int count"

JavaParameter placeholder = new Placeholder("TODO: Implement this method");
String comment = placeholder.generate();  // "/*TODO: Implement this method*/"
```

### Functional Patterns

Magma uses functional programming patterns for error handling and optional values:

#### Option Pattern

The `Option` type represents an optional value: either `Some` value or `None`.

```java
Option<String> maybeName = getUserName(userId);
String greeting = maybeName.map(name -> "Hello, " + name)
                          .orElse("Hello, guest");
```

#### Result Pattern

The `Result` type represents the result of an operation that might fail.

```java
Result<Integer, String> divide(int a, int b) {
    if (b == 0) {
        return new Err<>("Division by zero");
    }
    return new Ok<>(a / b);
}

String result = divide(10, 2).match(
    value -> "Result: " + value,
    error -> "Error: " + error
);  // "Result: 5"
```

## Code Generation Process

The code generation process in Magma involves several steps:

1. **Divide**: Split the input into segments using the `divide` method
2. **Compile**: Process each segment using the appropriate compiler method
3. **Assemble**: Combine the compiled segments into the final output

The main entry point for this process is the `compile` method in the `Main` class:

```java
private static String compile(final CharSequence input) {
    return Main.compileStatements(input, Main::compileRootSegment);
}
```

Different types of code elements are compiled using specialized methods:

- `compileStructure`: Compiles class or interface definitions
- `compileField`: Compiles field definitions
- `compileMethod`: Compiles method definitions
- `compileValue`: Compiles expressions and values
- `compileConditional`: Compiles if/else statements

## Error Handling

Magma uses the `Option` and `Result` patterns for error handling instead of exceptions. This makes error handling more explicit and composable.

### Option Pattern

The `Option` type is used for values that might be absent:

```java
Option<String> findUser(String id) {
    User user = database.getUser(id);
    return user != null ? new Some<>(user.name()) : new None<>();
}

// Usage
findUser("123").ifPresent(name -> System.out.println("Found user: " + name));
```

### Result Pattern

The `Result` type is used for operations that might fail:

```java
Result<String, IOException> readFile(Path path) {
    try {
        return new Ok<>(Files.readString(path));
    } catch (IOException e) {
        return new Err<>(e);
    }
}

// Usage
readFile(path).match(
    content -> System.out.println("File content: " + content),
    error -> System.err.println("Error reading file: " + error.getMessage())
);
```

## Extending Magma

Magma can be extended in several ways:

### Adding New Node Types

To add a new node type:

1. Create a new class that implements `JavaParameter` or one of its subinterfaces
2. Implement the `generate` method to produce the appropriate code
3. Add support for parsing and compiling the new node type in the `Main` class

Example:

```java
public record JavaField(String type, String name, String initialValue) implements JavaParameter {
    @Override
    public String generate() {
        return this.type + " " + this.name + 
               (initialValue != null ? " = " + initialValue : "") + ";";
    }
}
```

### Adding New Compilation Rules

To add new compilation rules:

1. Add a new method in the `Main` class for compiling the specific construct
2. Update the appropriate fold method to recognize and handle the new construct
3. Add any necessary helper methods for parsing and transforming the construct

## Best Practices

When working with Magma, follow these best practices:

1. **Use functional patterns**: Embrace the `Option` and `Result` types for error handling
2. **Keep nodes immutable**: All node classes should be immutable (records or final classes)
3. **Separate concerns**: Keep parsing, transformation, and code generation separate
4. **Test thoroughly**: Write tests for each compilation rule and edge case
5. **Document your code**: Add Javadoc comments to all classes and methods

## Conclusion

This developer guide provides an overview of the Magma code generation system and how to work with it. For more detailed information, refer to the Javadoc comments in the source code and the README.md file.