# Java to TypeScript Feature Roadmap

This page outlines how Java language features map to their TypeScript counterparts. Development is test-driven and favors a simple design. The current prototype stubs out Java method bodies with `// TODO` comments—one for each statement—while preserving method signatures and basic type mappings.

Only the features listed below are supported. Anything not mentioned here is considered unsupported.
## Supported Features

- **Package declarations** become module paths.
  - Use directory structure to mirror packages.
  - Tests: `TranspilerTest.removesPackageDeclaration`, `MainTest.printsTranspiledSource`.
- **Primitive types** (`int`, `float`, `double`, `long`) map to TypeScript's `number`.
  - Tests: `TranspilerTest.stubsMethodBodiesPreservingNames`.
- **boolean** / **Boolean** becomes `boolean`.
  - Tests: `TranspilerTest.mapsBooleanTypes`.
- **char**, **Character**, and **String** all become `string`.
  - Tests: `TranspilerTest.mapsCharCharacterAndStringToString`.
- **Arrays** translate directly (`int[]` → `number[]`, etc.).
  - Tests: `TranspilerTest.mapsArrayTypes`.
- **Classes** use standard `class` syntax.
  - Tests: `TranspilerTest.transpilesClassDefinitionWithModifier`.
- **Interfaces** map directly to TypeScript interfaces.
- **Abstract classes** are supported via the `abstract` keyword, though the project avoids them in its own code.
- **Enums** become `enum`.
  - Tests: `TranspilerTest.transpilesEnumDefinition`.
- **Generics** preserve type parameters, e.g. `List<T>` → `List<T>`.
  - Tests: `TranspilerTest.mapsGenericTypes`.
- **Methods** keep their names and basic return types such as `int` or `void` translate to `number` or `void`.
  - Return statements are emitted as `return /* TODO */;` while other statements become `// TODO` comments.
  - Tests: `TranspilerTest.stubsMethodBodiesPreservingNames`, `TranspilerTest.stubsVoidReturnTypes`, `TranspilerTest.stubsOneTodoPerStatement`.
  - **Fields** become class properties.
    - `final` fields are emitted with the `readonly` modifier.
    - Field initializations are ignored so assignments are dropped.
    - Tests: `TranspilerTest.transpilesFieldDeclarations`, `TranspilerTest.finalFieldsBecomeReadonly`, `TranspilerTest.stubsFieldAssignments`.
- **Access modifiers** (`public`, `private`, `protected`) map directly. `package‑private` is emitted as a public or internal export.
  - Tests: `TranspilerTest.transpilesClassDefinitionWithModifier`.
- **Inheritance** via `extends` is preserved.
  - Tests: `TranspilerTest.preservesExtendsClause`.
- **Implementing interfaces** uses `implements`.
  - Tests: `TranspilerTest.preservesImplementsClause`.
- **Exceptions** (`throw`, `try`/`catch`) are replaced with `Result` or `Option` objects.
- **Lambda expressions** become arrow functions.
  - Assignment statements inside arrow function bodies are replaced with `// TODO`.
  - Tests: `TranspilerTest.stubsAssignmentsInArrowFunctions`.
- **Streams** rely on array helpers such as `map`, `filter`, and `reduce`.
- **Standard library** utilities are replaced with small TypeScript helpers.

## Key Modules
- `com.example.Transpiler` – converts Java source into TypeScript
- `com.example.Main` – command line driver that runs the transpiler

## Further tasks
1. ~~Implement translation of basic class structure and type mappings.~~  
   Basic class definitions now output `export default class`.
2. ~~Add support for generics and inheritance.~~
3. Add interface translation with verifying tests.
   - Update the transpiler to emit TypeScript `interface` definitions.
   - Write failing tests that cover interface translation.
4. Replace exceptions with `Result`/`Option` constructs.
   - ~~Provide minimal `Result` and `Option` utilities.~~
   - ~~Refactor `Main` to return these types instead of using `throws`.~~
   - Refactor generated code to return these types.
5. Implement lambda expressions and stream translations.
   - Convert lambda expressions to arrow functions.
   - Map basic stream operations to array helpers.
6. Provide minimal replacements for common standard library utilities.
   - Introduce small helpers for `List` and `Map` behavior.
7. Explore concurrency patterns for future features.
   - Investigate Web Workers or async/await translation strategies.
8. Keep the list of tests up to date as new features are covered.

Each feature should begin with a failing test that describes the expected TypeScript output for a Java example.
