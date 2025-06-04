## Usage

Run the program using the provided helper script:

```bash
./run.sh
```

`./run.sh` compiles the project if necessary and then executes
`magma.Main`.

Executing the program creates a file named `diagram.puml` in the repository
root. The file contains a PlantUML diagram describing the discovered classes and
their relationships.

### Path abstraction

The Java sources use a lightweight `PathLike` interface instead of
`java.nio.file.Path`. The wrapper (`JVMPath`) delegates to the JDK class but
keeps it out of the public API so the generated TypeScript declarations remain
valid. When you need a path object, call `PathLike.of(...)` rather than
`Path.of(...)`.
