# Magma

Welcome to the **Magma** project!
All individuals wishing to contribute to this document should initially read this document
as well as the specifications and coding guidelines.

The repository includes a small demonstration of a Java → Magma compiler. The program in
`src/magma/Main.java` reads its own source, rewrites Java-style import statements into the
Magma form `import Child from parent.Child.`, and writes the result to `src/magma/Main.mgs`.

Unlike Java, Magma does not use package declarations. Consequently the example
`Main.java` and the generated `Main.mgs` start directly with their import
statements.

