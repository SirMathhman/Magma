To implement features, you must follow this process:

- Add a failing test
- Implement the test
- Remove semantic duplicates

Notes:

- You MUST call `mvn test` at the end. The build **must** pass. Do not prompt
  the user for this.
- Avoid regexes.