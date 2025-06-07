## Features

- [x] Parses Java sources to find classes, interfaces and records
- [x] Treats records as classes and converts record components into fields with a
  generated constructor
- [x] Detects inheritance and dependency relationships
- [x] Generates a PlantUML diagram summarizing the relations with
  orthogonal line routing
- [x] Converts the PlantUML file to an image for convenience
- [x] Omits interface dependencies when an implementing class is referenced. For
  example `TypeScriptStubs` depends directly on `Some` and `None` instead of the
  `Option` interface
- [x] Creates TypeScript stubs that match the Java hierarchy
- [x] Provides helper scripts for building, running and testing
- [x] Uses a small `Result` type for explicit error handling
- [x] Handles generic type arguments when converting return values
- [x] Preserves method type parameters on generated methods
- [x] Preserves the `static` modifier on generated methods
- [x] Parses method bodies but only emits stubs in the generated TypeScript
- [x] Preserves `extends` and `implements` on class declarations
- [x] Includes an `npm` command to validate the TypeScript output
- [x] Wraps file paths with `PathLike`/`JVMPath` so generated TypeScript does not
  reference `java.nio.file.Path`

## Missing

- [ ] Full support for `instanceof` expressions in method bodies
- [ ] CI pipeline does not compile the generated TypeScript stubs
- [ ] Method body parser lacks constructs such as `switch` or `try/catch` blocks
