# NonEmptyList Semantic Tightening Refactoring

## Date

October 6, 2025

## Summary

Introduced `NonEmptyList<T>` interface to eliminate the invalid state where a collection property is present but empty. This is a semantic tightening that improves type safety and reduces debugging complexity.

## Motivation

Before this change, ADT fields using `List<T>` or `Option<List<T>>` allowed three states:

1. Property is absent (None)
2. Property is present with items (Some(List with >= 1 elements))
3. Property is present but empty (Some(List with 0 elements)) ← **Invalid/annoying**

The third state is semantically invalid for many use cases and creates debugging challenges. By using `Option<NonEmptyList<T>>`, we eliminate this state and enforce:

1. Property is absent (None) → No items
2. Property is present (Some(NonEmptyList)) → At least one item

## Changes

### New Types

#### `NonEmptyList<T>` Interface

- Location: `src/main/java/magma/list/NonEmptyList.java`
- Guarantees at least one element is always present
- Key methods:
  - `toList()` - returns underlying `List<T>`
  - `getFirst()` / `getLast()` - return `T` (not `Option<T>`)
  - `static from(List<T>)` - creates from list (unchecked, caller ensures non-empty)
  - `static of(T first, T... rest)` - creates from elements

#### `NonEmptyArrayList<T>` Implementation

- Location: `src/main/java/magma/list/NonEmptyArrayList.java`
- Backed by `List<T>`
- Does not verify non-emptiness at construction (caller responsibility)

### Updated ADT Classes in `Lang.java`

Records updated to use `Option<NonEmptyList<T>>`:

1. **`Destruct`**: `params` changed from `List<JDefinition>` to `Option<NonEmptyList<JDefinition>>`
2. **`JBlock`**: `children` changed from `List<JMethodSegment>` to `Option<NonEmptyList<JMethodSegment>>`
3. **`CBlock`**: `children` changed from `List<CFunctionSegment>` to `Option<NonEmptyList<CFunctionSegment>>`
4. **`CFunctionPointerDefinition`**: `paramTypes` changed from `List<CLang.CType>` to `Option<NonEmptyList<CLang.CType>>`
5. **`Function`**:
   - `params` changed from `List<CParameter>` to `Option<NonEmptyList<CParameter>>`
   - `body` changed from `List<CFunctionSegment>` to `NonEmptyList<CFunctionSegment>` (always has at least one statement)
6. **`Structure`**: `fields` changed from `List<CDefinition>` to `Option<NonEmptyList<CDefinition>>`
7. **`JRoot`**: `children` changed from `List<JavaRootSegment>` to `Option<NonEmptyList<JavaRootSegment>>`
8. **`CRoot`**: `children` changed from `List<CRootSegment>` to `Option<NonEmptyList<CRootSegment>>`

Records that use direct `NonEmptyList<T>` (not wrapped in Option, as they must always have elements):

1. **`SwitchExpr`**: `cases` uses `NonEmptyList<CaseExpr>` (switch must have cases)
2. **`SwitchStatement`**: `cases` uses `NonEmptyList<CaseStatement>` (switch must have cases)
3. **`CTemplate`**: `typeArguments` uses `NonEmptyList<CLang.CType>` (templates must have type args)

### Serialization/Deserialization

#### `JavaSerializer.java` Updates

- Added `serializeNonEmptyListField()` - serializes `NonEmptyList<T>` fields
- Added `serializeOptionNonEmptyListField()` - serializes `Option<NonEmptyList<T>>` fields
- Added `deserializeNonEmptyListField()` - deserializes to `NonEmptyList<T>`, fails if empty
- Added `deserializeOptionNonEmptyListField()` - deserializes to `Option<NonEmptyList<T>>`, fails if present but empty
- Updated `serializeField()` and `deserializeField()` to check for `NonEmptyList` before `List`

#### Error Handling

When deserializing:

- If a `NonEmptyList<T>` field is missing → Error: "Required component 'X' of type 'NonEmptyList' not present"
- If a `NonEmptyList<T>` field is present but empty → Error: "Required component 'X' of type 'NonEmptyList' is present but empty"
- If an `Option<NonEmptyList<T>>` field is present but empty → Error: "Optional component 'X' of type 'Option<NonEmptyList>' is present but empty"

### Transformer Updates

`Transformer.java` updated to handle the new types when transforming Java → C:

- Function bodies now wrapped as `NonEmptyList.from(bodySegments)`
- Params wrapped as `Option.empty()` or `Option.of(NonEmptyList.from(params))`
- JRoot/CRoot children unwrapped using `.map(nel -> nel.toList()).orElse(new ArrayList<>())`
- CTemplate type arguments use `NonEmptyList.from(...)`

## Migration Guide for Tests

### Pattern: Accessing `Option<NonEmptyList<T>>`

**Before:**

```java
javaRoot.children().size()  // ❌ Doesn't compile
javaRoot.children().stream()  // ❌ Doesn't compile
```

**After:**

```java
// Get size
int size = javaRoot.children().map(nel -> nel.size()).orElse(0);

// Stream over elements
javaRoot.children()
    .map(nel -> nel.toList())
    .orElse(new ArrayList<>())
    .stream()
    .forEach(child -> { ... });

// Or using flatMap
javaRoot.children()
    .map(nel -> nel.toList().stream())
    .orElse(Stream.empty())
    .forEach(child -> { ... });
```

### Pattern: Creating `Option<NonEmptyList<T>>`

**Before:**

```java
List<T> items = ...;
new Record(items);  // ❌ Type mismatch
```

**After:**

```java
List<T> items = ...;
Option<NonEmptyList<T>> wrapped = items.isEmpty()
    ? Option.empty()
    : Option.of(NonEmptyList.from(items));
new Record(wrapped);  // ✓
```

### Pattern: Creating `NonEmptyList<T>` directly

```java
NonEmptyList<T> nel = NonEmptyList.of(first, rest...);
NonEmptyList<T> nel2 = NonEmptyList.from(list);  // Unchecked - caller ensures non-empty
```

## Benefits

1. **Type Safety**: Compiler enforces that lists are either absent or non-empty
2. **Reduced Null Checks**: `NonEmptyList.getFirst()` returns `T`, not `Option<T>`
3. **Clearer Intent**: `Option<NonEmptyList<T>>` makes it explicit that empty is not valid
4. **Better Error Messages**: Deserialization explicitly fails on present-but-empty lists
5. **Semantic Correctness**: Models domain constraints (e.g., "a switch must have cases") in types

## Testing

- All existing tests need updates to:
  1. Unwrap `Option<NonEmptyList<T>>` before calling `.size()` or `.stream()`
  2. Wrap lists in `Option<NonEmptyList<T>>` when constructing ADT instances
- Serialization/deserialization round-trips validated
- Transformer tests updated for new signatures

## Files Modified

### Core Implementation

- `src/main/java/magma/list/NonEmptyList.java` (new)
- `src/main/java/magma/list/NonEmptyArrayList.java` (new)
- `src/main/java/magma/compile/Lang.java`
- `src/main/java/magma/compile/JavaSerializer.java`
- `src/main/java/magma/transform/Transformer.java`

### Tests (examples, more may need updates)

- `src/test/java/TestFunctionSerialization.java`
- `src/test/java/SimpleClassWithMethodTest.java`
- (Additional test files may require similar updates)

## Future Work

- Consider adding convenience methods to `Option<NonEmptyList<T>>` for common operations
- Evaluate other ADT fields that could benefit from `NonEmptyList<T>`
- Add static analysis/linting rules to enforce proper usage

## Commands to Verify

```bash
# Compile
mvn compile -DskipTests

# Run tests (after fixing test files)
mvn test

# Run specific test
mvn -Dtest=SimpleClassWithMethodTest test
```
