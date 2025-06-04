# Magma Tools

This repository contains a small set of tools written in Java. The code scans
Java source files and extracts the class hierarchy and simple dependencies. From
that information it generates a PlantUML diagram and matching TypeScript stubs.
The project is an early experiment for a future Magma compiler pipeline.

## Getting Started

The sources are located in `src/java`. A modern JDK (21 or newer) is required to
build the project. A simple way to compile everything into the `out` directory is:

```bash
javac -d out $(find src/java -name '*.java')
```

The entry point of the program is `magma.GenerateDiagram`. After compiling you
can run:

```bash
java --enable-preview -cp out magma.GenerateDiagram
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
It compiles the Java sources with JDK&nbsp;21 and preview features enabled.
The workflow calls `build.sh` and `test.sh` to keep the CI steps in sync with
the local helper scripts.
Compilation of the generated TypeScript is currently **skipped** because the
compiler's TypeScript pipeline is still under development.
