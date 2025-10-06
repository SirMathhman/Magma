What changed

- Renamed two private helper methods in `magma.compile.JavaSerializer`:
  - `getObjectCompileErrorResult(Class, Node, String)` -> `findNestedSealedDeserialization`
  - `getObjectCompileErrorResult(Class, Node, String, Class)` -> `tryDeserializeNestedSealed`

Why

- The old name `getObjectCompileErrorResult` was vague and long. The new names better describe the intent:
  - `findNestedSealedDeserialization` searches nested permitted subclasses for a matching sealed subtype and returns an Option-wrapped Result when found.
  - `tryDeserializeNestedSealed` attempts deserialization for a single permitted subclass (used during the nested search).

How to verify

1. Compile the project to ensure the rename didn't break anything:

```cmd
mvn -DskipTests package
```

Expect: build completes successfully.

2. Confirm there are no remaining references to the old symbol:

```cmd
findstr /spin "getObjectCompileErrorResult" .
```

Expect: no matches.

3. (Optional) Run a focused test that exercises sealed-type deserialization:

```cmd
mvn -Dtest=DeserializationDebugTest test
```

Expect: test passes (or same result as before the change).

Files changed

- `src/main/java/magma/compile/JavaSerializer.java` (renamed private helpers)
- `docs/JAVA_SERIALIZER_RENAME.md` (this file)

Notes

- Change is purely a rename for clarity; behavior is unchanged.
- If you want a different name, I can update it again and re-run the compile.

