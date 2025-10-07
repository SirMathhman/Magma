# Fix for `Option<NonEmptyList<T>>` Deserialization

## Problem

The `JavaSerializer.deserializeOptionField` method was missing support for `Option<NonEmptyList<T>>` fields. When deserializing records like `JRoot` which has a field:

```java
public record JRoot(Option<NonEmptyList<JavaRootSegment>> children) {
}
```

The deserialization would fail with:

```
Incomplete deserialization for 'JRoot': leftover fields [children] were not consumed.
This indicates a mismatch between the Node structure and the target ADT.
```

## Root Cause

In `JavaSerializer.deserializeOptionField` (lines 573-625), the method handled:

1. `Option<String>` - special case
2. `Option<List<T>>` - via `List.class.isAssignableFrom(elementClass)` check
3. `Option<SomeRecord>` - fallback for single node

However, `NonEmptyList` is an interface that does NOT extend `List`, so the check `List.class.isAssignableFrom(elementClass)` returned false for `NonEmptyList`. This caused the method to fall through to the single-node case, which also failed, and ultimately returned `Option.empty()` **without consuming the field**.

The field validation feature (`validateAllFieldsConsumed`) correctly detected this as an error and reported the unconsumed `children` field.

## Solution

Added a check for `NonEmptyList.class.isAssignableFrom(elementClass)` before the `List` check in `deserializeOptionField`:

```java
if (NonEmptyList.class.isAssignableFrom(elementClass))
    return deserializeOptionNonEmptyListField(fieldName, elementType, node, consumedFields);

if (List.class.isAssignableFrom(elementClass))
    return deserializeOptionListField(fieldName, elementType, node, consumedFields);
```

The helper method `deserializeOptionNonEmptyListField` already existed and correctly handles the deserialization and field consumption.

## Impact

- `JRoot` and `CRoot` records can now be successfully deserialized
- Field validation correctly passes for `Option<NonEmptyList<T>>` fields
- The fix maintains type safety and proper field consumption tracking

## Verification

Run `mvn exec:java` - the previous error about unconsumed `children` field in `JRoot` no longer occurs. The compiler now successfully deserializes `JRoot` instances and processes Java files (though it may encounter other limitations in the Java parser for advanced language features).

## Files Modified

- `src/main/java/magma/compile/JavaSerializer.java` (line ~616)
