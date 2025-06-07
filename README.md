# Magma

Magma is an experimental compiler that reads a subset of Java and emits TypeScript code. The current proof of concept focuses on translating `src/magma/Main.java` into `src/magma/Main.ts`.

## Building

Compile the Java sources with `javac`:

```bash
javac $(find src -name "*.java")
```

Run the compiler to regenerate the TypeScript output:

```bash
java -cp src magma.Main
```

## Status

This project is still in a very early stage and the generated TypeScript is primarily for demonstration purposes.
