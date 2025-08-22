We are following test driven design.
- I will provide the failing test.
- Run the tests using `mvn clean test` to ensure that it fails.
- Implement the failing test.
- Run the tests using `mvn clean test` to ensure that it passes.

Keep in mind the constraints:
- Do NOT edit Runner.java. You should probably only edit Compiler.java.
- Do NOT use regexes.
- Prefer pure methods.
- You probably don't need the `-e` flag when running `mvn clean test`.

Always resolve CheckStyle issues before your task is complete.
- Max 15 cyclomatic complexity per method.
- Max 10 methods per class.
- Only one assert per test method. You might have to separate test methods into different classes.