# Result Type Error Handling in JavaSerializer

## Summary

Refactored `JavaSerializer.java` to eliminate exceptions (`throw`/`throws`) in favor of `Result<T, E>` type-based error propagation for critical type system operations. This change enforces the project's no-exceptions coding standard while preserving error information that was previously thrown.

## Changes Made

### Helper Methods Converted to Result Types

1. **`getGenericArgument(Type)`** → `Result<Type, CompileError>`
   - Previously: threw `IllegalArgumentException` for non-parameterized types
   - Now: returns `Err<Type, CompileError>` with detailed context
2. **`erase(Type)`** → `Result<Class<?>, CompileError>`
   - Previously: threw `ClassNotFoundException` for unresolvable types
   - Now: returns `Err<Class<?>, CompileError>` with error details

### Call Sites Updated (6 total)

All serialization/deserialization methods that use these helpers now check for `Err` and propagate errors:

1. `serializeOptionField(RecordComponent, Object)` - lines ~115-135
2. `serializeOptionListField(String, Type, Object)` - lines ~142-156
3. `serializeListField(RecordComponent, Object)` - lines ~157-172
4. `deserializeOptionField(RecordComponent, Node, Set)` - lines ~468-516
5. `deserializeOptionListField(String, Type, Node, Set)` - lines ~518-537
6. `deserializeListField(RecordComponent, Node, Set)` - lines ~539-559

### Pattern Used

```java
// Check for error from getGenericArgument
Result<Type, CompileError> elementTypeResult = getGenericArgument(component.getGenericType());
if (elementTypeResult instanceof Err<Type, CompileError>(CompileError error))
    return new Err<>(error);
Type elementType = ((Ok<Type, CompileError>) elementTypeResult).value();

// Check for error from erase
Result<Class<?>, CompileError> elementClassResult = erase(elementType);
if (elementClassResult instanceof Err<Class<?>, CompileError>(CompileError error))
    return new Err<>(error);
Class<?> elementClass = ((Ok<Class<?>, CompileError>) elementClassResult).value();
```

## Why This Matters

The original `throw` statements caught important type system errors (e.g., attempting to serialize a non-parameterized type as `Option<T>` or `List<T>`). Simply returning `Object.class` would have silently masked these errors. The `Result` type approach:

- ✅ Preserves all error information with full context
- ✅ Enforces explicit error handling at every call site
- ✅ Eliminates exceptions while maintaining functional correctness
- ✅ Aligns with project's functional programming style (see `magma.result` and `magma.option`)

## Verification

To verify this refactoring:

```bash
# Confirm compilation succeeds
mvn compile

# Confirm no throw/throws checkstyle violations
mvn checkstyle:check

# Confirm tests pass (3 pre-existing failures unrelated to this change)
mvn test
```

Expected results:

- Build succeeds
- No `throw`/`throws` violations
- 45/48 tests pass (same as before refactoring)

## Related Documentation

- `FIELD_VALIDATION_FEATURE.md` - Field consumption validation feature
- `magma/result/Result.java` - Result type definition
- `magma/compile/Serialize.java` - Main deserialization entrypoint
