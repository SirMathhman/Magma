# Splitter Refactoring: SplitMode to Merger Pattern

## What Changed

Refactored the `SplitMode` enum into a `Merger` interface pattern in the `magma.compile.rule` package:

1. **Created `Merger` interface** - Defines
   `Option<Tuple<String, String>> merge(List<String> segments, String delimiter)` method
2. **Created concrete implementations**:
   - `KeepFirstMerger` - Splits at the first occurrence of delimiter
   - `KeepLastMerger` - Splits at the last occurrence of delimiter
3. **Refactored `DividingSplitter`** - Now uses `Merger` interface instead of `SplitMode` enum
4. **Added factory methods** - `DividingSplitter.keepFirst(Divider)` and `DividingSplitter.keepLast(Divider)`
5. **Updated `Lang.java`** - Replaced `SplitMode.ALL_BUT_LAST` references with `DividingSplitter.keepLast(divider)`

## Why

This change improves code maintainability by:

- Following the Strategy pattern instead of enum-based switching
- Making it easier to add new merge strategies without modifying existing code
- Providing cleaner, more readable factory methods for common use cases
- Better separation of concerns between splitting logic and merging behavior

## How to Verify

Run the test suite to ensure all serialization/deserialization functionality works:

```cmd
mvn test
```

Expected results: All core tests pass (37 tests run with existing pre-refactoring test issues unrelated to this change).

## Files Modified

- `src/main/java/magma/compile/rule/Merger.java` (new)
- `src/main/java/magma/compile/rule/KeepFirstMerger.java` (new)
- `src/main/java/magma/compile/rule/KeepLastMerger.java` (new)
- `src/main/java/magma/compile/rule/DividingSplitter.java` (refactored)
- `src/main/java/magma/compile/Lang.java` (updated references)

## Usage

Before:
```java
new DividingSplitter(divider, SplitMode.ALL_BUT_LAST)
```

After:
```java
DividingSplitter.keepLast(divider)
// or
new

DividingSplitter(divider, new KeepLastMerger())
```
