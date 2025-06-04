# Magma

This repository contains a simple Java program that generates a PlantUML file.

## Usage

Run the program using the provided helper script:

```bash
./run.sh
```

Executing the program creates a file named `diagram.puml` in the same directory.
The file contains a minimal example of a PlantUML diagram.

## Running tests

The project follows a test-driven development approach using JUnit 5. A
`test.sh` script downloads the JUnit Platform console runner if necessary and
executes the tests:

```bash
./test.sh
```

## Coding guidelines

Code in this repository follows Kent Beck's rules of simple design. Every
change should ensure that:

1. **All tests run successfully.**
2. **There is no duplicated logic.**
3. **The code expresses the programmer's intent as clearly as possible.**
4. **Any code that doesn't support the above goals is removed.**

Following these principles helps keep the codebase easy to understand and
maintain.
