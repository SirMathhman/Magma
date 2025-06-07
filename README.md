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
return types are converted to their TypeScript equivalents. Future
tests will drive the full implementation.

## Documentation

Additional notes and a feature mapping between Java and TypeScript live in
[`docs/java-to-typescript-roadmap.md`](docs/java-to-typescript-roadmap.md).

### Building and Testing

The project intentionally avoids Maven for now. To compile the code and run the
JUnit tests you can use the JUnit Console launcher. The following commands place
compiled classes in a `bin` directory:

```bash
curl -L -o junit-platform-console-standalone.jar \
  https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.1/junit-platform-console-standalone-1.10.1.jar

mkdir -p bin
find src/main/java src/test/java -name "*.java" \
  | xargs javac -cp junit-platform-console-standalone.jar -d bin

java -jar junit-platform-console-standalone.jar -cp bin --scan-classpath
```

After compiling, you can invoke the transpiler via the CLI:

```bash
java -cp bin com.example.Main path/to/Source.java
```

