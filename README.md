# Java → TypeScript Transpiler Prototype

This repository begins a self‑hosted transpiler from Java to TypeScript. It keeps dependencies to a minimum and follows a test‑driven approach with a simple design. A key goal is to avoid relying on the Java standard library so that it can later be replaced with platform‑specific code.

## Main Classes

- `com.example.SelfReplicator` – copies the running class file.
- `com.example.Transpiler` – prototype Java → TypeScript converter.
- Tests mirror each class (`SelfReplicatorTest`, `TranspilerTest`).

The transpiler currently removes the `package` declaration since
TypeScript does not use Java-style packages.

To run the tests:

```bash
mvn test
```

To execute the program and copy the class file (the copy uses a `.ts` extension):

```bash
mvn package
java -cp target/self-replicator-1.0-SNAPSHOT.jar com.example.SelfReplicator <destination.ts>
```
