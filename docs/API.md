# Magma API Documentation

This document provides an overview of the key classes and interfaces in the Magma self-hosted Java to C compiler.

## Table of Contents

1. [Core Components](#core-components)
2. [Node Hierarchy](#node-hierarchy)
3. [Functional Patterns](#functional-patterns)
4. [Utility Classes](#utility-classes)

## Core Components

### Main

The `Main` class is the entry point for the Magma Java to C compiler. It contains methods for reading Java source code, parsing it, compiling it to C, and writing the output.

**Key Methods:**

- `main(String[] args)`: Entry point for the application
- `readString(Path source)`: Reads a Java source file into a string
- `writeString(Path target, CharSequence output)`: Writes the generated C code to a file
- `compile(CharSequence input)`: Compiles Java code into C code
- `divide(CharSequence input, BiFunction<DivideState, Character, DivideState> folder)`: Divides input into segments for parsing

### DivideState

The `DivideState` class is a state machine for parsing and tokenizing Java source code.

**Key Methods:**

- `DivideState(CharSequence input)`: Creates a new state with the given input
- `pop()`: Retrieves the next character and advances the position
- `append(char c)`: Appends a character to the buffer
- `advance()`: Completes the current segment and clears the buffer
- `enter()`: Increases the nesting depth
- `exit()`: Decreases the nesting depth
- `isLevel()`: Checks if at the top level (depth 0)
- `isShallow()`: Checks if at the first level of nesting (depth 1)

## Node Hierarchy

### JavaParameter

The `JavaParameter` interface is the base for all code generation nodes in the system, representing elements of Java code that will be translated to C.

**Key Methods:**

- `generate()`: Generates the C string representation of the node

### JavaMethodHeader

The `JavaMethodHeader` interface extends `JavaParameter` and represents Java method headers that will be translated to C function declarations.

### CDefinition

The `CDefinition` class represents a C-style definition with optional type parameters, used for translating Java variables and parameters to C.

**Key Methods:**

- `CDefinition(Option<String> maybeTypeParameter, String type, String name)`: Creates a new definition
- `CDefinition(String type, String name)`: Creates a new definition without a type parameter
- `generate()`: Generates the C string representation of the definition

### JavaConstructor

The `JavaConstructor` class represents a Java constructor that will be translated to a C initialization function.

**Key Methods:**

- `generate()`: Generates the C string representation of the constructor

### Placeholder

The `Placeholder` class represents a placeholder or comment in generated C code.

**Key Methods:**

- `wrap(String input)`: Wraps the input in C-style comment delimiters
- `generate()`: Generates the string representation of the placeholder

## Functional Patterns

### Option

The `Option` interface represents an optional value: either Some value or None. Used throughout the compiler for handling optional results.

**Key Methods:**

- `map(Function<T, R> mapper)`: Transforms the value if present
- `orElse(T other)`: Returns the value if present, otherwise the default
- `ifPresent(Consumer<T> consumer)`: Executes the consumer if a value is present
- `isEmpty()`: Checks if this Option is empty (None)
- `or(Supplier<Option<T>> other)`: Returns this Option if Some, otherwise the alternative
- `orElseGet(Supplier<T> other)`: Returns the value if present, otherwise from the supplier
- `flatMap(Function<T, Option<R>> mapper)`: Transforms with a function returning an Option
- `stream()`: Converts to a Stream
- `toTuple(T other)`: Converts to a Tuple with a boolean and the value

### Some

The `Some` class is an implementation of `Option` that represents the presence of a value.

### None

The `None` class is an implementation of `Option` that represents the absence of a value.

### Result

The `Result` interface represents the result of an operation that might fail, used for error handling in the compiler.

**Key Methods:**

- `match(Function<T, R> whenOk, Function<X, R> whenErr)`: Pattern matches on this Result

### Ok

The `Ok` class is an implementation of `Result` that represents a successful operation.

### Err

The `Err` class is an implementation of `Result` that represents a failed operation.

## Utility Classes

### Tuple

The `Tuple` class is a generic container for two values of potentially different types.

**Key Methods:**

- `left()`: Returns the left value
- `right()`: Returns the right value

### Actual

The `@Actual` annotation marks methods that perform actual I/O operations or have side effects.

## Usage Examples

### Reading and Writing Files

```java
Path source = Paths.get("input.java");
Path target = Paths.get("output.c");

Main.readString(source).match(input -> {
    final var output = Main.compile(input);
    return Main.writeString(target, output);
}, Some::new).ifPresent(Throwable::printStackTrace);
```

### Creating and Using Nodes

```java
// Create a C-style definition for a Java int variable
JavaParameter param = new CDefinition("int", "count");
String code = param.generate();  // "int count"

// Create a placeholder/comment for C code
JavaParameter placeholder = new Placeholder("Translated from Java class MyClass");
String comment = placeholder.generate();  // "/*Translated from Java class MyClass*/"
```

### Using Option and Result

```java
// Option example
Option<String> maybeName = getUserName(userId);
String greeting = maybeName.map(name -> "Hello, " + name)
                          .orElse("Hello, guest");

// Result example - compiling a Java file to C
Result<String, IOException> result = compileJavaFile(javaFile);
String message = result.match(
    cCode -> "Successfully compiled to C: " + cCode.substring(0, 50) + "...",
    error -> "Error compiling Java to C: " + error.getMessage()
);
```

## Further Information

For more detailed information about each class and method, refer to the Javadoc comments in the source code. For a higher-level overview of the system and how to use it, see the [Developer Guide](DeveloperGuide.md) and the [README](../README.md).