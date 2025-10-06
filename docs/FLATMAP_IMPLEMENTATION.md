# flatMap Implementation for HeadedStream

## What Changed

Added implementation of the `flatMap` method in `HeadedStream.java` (located at `src/main/java/magma/list/HeadedStream.java`).

## Why

The `flatMap` method was declared in the `Stream` interface but not implemented in `HeadedStream`, causing `UnsupportedOperationException` at runtime. This method is essential for stream processing operations, particularly used in `Transformer.transform()` to flatten nested streams of AST nodes.

## How It Works

The implementation uses a stateful `Head<R>` that:
1. Maintains a reference to the current inner stream's head (`currentInnerHead`)
2. Exhausts each inner stream completely before moving to the next outer element
3. Maps each element from the outer stream to an inner stream using the provided mapper function
4. Flattens all inner streams into a single continuous stream

Key implementation details:
- Pattern matches on `HeadedStream` to access the inner head
- Returns `None` when both inner and outer streams are exhausted
- Uses a while loop to handle the transition between inner streams seamlessly

## How to Verify

Run the following command to verify the implementation works:
```
mvn exec:java
```

Expected result: The program should compile all Java files without throwing `UnsupportedOperationException`. The Transformer should successfully use `flatMap` to flatten `List<Lang.CRootSegment>` streams when transforming `JRoot` to `CRoot`.

## Files Modified

- `src/main/java/magma/list/HeadedStream.java` - Added `flatMap` implementation with stateful Head and proper stream flattening logic. Also added missing import for `None`.

