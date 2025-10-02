# Type Mismatch Validation

## Summary

Added validation to `Serialize.deserializeOptionField` to detect when an `Option<String>` field encounters data of the
wrong type (node or list). This prevents silent data loss by producing a clear error message.

## The Validation

### Location

`src/main/java/magma/compile/Serialize.java` - `deserializeOptionField` method

### Behavior

**Before**: When deserializing `Option<String>` field:

- If string found: return `Some(string)` ✓
- If nothing found: return `Option.empty()` ✓
- **If node found**: return `Option.empty()` ❌ (silent data loss!)
- **If list found**: return `Option.empty()` ❌ (silent data loss!)

**After**: When deserializing `Option<String>` field:

- If string found: return `Some(string)` ✓
- If nothing found: return `Option.empty()` ✓
- **If node found**: return error with message ✓
- **If list found**: return error with message ✓

### Error Messages

```
Field 'body' of type 'Option<String>' found a node instead of string in 'method'
```

```
Field 'body' of type 'Option<String>' found a list instead of string in 'method'
```

## Example: The Original Bug

### Scenario

```java
// Parser rule (in Lang.java)
private static Rule Method() {
    Rule withBody = Suffix(First(header, "{", Statements("body", JFunctionSegment())), "}");
    //                                          ^^^^^^^^^^
    //                                          Produces a LIST
}

// Data model (in Lang.java)
public record Method(..., Option<String> body, ...) {}
//                        ^^^^^^^^^^^^^^^^
//                        Expects a STRING
```

### What Happened

1. **Lexing**: Method with body `{ int x = 0; }` lexed successfully
2. **Parser**: Created node with `"body"` as **list of JFunctionSegment nodes**
3. **Deserialization**: Tried to deserialize `Method` record
    - Field `body` has type `Option<String>`
    - Node has `"body"` as **list**
    - **OLD**: Returned `Option.empty()` - silently dropped the body
    - **NEW**: Returns error - "found a list instead of string"

### Impact

**Without Validation**:

- Methods deserialized with empty bodies
- Generated C++ had signatures but no implementations
- No error, no warning - silent data loss
- Bug only visible when checking generated output

**With Validation**:

- Immediate error at deserialization time
- Clear message indicating type mismatch
- Points to exact field and record type
- Prevents silent data loss

## Testing

### Tests Added

1. **TypeMismatchValidationTest** - Verifies validation behavior
2. **ValidationDemonstrationTest** - Shows before/after behavior
3. **SimpleClassWithMethodTest** - Confirms fix works correctly

### Results

All tests pass with correct types:

- 40 total tests
- 39 passing (1 pre-existing failure unrelated to this change)
- Validation active and working

## Prevention

This validation prevents entire classes of bugs:

1. **Grammar/Model Mismatches**: When parser rule changes but record doesn't
2. **Copy-Paste Errors**: Wrong type used in record definition
3. **Refactoring Mistakes**: Type changed in one place but not another
4. **Silent Data Loss**: Optional fields silently becoming empty

## Future Considerations

The same validation pattern could be applied to:

- `Option<Node>` fields (detect when string/list present instead)
- `Option<List<T>>` fields (detect when string/node present instead)
- Required fields (never return default when wrong type present)

This would create comprehensive type safety at the deserialization boundary.
