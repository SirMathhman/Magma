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
Variable declarations in the generated TypeScript include spaces around the
colon so assignments read `let x : number`.

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

To check that the generated TypeScript at `src/main/node` still parses,
run the `validate-ts.sh` helper. It invokes `tsc` with `noEmit` so the
compiler only verifies the syntax without writing any output files.

```bash
./validate-ts.sh    # run TypeScript compiler in check mode
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
- `magma.path.PathLike` – abstracts file system operations such as `walk`,
  `readString`, `writeString`, and directory helpers without exposing
  `IOException`
- `magma.path.NioPath` – wraps `java.nio.file.Path` and implements these I/O
  helpers using the JDK
- `magma.list.ListLike` – minimal list abstraction using a custom `ListIter`
  that now supports `map`, `fold`, and `flatMap` operations. The `flatMap`
  helper accepts an iterator-returning function so callers stay independent of
  concrete list types.
- `magma.list.JdkList` – default implementation backed by `ArrayList`
- `magma.list.SetIter` – wraps a `java.util.Set` so callers can reuse iterator helpers
- `magma.app.MethodStubber` – replaces method bodies with `// TODO` stubs.
  Helpers now use a single scan so functions never contain more than one loop,
  and indentation levels stay at two or fewer. `var` declarations infer a
  TypeScript type from simple values, including constructor calls which return
  the constructed class name. When assigned to another method in the same class,
  the stub uses that method's return type. Method calls on newly created
  objects also reuse the target method's return type so `var x = new Foo().getValue();`
  becomes `let x : number = new Foo().getValue();`. Calls on variables typed
  with known interfaces such as `PathLike` infer their interface return types,
  so `var files = root.walk();` becomes `let files : Result<Set<PathLike>> = root.walk();`.
  More complex expressions still
  default to `unknown`.
  The generated `Main.ts` now demonstrates this, typing `srcRoot.walk()` as
  `Result<Set<PathLike>>` rather than `unknown`.
Arrow blocks passed as arguments are detected and their statements are parsed
so assignments inside the block become stubs before the closing `});`.
- `magma.app.FieldTranspiler` – converts Java fields into TypeScript
  properties while ignoring initializations
- `magma.app.ImportHelper` – rewrites package declarations and import lines
  and now inserts missing imports when classes are referenced without an
  explicit `import` statement
- `magma.app.ArrowHelper` – turns lambda expressions into arrow functions
- `magma.Actual` – marker annotation recognized by the transpiler to
  omit placeholder classes from the generated code
- Interface method signatures now keep parameter and return types so
  `PathLike resolve(String other);` becomes `resolve(other : string): PathLike`.
  - `magma.app.TypeMapper` – maps primitive, boxed, and generic types and preserves
    unknown identifiers
- `src/test/java` – growing suite of tests that now covers nested and chained
  invocations so the parsing logic remains small yet reliable
