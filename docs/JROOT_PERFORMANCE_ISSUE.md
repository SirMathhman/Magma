# JRoot() Performance Issue on Main.java

## Summary

The `JRoot()` lexer experiences severe performance degradation when parsing complex Java files like `Main.java` (338 lines), failing to complete within a reasonable timeout (5 seconds).

## Investigation

**Test Created:** `JavaRootOnMainFileTest.testJavaRootCanBeAppliedToMainJava()`

**Findings:**

- The test uses `assertTimeoutPreemptively(Duration.ofSeconds(5), ...)` to enforce a strict 5-second timeout
- Lexing `Main.java` consistently fails to complete within this timeframe
- Stack trace reveals exponential complexity in string building operations

## Root Cause

Based on the stack trace captured during timeout:

```
at java.base/java.lang.AbstractStringBuilder.append(AbstractStringBuilder.java:814)
at magma.compile.rule.DivideState.append(DivideState.java:40)
at magma.compile.Lang$InvocationFolder.fold(Lang.java:371)
at magma.compile.rule.EscapingFolder.fold(EscapingFolder.java:13)
at magma.compile.rule.FoldingDivider.divide(FoldingDivider.java:18)
at magma.compile.rule.DividingSplitter.split(DividingSplitter.java:24)
at magma.compile.rule.SplitRule.lex(SplitRule.java:28)
at magma.compile.rule.OrRule.lex(OrRule.java:23)
[repeated recursion through OrRule, LazyRule, NodeRule, etc.]
```

The problem appears to be:

1. **Excessive string concatenation** in `DivideState.append()` being called repeatedly
2. **Deeply nested `OrRule` evaluation** leading to exponential parsing attempts
3. **Backtracking** in the parser trying multiple rule combinations

## Impact

- Cannot parse moderately complex Java files (338+ lines)
- Lexer performance degrades exponentially with file complexity
- The `Main.java` compilation step itself works (as evidenced by the project building), but round-trip lexing/parsing for testing is impractical

## Recommendations

1. **Profile `DivideState.append()`** - Consider using `StringBuilder` with pre-allocated capacity or switch to immutable string operations
2. **Optimize `OrRule` evaluation** - Add memoization or early-exit strategies to avoid redundant parsing attempts
3. **Consider alternative parsing strategies** - Investigate if certain grammar rules can be simplified or made more deterministic
4. **Add incremental parsing** - For large files, consider parsing method-by-method or class-by-class

## Test Status

The test `testJavaRootCanBeAppliedToMainJava()` is currently **expected to fail** with a timeout.  
This test serves as a regression detector: if lexer performance improves, this test will start passing.

**Command to verify:**

```bash
mvn -Dtest=JavaRootOnMainFileTest test
```

**Expected result (current):** Test fails with "execution timed out after 5000 ms"  
**Desired result (future):** Test passes within 5 seconds

## Related Files

- Test: `src/test/java/JavaRootOnMainFileTest.java`
- Lexer entry point: `src/main/java/magma/compile/Lang.java` (method `JRoot()`)
- Performance bottleneck: `src/main/java/magma/compile/rule/DivideState.java` (method `append()`)
- Problematic rules: `OrRule`, `InvocationFolder`, `EscapingFolder`, `FoldingDivider`

---

**Date:** October 6, 2025  
**Status:** Open Issue  
**Priority:** Medium (lexer works for simple cases; issue only affects complex files)
