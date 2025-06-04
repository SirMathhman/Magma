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
