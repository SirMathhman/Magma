# Field Consumption Validation Feature

## Overview

The Magma serialization framework now includes **field consumption validation** to ensure a **1-1 correspondence**
between Node fields and ADT (Algebraic Data Type) fields during deserialization. This feature detects when a Node
contains fields that are not consumed during the deserialization process, which indicates an incomplete or incorrect
mapping between the data structure and the target type.

## Problem Solved

Previously, if a Node contained extra fields that weren't part of the target ADT, these fields would be silently ignored
during deserialization. This could mask data structure mismatches and lead to subtle bugs where important data was lost
without any indication of the problem.

## How It Works

### Field Tracking

During deserialization, the framework now:

1. **Tracks consumed fields**: As each record component is deserialized, the corresponding Node field name is added to a
   `Set<String> consumedFields`.

2. **Validates completeness**: After all components are processed, the framework checks if any Node fields were left
   unconsumed.

3. **Reports errors**: If leftover fields are found, a `CompileError` is generated listing all unconsumed fields.

### Validation Logic

The validation occurs in the `validateAllFieldsConsumed` method:

```java
private static Option<CompileError> validateAllFieldsConsumed(
    Node node,
    Set<String> consumedFields,
    Class<?> targetClass
) {
    // Collect all field names from the Node
    Set<String> allFields = new HashSet<>();
    allFields.addAll(node.getStringKeys());
    allFields.addAll(node.nodes.keySet());
    allFields.addAll(node.nodeLists.keySet());

    // Find fields that were not consumed
    Set<String> leftoverFields = new HashSet<>(allFields);
    leftoverFields.removeAll(consumedFields);

    if (!leftoverFields.isEmpty()) {
        // Generate error with details about leftover fields
        return Option.of(new CompileError(...))
		}

    return Option.empty();
}
```

## Field Types Tracked

The validation tracks consumption of all three types of Node fields:

- **String fields** (`node.strings`): Simple key-value string pairs
- **Node fields** (`node.nodes`): Nested object fields
- **Node list fields** (`node.nodeLists`): Arrays/lists of nested objects

## Examples

### ✅ Successful Deserialization

```java

@Tag("Person")
public record Person(String name, String email) {}

Node node = new Node().retype("Person").withString("name", "Alice").withString("email", "alice@example.com");

// ✅ SUCCESS: All fields consumed
Result<Person, CompileError> result = Serialize.deserialize(Person.class, node);
```

### ❌ Leftover Fields Detected

```java

@Tag("Person")
public record Person(String name) {}  // Only expects 'name'

Node node = new Node().retype("Person")
											.withString("name", "Alice")
											.withString("email", "alice@example.com")  // ❌ Leftover field
											.withString("phone", "555-1234");          // ❌ Leftover field

// ❌ FAILURE: Leftover fields [email, phone] detected
Result<Person, CompileError> result = Serialize.deserialize(Person.class, node);
```

### Error Message

When leftover fields are detected, you'll see an error like:

```
Incomplete deserialization for 'Person': leftover fields [email, phone] were not consumed.
This indicates a mismatch between the Node structure and the target ADT.
```

## Optional Fields

Optional fields are handled correctly:

- If an `Option<T>` field is present in the Node, it's consumed normally
- If an `Option<T>` field is absent from the Node, no error occurs (the Optional remains empty)
- Only actual extra fields that don't correspond to any record component cause errors

## Nested Validation

The validation is recursive - nested objects are also validated for field consumption. If a nested object has leftover
fields, the deserialization will fail with appropriate error context.

## Benefits

1. **Data Integrity**: Ensures no data is silently lost during deserialization
2. **Type Safety**: Catches mismatches between Node structure and target ADT at runtime
3. **Debugging Aid**: Clear error messages help identify structural inconsistencies
4. **Contract Enforcement**: Enforces the expectation of exact correspondence between data and types

## Migration Notes

This is a **breaking change** for code that previously relied on extra fields being silently ignored. If you have
existing Nodes with extra fields that should be ignored, you'll need to either:

1. Remove the extra fields from the source Nodes
2. Add corresponding fields to your ADT definitions
3. Consider if the extra fields represent important data that should be preserved

## Implementation Details

- Added `Set<String> consumedFields` parameter to deserialization methods
- Modified all field consumption points to track field names
- Added `getStringKeys()` method to `Node` class to expose string field names
- Added comprehensive validation at the end of the deserialization process
- Maintained backward compatibility for all successful deserialization cases

The feature ensures robust data handling while providing clear feedback when structural mismatches occur.
