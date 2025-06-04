## Continuous Integration

The project uses a single GitHub Actions workflow defined at
`.github/workflows/ci.yml`. It builds the project using `build.sh` and runs the
tests with `test.sh`. Earlier revisions compiled only `GenerateDiagram.java`,
which caused errors like `cannot find symbol` when `Result.java` was added.
Running the helper script ensures every source file is compiled and keeps the
pipeline green.
