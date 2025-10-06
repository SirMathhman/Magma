# `tryDirectPermittedSubclasses` Refactoring

## What Changed

Refactored `tryDirectPermittedSubclasses` in `JavaSerializer.java` to return `Option<Result<Object, CompileError>>` instead of `Result<Object, CompileError>`.

## Why

The previous return type was confusing because it conflated two distinct scenarios:

1. **Operation not applicable** (`Err` with "No direct match found"): When the nodeType doesn't match any permitted subclass tag
2. **Operation applicable but failed** (`Err` with deserialization error): When a matching subclass is found, but deserialization of that subclass fails

The new return type `Option<Result<Object, CompileError>>` clearly distinguishes these cases:

- `None`: No permitted subclass matches the nodeType (operation not applicable)
- `Some(Ok(value))`: Found a matching subclass and successfully deserialized it
- `Some(Err(error))`: Found a matching subclass but deserialization failed

## How to Verify

Run the sealed interface deserialization test:

```powershell
mvn -Dtest=SealedInterfaceDeserializationTest test
```

Expected result: All tests pass. The test exercises sealed interface deserialization with nested permitted subclasses, verifying that:
- Direct matches work correctly
- Nested sealed interfaces are properly handled
- Error messages are clear when no match is found

## Files Modified

- `src/main/java/magma/compile/JavaSerializer.java`:
  - Changed `tryDirectPermittedSubclasses` return type from `Result<Object, CompileError>` to `Option<Result<Object, CompileError>>`
  - Removed error return for "No direct match found" (now returns `None`)
  - Wrapped successful/failed deserialization in `Some(...)`
  - Updated caller in `deserializeSealed` to pattern match on `Some` to extract the result

## Related Patterns

This refactoring follows the project's pattern of using `Option` to represent "maybe applicable" semantics and `Result` for "applicable but may fail" semantics, consistent with usage in `tryDeserializeNestedSealed` and other similar methods.
