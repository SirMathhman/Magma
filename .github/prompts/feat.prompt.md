---
mode: agent
---

The user will usually provide features that will describe the behavior of the program, and will not discuss architecture.
Architecture is for you to decide.

1. Read the project documentation (start with `README.md` and the files in `docs/`).

2. Design how the requested feature should fit into the existing architecture and codebase.

3. Add an end-to-end test that describes the expected behavior and initially fails.

4. Run the test to confirm it fails. If it already passes, skip to step 7.

5. Implement the feature with the smallest, safest change that satisfies the test.

6. Run the test suite and verify the new test passes along with existing tests.

7. Update documentation and examples to show how to use the new feature.

8. Ensure the project builds cleanly: resolve any build errors introduced by your changes before finalizing the feature. Do not modify build configuration files (pom.xml, build scripts, CI configs) unless the user explicitly requests it; if packaging or file placement issues arise, prefer moving files using file-system commands (for example `git mv` or `mv`) then editing them in-place rather than copying files one-by-one.

Optional: Keep changes small, add focused unit tests where helpful, and prefer readable, idiomatic C output for the code generator.

Additional guidance for end-to-end codegen tests:

- When the requested feature can be verified by emitting a small C program (for example, a Magma program that is a single numeric literal), prefer emitting a concise C source file, compiling it with `clang` or `cc` (if available on PATH), running the resulting binary, and asserting its exit code or output in the test.
- If no system C compiler is available on the test runner, the test should gracefully fall back or be skipped; the agent should detect compiler presence and document the fallback in the test or prompt.