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

The script performs the same steps that were previously described manually using
`javac` and `java -cp src magma.Main`.

## Status

This project is still in a very early stage and the generated TypeScript is primarily for demonstration purposes.
