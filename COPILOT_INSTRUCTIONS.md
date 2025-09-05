Copilot instructions: The use of the null literal is banned in this project. Use java.util.Optional as the alternative to null.

Additionally: prefer using switch statements with pattern matching and record destructuring when handling the `Result` ADT in tests (for example, updating `assertInterprets` to use a switch with `Result.Ok` / `Result.Err` patterns).

Also: resolve all linting/static analysis errors (Checkstyle, PMD, CPD) before considering a change complete. If static checks require refactoring, perform minimal safe refactors to satisfy them.
