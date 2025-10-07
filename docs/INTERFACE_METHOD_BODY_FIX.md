# Interface Method Body Fix

## Problem

The `mvn exec:java` command was failing with a deserialization error:

```
0.0.0.0.0.0.0.0.0.0) Required component 'body' of type 'NonEmptyList' not present
```

This occurred when trying to deserialize interface methods (e.g., `String stringify();` in the `CType` interface in `CLang.java`).

## Root Cause

The `JMethod` record in `Lang.java` (line 320) defined the `body` field as:
```java
NonEmptyList<JMethodSegment> body
```

However, the parsing rule for methods in `JMethod()` (line 737) allowed two alternatives:
1. Methods with a semicolon (abstract/interface methods): `Suffix(header, ";")`
2. Methods with a body (concrete methods): `withBody`

Interface methods don't have bodies—they're just method signatures ending with semicolons. When the deserializer tried to create a `JMethod` record for such methods, it failed because the required `body` field was missing from the serialized data.

## Solution

Changed the `body` field in the `JMethod` record from a required `NonEmptyList` to an optional `Option<NonEmptyList>`:

**File: `src/main/java/magma/compile/Lang.java` (line 320)**
```java
// Before:
public record JMethod(JDefinition definition, Option<NonEmptyList<JDefinition>> params, 
                      NonEmptyList<JMethodSegment> body,
                      Option<NonEmptyList<Identifier>> typeParameters) implements JStructureSegment

// After:
public record JMethod(JDefinition definition, Option<NonEmptyList<JDefinition>> params, 
                      Option<NonEmptyList<JMethodSegment>> body,
                      Option<NonEmptyList<Identifier>> typeParameters) implements JStructureSegment
```

Additionally, updated the `Transformer.transformMethod()` to handle the optional body:

**File: `src/main/java/magma/transform/Transformer.java` (line 38)**
```java
// Before:
final NonEmptyList<Lang.CFunctionSegment> bodySegments = method.body()
    .stream()
    .map(Transformer::transformFunctionSegment)
    .collect(new NonEmptyListCollector<>())
    .orElse(NonEmptyList.of(new Lang.Invalid("???")));

// After:
final NonEmptyList<Lang.CFunctionSegment> bodySegments = method.body()
    .map(body -> body.stream()
                      .map(Transformer::transformFunctionSegment)
                      .collect(new NonEmptyListCollector<Lang.CFunctionSegment>())
                      .orElse(NonEmptyList.of(new Lang.Invalid("???"))))
    .orElse(NonEmptyList.of(new Lang.Invalid("???")));
```

## Verification

Run the following command to verify the fix:
```bash
mvn exec:java
```

**Expected result:** The build completes with `BUILD SUCCESS` and no longer shows the "Required component 'body' of type 'NonEmptyList' not present" error. The deserialization now correctly handles both:
- Interface methods (no body, just semicolon)
- Concrete methods (with body in braces)

## Impact

- Abstract/interface methods can now be properly deserialized
- The `JMethod` record is more semantically accurate (bodies are truly optional for interface methods)
- Code that uses `JMethod.body()` must now handle the `Option` wrapper (see `Transformer.transformMethod()` for the pattern)
