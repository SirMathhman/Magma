## Running tests

The project follows a test-driven development approach using JUnit 5. A
`test.sh` script downloads the JUnit Platform console runner if necessary and
executes the tests:

```bash
./test.sh
```

The test script compiles the Java sources and then runs the JUnit test suite.
`Main` is not executed automatically during the tests.
