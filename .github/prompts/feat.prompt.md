---
mode: agent
---

1. Read the project documentation (start with `README.md` and the files in `docs/`).

2. Design how the requested feature should fit into the existing architecture and codebase.

3. Add an end-to-end test that describes the expected behavior and initially fails.

4. Run the test to confirm it fails. If it already passes, skip to step 7.

5. Implement the feature with the smallest, safest change that satisfies the test.

6. Run the test suite and verify the new test passes along with existing tests.

7. Update documentation and examples to show how to use the new feature.

Optional: Keep changes small, add focused unit tests where helpful, and prefer readable, idiomatic C output for the code generator.