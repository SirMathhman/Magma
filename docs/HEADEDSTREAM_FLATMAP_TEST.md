# HeadedStream.flatMap Test Documentation

## What Changed

Added comprehensive test suite `HeadedStreamFlatMapTest.java` to verify the correctness of the `HeadedStream.flatMap` implementation.

## Why

The `flatMap` method is a critical stream operation that flattens nested streams into a single continuous stream. While the implementation exists in `HeadedStream.java`, it lacked dedicated test coverage to ensure:

- Correct behavior with edge cases (empty streams)
- Proper flattening of nested stream structures
- Order preservation during flattening
- Compatibility with stream chaining

## Test Coverage

The test suite includes 8 comprehensive test cases:

1. **testFlatMapWithEmptyOuterStream** - Verifies that flatMap on an empty stream returns an empty result
2. **testFlatMapWithEmptyInnerStreams** - Tests behavior when all mapped streams are empty
3. **testFlatMapBasicFlattening** - Core functionality test: maps integers to ranges and verifies correct flattening (e.g., `[1,2,3]` → `[0, 0,1, 0,1,2]`)
4. **testFlatMapWithSingleElement** - Tests single-element stream flattening
5. **testFlatMapWithMixedEmptyAndNonEmpty** - Verifies handling of a mix of empty and non-empty inner streams
6. **testFlatMapChaining** - Tests multiple chained `flatMap` operations
7. **testFlatMapWithListOf** - Verifies flattening of `List` streams (e.g., `[[1,2], [3,4,5]]` → `[1,2,3,4,5]`)
8. **testFlatMapPreservesOrder** - Confirms that element order is preserved during flattening

## How to Verify

Run the test:

```
mvn surefire:test -Dtest=HeadedStreamFlatMapTest -DfailIfNoTests=false
```

Expected result: All 8 tests pass with output showing test names and success indicators (✅).

Verify CheckStyle compliance for the new test file (it contains no `null` literals or `throw` statements, and has at most one loop per method):

```
mvn checkstyle:check
```

Note: Pre-existing CheckStyle violations in other files are unrelated to this test.

## Files Modified

- `src/test/java/HeadedStreamFlatMapTest.java` (NEW) - Comprehensive test suite for `HeadedStream.flatMap` with 8 test cases covering edge cases, basic functionality, chaining, and order preservation.
