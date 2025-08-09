# Guidelines

You are required to:

- Build using `build.bat`
- Build using `test.bat`

## SDLC:

The development cycle must follow TDD and Kent Beck's rules of simple design:

1) Write a test that has only one assertion
2) Confirm the test fails
3) Implement the failing test
4) Remove semantic duplicates
5) Choose language to best express the intent of the code
6) Update both inline documentation and project-documentation. The inline documentation should explain the _why_ of the
   implementation, and the project-level documentation should give a high-level overview.

If a feature is too complicated, you might have to repeat this process several times, making multiple tests. Again, each
test should have one assertion.

## Formatting

1) No method is allowed to have a cyclomatic complexity greater than 10.
2) No more than two parameters are allowed per method.
3) No more than one loop per method.
4) No more than ten methods per class. No more than five fields per class.
5) No ternary statements.
6) Do not use inheritance, and instead use composition.
7) Do NOT use logging or `System.out.println(...);` statements to debug. Instead, write tests that confirm the expected
   behavior. In other
   words, if the user requests a complex feature and it seems to be challenging to debug, write tests incrementally (and
   modify the test suite) to diagnose where the expected behavior should be.

## Emergence Rules

1) It is preferred to have multiple, smaller, classes, than one giant class that does everything. Ideally, classes
   should naturally emerge when two methods share the same parameters.
3) Prefer pure functions to modification. Especially avoid mutating arguments.
4) Prefer using streams over loops.
5) Prefer using `java.util.Optional` over `null`.
6) Prefer using records instead of POJOs.
7) Prefer modifying old code than writing new code.
8) Avoid the `static` modifier when possible.