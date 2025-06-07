# Java → TypeScript Transpiler Prototype

This repository begins a self‑hosted transpiler from Java to TypeScript. It keeps dependencies to a minimum and follows a test‑driven approach with a simple design. A key goal is to avoid relying on the Java standard library so that it can later be replaced with platform‑specific code.

## Main Classes

- `magma.app.Transpiler` – prototype Java → TypeScript converter
- `magma.Main` – CLI that converts all sources under `src/main/java`
  to TypeScript files under `src/main/node`
- Helper classes split the converter into smaller pieces:
  - `ImportHelper` handles packages and imports
  - `MethodStubber` replaces method bodies with stubs. Its
    `parseValue` helper recursively processes expression values,
    including chains of method calls and fields. Return statements and
    variable assignments delegate to this helper so value handling lives
    in one place.
  - `FieldTranspiler` rewrites field declarations
  - `ArrowHelper` processes lambda expressions
  - `TypeMapper` maps Java types to TypeScript
- `magma.result.Result` – base interface with `Ok` and `Err` implementations
- `magma.option.Option` – base interface with `Some` and `None` variants
- `Main.run` now returns an `Option<String>` with an error message on failure
- Tests mirror the transpiler (`TranspilerClassTest`, `TranspilerMethodTest`,
  `TranspilerFieldTest`, `TranspilerStatementTest`) and CLI (`MainTest`).

The `parseValue` routine walks characters one at a time to split
function arguments and to recognize strings, numbers, member access,
and method calls. This avoids brittle regular expressions while still
handling nested parentheses and keeps the parsing logic centralized.

Abstract classes are intentionally avoided. The project prefers composition of
small classes over inheritance hierarchies. Functions are kept small: each
method contains at most one loop and braces never nest more than two levels.

The transpiler removes the `package` declaration since TypeScript does
not use Java-style packages. It also rewrites simple class definitions
so that Java modifiers like `public` become `export default`. Method
body text is replaced with stubs in the generated TypeScript while
preserving each method's name and indentation. Stubs insert one
`// TODO` comment for every statement in the original method. Return statements
retain the `return` keyword with `/* TODO */` as a placeholder value. Conditional blocks (`if` and `while`) parse their conditions using `parseValue` so that method calls or member access are stubbed consistently. Other expressions still become `/* TODO */`. Basic parameter and
return types are converted to their TypeScript equivalents. Array types
map directly as well, so `int[]` becomes `number[]` and `String[]`
becomes `string[]`. Future
tests will drive the full implementation.

Field declarations inside classes are converted to TypeScript property
syntax with the appropriate type mappings.
`final` fields become `readonly` properties in the generated TypeScript.
Field assignments are removed so initial values are not emitted.
Assignments inside arrow function bodies are replaced with `// TODO` comments.
Assignments that define new variables inside methods become `let` declarations
with `/* TODO */` as the initializer.
String literals remain intact if they begin and end with double quotes.
Numeric literals are now preserved as well, so `int n = 7;` becomes
`let n: number = 7;` and `return 42;` is emitted unchanged.
The `parseValue` helper also understands the logical not operator, so
`if (!flag)` is emitted exactly the same in TypeScript.
Invokable expressions such as `doThing()` or `new Some<>()` now keep the method
name intact. Arguments are parsed recursively so unknown values still emit
`/* TODO */`. Constructor calls keep the `new` keyword and the type name, so
`new Bar(1)` remains unchanged. Variable assignments like `int x = run();`
become `let x: number = run();` with the call preserved.
Calls on freshly constructed objects such as `new Main().run()` are now preserved intact, so the expression stays `new Main().run()`. This keeps initialization chains visible in the generated code.
Member access expressions like `parent.field` are preserved so assignments such as
`int x = parent.field;` become `let x: number = parent.field;`. Chains that mix method calls and fields, for example `doStuff().value.next`, keep both the property access and the method names intact.
Import statements are rewritten to relative paths that mirror the Java package
structure.

Generic type parameters are preserved, so `List<String>` becomes
`List<string>` in the transpiled output.

Interface definitions are translated directly, so `public interface Foo`
becomes `export interface Foo`.

Inheritance via the `extends` keyword and interface implementation with
`implements` are copied directly to the TypeScript output.

Enum declarations are also converted so that `public enum Foo` becomes
`export enum Foo` in the resulting TypeScript.

The primitive `boolean` and its wrapper `Boolean` both become TypeScript
`boolean`, as verified by `TranspilerMethodTest.mapsBooleanTypes`.

Lambda expressions use TypeScript arrow function syntax, so `() -> {}`
becomes `() => {}`.

Error handling avoids Java exceptions. Do not use `throw`, `try`, or `catch`.
Instead, functions should return a `Result` or `Option` object.
`Main` now exposes a `run` method that returns an `Option<String>` so callers
can inspect errors without relying on exceptions.

Annotations are currently skipped entirely, so no TypeScript decorators are
generated.

## Documentation

Additional notes and a feature mapping between Java and TypeScript live in
[`docs/java-to-typescript-roadmap.md`](docs/java-to-typescript-roadmap.md).
The roadmap now lists the tests that verify each implemented feature.
Developer guidelines are summarized in
[`docs/coding-standards.md`](docs/coding-standards.md).
Additional guidelines for contributors can be found in [AGENTS.md](AGENTS.md).

### Building and Testing

The project intentionally avoids Maven. Use the provided helper scripts to
compile and run the JUnit tests. The build script downloads the JUnit Console
launcher if needed and places compiled classes in a `bin` directory. When the
compilation step finishes you will see a confirmation message so it is clear the
build succeeded.

```bash
./build.sh  # compile sources
./test.sh   # execute all tests
```

After compiling, you can invoke the transpiler via the CLI. It scans
`src/main/java` and writes TypeScript files under `src/main/node`:

```bash
java -cp bin magma.Main
```

