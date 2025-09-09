---
mode: agent
---
Agents MUST follow the 7-step workflow below when implementing new user requests provided as acceptance tests. The user will specify WHAT the program should do (acceptance tests / behavior), not HOW to implement it. The agent is responsible for design, tests, implementation, and documentation.

7-step workflow (required):

1) Consider existing documentation & implementation
  - Locate and read current user-facing docs (e.g., `README.md`, files under `docs/`) and the relevant implementation under `src/` to understand current behavior and constraints.
  - Note any gaps between the requested behavior and current docs or implementation.

2) Create or modify an architecture plan
  - Create or update `./docs/architecture.md` describing the design changes required to implement the requested behavior.
  - Include: a short goal statement, proposed components/files to change, data shapes or public API contract, and quality gates.
  - Minimal `architecture.md` template:
    - Title and goal (1-2 sentences)
    - Files/Modules affected (list)
    - Inputs/Outputs/Errors (contract)
    - Migration or compatibility notes (if applicable)
    - Tests to add (file paths and brief descriptions)

3) Write failing tests first
  - Add unit and/or integration tests that express the acceptance criteria under the repository's test structure (for this project: `src/test/java/...`).
  - Tests MUST be written so they fail initially (red tests). Name tests clearly (e.g., `FeatureName_behavior_description`).

4) Verify failing tests actually fail
  - Run the test suite (e.g., `mvn -DskipTests=false test`) and confirm the new tests fail.
  - Record the failing assertions and expected behavior in `docs/architecture.md` or a short note in the PR description.

5) Implement the tests
  - Implement minimal, well-factored changes to make the tests pass.
  - Follow project conventions and keep changes minimal and reversible.

6) Resolve build & lint errors
  - Run the full build (e.g., `mvn -DskipTests=false package`) and resolve any compilation, test, or lint/checkstyle errors.
  - Quality gates:
    - Build: `mvn package` must succeed
    - Tests: new and existing tests must pass
    - Lint/Style: address checkstyle warnings/errors where practical

7) Update documentation
  - Update `README.md` or files under `docs/` to reflect behavior changes and include simple usage examples if user-facing.
  - Add a one-line "Documentation changes" summary to the PR description stating which files were updated or "Documentation: no changes required" with a reason.

Checks & expectations for agents
  - Always add or update `./docs/architecture.md` when acceptance tests or new features are added. If the change is trivial/internal, add a one-line note to the PR explaining why no architecture doc was needed.
  - Tests must be added under the project's test structure (e.g., `src/test/java/...`) and must be executed as part of the verification.
  - Use existing build tooling (Maven) and follow the project's style and conventions (check `pom.xml` and `config/checkstyle/`).
  - When making changes that affect public behavior or APIs, include small examples and update README or docs.

Example minimal checklist to include in PR description:
  - [ ] Architecture: `docs/architecture.md` updated
  - [ ] Tests: failing tests added
  - [ ] Tests: tests executed and passed after implementation
  - [ ] Build: `mvn package` completed successfully
  - [ ] Docs: `README.md` or `docs/` updated (or "none required" reason)

Notes for automated agents
  - If you modify code, run the tests and the build locally and include the results in the PR or commit message.
  - Prefer small, incremental commits with clear messages describing each step (tests, implementation, docs).
  - If blocked by missing information, add a small clarifying test or TODO in `docs/architecture.md` and surface the blocker in the PR.

Documentation rule (repository policy):
  - Public-facing or behavioral changes must update documentation. Internal refactors must include a one-line PR note: "Documentation: no changes required (reason)".

Failure modes and what to document on failure
  - If tests cannot be made to pass within reasonable changes, document the attempted design and tests in `docs/architecture.md` and open an issue for further review.

----

End of required workflow.