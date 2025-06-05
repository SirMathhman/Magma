## Building

Compile the sources using the provided helper script. The script automatically
compiles all `*.java` files under the `src` directory:

```bash
./build.sh
```

The script simply compiles the Java sources into the `out` directory. After
building you can manually run `Main` to create the TypeScript stubs
and update `diagram.puml`. If you modify the diagram manually, run
`./render-diagram.sh` to regenerate the image. If you want to check that the
generated TypeScript files compile, execute:

```bash
npm run check-ts
```

This utility is optional and not part of the automated CI pipeline.
