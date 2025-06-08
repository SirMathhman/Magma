# Java to TypeScript Feature Roadmap

This page outlines how Java language features map to their TypeScript counterparts. Development is test-driven and favors a simple design. The current prototype stubs out Java method bodies with `// TODO` comments—one for each statement—while preserving method signatures and basic type mappings.

Only the features listed below are supported. Anything not mentioned here is considered unsupported.
## Supported Features

- **Package declarations** become module paths.
  - Use directory structure to mirror packages.
  - Tests: `TranspilerClassTest.removesPackageDeclaration`, `MainTest.printsTranspiledSource`.
- **Primitive types** (`int`, `float`, `double`, `long`) map to TypeScript's `number`.
  - Tests: `TranspilerMethodTest.stubsMethodBodiesPreservingNames`.
- **Boxed numeric types** (`Integer`, `Long`, `Float`, `Double`, `Short`, `Byte`) also map to `number`.
  - Tests: `TranspilerMethodTest.mapsBoxedNumberTypes`.
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
  - Numeric literals are preserved in both assignments and return statements. Other statements become `// TODO` comments.
  - The logical not operator `!` is preserved so conditions like `!flag` remain unchanged.
    - When a negated value is a method call, the callee name is preserved and arguments are parsed recursively.
    - `if` and `while` statements parse their conditions using `parseValue`. Method calls now keep their names while unknown values become `/* TODO */`.
  - Tests: `TranspilerMethodTest.stubsMethodBodiesPreservingNames`, `TranspilerMethodTest.stubsVoidReturnTypes`, `TranspilerStatementTest.stubsOneTodoPerStatement`, `TranspilerStatementTest.stubsIfStatements`, `TranspilerStatementTest.stubsWhileStatements`, `TranspilerStatementTest.keepsNumericValues`.
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
  - Tests: `TranspilerStatementTest.stubsAssignmentsInArrowFunctions`,
    `TranspilerStatementTest.convertsSingleParameterLambda`,
    `TranspilerStatementTest.convertsTypedParameterLambda`,
    `TranspilerStatementTest.expandsMultipleAssignmentsInArrowFunction`.
  - Variable definitions within method bodies are emitted as `let` declarations
    with `/* TODO */` for the assigned value.
  - Tests: `TranspilerStatementTest.stubsOneTodoPerStatement`,
    `TranspilerStatementTest.leavesValueAssignmentsAsTodo`.
  - **Invokable expressions** like method or constructor calls keep the method
    name. Arguments are parsed recursively so identifiers and literals are kept
    while unknown expressions emit `/* TODO */`.
    Assignments such as `int x = run();` become `let x: number = run();`.
    Constructor calls retain the `new` keyword and type name. Calls on freshly
    created objects such as `new Main().run()` are preserved intact.
  - Tests: `TranspilerStatementTest.stubsInvokables`,
    `TranspilerStatementTest.stubsInvokablesInLetStatements`,
    `TranspilerStatementTest.stubsConstructorCalls`,
    `TranspilerStatementTest.stubsConstructorCallsInLetStatements`,
    `TranspilerStatementTest.preservesCallsOnNewInstances`,
    `TranspilerStatementTest.preservesCallsOnNewInstancesInLetStatements`.
- **Member access** expressions like `parent.child` are kept intact.
  - Tests: `TranspilerStatementTest.preservesMemberAccessInAssignments`,
    `TranspilerStatementTest.preservesMemberAccessInReturns`.
- **Identifier values** remain unchanged in assignments and returns.
  - Tests: `TranspilerStatementTest.keepsIdentifierValues`,
    `TranspilerMethodTest.mapsBooleanTypes`,
    `TranspilerMethodTest.mapsGenericTypes`,
    `TranspilerMethodTest.mapsCharCharacterAndStringToString`,
    `TranspilerStatementTest.stubsOneTodoPerStatement`.
- **Unknown type names** are kept as-is instead of defaulting to `any`.
  - Tests: `TranspilerStatementTest.preservesMemberAccessInAssignments`,
    `TranspilerStatementTest.parsesMemberAccessInWhileCondition`.
- **Streams** rely on array helpers such as `map`, `filter`, and `reduce`.
- **java.util.function** interfaces become arrow function types.
- **Standard library** utilities are replaced with small TypeScript helpers.

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
   `ListLike` now wraps `java.util.List` and exposes a custom `ListIter` backed by the generic `Iter` interface with `map`, `fold`, and `flatMap` methods. `flatMap` accepts a function returning
   another iterator so callers can compose nested lists without direct references to concrete collection types.
    A `Map` wrapper is still pending.
   - `Option` values can now convert to an `Iter` so optional results integrate with iterator helpers.
7. Explore concurrency patterns for future features.
   - Investigate Web Workers or async/await translation strategies.
8. Keep the list of tests up to date as new features are covered.
9. ~~Parse invokable expressions and stub out the caller and arguments.~~
   Tests now verify that method names are preserved while arguments fall back to
   `/* TODO */` when unknown.
10. ~~Translate `import` statements to relative paths reflecting the package hierarchy.~~
    `ImportHelper` now rewrites import lines so they use relative module paths.
11. ~~Preserve member access expressions like `obj.field` and allow chaining after
    method calls such as `doStuff().value`.~~
    Tests cover `obj.field` assignments, returns, chained accesses, and now
    verify conditions including `if (p.child)` and `if (!p.child)`.
12. Parse constructor types so stubs emit `new Type(/* TODO */)`.
13. ~~Preserve method calls on newly created instances.~~
   Calls like `new Main().run()` now remain unchanged in the output.
14. ~~Preserve unknown type identifiers rather than using `any`.~~
   Parameters and fields now keep their original type names when no
   mapping exists.
15. Parse statements inside `if` and `while` blocks so nested code is
    handled the same as top-level statements.
16. Introduce a `PathLike` interface to wrap `java.nio.file.Path`.
    Refactor `Main` to use this abstraction so future code can swap out
    the NIO implementation.
17. Move file I/O helpers onto `PathLike` so `Main` no longer deals with
    `IOException`. Methods now return `Result` or `Option` objects.

Each feature should begin with a failing test that describes the expected TypeScript output for a Java example.
