# One Loop Per Method CheckStyle Rule

## What Changed

Added a CheckStyle rule to enforce that each method contains at most one loop (for, while, or do-while). This constraint encourages developers to extract complex loop logic into separate, well-named helper methods, improving code readability and maintainability.

All violations in the codebase have been fixed by extracting nested loops into separate helper methods.

## Why

Having multiple loops in a single method often indicates that the method is doing too much and violates the Single Responsibility Principle. By limiting methods to one loop, we encourage:
- Better method decomposition
- More descriptive method names for each loop's purpose
- Easier testing of individual loop behaviors
- Reduced cognitive complexity

## Configuration Details

The rule is implemented in `config/checkstyle/checkstyle.xml` using the `DescendantToken` module:
- Applies to: `METHOD_DEF` and `CTOR_DEF` (methods and constructors)
- Counts: `LITERAL_FOR`, `LITERAL_WHILE`, `LITERAL_DO` (all loop types)
- Limit: Maximum 1 loop per method
- Configured to run during the `test` phase and fail the build on violations (`failsOnError=true`)

## Fixes Applied

The following violations were fixed by extracting loops into helper methods:

1. **JavaSerializer.java line 190** - `deserializeSealed`:
   - Extracted `tryDirectPermittedSubclasses` for first loop
   - Extracted `tryNestedSealedInterfaces` for second loop

2. **JavaSerializer.java line 268** - `levenshteinDistance`:
   - Extracted `fillLevenshteinRow` for inner loop logic

3. **JavaSerializer.java line 548** - `findStringInChildren`:
   - Extracted `findStringInNodeLists` for second loop
   - Further extracted `searchChildrenList` for nested loop within that

4. **Main.java line 148** - `flattenStructure`:
   - Extracted `addRecordParamsAsFields` for record parameter processing loop

All extracted methods follow the project's existing patterns and maintain the same behavior.

## How to Verify

Run CheckStyle to verify no violations:
```bash
mvn checkstyle:check
```

Expected result: Build should succeed with "You have 0 Checkstyle violations."

CheckStyle now runs automatically during `mvn test` and will fail the build if new violations are introduced.

## References

- CheckStyle configuration: `config/checkstyle/checkstyle.xml`
- Maven POM configuration: `pom.xml` (CheckStyle plugin execution phase set to `test`)
- CheckStyle DescendantToken documentation: https://checkstyle.sourceforge.io/config_misc.html#DescendantToken
