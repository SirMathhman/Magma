# Unknown Tag Validation and Error Message Improvements

## Problem

The serializer was silently ignoring nodes with unknown tags (tags that don't correspond to any record type in the
sealed interface hierarchy). For example, when an `if` statement node with `@type: "if"` was parsed but no corresponding
`If` record exists in the `JFunctionSegment` sealed interface, the node was silently skipped during deserialization
rather than producing an error.

Additionally, when deserialization failed deep in a nested structure, error messages were confusing and misleading,
pointing to the wrong level of the hierarchy (e.g., reporting `"class"` as an unknown tag when the real issue was an
`"if"` tag deeper in the tree).

## Root Cause

In `Serialize.deserializeListElements()`, when a node failed to deserialize, the code used `shouldBeDeserializableAs()`
to decide whether to report an error or silently skip it. This function returned `false` for nodes with unknown tags,
causing them to be silently ignored.

When errors did occur, the error propagation from nested sealed types wasn't properly distinguishing between "tag
doesn't match" and "tag matches but deserialization failed for another reason", leading to misleading error messages at
higher levels of the hierarchy.

## Solution

### 1. Unknown Tag Detection

Modified `Serialize.deserializeListElements()` to distinguish between:

1. **Unknown tag errors** (sealed type + node has type tag): Always report as error
2. **Expected mismatches** (non-sealed or `shouldBeDeserializableAs` check): Only report if it looks like it should
   match
3. **Contextual skips** (e.g., whitespace in wrong context): Silently skip

### 2. Improved Error Propagation

Enhanced `Serialize.deserializeSealed()` to:

- Check if a recursive deserialization attempt would have matched the tag (using `canMatchType()`)
- If the tag matches but deserialization failed for other reasons, propagate that nested error instead of generating a
  misleading "unknown tag" error
- Recursively collect all valid tags from nested sealed interfaces for comprehensive error messages

### 3. Actionable Error Messages

Added helper methods to provide actionable suggestions:

- `getSuggestionForUnknownTag()`: Provides helpful guidance based on the error context
- `findClosestTag()`: Uses Levenshtein distance to suggest similar valid tags
- Enhanced list error messages to include element indices and counts

### 4. Better Error Context

Improved `deserializeListElements()` to:

- Track the index of each element being deserialized
- Wrap errors with context about which element failed and why
- Provide statistics (e.g., "Failed to deserialize 1 of 2 list elements")

## Changes

### Modified Files

- `src/main/java/magma/compile/Serialize.java`
    - Enhanced `deserializeSealed()` to check `canMatchType` and propagate nested errors appropriately
    - Added `collectAllValidTags()` helper method to recursively gather valid tags
    - Added `canMatchType()` to check if a tag could match in a sealed hierarchy
    - Added `getSuggestionForUnknownTag()` to provide actionable error guidance
    - Added `findClosestTag()` with Levenshtein distance to suggest alternatives
    - Added `levenshteinDistance()` for string similarity calculation
    - Modified `deserializeListElements()` to provide element indices and better context
    - Changed error messages to use `getSimpleName()` for better readability

### Test Files

- `src/test/java/IfStatementTest.java` - Demonstrates unknown tag detection with clear error messages
- `src/test/java/ValidationDemonstrationTest.java` - Fixed compile errors

### Grammar Files

- `src/main/java/magma/compile/Lang.java`
    - Added back `If(rule)` to `JMethodSegment()` for testing
    - Changed `JExpression()` to return `Invalid()` instead of empty `Or()` to allow if statements to parse

## Verification

Run the test:

```bash
mvn test -Dtest=IfStatementTest
```

The test confirms that code containing an `if` statement (which has no corresponding `If` record in `JFunctionSegment`)
now produces a clear, actionable deserialization error.

## Example Error Messages

### Before (Confusing)

```
No permitted subtype of 'magma.compile.Lang$JavaRootSegment' matched node type 'class'.
Valid tags are: [invalid, import, interface, class, record, package, whitespace]
```

_Problem: Says "class" doesn't match, but "class" IS in the valid tags list!_

### After (Clear and Actionable)

```
Element at index 0 with type 'class' cannot be deserialized as 'JavaRootSegment':
  Element at index 0 with type 'method' cannot be deserialized as 'JStructureSegment':
    Element at index 0 with type 'if' cannot be deserialized as 'JFunctionSegment':
      No permitted subtype of 'JFunctionSegment' matched node type 'if'.
      Valid tags are: [invalid, placeholder, whitespace].
      Add a record type with @Tag("if") and include it in the 'permits' clause of 'JFunctionSegment'.
```

This makes it immediately clear that:

1. The error chain traces from the root down to the actual problem
2. Each level shows which element failed and what type it was trying to deserialize as
3. The final error identifies the unknown `"if"` tag
4. Valid alternatives are listed
5. Actionable guidance is provided (add a record type with the appropriate tag)

## Future Work

When `if` statements are fully implemented:

1. Create an `If` record type:

```java
@Tag("if")
public record If(Node condition, Node body) implements JFunctionSegment {}
```

2. Add it to the `JFunctionSegment` sealed interface's `permits` clause:

```java
sealed public interface JFunctionSegment permits Invalid, Placeholder, Whitespace, If {}
```

3. Implement the C++ transformation logic in `Main.java` to handle `If` nodes

## Benefits

1. **No more silent failures**: Unknown tags are always caught and reported
2. **Clear error chains**: Error messages trace the full path from root to problem
3. **Actionable guidance**: Suggestions tell you exactly what to fix
4. **Better debugging**: Element indices and counts help locate issues quickly
5. **Typo detection**: Levenshtein distance suggests corrections for misspelled tags
