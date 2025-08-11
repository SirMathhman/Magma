# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Magma is a simple compiler that translates a basic programming language syntax to C code. Currently supports variable
declarations with type annotations, translating `let` statements from Magma syntax to C variable declarations.

Follow the provided process when implementing a feature:

- Write a failing test
- Implement the failing test
- Refactor the code to adhere to standards

For complex features, you might have to break down the problem and repeat this multiple times.
Furthermore, you might have to test at the right level of abstraction, instead of an end-to-end test.

For emergent design:

- High complexity -> Refactor smaller methods
- Too many methods / parameters -> Pull out classes. Don't be afraid to do this!
- Ensure that there are no more than 10 classes per package.
- Ensure that there is no more than one assertion per test.

### Essential Commands

- `mvn compile` - Compile the main source code
- `mvn test` - Run all tests
- `mvn clean` - Clean build artifacts
- `mvn validate` - Validate project configuration

### Running Specific Tests

- `mvn test -Dtest=CompilerTest` - Run only CompilerTest
- `mvn test -Dtest=CompilerTest#let` - Run specific test method