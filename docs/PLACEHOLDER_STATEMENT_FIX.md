# Placeholder Statement Parsing Fix

## Problem

Method bodies were being parsed by the lexer, but function statements were not being captured properly during
deserialization. Additionally, even when captured, the statements were not appearing in the generated C++ output. Two
separate serialization bugs were involved.

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

The `Lang.Function()` generation rule expected body as a node list:

```java
final Rule body = Statements("body", JFunctionSegment());
```

But the `Function` record has `body` as a `String` field. During C++ generation, the rule looked for a node list but
found a string, resulting in empty function bodies in the generated C++ even though the body string was set correctly.

## Solution

Four changes were made to properly capture and generate method body statements as placeholders:

### 1. Add Placeholder to JFunctionSegment sealed interface (Line 42)

```java
sealed public interface JFunctionSegment permits Invalid, Placeholder, Whitespace {}
```

### 2. Add Placeholder record with @Tag annotation (Lines 107-108)

```java
@Tag("placeholder")
public record Placeholder(String value) implements JFunctionSegment {}
```

The `@Tag("placeholder")` annotation is critical because `Serialize.resolveTypeIdentifier()` uses it to map the node
type `"placeholder"` from the lexed output to the `Placeholder` class during deserialization.

### 3. Update JFunctionSegment parsing rule (Lines 279-281)

```java
private static Rule JFunctionSegment() {
    return Or(Whitespace(), Tag("placeholder", Placeholder(String("value"))));
}
```

This wraps each statement with:

- `Tag("placeholder", ...)` - creates a node with `@type: "placeholder"`
- `Placeholder(String("value"))` - wraps the statement text in a placeholder comment during C++ generation
- `String("value")` - captures the statement text into the `value` field

### 4. Fix Function() generation rule to expect String body (Line 153)

```java
public static Rule Function() {
    ...
    final Rule body = String("body");  // Changed from Statements("body", JFunctionSegment())
    ...
}
```

This fixes the type mismatch so that the `Function` record's String body field is properly serialized and deserialized
during C++ generation.

## Behavior

With this fix, a Java method like:

```java
public void method() {
    System.out.println("Hello");
    int x = 5;
}
```

Is now parsed into:

```java
Method(
    ...
    body = Some([
        Placeholder(value = "\n\t\tSystem.out.println(\"Hello\");"),
        Placeholder(value = "\n\t\tint x = 5;"),
        Whitespace()
    ])
)
```

The statements are preserved as `Placeholder` instances with their full text content, ready for future parsing and
transformation into proper C++ statements.

## How to Verify

Run the test:

```bash
mvn test -Dtest=DebugMethodBodyTest
```

Expected output shows:

```
Body list size: 3
  [0] Type: Placeholder, Value: Placeholder[value=...System.out.println("Hello");]
  [1] Type: Placeholder, Value: Placeholder[value=...int x = 5;]
  [2] Type: Whitespace
```

## Impact

- All 42 tests still pass (1 pre-existing MainIntegrationTest failure unrelated)
- Method bodies now contain their statements as Placeholder instances
- Future work can parse these placeholder statements into proper AST nodes
- C++ generation continues to work, with placeholder statements wrapped in `/* ... */` comments
