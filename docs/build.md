## Building

Compile the sources using the provided helper script. The script automatically
compiles all `*.java` files under the `src` directory:

```bash
./build.sh
```

Running `build.sh` also generates matching TypeScript stubs under `src/node`
and writes a fresh `diagram.puml` by invoking `GenerateDiagram`.
