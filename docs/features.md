## Features

- Parses Java sources to find classes, interfaces and records
- Detects inheritance and dependency relationships
- Generates a PlantUML diagram summarizing the relations
- Omits interface dependencies when an implementing class is referenced. For
  example `TypeScriptStubs` depends directly on `Some` and `None` instead of the
  `Option` interface
- Creates TypeScript stubs that match the Java hierarchy
- Provides helper scripts for building, running and testing
- Uses a small `Result` type for explicit error handling
- Handles generic type arguments when converting return values
- Preserves method type parameters on generated methods
- Preserves the `static` modifier on generated methods
- Preserves `extends` and `implements` on class declarations
- Includes an `npm` command to validate the TypeScript output
- Wraps file paths with `PathLike`/`JVMPath` so generated TypeScript does not
  reference `java.nio.file.Path`
