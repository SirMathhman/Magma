# Magma

Magma is an experimental programming language with an implementation in Java.

Further documentation about building or testing the project will be added in the future.
Magma is an experimental compiler that reads Java sources under the `src/` directory and emits TypeScript under `src-web/` while preserving the package directory structure.
The current translation capabilities are summarised in [docs/features.md](docs/features.md).

## Building

The build requires Java 21 because the compiler uses preview language features.
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

The GitHub Actions workflow also uses Java 21. Building and testing run as two
independent jobs so failures in either stage are reported clearly.

We encourage contributors to practice **test-driven development**. New features
or bug fixes should begin with a failing unit test under the `test/` directory
that demonstrates the expected behaviour. Run the tests with `./test.sh`
before and after implementing your change and ensure they pass prior to
committing.

## Status

This project is still in a very early stage and the generated TypeScript is primarily for demonstration purposes.

## Development Notes

Development generally follows a test-driven workflow. When adding new features or fixing bugs, a failing unit test is written first. The implementation is then updated until the test passes. For example, resolving type parameters within record fields was implemented by first adding `RecordTypeParamTest` and `TypeParamResolutionTest` to demonstrate the issue.
All contributions should include appropriate tests and must pass `./test.sh` before submission.

Contributors should also strive for simple design. Kent Beck summarizes this approach with four rules:

- Pass all the tests.
- Remove duplication.
- Express the programmer's intent clearly.
- Keep the number of classes and methods small.
