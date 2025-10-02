# Placeholder Statement and Function Body Architecture Fix

## Problem

Method bodies were being parsed by the lexer, but function statements were not being captured properly during
deserialization. Additionally, even when captured, the statements were not appearing in the generated C++ output.
Multiple serialization and architecture bugs were involved.

## Root Causes

### Bug 1: Deserialization - Missing Placeholder Support

The `JFunctionSegment()` parsing rule was defined as:

```java
private static Rule JFunctionSegment() {
    return Or(Whitespace(), Invalid());
}
```

This meant that statements were being parsed as `Invalid` instances, but the `Placeholder` record type (which is meant
to temporarily wrap unparsed statements) was:

1. Missing from the `JFunctionSegment` sealed interface permits list
2. Missing the `@Tag("placeholder")` annotation required for deserialization
3. Not being used in the parsing rule

### Bug 2: Generation - Function Body Type Mismatch

The `Function` record initially had `body` as a `String` field, but the `Lang.Function()` generation rule expected body
as a node list of segments. This caused a type mismatch where:

- The parsing rule expected: `Statements("body", CFunctionSegment())`
- The record had: `String body`
- Result: Empty function bodies in generated C++ even though the body was set

### Bug 3: Architecture - Missing CFunctionSegment Type

C++ functions should mirror Java methods' structure. Java methods have `Option<List<JFunctionSegment>> body`, so C++
functions should have `List<CFunctionSegment> body`, not `String body`.

## Solution

The fix required adding a `CFunctionSegment` sealed interface (parallel to `JFunctionSegment`) and ensuring both Java
and C++ function segments use the same placeholder mechanism:

### 1. Add sealed interfaces for both Java and C++ function segments (Lines 41-42)

```java
sealed public interface JFunctionSegment permits Invalid, Placeholder, Whitespace {}
sealed public interface CFunctionSegment permits Invalid, Placeholder, Whitespace {}
```

### 2. Update record types to implement both interfaces (Lines 83, 105, 107-108)

```java
@Tag("invalid")
public record Invalid(String value, Option<String> after)
    implements JavaRootSegment, JStructureSegment, CRootSegment, JavaType, CType, JFunctionSegment, CFunctionSegment {}

public record Whitespace() implements JavaRootSegment, JStructureSegment, JFunctionSegment, CFunctionSegment {}

@Tag("placeholder")
public record Placeholder(String value) implements JFunctionSegment, CFunctionSegment {}
```

The `@Tag("placeholder")` annotation is critical because `Serialize.resolveTypeIdentifier()` uses it to map the node
type `"placeholder"` from the lexed output to the `Placeholder` class during deserialization.

### 3. Update Function record to use List<CFunctionSegment> body (Line 128)

```java
@Tag("function")
public record Function(CDefinition definition, List<CParameter> params, List<CFunctionSegment> body, Option<String> after,
                       Option<List<Identifier>> typeParameters) implements CRootSegment {}
```

### 4. Add parsing rules for both segment types (Lines 279-285)

```java
private static Rule JFunctionSegment() {
    return Or(Whitespace(), Tag("placeholder", Placeholder(String("value"))));
}

private static Rule CFunctionSegment() {
    return Or(Whitespace(), Tag("placeholder", Placeholder(String("value"))));
}
```

### 5. Update Function() generation rule to use Statements (Line 153)

```java
public static Rule Function() {
    ...
    final Rule body = Statements("body", CFunctionSegment());
    ...
}
```

### 6. Update Main.java to cast between segment types safely (Lines 200-213)

```java
// Convert method body from Option<List<JFunctionSegment>> to List<CFunctionSegment>
// Safe cast because both interfaces permit the same types: Invalid, Placeholder, Whitespace
final List<CFunctionSegment> bodySegments = switch (method.body()) {
    case None<List<JFunctionSegment>> _ -> Collections.emptyList();
    case Some<List<JFunctionSegment>>(var segments) -> {
        @SuppressWarnings("unchecked")
        List<CFunctionSegment> cSegments = (List<CFunctionSegment>) (List<?>) segments;
        yield cSegments;
    }
};
```

## Current State

- **Method bodies (Java)**: Parsed as `Option<List<JFunctionSegment>>` where statements are `Placeholder` instances
- **Function bodies (C++)**: Parsed as `List<CFunctionSegment>` where statements are also `Placeholder` instances
- **Shared implementations**: `Invalid`, `Placeholder`, and `Whitespace` implement both `JFunctionSegment` and
  `CFunctionSegment`
- **Safe casting**: Main.java can cast between the segment types because they have identical permitted types
- **Generation**: PlaceholderRule wraps content in `/* ... */` comments during C++ generation
- **Test coverage**: 44 out of 45 tests passing (1 pre-existing MainIntegrationTest error unrelated to this fix)

## Verification

Run tests with:

```bash
mvn test
```

Check generated C++ in `src/main/windows/magma/Main.cpp` - function bodies now contain placeholder comments:

```cpp
void main_Main(char** args) {/*
		if (run() instanceof Some<ApplicationError>(ApplicationError value)) System.err.println(value.display());*/}
```

All method statements are preserved as placeholder-wrapped comments in the generated C++.

## Architecture Benefits

1. **Type Safety**: Sealed interfaces ensure only valid segment types (Invalid, Placeholder, Whitespace)
2. **Code Reuse**: Shared record implementations between Java and C++ segments
3. **Safe Casting**: Transformation can cast between segment types because they have identical permitted types
4. **Extensibility**: When expanding placeholder parsing to actual statements, just add new records that implement the
   appropriate interface
5. **Symmetry**: Java methods and C++ functions have parallel structures (both use segment lists for bodies)
