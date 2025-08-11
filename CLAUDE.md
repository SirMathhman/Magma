# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Magma is a simple compiler that translates a basic programming language syntax to C code. Currently supports variable
declarations with type annotations, translating `let` statements from Magma syntax to C variable declarations.

## Build and Development Commands

### Essential Commands

- `mvn compile` - Compile the main source code
- `mvn test` - Run all tests
- `mvn clean` - Clean build artifacts
- `mvn validate` - Validate project configuration

### Running Specific Tests

- `mvn test -Dtest=CompilerTest` - Run only CompilerTest
- `mvn test -Dtest=CompilerTest#let` - Run specific test method

## Architecture

### Core Components

**Compiler (`src/main/java/magma/Compiler.java`)**

- Main compilation logic using regex pattern matching
- `LET_PATTERN` regex captures variable declarations with optional type annotations
- `TYPE_MAPPING` static map converts Magma types to C types
- `compile(String input)` method is the primary API

**Supported Type System**

- Magma signed types: I8, I16, I32, I64 → C types: int8_t, int16_t, int32_t, int64_t
- Magma unsigned types: U8, U16, U32, U64 → C types: uint8_t, uint16_t, uint32_t, uint64_t
- Default type when unspecified: int32_t

**CompileException (`src/main/java/magma/CompileException.java`)**

- Custom exception for compilation errors
- Thrown for invalid input or unsupported syntax

### Current Language Support

The compiler currently handles:

- Variable declarations: `let variableName = value;`
- Typed declarations: `let variableName : Type = value;`
- Empty input (returns empty output)

Examples:

- `let x = 10;` → `int32_t x = 10;`
- `let y : U32 = 255;` → `uint32_t y = 255;`

### Test Architecture

Tests use JUnit 5 with a helper method pattern:

- `assertValid(input, expectedOutput)` - Tests successful compilation
- `assertThrows()` - Tests error conditions
- Tests cover type variations, different variable names, values, and error cases

## Key Implementation Details

- Uses regex pattern matching for parsing (not a full parser)
- Single-pass compilation from string input to string output
- Static type mapping for efficient type translation
- Stateless compiler design - each `compile()` call is independent