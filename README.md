# Magma Tools

This repository contains a small set of tools written in Java. The code scans
Java source files and extracts the class hierarchy and simple dependencies. From
that information it generates a PlantUML diagram and matching TypeScript stubs.
The project is an early experiment for a future Magma compiler pipeline.

## Features

- Extracts classes and interfaces from Java source files
- Generates PlantUML diagrams of inheritance and dependencies
- Produces TypeScript stubs mirroring the Java hierarchy
- Provides build, run and test helper scripts
- Includes a simple `Result` type for functional-style error handling
- Provides a minimal `Option` type (`Some`/`None`) instead of relying on
  `java.util.Optional` to keep the code platform agnostic
- Handles generic type arguments when generating TypeScript
- Preserves the `static` modifier on methods in the stubs
- Preserves `extends` and `implements` on class declarations
- Provides an `npm` command to type-check the generated stubs
- Abstracts `java.nio.file.Path` behind a `PathLike` interface so TypeScript
  declarations do not reference JDK classes


## Getting Started

The sources are located in `src/java`. A recent JDK (17 or newer) is required
to build the project. The helper scripts automatically match the installed JDK
version. A simple way to compile everything into the `out` directory is:

```bash
javac -d out $(find src/java -name '*.java')
```

The entry point of the program is `magma.Main`. After compiling you
can run:

```bash
java --enable-preview -cp out magma.Main
```

For convenience there are helper scripts at the repository root:

```bash
./build.sh   # compile the Java sources
./run.sh     # run the program (builds automatically if needed)
./test.sh    # execute the JUnit test suite
```

Running the program creates a `diagram.puml` file in the repository root and
generates `.ts` stubs under `src/node`. Primitive Java types are translated to
their TypeScript equivalents. Numeric primitives (`byte`, `short`, `int`, `long`,
`float`, `double`) become `number`, `boolean` stays `boolean` and `char` or
`String` are emitted as `string`.

## Repository Layout

- `src/java` – Java source code
- `test/java` – JUnit tests
- `docs/` – Documentation

Additional notes on regex usage, coding style and CI can be found in the `docs/`
directory.

Instance fields in the Java sources are always accessed using `this`. Java does
not require it, but TypeScript does, and using the same convention avoids an
extra lookup step when translating the compiler.

## Continuous Integration

The repository is built on every pull request using a GitHub Actions workflow.
It compiles the Java sources with the JDK available on the runner
(currently 21) and preview features enabled.
The workflow calls `build.sh` and `test.sh` to keep the CI steps in sync with
the local helper scripts.
Compilation of the generated TypeScript is not part of the pipeline. You can
manually verify the stubs by running `npm run check-ts` after generating them.
