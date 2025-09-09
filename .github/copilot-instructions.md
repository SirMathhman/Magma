# Copilot / AI agent instructions

These instructions guide automated AI contributors (Copilot-style agents) working on this repository.

## Primary rule

- Always update documentation when a task is completed.

## What "update documentation" means

- If you changed public behaviour, APIs, or user-facing features, update the relevant docs (README, docs/, API reference, or inline docs) to reflect the change.
-- If you fixed a bug, document the fix in the relevant docs or the pull request description and rely on git history / PR notes for chronological tracking. `CHANGELOG.md` should only be updated for major releases.
- If you added or updated configuration, scripts, or build steps, update the Developer or CONTRIBUTING documentation so future contributors can reproduce the work.
- If the change is purely internal and no docs need modification, still add a one-line note to the pull request description stating: "Documentation: no changes required" and why.

## How to update docs (minimal checklist)

1. Identify the documentation location(s) affected (README.md, docs/, inline comments, or API docs). For chronological tracking of changes, rely on git commits and PR descriptions; `CHANGELOG.md` is reserved for major releases.
2. Make small, focused edits that clearly explain the change and any migration or usage steps.
3. Add tests or examples if relevant and update code comments where they help future maintainers.
4. In the pull request description, include a short section titled "Documentation changes" that lists the files modified and a one-line summary.

## Example PR description snippet

Documentation changes:

- Updated `docs/usage.md` to include the new `--fast-mode` flag and an example.
-- Added a brief note in the PR description and updated the relevant docs to describe the bug fix. Rely on git/PR notes for a running history; update `CHANGELOG.md` only for major release notes.

If no documentation files were changed, include this line instead:

Documentation: no changes required (reason: internal refactor with no user-visible impact)

## Why this rule exists

Keeping documentation in sync with code reduces onboarding time, prevents regressions, and improves the overall quality of the project. Automated agents must follow this rule to maintain repository health.

---
Last-updated: 2025-09-08
