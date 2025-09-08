---
mode: agent
---
Follow this feature delivery workflow when implementing changes:

1. Read the project's current documentation to understand the existing architecture and conventions.
2. Draft a plan that implements the requested feature within the current architecture (practical plan).
3. Design an "ideal" architecture or approach for the feature (what the perfect implementation would look like).
4. Compare the practical plan and the ideal design; identify gaps and migration steps.
5. Refactor the current codebase toward the ideal design until the implementation is partially aligned.
6. Add end-to-end tests that exercise the new feature.
7. Run tests to confirm they fail (prove the test catches the missing behavior).
8. Implement the feature changes required to make the tests pass.
9. Run the test-suite and confirm tests pass.
10. Update documentation to reflect the new feature and any architectural changes.