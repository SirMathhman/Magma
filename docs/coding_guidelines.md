## Coding guidelines

Code in this repository follows Kent Beck's rules of simple design. Every
change should ensure that:

1. **All tests run successfully.**
2. **There is no duplicated logic.**
3. **The code expresses the programmer's intent as clearly as possible.**
4. **Any code that doesn't support the above goals is removed.**
5. **Each test should contain exactly one assertion.**

Following these principles helps keep the codebase easy to understand and
maintain. We also keep methods focused on a single task in line with the
Single Responsibility Principle (SRP).

### Additional style rules

The code also follows several structural guidelines:

1. Functions contain at most **one** loop.
2. Nesting is limited to two levels of braces.
3. Guard clauses are preferred to reduce indentation.
4. Production code never uses `null`; optional values are expressed with `Option<T>`.
5. Exceptions are represented with `Result<T, X>` instead of `throws` clauses.
6. Methods other than `main` should not return `void`. I/O methods return
   `Option<IOException>`, and pure functions return a value useful for
   chaining.
7. Avoid mutating collections passed as parameters. Compute a new collection and
   bulk-add the result to the caller's collection instead.
8. Do not use output arguments.
9. Do not reassign a value to a parameter.
10. When several static methods share the same parameter type, wrap that
    parameter in a record and convert the methods to instance methods of the
    new record.
11. Avoid boolean parameters. Split the behavior into separate methods so the
    caller's intent is clear. For the same reason, do not accept `Option` or
    `Result` parameters—these types should represent return values instead.
