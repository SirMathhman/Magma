# Magma

This repository contains a simple Java program that generates a PlantUML file.

## Building

Compile the sources using the provided helper script. The script automatically
compiles all `*.java` files under the `src` directory:

```bash
./build.sh
```

## Usage

Run the program using the provided helper script:

```bash
./run.sh
```

Executing the program creates a file named `diagram.puml` in the same directory.
The file contains a PlantUML diagram describing the `GenerateDiagram` class.

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
5. **Each test should contain exactly one assertion.**

Following these principles helps keep the codebase easy to understand and
maintain. We also keep methods focused on a single task in line with the
Single Responsibility Principle (SRP).

## Continuous Integration

The GitHub Actions workflow builds the project using `build.sh`. Earlier
revisions compiled only `GenerateDiagram.java`, which caused errors like
`cannot find symbol` when `Result.java` was added. Running the helper script
ensures every source file is compiled and keeps the pipeline green.

## Error handling

This project avoids using checked exceptions for control flow. Instead, methods return `Optional` or the custom `Result` type to represent failures explicitly.
This makes error cases clear in the type system and keeps method signatures easy to read.

## Regex patterns

`GenerateDiagram` relies on several regular expressions to extract class names and relationships. These patterns are fairly dense, so a short overview is provided here:

* `^\s*(?:public\s+|protected\s+|private\s+)?(?:static\s+)?(?:final\s+)?(?:sealed\s+)?(?:class|interface)\s+(\w+)`
  - Matches a class or interface declaration at the start of a line. Optional modifiers such as `public` or `final` are allowed. The captured group is the declared name.
* `(?:class|interface)\s+(\w+)\s+extends\s+([\w\s,<>]+)`
  - Captures inheritance relationships. Group 1 is the child class or interface and group 2 contains the comma-separated parents.
* `class\s+(\w+)(?:\s+extends\s+\w+)?\s+implements\s+([\w\s,<>]+)`
  - Captures implemented interfaces. Group 1 holds the class name and group 2 lists the interfaces.

Before applying these patterns the program strips generic parameters such as `<T>` so that the regexes operate purely on class names.
