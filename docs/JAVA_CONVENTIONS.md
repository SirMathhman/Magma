# Non-traditional Java Conventions in Magma

Magma deliberately uses several non-standard or non-mainstream Java conventions to achieve a small, expressive compiler codebase that favors explicit control flow and safer data handling. This document explains those conventions, why they were chosen, and practical examples showing how to work with them.

Audience: contributors who are comfortable with Java but may be unfamiliar with the project's stylistic and architectural choices.

## Quick list of conventions

- Result / Option ADTs instead of exceptions or nullable returns
- Pattern matching on records in `instanceof` and `switch` expressions (Java preview features)
- Heavy use of sealed interfaces and records to create closed ADTs (exhaustive pattern handling)
- `NonEmptyList<T>` to encode present-but-empty vs absent semantics
- `One loop per method` coding guideline (Checkstyle-enforced)
- Minimal use of nulls — `Option<T>` preferred
- No use of external libraries for core ADTs (small custom implementation in `magma.option` and `magma.result`)
- Tests use explicit `instanceof Ok<?, ?>` / `instanceof Err<?, ?>` assertions and pattern matching
- Java compiler preview flags are required (project targets Java 24)

## Rationale

1. Predictable failure flow: `Result<T, E>` makes error paths explicit and easy to reason about. It avoids hidden exceptions bubbling up at unpredictable points.
2. Exhaustiveness: Sealed interfaces + records allow compile-time reasoning about possible variants and make maintenance easier by forcing explicit handling of new cases.
3. No hidden nulls: `Option<T>` provides a clearer contract for optional values and avoids `NullPointerException` surprises.
4. Semantic correctness: `NonEmptyList<T>` encodes domain invariants at the type level ("present but empty" is a different state from "absent").
5. Small dependency surface: The project avoids pulling in heavy third-party libraries for small functional types to keep the toolchain simple and deterministic.
6. Readability for pattern-heavy code: Pattern-matching on records and switch expressions produces concise and clearer code for the kinds of AST transformations this project performs.

## Practical examples

### Result & Option

Pattern for functions that can fail:

```java
Result<Node, CompileError> lexResult = JRoot().lex(source);
if (lexResult instanceof Err<Node, CompileError>(CompileError error)) {
    // handle error
}

if (lexResult instanceof Ok<Node, CompileError>(Node node)) {
    // continue
}
```

Chaining operations:

```java
return readString(source)
    .flatMap(s -> Parser.lex(s))
    .flatMap(node -> JavaSerializer.deserialize(Root.class, node))
    .flatMap(ast -> Transformer.transform(ast));
```

### Pattern matching and sealed interfaces

```java
Result<JavaRootSegment, CompileError> result = JavaSerializer.deserialize(JavaRootSegment.class, node);
if (result instanceof Ok<JavaRootSegment, CompileError>(JavaRootSegment segment)) {
    // Exhaustively handle variants
    switch (segment) {
        case JClass jc -> handleClass(jc);
        case Interface i -> handleInterface(i);
        case RecordNode rn -> handleRecord(rn);
        default -> throw new IllegalStateException("Unhandled JavaRootSegment: " + segment.getClass());
    }
}
```

### NonEmptyList vs List

Why not `List<T>` for everything?

- A field typed as `Option<List<T>>` admits three semantic states:
  1. Absent (None)
  2. Present with elements (Some(List with > 0 items))
  3. Present but empty (Some(List with 0 items)) — often invalid or ambiguous

`Option<NonEmptyList<T>>` makes the intent explicit: either absent or present with at least one element.

Creation pattern:

```java
Option<NonEmptyList<CParameter>> params = paramsList.isEmpty()
    ? Option.empty()
    : Option.of(NonEmptyList.from(paramsList));
```

Deserialization must validate present-but-empty lists and return an error.

### One loop per method

To improve readability and satisfy Checkstyle rules, methods should contain at most one loop. Nested loop logic should be extracted into helper methods.

Before:

```java
for (Node child : children) {
    for (Node inner : child.findNodeList("items").orElse(new ArrayList<>())) {
        process(inner);
    }
}
```

After:

```java
for (Node child : children) {
    processChildItems(child);
}

private void processChildItems(Node child) {
    for (Node inner : child.findNodeList("items").orElse(new ArrayList<>())) {
        process(inner);
    }
}
```

## Tests and assertions

Tests assert success/failure using the `Result` ADT explicitly. Avoid calling `get()` or unwrapping types without checking `instanceof` first.

Example assertion:

```java
Result<MyType, CompileError> r = JavaSerializer.deserialize(MyType.class, node);
assertTrue(r instanceof Ok<?, ?>, "Deserialization should succeed");
MyType t = ((Ok<MyType, CompileError>) r).value();
```

Also prefer writing tests that reproduce the whole pipeline (lex → deserialize → transform) for integration coverage.

## Tooling and compilation flags

- The `pom.xml` sets Java source/target to `24`.
- The build and your IDE must enable preview features used by the codebase (pattern matching, record patterns, enhanced switch).
- Checkstyle rules are configured in `config/checkstyle/checkstyle.xml` and include the one-loop-per-method rule and other project-specific rules.

## When to break the rules

These conventions are deliberate trade-offs. If a change meaningfully simplifies code or fixes bugs, exceptions are acceptable — but document why you broke a convention and prefer to update docs.

## Migration notes for contributors

- If you add public APIs, update `README.md` or `docs/ARCHITECTURE.md` as appropriate.
- Add tests that show both happy-path and error-path behaviors.
- If you change `Result`/`Option` semantics or the shapes of ADTs (e.g., `NonEmptyList`), run the full test suite and update docs; this repository enforces this via `.github/copilot-instructions.md` wording.

## References

- See `src/main/java/magma/option` and `src/main/java/magma/result` for implementations
- See `docs/DEVELOPER_GUIDE.md` for how-to examples
- See `FIELD_VALIDATION_FEATURE.md` and `TYPE_MISMATCH_VALIDATION.md` for validation examples
