# Magma

Magma is an experimental programming language with an implementation in Java.

Further documentation about building or testing the project will be added in the future.
Magma is an experimental compiler that reads a subset of Java and emits TypeScript code. The current proof of concept focuses on translating `src/magma/Main.java` into `src/magma/Main.ts`.

## Building

The repository provides a convenience script `build.sh` that compiles the Java
sources and regenerates the TypeScript output. Simply run:

```bash
./build.sh
```

The script performs the same steps that were previously described manually using `javac` and `java -cp src magma.Main`.

## Testing

Basic JUnit tests can be executed with `test.sh`:

```bash
./test.sh
```

In the GitHub Actions workflow, building and testing run as two
independent jobs so failures in either stage are reported clearly.

## Status

This project is still in a very early stage and the generated TypeScript is primarily for demonstration purposes.

## Development Notes

Development generally follows a test-driven workflow. When adding new features or fixing bugs, a failing unit test is written first. The implementation is then updated until the test passes. For example, resolving type parameters within record fields was implemented by first adding `RecordTypeParamTest` and `TypeParamResolutionTest` to demonstrate the issue.
