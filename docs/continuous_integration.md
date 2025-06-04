## Continuous Integration

The GitHub Actions workflow builds the project using `build.sh`. Earlier
revisions compiled only `GenerateDiagram.java`, which caused errors like
`cannot find symbol` when `Result.java` was added. Running the helper script
ensures every source file is compiled and keeps the pipeline green.
