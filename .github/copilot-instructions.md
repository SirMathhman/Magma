We are following test driven design.
- I will sometimes provide the failing tests, but if not, you should make your own.
- Run the tests using `mvn clean test` to ensure that the tests fail. If the test passes,
then we don't have to do anything more!
- Implement the failing test.
- Run the tests using `mvn clean test` to ensure that the tests pass.

Keep in mind the constraints:
- Do NOT edit Runner.java.
- Do NOT use regexes.
- Prefer pure methods. Do not modify arguments.
- You probably don't need the `-e` flag when running `mvn clean test`.

Always resolve CheckStyle issues before your task is complete.
- Max 15 cyclomatic complexity per method. Create helper methods.
- Max 10 methods per class. You can refactor code into new classes if needed.
- Only one assert per test method. You might have to separate test methods into different classes.