# NonEmptyList<T> Introduction

## Overview

Introduced `NonEmptyList<T>`, a new list type that guarantees at least one element at compile time. This provides a static invariant that simplifies signatures and eliminates the need for `Option` types when accessing the first or last element.

## Motivation

Previously, types like `CTemplate` used `List<CLang.CType> typeArguments`, which could theoretically be empty. However, templates always have at least one type argument by definition. Using `NonEmptyList<CLang.CType>` makes this invariant explicit and prevents runtime errors.

## Design

### Interface Definition

`NonEmptyList<T>` is a standalone interface (not extending `List<T>`) with its own API:

**File**: `src/main/java/magma/list/NonEmptyList.java`

Key methods:

- `T first()` — Returns the first element (always present, no Option wrapper needed)
- `T last()` — Returns the last element (always present, no Option wrapper needed)
- `List<T> rest()` — Returns the remaining elements after the first
- `Stream<T> stream()` — Returns a stream of all elements
- `NonEmptyList<T> addLast(T element)` — Adds an element to the end
- `List<T> toList()` — Converts to a regular List

### Implementation

**File**: `src/main/java/magma/list/ArrayNonEmptyList.java`

The implementation is a record with two fields:

- `T head` — The first element (guaranteed to exist)
- `List<T> tail` — The remaining elements (may be empty)

This structure ensures the non-empty invariant is maintained at construction time.

### Factory Methods

- `NonEmptyList.of(T element)` — Creates a singleton list
- `NonEmptyList.of(T first, T... others)` — Creates a list from varargs
- `NonEmptyList.fromList(List<T> list)` — Returns `Option<NonEmptyList<T>>`, succeeds only if the list is non-empty

## Usage

### Before

```java
@Tag("template")
public record CTemplate(String base, List<CLang.CType> typeArguments) implements CLang.CType {
    @Override
    public String stringify() {
        // Had to assume typeArguments is non-empty
        return base + "_" + typeArguments.stream()
            .map(CLang.CType::stringify)
            .collect(new Joiner("_"));
    }
}
```

### After

```java
@Tag("template")
public record CTemplate(String base, magma.list.NonEmptyList<CLang.CType> typeArguments) implements CLang.CType {
    @Override
    public String stringify() {
        // Static guarantee that typeArguments is non-empty
        return base + "_" + typeArguments.stream()
            .map(CLang.CType::stringify)
            .collect(new Joiner("_"));
    }
}
```

### Creating CTemplate Instances

**File**: `src/main/java/magma/transform/Transformer.java`

```java
private static CLang.CType transformGeneric(Lang.JGeneric generic) {
    // Convert Function<T, R> to function pointer R (*)(T)
    final List<Lang.JType> listOption = generic.typeArguments().orElse(new ArrayList<Lang.JType>());
    if (generic.base().endsWith("Function") && listOption.size() == 2) {
        final CLang.CType paramType = transformType(listOption.get(0).orElse(null));
        final CLang.CType returnType = transformType(listOption.get(1).orElse(null));
        return new CLang.CFunctionPointer(returnType, List.of(paramType));
    }

    // Transform type arguments to CType list
    final List<CLang.CType> transformedTypes = listOption.stream().map(Transformer::transformType).toList();

    // Create NonEmptyList from the transformed types
    // If the list is empty, this is an error case - generics should always have type arguments
    return magma.list.NonEmptyList.fromList(transformedTypes)
        .map(nonEmptyTypes -> (CLang.CType) new Lang.CTemplate(generic.base().last(), nonEmptyTypes))
        .orElse(new Lang.Invalid("Empty type arguments for generic " + generic.base().last(), new magma.option.None<>()));
}
```

This approach ensures that if a generic has no type arguments (an invalid state), it's explicitly converted to an `Invalid` node rather than creating a malformed `CTemplate`.

## Serialization Support

Added full serialization/deserialization support for `NonEmptyList<T>` in the compiler's serialization framework:

**File**: `src/main/java/magma/compile/JavaSerializer.java`

### Serialization

- `serializeNonEmptyListField(RecordComponent, Object)` — Converts a `NonEmptyList` to a node list
- Delegates to existing `serializeListElements` after converting to `List` via `toList()`

### Deserialization

- `deserializeNonEmptyListField(RecordComponent, Node, Set<String>)` — Reconstructs a `NonEmptyList` from a node list
- Validates that the list is non-empty before creating the `NonEmptyList`
- Returns an error if the deserialized list is empty (validation failure)
- Uses `NonEmptyList.fromList()` to safely construct the result

## Benefits

1. **Type Safety**: Compile-time guarantee that certain lists are non-empty
2. **Simplified APIs**: No need for `Option<T>` when accessing first/last elements
3. **Self-Documenting**: The type signature communicates the invariant
4. **Error Prevention**: Impossible to create an empty `CTemplate`

## Verification

To verify the changes work correctly:

```bash
mvn clean compile
mvn exec:java
```

Expected: All Java files should compile successfully, including files using `CTemplate` with `NonEmptyList<CLang.CType> typeArguments`.

## Files Modified

- `src/main/java/magma/list/NonEmptyList.java` (new interface)
- `src/main/java/magma/list/ArrayNonEmptyList.java` (new implementation)
- `src/main/java/magma/compile/Lang.java` (`CTemplate` record updated)
- `src/main/java/magma/transform/Transformer.java` (`transformGeneric` method updated)
- `src/main/java/magma/compile/JavaSerializer.java` (serialization/deserialization support added)

## Future Opportunities

Other candidates for `NonEmptyList<T>`:

- Constructor parameter lists that must have at least one parameter
- Method parameter lists for non-nullary methods
- Generic type parameter lists (similar to `CTemplate`)
- Any domain concept that requires at least one element
