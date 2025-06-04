## Building

Compile the sources using the provided helper script. The script automatically
compiles all `*.java` files under the `src` directory:

```bash
./build.sh
```

The script simply compiles the Java sources into the `out` directory. After
building you can manually run `GenerateDiagram` to create the TypeScript stubs
and update `diagram.puml`.
