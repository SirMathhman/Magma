# Java → TypeScript Transpiler Prototype

This project explores a minimal approach to translating small Java
programs into TypeScript.  The code is test driven and keeps the design
as simple as possible so it can eventually self host without relying on
the full Java standard library. Unknown type identifiers are emitted
unchanged so the generated TypeScript stays close to the original
sources.

File paths no longer rely directly on `java.nio.file.Path`. A simple
`PathLike` wrapper keeps the rest of the code independent of NIO. The
`NioPath` implementation now provides small helpers for reading and
writing files so other classes never touch `java.nio.file.Files`.

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

The files under `src/main/node` are generated output. They are tracked in
version control so we can review the transpiler's progress, but they should
never be edited directly.

## Key Classes

- `magma.Main` – simple CLI for the transpiler
- `magma.app.Transpiler` – converts Java code to TypeScript
- `magma.path.PathLike` – abstracts file system operations such as `walk`
- `magma.path.NioPath` – wraps `java.nio.file.Path` and handles basic I/O
- `magma.list.ListLike` – minimal list abstraction using a custom `ListIterator`
  that now supports `map`, `fold`, and `flatMap` operations. The `flatMap`
  helper accepts an iterator-returning function so callers stay independent of
  concrete list types.
- `magma.list.JdkList` – default implementation backed by `ArrayList`
