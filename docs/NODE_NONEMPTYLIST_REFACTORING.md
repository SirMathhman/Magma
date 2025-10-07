# Node.nodeLists NonEmptyList Refactoring

## Summary

Refactored `Node.nodeLists` from `Map<String, List<Node>>` to `Map<String, NonEmptyList<Node>>` to enforce the semantic invariant that **if a node list property is absent from the map, it was empty**.

## Motivation

The original design allowed storing empty lists in `Node.nodeLists`, which violated a key semantic principle: if a list property is empty, it shouldn't be present in the serialized node at all. This was already partially enforced in practice (e.g., `serializeListField` in `JavaSerializer` returned an empty `Node()` for empty lists rather than calling `withNodeList`), but the type system didn't enforce it.

By using `NonEmptyList<Node>` instead of `List<Node>`, we make this invariant explicit and catch violations at compile time.

## What Changed

### Core Type Changes

**File**: `src/main/java/magma/compile/Node.java`

- Changed field type: `Map<String, NonEmptyList<Node>> nodeLists`
- Updated `withNodeList(String, NonEmptyList<Node>)` signature
- Updated `findNodeList(String)` to return `Option<NonEmptyList<Node>>`
- Removed `isEmpty()` check in `extracted()` method since `NonEmptyList` is never empty
- Removed unused `magma.list.List` import

### Serialization Updates

**File**: `src/main/java/magma/compile/JavaSerializer.java`

- Added `NonEmptyList` import
- Updated `serializeListField()` and `serializeNonEmptyListField()` to convert `List<Node>` results to `NonEmptyList<Node>` before calling `withNodeList`
  - Empty lists return an empty `Node()` (no field added)
  - Non-empty lists are converted via `NonEmptyList.fromList()` and then added
- Updated all deserialization methods to work with `Option<NonEmptyList<Node>>`:
  - `deserializeOptionListField()`: Convert `NonEmptyList` to `List` via `toList()`
  - `deserializeNonEmptyListField()`: Removed redundant `isEmpty()` check (impossible for `NonEmptyList`)
  - `deserializeListField()`: Convert `NonEmptyList` to `List` via `toList()`
- Updated `findStringInNodeLists()` to iterate over `NonEmptyList<Node>` values and convert each to `List` for processing

### Rule Updates

**File**: `src/main/java/magma/compile/rule/NodeListRule.java`

- Added `NonEmptyList` import
- Updated `lex()` to only call `withNodeList` when the parsed list is non-empty
  - Empty lists now return an empty `Node()` instead of calling `withNodeList`
- Updated `generateList()` signature to accept `NonEmptyList<Node>`
- Removed `isEmpty()` check from `generateList()` (impossible for `NonEmptyList`)

**File**: `src/main/java/magma/compile/rule/NonEmptyListRule.java`

- Changed import from `magma.list.List` to `magma.list.NonEmptyList`
- Simplified `generate()` method by removing the `isEmpty()` guard case
  - Since `NonEmptyList` can never be empty, the pattern match only needs `None` and `Some` cases

## Semantic Guarantees

### Before

- `node.findNodeList(key)` returning `Some(list)` meant the key was present, but `list` could be empty
- Empty lists could be stored in the map, creating ambiguity: was the field explicitly empty, or never set?

### After

- `node.findNodeList(key)` returning `Some(nonEmptyList)` **guarantees** the list has at least one element
- `node.findNodeList(key)` returning `None` means either:
  - The field was never set, OR
  - The field was explicitly empty (and therefore not stored)
- These two cases are semantically equivalent: "no data for this field"

## Migration Notes

Any code that previously called `withNodeList` with a potentially empty `List<Node>` must now:

1. Check if the list is empty first
2. If empty, don't call `withNodeList` (just return an empty `Node()`)
3. If non-empty, convert to `NonEmptyList` via `NonEmptyList.fromList(list)` before calling `withNodeList`

Example:

```java
// Before
return new Node().withNodeList(fieldName, nodes);

// After
if (nodes.isEmpty())
    return new Node();
return NonEmptyList.fromList(nodes)
        .map(nonEmptyNodes -> new Node().withNodeList(fieldName, nonEmptyNodes))
        .orElse(new Node()); // Should never happen if we checked isEmpty
```

Any code that previously received a `List<Node>` from `findNodeList` must now handle `NonEmptyList<Node>` and convert if needed:

```java
// Before
Option<List<Node>> maybeList = node.findNodeList(fieldName);
if (maybeList instanceof Some<List<Node>>(List<Node> value)) {
    // use value directly
}

// After
Option<NonEmptyList<Node>> maybeList = node.findNodeList(fieldName);
if (maybeList instanceof Some<NonEmptyList<Node>>(NonEmptyList<Node> value)) {
    List<Node> list = value.toList(); // convert if needed
    // use list
}
```

## Verification

Run the build to verify all changes:

```powershell
mvn clean compile
```

Expected result: BUILD SUCCESS with no compilation errors related to this change.

## Benefits

1. **Type Safety**: The type system now enforces that node lists in the map are always non-empty
2. **Clearer Semantics**: Absence from the map explicitly means "empty or unset"
3. **Fewer Runtime Checks**: Code can assume retrieved lists are non-empty
4. **Better Documentation**: The type signature documents the invariant
5. **Consistency**: Aligns with the existing `NonEmptyList` usage in other parts of the codebase (see `NONEMPTYLIST_INTRODUCTION.md`)

## Related Documentation

- `NONEMPTYLIST_INTRODUCTION.md` — Introduction to the `NonEmptyList<T>` type and its usage patterns
- `FIELD_VALIDATION_FEATURE.md` — Field validation feature that relies on `node.nodeLists.keySet()`
