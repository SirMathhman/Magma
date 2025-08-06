# Magma Compiler

A simple compiler implementation for the Magma programming language that compiles Magma code to C.

## Project Structure

The Magma compiler is organized into the following components:

- **Lexer**: Converts source code into tokens
- **Parser**: Converts tokens into an Abstract Syntax Tree (AST)
- **AST**: Represents the structure of the program
- **Semantic Analyzer**: (Placeholder for future implementation)
- **Code Generator**: (Placeholder for future implementation) Will generate C code from the AST

## Directory Structure

```
Magma/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── magma/
│                   └── compiler/
│                       ├── lexer/       # Lexical analysis
│                       ├── parser/      # Syntax analysis
│                       ├── ast/         # Abstract Syntax Tree
│                       ├── semantic/    # Semantic analysis (future)
│                       └── codegen/     # C code generation (future)
├── examples/           # Example Magma programs
└── README.md           # This file
```

## Components

### Lexer

The lexer (or tokenizer) is responsible for breaking the source code into tokens. It's implemented in the `com.magma.compiler.lexer` package:

- `Token.java`: Represents a token in the source code
- `TokenType.java`: Enum of all possible token types
- `Lexer.java`: Interface for lexical analyzers
- `MagmaLexer.java`: Implementation of the Lexer interface

### Parser

The parser is responsible for analyzing the syntax of the program and building an Abstract Syntax Tree (AST). It's implemented in the `com.magma.compiler.parser` package:

- `Parser.java`: Interface for parsers
- `MagmaParser.java`: Implementation of the Parser interface using recursive descent parsing

### AST

The Abstract Syntax Tree (AST) represents the structure of the program. It's implemented in the `com.magma.compiler.ast` package:

- `Expr.java`: Base class for expression nodes
- `Stmt.java`: Base class for statement nodes

### Main Class

The `Magma.java` class serves as the entry point for the compiler. It provides functionality to:

- Run the compiler on a file
- Run the compiler in interactive mode
- Process source code through the lexer and parser

## Language Features

The Magma language supports:

- Variable declarations
- Print statements
- If statements
- While loops
- Arithmetic expressions
- Boolean expressions
- Nested expressions

## Example

Here's a simple Magma program:

```
// Variable declaration
var greeting = "Hello, World!";
var count = 5;

// Print statements
print greeting;
print count;

// If statement
if (count > 3) {
    print "Count is greater than 3";
} else {
    print "Count is not greater than 3";
}

// While loop
var i = 0;
while (i < count) {
    print i;
    i = i + 1;
}
```

## Future Enhancements

This is a basic scaffold for a compiler. Future enhancements could include:

1. Semantic analysis
2. Intermediate code generation
3. Optimization
4. C code generation
5. Integration with C compiler toolchain
6. Function declarations and calls
7. Classes and objects
8. Error recovery
9. Type checking

## Usage

To run the compiler on a file:

```
java com.magma.compiler.Magma examples/hello.mg
```

To run the compiler in interactive mode:

```
java com.magma.compiler.Magma
```

## License

This project is open source and available under the MIT License.