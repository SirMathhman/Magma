# Java → TypeScript Transpiler Prototype

This repository begins a self‑hosted transpiler from Java to TypeScript. It keeps dependencies to a minimum and follows a test‑driven approach with a simple design. A key goal is to avoid relying on the Java standard library so that it can later be replaced with platform‑specific code.

## Main Classes

- `com.example.Transpiler` – prototype Java → TypeScript converter
- `com.example.Main` – command line entry that runs the transpiler
- Tests mirror the transpiler (`TranspilerTest`) and CLI (`MainTest`).

The transpiler removes the `package` declaration since TypeScript does
not use Java-style packages. It also rewrites simple class definitions
so that Java modifiers like `public` become `export default`. Method
bodies are replaced with stubs in the generated TypeScript while
preserving each method's name and indentation. Each stub contains a
`// TODO` comment. Basic parameter and
return types are converted to their TypeScript equivalents. Array types
map directly as well, so `int[]` becomes `number[]` and `String[]`
becomes `string[]`. Future
tests will drive the full implementation.

## Documentation

Additional notes and a feature mapping between Java and TypeScript live in
[`docs/java-to-typescript-roadmap.md`](docs/java-to-typescript-roadmap.md).
The roadmap now lists the tests that verify each implemented feature.
Developer guidelines are summarized in
[`docs/coding-standards.md`](docs/coding-standards.md).

### Building and Testing

The project intentionally avoids Maven. Use the provided helper scripts to
compile and run the JUnit tests. The build script downloads the JUnit Console
launcher if needed and places compiled classes in a `bin` directory.

```bash
./build.sh  # compile sources
./test.sh   # execute all tests
```

After compiling, you can invoke the transpiler via the CLI:

```bash
java -cp bin com.example.Main path/to/Source.java
```

