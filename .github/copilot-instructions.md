- Do NOT edit Runner.java. You should probably only edit Compiler.java.
- Always run `mvn clean test` at the start and end of your task. You probably don't need the `-e` flag.
- Always resolve CheckStyle issues before your task is complete.

Keep in mind the constraints.
- Max 15 cyclomatic complexity per method.
- Max 10 methods per class.
- Only one assert per test method. You might have to separate test methods into different classes.