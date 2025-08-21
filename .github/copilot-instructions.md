Copilot instructions

```instructions
Do NOT modify `Runner.java`.
Do NOT use regexes.
Do NOT use `null`. Instead, use `java.util.Optional`.

Prefer pure functions.

Always run `mvn clean test` at the start and end of your task.

You must resolve all linting and duplication problems.
```

## Linting and quality rules (project configuration)

The repository enforces the following static analysis and style checks via Maven plugins:
  - MethodCount: limit the maximum number of methods per type declaration
    - maxTotal = 10
  - CyclomaticComplexity: limit cyclomatic complexity per method
    - max = 15
  - CPD (copy-paste detector) runs during the `test` phase (goal `cpd-check`).
    - minimumTokens = 10