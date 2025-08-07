# Magma: A Magma to C Compiler

## Project Overview

Magma is a compiler that translates Magma code to C code. It is designed to be a self-hosted compiler that follows
test-driven design and Kent Beck's rules of simple design.

## Project Structure

The project follows a Maven-like directory structure:

```
Magma/
├── build.bat                 # Build script for Windows
├── lib/                      # External libraries
│   └── junit-platform-console-standalone-1.9.2.jar  # JUnit for testing
├── src/                      # Source code
│   ├── main/                 # Main source code
│   │   └── java/             # Java source files
│   │       ├── Main.java     # Main compiler class
│   │       └── TypeMapper.java # Type mapping enum
│   └── test/                 # Test code
│       └── java/             # Java test files
│           ├── MainTest.java # Tests for the Main class
│           ├── ArrayTest.java # Tests for array handling
│           ├── TypeInferenceTest.java # Tests for type inference
│           ├── CharTypeTest.java # Tests for char type support
│           ├── StringTest.java # Tests for string handling
│           └── ErrorHandlingTest.java # Tests for error handling
└── README.md                 # This file
```

## Development Approach

This project follows Test-Driven Development (TDD) principles and Kent Beck's rules of simple design:

1. Create a failing test
2. Implement the failing test without hardcoding values
3. Remove semantic duplicates
4. Adjust method names and refactor, following these guidelines:
    - No more than one loop per function
    - No more than two levels of nesting per function
    - Extract a record when multiple functions pass around the same "group" of parameters
5. Adjust documentation

## Current Features

The compiler currently supports:

1. **Hello World Programs**: Compiles a simple Magma Hello World program to C.
2. **Arrays**: Supports array declarations with the syntax `let myArray : [Type; Size] = [val1, val2, ...];`
3. **Integer Types**: Supports various integer types:
    - Signed integers: I8, I16, I32, I64 (mapped to int8_t, int16_t, int32_t, int64_t in C)
    - Unsigned integers: U8, U16, U32, U64 (mapped to uint8_t, uint16_t, uint32_t, uint64_t in C)
4. **Boolean Type**: Supports Bool type with true and false values (mapped to bool in C)
5. **Char Type**: Supports Char type for character literals in single quotes (mapped to uint8_t in C)
6. **Comparison Operators**: Supports basic comparison operators (==, !=, <, >, <=, >=) that return boolean values

Example of variable declarations:

```
let a : I8 = -8;     // Signed 8-bit integer
let b : U32 = 32;    // Unsigned 32-bit integer
let c = 42;          // Type omitted, defaults to I32 (int32_t)
let d : Bool = true; // Boolean type
let e = false;       // Type omitted, inferred as Bool
let f : Char = 'a';  // Character type
let g = 'b';         // Type omitted, inferred as Char
```

Example of comparison operators:

```
let x = 10;
let y = 20;
let result1 = x == y;  // Equal to (false)
let result2 = x != y;  // Not equal to (true)
let result3 = x < y;   // Less than (true)
let result4 = x > y;   // Greater than (false)
let result5 = x <= y;  // Less than or equal to (true)
let result6 = x >= y;  // Greater than or equal to (false)
```

Example of array declarations:

```
let byteArray : [U8; 4] = [10, 20, 30, 40];    // Array of 4 unsigned 8-bit integers
let intArray : [I32; 2] = [100, 200];          // Array of 2 signed 32-bit integers
let boolArray : [Bool; 3] = [true, false, true]; // Array of 3 booleans
```

The compiler supports both explicit type declarations and typeless declarations. When the type is omitted:

- For numbers, it defaults to I32
- For boolean literals (true/false), it infers the Bool type
- For character literals in single quotes (e.g., 'a'), it infers the Char type

## How to Build and Run

To build and run the project, execute the `build.bat` script:

```
.\build.bat
```

This will:

1. Create necessary build directories
2. Download JUnit dependencies if needed
3. Compile the main Java source files
4. Compile the test Java source files
5. Run the tests
6. Run the compiled program

## Implementation Details

The compiler uses a pattern-matching approach to identify specific Magma code patterns and generate corresponding C
code. This is a simplified approach for educational purposes, and a real compiler would use more sophisticated parsing
and code generation techniques.

### Main Class

The `Main` class is the entry point of the compiler and provides the following functionality:

- `main(String[] args)`: Entry point for the command-line application
- `compile(String magmaCode)`: Compiles Magma code to C code
- Helper methods for detecting different Magma code patterns
- Helper methods for generating C code for different patterns

The compiler uses a `TypeMapper` record to map Magma types to C types, following Kent Beck's rules of simple design:

- Each method has a single responsibility
- No more than one loop per function
- No more than two levels of nesting per function
- Semantic duplication is eliminated by extracting common functionality

The `TypeMapper` record encapsulates:

- Magma type name (e.g., "I32")
- Corresponding C type (e.g., "int32_t")
- Type pattern for detection in source code

### Testing

The project includes several test classes designed to ensure robustness and comprehensive coverage:

- `MainTest`: Tests the basic functionality:
    - Hello World compilation
    - Variable declarations with different integer types (I8, I16, I32, I64, U8, U16, U32, U64)
    - Boolean type declarations
    - Typeless declarations with type inference

- `ArrayTest`: Tests the compilation of array declarations with comprehensive edge case coverage:
    - Simple array declarations (e.g., `let myArray : [U8; 3] = [1, 2, 3];`)
    - Arrays of different types and sizes (U8, I32, Bool)
    - Multi-dimensional arrays (2D and 3D)
    - Empty arrays (arrays with no elements)
    - Single element arrays
    - Large arrays (arrays with many elements)
    - Arrays with boundary values (min/max values for different types)
    - Mixed dimension arrays (1D, 2D, and 3D arrays in the same program)

- `StringTest`: Tests string handling with robust edge case coverage:
    - Basic string declarations as arrays of U8
    - Multiple string declarations
    - Empty strings
    - Strings with all common escape sequences (\n, \t, \r, \', \", \\)
    - Very long strings
    - Strings with special characters
    - Strings with mixed regular characters and escape sequences

- `CharTypeTest`: Tests char type support with comprehensive edge cases:
    - Explicit char type declarations (let x : U8 = 'a';)
    - Char type inference from literals (let x = 'a';)
    - All common escape sequences (\n, \t, \r, \', \", \\, \0)
    - Special characters (non-alphanumeric characters)
    - Numeric characters (digits)
    - Boundary values (lowest and highest printable ASCII characters)

- `TypeInferenceTest`: Tests type inference with robust edge case coverage:
    - Type inference from values with type suffixes (e.g., 100I8, 200U16)
    - Boundary values for different integer types (min/max values)
    - Boolean literals (true/false)
    - Character literals (values in single quotes)
    - Mixed type declarations in a single program

- `ErrorHandlingTest`: Tests error handling and robustness against invalid inputs:
    - Mismatched array sizes and initializers
    - Invalid type declarations
    - Invalid escape sequences in string literals
    - Out-of-range values for types
    - Malformed array declarations (e.g., negative array size)
    - Malformed multi-dimensional array declarations

These comprehensive tests ensure that the compiler is robust and handles edge cases correctly.

## Future Enhancements

Future versions of the compiler could include support for:

1. More complex Magma language features
2. Better error handling and reporting
3. Optimization of the generated C code
4. Support for Magma libraries and imports
5. A more sophisticated parsing approach using a proper parser generator

## License

This project is open source and available under the MIT License.