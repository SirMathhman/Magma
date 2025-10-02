# Unknown Tag Validation Fix

## Problem

The serializer was silently ignoring nodes with unknown tags (tags that don't correspond to any record type in the
sealed interface hierarchy). For example, when an `if` statement node with `@type: "if"` was parsed but no corresponding
`If` record exists in the `JFunctionSegment` sealed interface, the node was silently skipped during deserialization
rather than producing an error.

## Root Cause

In `Serialize.deserializeListElements()`, when a node failed to deserialize, the code used `shouldBeDeserializableAs()`
to decide whether to report an error or silently skip it. This function returned `false` for nodes with unknown tags,
causing them to be silently ignored.

## Solution

Modified `Serialize.deserializeListElements()` to distinguish between:

1. **Unknown tag errors** (sealed type + node has type tag): Always report as error
2. **Expected mismatches** (non-sealed or `shouldBeDeserializableAs` check): Only report if it looks like it should
   match
3. **Contextual skips** (e.g., whitespace in wrong context): Silently skip

Also enhanced `Serialize.deserializeSealed()` to:

- Recursively collect all valid tags from nested sealed interfaces
- Provide comprehensive error messages listing all permitted tags

## Changes

### Modified Files

- `src/main/java/magma/compile/Serialize.java`
    - Enhanced `deserializeSealed()` to report all valid tags in error messages
    - Added `collectAllValidTags()` helper method
    - Modified `deserializeListElements()` to treat unknown tags as errors for sealed types

### Test Files

- `src/test/java/IfStatementTest.java` - New test demonstrating unknown tag detection
- `src/test/java/ValidationDemonstrationTest.java` - Fixed compile errors

## Verification

Run the test:

```bash
mvn test -Dtest=IfStatementTest
```

The test confirms that code containing an `if` statement (which has no corresponding `If` record in `JFunctionSegment`)
now produces a deserialization error with a message indicating the unknown tag.

## Example Error Message

When encountering an unknown `"if"` tag:

```
No permitted subtype of 'magma.compile.Lang$JFunctionSegment' matched node type 'if'. 
Valid tags are: [invalid, placeholder, whitespace]
```

This makes it immediately clear that:

1. The `"if"` tag is not recognized
2. The valid options for `JFunctionSegment` are listed
3. Either the grammar needs updating or a corresponding record type needs to be added

## Future Work

When `if` statements are fully implemented, create an `If` record type:

```java
@Tag("if")
public record If(/* fields */) implements JFunctionSegment {}
```

And add it to the `JFunctionSegment` sealed interface's `permits` clause.
