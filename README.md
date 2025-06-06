# Magma

Welcome to the **Magma** project!
All individuals wishing to contribute to this document should initially read this document
as well as the specifications and coding guidelines.
All tasks should conclude with fully updated documentation to reflect the changes made.
Every update should be recorded in a sorted list somewhere in the repository. This log of changes can live in any documentation file and does not need to be a dedicated `CHANGELOG.md`.

The repository includes a small demonstration of a Java → Magma compiler. The program in
`src/magma/Main.java` reads its own source, rewrites Java-style import statements into the
Magma form `import Child from parent.Child.`, and writes the result to `src/magma/Main.mgs`.

Unlike Java, Magma does not use package declarations. Consequently the example
`Main.java` and the generated `Main.mgs` start directly with their import
statements.

Magma classes are defined using the `class def` syntax. For example
`class def MyType() => {}` defines a class named `MyType` with an empty body.

