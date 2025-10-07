# Developer Guide

A practical guide for developers working on the Magma project. This document covers common development workflows, debugging techniques, and best practices.

## Table of Contents

1. [Getting Started](#getting-started)
2. [Development Workflow](#development-workflow)
3. [Common Tasks](#common-tasks)
4. [Debugging Techniques](#debugging-techniques)
5. [Testing Guidelines](#testing-guidelines)
6. [Code Patterns](#code-patterns)
7. [Troubleshooting](#troubleshooting)

## Getting Started

### Setup

```bash
# Clone the repository
git clone <repository-url>
cd Magma

# Verify Java version
java -version  # Should be Java 24

# Build the project
mvn clean compile

# Run tests
mvn test
```

### IDE Setup

**IntelliJ IDEA:**

1. Open project as Maven project
2. Set Project SDK to Java 24:
   - File → Project Structure → Project → SDK
3. Enable pattern matching preview features:
   - Preferences → Build, Execution, Deployment → Compiler → Java Compiler
   - Add to "Additional command line parameters": `--enable-preview`
4. Install Checkstyle plugin (optional but recommended)

**VS Code:**

1. Install Java Extension Pack
2. Configure Java 24 in settings
3. Maven extension should auto-detect `pom.xml`

## Development Workflow

### Basic Cycle

```bash
# 1. Make changes to source code
# 2. Compile
mvn compile

# 3. Run tests
mvn test

# 4. Check style (optional)
mvn checkstyle:check

# 5. Run the transpiler
mvn exec:java
```

### Working on a Feature

```bash
# 1. Create a feature branch
git checkout -b feature/my-feature

# 2. Make changes and test frequently
mvn test

# 3. Run specific test during development
mvn -Dtest=MyFeatureTest test

# 4. Update documentation
# Edit docs/*.md and README.md

# 5. Final verification
mvn clean test
mvn checkstyle:check

# 6. Commit and push
git add .
git commit -m "Add feature: description"
git push origin feature/my-feature
```

## Common Tasks

### Adding a New AST Node Type

**Example: Adding a `ForLoop` node**

1. **Define the record in `Lang.java`:**

```java
@Tag("for")
public record ForLoop(
    JDefinition variable,
    JExpression condition,
    JExpression increment,
    NonEmptyList<JMethodSegment> body
) implements JMethodSegment {}
```

2. **Add to sealed interface `permits` clause:**

```java
sealed public interface JMethodSegment
    permits BlockComment, Break, Catch, ForLoop, /* ... */ {
}
```

3. **Define parsing rule in `Lang.java`:**

```java
private static Rule ForLoop() {
    return Tag("for",
        Suffix(
            First(
                String("for"),
                String("("),
                Node("variable", JDefinition()),
                String(";"),
                Node("condition", JExpression()),
                String(";"),
                Node("increment", JExpression()),
                String(")"),
                String("{"),
                Statements("body", JMethodSegment())
            ),
            "}"
        )
    );
}
```

4. **Add rule to parent rule (if needed):**

```java
private static Rule JMethodSegment() {
    return Or(
        ForLoop(),  // Add here
        JWhile(),
        JIf(),
        // ... other options
        Invalid()
    );
}
```

5. **Add transformation in `Transformer.java`:**

```java
private static Result<CFunctionSegment, CompileError> transformMethodSegment(
    JMethodSegment segment
) {
    return switch (segment) {
        case ForLoop loop -> transformForLoop(loop);
        // ... other cases
    };
}

private static Result<CWhile, CompileError> transformForLoop(ForLoop loop) {
    // Transform to C++ equivalent (maybe CWhile with initialization)
    // ...
}
```

6. **Add tests in `src/test/java/`:**

```java
@Test
public void testForLoopParsing() {
    String source = """
        for (int i = 0; i < 10; i++) {
            System.out.println(i);
        }
        """;

    Result<Node, CompileError> lexResult = JMethodSegment().lex(source);
    assertTrue(lexResult instanceof Ok<?, ?>, "Lexing should succeed");

    Node node = ((Ok<Node, CompileError>) lexResult).value();
    Result<ForLoop, CompileError> result = JavaSerializer.deserialize(ForLoop.class, node);

    assertTrue(result instanceof Ok<?, ?>, "Deserialization should succeed");
    ForLoop loop = ((Ok<ForLoop, CompileError>) result).value();
    assertEquals("i", loop.variable().name());
}
```

### Adding Validation

**Example: Validate that method names don't start with numbers**

1. **Add validation method in appropriate class:**

```java
// In JavaSerializer.java or a validation utility
private static Option<CompileError> validateMethodName(String name, Context context) {
    if (name.matches("^[0-9].*")) {
        return Option.of(new CompileError(
            "Method name cannot start with a digit: '" + name + "'",
            context
        ));
    }
    return Option.empty();
}
```

2. **Call during deserialization:**

```java
// In deserializeMethod or similar
Option<CompileError> nameError = validateMethodName(methodName, new NodeContext(node));
if (nameError instanceof Some<CompileError>(CompileError error)) {
    return new Err<>(error);
}
```

3. **Add tests:**

```java
@Test
public void testMethodNameValidation() {
    Node node = new Node()
        .retype("method")
        .withString("name", "9invalidName");

    Result<Method, CompileError> result = JavaSerializer.deserialize(Method.class, node);

    assertTrue(result instanceof Err<?, ?>, "Should fail validation");
    String error = ((Err<Method, CompileError>) result).error().toString();
    assertTrue(error.contains("cannot start with a digit"));
}
```

### Modifying an Existing ADT

**Scenario: Add optional return type to `Method`**

1. **Update the record:**

```java
@Tag("method")
public record Method(
    JDefinition definition,
    Option<NonEmptyList<JDefinition>> params,
    Option<String> body,
    Option<JType> returnType  // NEW FIELD
) implements JStructureSegment {}
```

2. **Update parsing rule:**

```java
private static Rule Method() {
    return Tag("method",
        Suffix(
            First(
                Node("returnType", JType()),  // NEW
                String(" "),
                Node("definition", JDefinition()),
                // ... rest
            ),
            "}"
        )
    );
}
```

3. **Update all construction sites:**

```bash
# Find all usages
grep -r "new Method(" src/
```

Update each to include new field (or `Option.empty()`).

4. **Update transformation:**

```java
private static Result<Function, CompileError> transformMethod(Method method) {
    // Use new returnType field
    Result<CLang.CType, CompileError> returnType = method.returnType()
        .map(type -> transformType(type))
        .orElse(new Ok<>(new Identifier("void")));
    // ...
}
```

5. **Run tests and fix failures:**

```bash
mvn test
# Fix each test that breaks due to new field
```

## Debugging Techniques

### Inspecting Parsed Nodes

```java
// In a test or main method
Result<Node, CompileError> lexResult = JRoot().lex(source);
if (lexResult instanceof Ok<Node, CompileError>(Node node)) {
    System.out.println(node.toString());  // Pretty-printed tree structure
}
```

### Debugging Deserialization

```java
// Add debug output in JavaSerializer
System.out.println("Deserializing " + clazz.getName() + " from node type: " + node.type);
System.out.println("Node fields: " + node.getStringKeys());

// Check what fields were consumed
Set<String> consumedFields = new HashSet<>();
// ... deserialization logic
System.out.println("Consumed fields: " + consumedFields);
System.out.println("Leftover fields: " + getRemainingFields(node, consumedFields));
```

### Debugging Transformation

```java
// In Transformer.java
System.out.println("Transforming JClass: " + jClass.name());
System.out.println("Children count: " + jClass.children().size());

jClass.children().forEach(child -> {
    System.out.println("  Child type: " + child.getClass().getSimpleName());
});
```

### Running a Single Test

```bash
# Run one test class
mvn -Dtest=ComprehensiveFieldValidationTest test

# Run one test method
mvn -Dtest=ComprehensiveFieldValidationTest#testLeftoverFieldDetection test

# Run with verbose output
mvn -Dtest=MyTest test -X
```

### Debugging Checkstyle Issues

```bash
# Run checkstyle with detailed output
mvn checkstyle:check -X

# Generate checkstyle report
mvn checkstyle:checkstyle
# Opens target/site/checkstyle.html
```

## Testing Guidelines

### Test Structure

Use JUnit 5 with explicit pattern matching:

```java
@Test
public void testFeatureName() {
    // Arrange
    String input = "...";

    // Act
    Result<T, CompileError> result = performOperation(input);

    // Assert
    assertTrue(result instanceof Ok<?, ?>, "Operation should succeed");
    T value = ((Ok<T, CompileError>) result).value();
    assertEquals(expectedValue, value.someProperty());
}
```

### Testing Error Cases

```java
@Test
public void testErrorCondition() {
    // Arrange
    Node invalidNode = new Node().retype("invalid");

    // Act
    Result<MyType, CompileError> result = JavaSerializer.deserialize(MyType.class, invalidNode);

    // Assert
    assertTrue(result instanceof Err<?, ?>, "Should fail with error");
    CompileError error = ((Err<MyType, CompileError>) result).error();
    assertTrue(error.message().contains("expected substring"),
        "Error message should describe the issue");
}
```

### Testing Roundtrips

```java
@Test
public void testSerializationRoundtrip() {
    // Create original
    MyRecord original = new MyRecord("value");

    // Serialize
    Result<Node, CompileError> serializeResult = JavaSerializer.serialize(MyRecord.class, original);
    assertTrue(serializeResult instanceof Ok<?, ?>);
    Node node = ((Ok<Node, CompileError>) serializeResult).value();

    // Deserialize
    Result<MyRecord, CompileError> deserializeResult = JavaSerializer.deserialize(MyRecord.class, node);
    assertTrue(deserializeResult instanceof Ok<?, ?>);
    MyRecord roundtripped = ((Ok<MyRecord, CompileError>) deserializeResult).value();

    // Verify equality
    assertEquals(original, roundtripped);
}
```

### Test Coverage

Aim to test:

1. **Happy path** — Normal, expected usage
2. **Edge cases** — Empty lists, None values, boundary conditions
3. **Error cases** — Invalid input, missing fields, type mismatches
4. **Roundtrips** — Serialize → deserialize → equals original

## Code Patterns

### Pattern Matching on Result

```java
Result<T, E> result = operation();

// Pattern 1: If-instanceof
if (result instanceof Ok<T, E>(T value)) {
    // Use value
} else if (result instanceof Err<T, E>(E error)) {
    // Handle error
}

// Pattern 2: Switch expression
String message = switch (result) {
    case Ok<T, E>(T value) -> "Success: " + value;
    case Err<T, E>(E error) -> "Error: " + error;
};
```

### Pattern Matching on Option

```java
Option<T> option = getValue();

// Pattern 1: If-instanceof
if (option instanceof Some<T>(T value)) {
    // Use value
} else {
    // Handle absence
}

// Pattern 2: Method chaining
option
    .map(value -> transform(value))
    .orElse(defaultValue);
```

### Chaining Results

```java
Result<String, CompileError> result = readFile(path)
    .flatMap(content -> parse(content))
    .flatMap(ast -> transform(ast))
    .map(output -> generate(output));
```

### Handling Option in Records

```java
// Creating
Option<String> name = hasName ? Option.of(nameValue) : Option.empty();
MyRecord record = new MyRecord(name);

// Extracting
String displayName = record.name()
    .map(n -> n)
    .orElse("Unknown");

// Transforming
Option<Integer> length = record.name()
    .map(String::length);
```

## Troubleshooting

### Compilation Errors

**Error: "cannot find symbol"**

```bash
# Clean and rebuild
mvn clean compile
```

Check imports and ensure Java 24 is configured.

**Error: "pattern matching not available"**

Ensure `--enable-preview` is set in compiler options (already configured in `pom.xml`).

### Test Failures

**"Leftover fields detected"**

Your Node has extra fields that aren't in the target record. Either:

1. Remove fields from Node, or
2. Add fields to record definition

**"Type mismatch: expected string but found node"**

The parser is producing a node field where a string is expected. Check:

1. Parser rule — should use `String("field", ...)` not `Node("field", ...)`
2. Record definition — should have `String field` not `SomeType field`

**"Unknown tag: xyz"**

The node has type "xyz" but no record with `@Tag("xyz")` exists. Check:

1. Tag annotation on record
2. Sealed interface `permits` clause includes the record
3. Parser rule uses correct `Tag("xyz", ...)`

### Checkstyle Violations

**"Only one loop allowed per method"**

Extract inner loops into helper methods:

```java
// Before
for (Item item : items) {
    for (SubItem sub : item.subs()) {
        process(sub);
    }
}

// After
for (Item item : items) {
    processSubItems(item);
}

private void processSubItems(Item item) {
    for (SubItem sub : item.subs()) {
        process(sub);
    }
}
```

### Runtime Errors

**`ClassCastException` in pattern matching**

Ensure you're checking the correct type:

```java
// Wrong
if (result instanceof Ok(String value)) { ... }  // Missing type params

// Right
if (result instanceof Ok<String, CompileError>(String value)) { ... }
```

**`NullPointerException`**

Use `Option<T>` instead of nullable references. If you must use null:

```java
// Check for null
if (Objects.nonNull(value)) {
    use(value);
}
```

## Best Practices

1. **Always run tests** after changes: `mvn test`
2. **Use pattern matching** instead of `.get()` or type casts
3. **Return `Result<T, E>`** for operations that can fail
4. **Use `Option<T>`** instead of nullable fields
5. **Add `@Tag` annotations** to all ADT records
6. **Update documentation** for significant changes
7. **Keep methods small** (Checkstyle enforces one loop per method)
8. **Write tests first** when fixing bugs
9. **Use descriptive names** for records, fields, and methods
10. **Check for exhaustiveness** — sealed interfaces should cover all cases

## Additional Resources

- [Architecture Guide](ARCHITECTURE.md) — System architecture overview
- [Documentation Index](INDEX.md) — All feature documentation
- [Main README](../README.md) — Project overview and quick start
- [Field Validation Feature](../FIELD_VALIDATION_FEATURE.md) — Validation system

## Getting Help

1. Check existing documentation in `docs/`
2. Look for similar tests in `src/test/java/`
3. Inspect `Node.toString()` output to understand parsing
4. Add debug `System.out.println()` statements
5. Run with `-X` flag for verbose Maven output

Happy coding! 🚀
