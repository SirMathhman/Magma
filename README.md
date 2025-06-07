# Java → TypeScript Transpiler Prototype

This project explores a minimal approach to translating small Java
programs into TypeScript.  The code is test driven and keeps the design
as simple as possible so it can eventually self host without relying on
the full Java standard library.

Full module descriptions live in
[`docs/architecture-overview.md`](docs/architecture-overview.md).  A
feature matrix and roadmap of the supported Java constructs can be found
in [`docs/java-to-typescript-roadmap.md`](docs/java-to-typescript-roadmap.md).
Coding conventions are summarized in
[`docs/coding-standards.md`](docs/coding-standards.md).

## Building and Testing

Use the provided scripts to compile and run the tests.  The build script
downloads JUnit if necessary and places compiled classes in `bin`.
On Windows, invoke the scripts through `bash` so the same commands work
across operating systems.

```bash
./build.sh          # compile sources
./test.sh           # execute all tests
# Windows users
bash build.sh       # compile sources
bash test.sh        # execute all tests
```

After building you can run the transpiler.  It reads Java sources under
`src/main/java` and writes TypeScript files to `src/main/node`:

```bash
java -cp bin magma.Main
```
