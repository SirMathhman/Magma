# Java to TypeScript Feature Roadmap

This page outlines how Java language features map to their TypeScript counterparts. The project follows a test‑driven approach and Kent Beck's rules for simple design. Each feature should be supported by failing tests before implementation and refactored to remove duplication. The current prototype stubs out Java method bodies in the generated TypeScript with a `// TODO` comment while method signatures are preserved, including basic return type mappings.

| Java Feature | TypeScript Equivalent | Notes | Tests |
| ------------ | -------------------- | ----- | ----- |
| `package` declarations | `module` or ES module system | Use directory structure to mirror packages. | `TranspilerTest.removesPackageDeclaration`, `MainTest.printsTranspiledSource` |
| Primitive types (`int`, `float`, `double`, `long`) | `number` | TypeScript uses a single `number` type. | `TranspilerTest.stubsMethodBodiesPreservingNames` |
| `boolean` / `Boolean` | `boolean` | Direct mapping. | `TranspilerTest.mapsBooleanTypes` |
| `char` | `string` | Primitive character becomes a string. | `TranspilerTest.mapsCharCharacterAndStringToString` |
| `Character` | `string` | Wrapper character type. | `TranspilerTest.mapsCharCharacterAndStringToString` |
| `String` | `string` | Direct mapping. | `TranspilerTest.mapsCharCharacterAndStringToString` |
| Arrays | Arrays | `int[]` → `number[]`, etc. | `TranspilerTest.mapsArrayTypes` |
| Classes | Classes | Use `class` syntax. | `TranspilerTest.transpilesClassDefinitionWithModifier` |
| Interfaces | Interfaces | Direct mapping. | |
| Abstract classes | Abstract classes | Use the `abstract` keyword. The project itself avoids abstract base classes in favor of composition. | |
| Enums | `enum` | TypeScript `enum` provides similar semantics. | `TranspilerTest.transpilesEnumDefinition` |
| Generics | Generics | Direct mapping of type parameters, e.g. `List<T>` → `List<T>`. | `TranspilerTest.mapsGenericTypes` |
| Methods | Methods | Instance and static methods translate directly. Basic return types such as `int` or `void` become `number` or `void`. | `TranspilerTest.stubsMethodBodiesPreservingNames`, `TranspilerTest.stubsVoidReturnTypes` |
| Fields | Properties | Public/private modifiers apply. | `TranspilerTest.transpilesFieldDeclarations` |
| Access modifiers (`public`, `private`, `protected`) | `public`, `private`, `protected` | `package‑private` becomes `public` or internal module export. | `TranspilerTest.transpilesClassDefinitionWithModifier` |
| Inheritance (`extends`) | `extends` | Works with classes and interfaces. | `TranspilerTest.preservesExtendsClause` |
| Implementing interfaces (`implements`) | `implements` | Direct mapping. | `TranspilerTest.preservesImplementsClause` |
| Exceptions (`throw`, `try`/`catch`) | `Result`/`Option` types | Prefer returning a `Result` or `Option` object instead of using exceptions. | |
| Annotations | *(not supported)* | Decorators are not used in this project. | |
| Lambda expressions | Arrow functions | `() -> {}` → `() => {}`. | |
| Streams | Array methods / custom helpers | Use `map`, `filter`, `reduce`. | |
| Standard library (`java.util`, etc.) | TypeScript/JS standard APIs or polyfills | Replace with equivalent utilities. | |
| Reflection | Limited or custom metadata | TypeScript has limited runtime type information. | |
| `synchronized` | Not applicable | Use higher‑level concurrency primitives if needed. | |
| Threads | Web Workers or async/await | Depends on target platform. | |

Further tasks:
1. ~~Implement translation of basic class structure and type mappings.~~
   Basic class definitions now output `export default class`.
2. ~~Add support for generics~~ and inheritance.
3. Replace exceptions with `Result`/`Option` constructs and update control flow accordingly.
4. ~~Map annotations to decorators.~~ Decorators will not be used.
5. Gradually cover advanced features like reflection or concurrency.

6. Record the related test names in the "Tests" column whenever a feature is completed.

Each step should begin with a test describing the expected TypeScript output for a Java input example.
