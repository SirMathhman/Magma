# Java to TypeScript Feature Roadmap

This page outlines how Java language features map to their TypeScript counterparts. Development is test-driven and favors a simple design. The current prototype stubs out Java method bodies with `// TODO` comments—one for each statement—while preserving method signatures and basic type mappings.

Only the features listed below are supported. Anything not mentioned here is considered unsupported.
## Supported Features

- **Package declarations** become module paths.
  - Use directory structure to mirror packages.
  - Tests: `TranspilerClassTest.removesPackageDeclaration`, `MainTest.printsTranspiledSource`.
- **Primitive types** (`int`, `float`, `double`, `long`) map to TypeScript's `number`.
  - Tests: `TranspilerMethodTest.stubsMethodBodiesPreservingNames`.
- **boolean** / **Boolean** becomes `boolean`.
  - Tests: `TranspilerMethodTest.mapsBooleanTypes`.
- **char**, **Character**, and **String** all become `string`.
  - Tests: `TranspilerMethodTest.mapsCharCharacterAndStringToString`.
- **Arrays** translate directly (`int[]` → `number[]`, etc.).
  - Tests: `TranspilerMethodTest.mapsArrayTypes`.
- **Classes** use standard `class` syntax.
  - Tests: `TranspilerClassTest.transpilesClassDefinitionWithModifier`.
- **Interfaces** map directly to TypeScript interfaces.
  - Tests: `TranspilerClassTest.transpilesInterfaceDefinition`.
- **Abstract classes** are supported via the `abstract` keyword, though the project avoids them in its own code.
- **Enums** become `enum`.
  - Tests: `TranspilerClassTest.transpilesEnumDefinition`.
- **Generics** preserve type parameters, e.g. `List<T>` → `List<T>`.
  - Tests: `TranspilerMethodTest.mapsGenericTypes`.
- **Methods** keep their names and basic return types such as `int` or `void` translate to `number` or `void`.
  - Return statements are emitted as `return /* TODO */;` while other statements become `// TODO` comments.
    - `if` and `while` statements output `<keyword> (/* TODO */) {` with a single `// TODO` in the body.
      - Tests: `TranspilerMethodTest.stubsMethodBodiesPreservingNames`, `TranspilerMethodTest.stubsVoidReturnTypes`, `TranspilerStatementTest.stubsOneTodoPerStatement`, `TranspilerStatementTest.stubsIfStatements`, `TranspilerStatementTest.stubsWhileStatements`.
  - **Fields** become class properties.
    - `final` fields are emitted with the `readonly` modifier.
    - Field initializations are ignored so assignments are dropped.
    - Tests: `TranspilerFieldTest.transpilesFieldDeclarations`, `TranspilerFieldTest.finalFieldsBecomeReadonly`, `TranspilerFieldTest.stubsFieldAssignments`.
- **Access modifiers** (`public`, `private`, `protected`) map directly. `package‑private` is emitted as a public or internal export.
  - Tests: `TranspilerClassTest.transpilesClassDefinitionWithModifier`.
- **Inheritance** via `extends` is preserved.
  - Tests: `TranspilerClassTest.preservesExtendsClause`.
- **Implementing interfaces** uses `implements`.
  - Tests: `TranspilerClassTest.preservesImplementsClause`.
- **Exceptions** (`throw`, `try`/`catch`) are replaced with `Result` or `Option` objects.
- **Lambda expressions** become arrow functions.
  - Assignment statements inside arrow function bodies are replaced with `// TODO`.
  - Tests: `TranspilerStatementTest.stubsAssignmentsInArrowFunctions`.
  - Variable definitions within method bodies are emitted as `let` declarations
    with `/* TODO */` for the assigned value.
  - Tests: `TranspilerStatementTest.stubsOneTodoPerStatement`,
    `TranspilerStatementTest.leavesValueAssignmentsAsTodo`.
- **Invokable expressions** like method or constructor calls are stubbed with
  `/* TODO */` placeholders for the callee and each argument. This includes
  assignments such as `int x = run();` which become `let x: number = /* TODO */();`.
  - Tests: `TranspilerStatementTest.stubsInvokables`,
    `TranspilerStatementTest.stubsInvokablesInLetStatements`.
- **Streams** rely on array helpers such as `map`, `filter`, and `reduce`.
- **Standard library** utilities are replaced with small TypeScript helpers.

## Key Modules
- `com.example.app.Transpiler` – orchestrates the conversion to TypeScript
- `ImportHelper` – handles packages and import statements
- `MethodStubber` – stubs out method bodies
- `FieldTranspiler` – converts field declarations
- `ArrowHelper` – rewrites lambda expressions
- `TypeMapper` – maps Java types to their TypeScript equivalents
- `com.example.Main` – command line driver that runs the transpiler

## Further tasks
1. ~~Implement translation of basic class structure and type mappings.~~  
   Basic class definitions now output `export default class`.
2. ~~Add support for generics and inheritance.~~
3. ~~Add interface translation with verifying tests.~~
   Interface definitions now emit `export interface` lines.
   Tests cover the translation in `TranspilerClassTest.transpilesInterfaceDefinition`.
4. Replace exceptions with `Result`/`Option` constructs.
 - ~~Provide minimal `Result` and `Option` utilities.~~
 - ~~Refactor `Main` to return these types instead of using `throws`.~~
  - Split `Option` and `Result` into `Some`/`None` and `Ok`/`Err` variants.
  - Refactor generated code to return these types.
5. Implement lambda expressions and stream translations.
   - Convert lambda expressions to arrow functions.
   - Map basic stream operations to array helpers.
6. Provide minimal replacements for common standard library utilities.
   - Introduce small helpers for `List` and `Map` behavior.
7. Explore concurrency patterns for future features.
   - Investigate Web Workers or async/await translation strategies.
8. Keep the list of tests up to date as new features are covered.
9. ~~Parse invokable expressions and stub out the caller and arguments.~~
   Tests ensure calls are stubbed in both standalone statements and in `let`
   declarations.
10. Translate `import` statements to relative paths reflecting the package hierarchy.

Each feature should begin with a failing test that describes the expected TypeScript output for a Java example.
