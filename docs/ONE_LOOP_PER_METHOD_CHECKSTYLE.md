# One Loop Per Method CheckStyle Rule

## What Changed

Added a CheckStyle rule to enforce that each method contains at most one loop (for, while, or do-while). This constraint encourages developers to extract complex loop logic into separate, well-named helper methods, improving code readability and maintainability.

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

## How to Verify

Run CheckStyle to see violations:
```bash
mvn checkstyle:check
```

Expected result: The build will fail with violations reported for methods containing more than one loop. As of this change, there are 4 known violations in the codebase:
- `JavaSerializer.java`: lines 190, 268, 548
- `Main.java`: line 148

These violations are intentionally not fixed yet and serve as examples of code that needs refactoring.

## References

- CheckStyle configuration: `config/checkstyle/checkstyle.xml`
- CheckStyle DescendantToken documentation: https://checkstyle.sourceforge.io/config_misc.html#DescendantToken
