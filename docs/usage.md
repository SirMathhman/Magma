## Usage

Run the program using the provided helper script:

```bash
./run.sh
```

`./run.sh` compiles the project if necessary and then executes
`magma.Main`. After execution it converts `diagram.puml` to `diagram.png`
using PlantUML.

Executing the program creates a file named `diagram.puml` in the repository
root. The file contains a PlantUML diagram describing the discovered classes and
their relationships. The diagram uses `skinparam linetype ortho` for clearer
routing.

### Path abstraction

The Java sources use a lightweight `PathLike` interface instead of
`java.nio.file.Path`. The wrapper (`JVMPath`) delegates to the JDK class but
keeps it out of the public API so the generated TypeScript declarations remain
valid. `PathLike` exposes convenience methods such as `writeString`,
`createDirectories` and `walk` so callers never interact with a raw
`java.nio.file.Path`. Errors are returned via `Option` or `Result` objects
instead of being thrown. When you need a path object, call `PathLike.of(...)`
rather than `Path.of(...)`.
`JVMPath` is intentionally small and forwards each call directly to the
underlying JDK path.
