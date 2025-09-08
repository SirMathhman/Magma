mode: agent
IMPORTANT: This prompt is strictly for planning and documentation purposes ONLY. Under no circumstances should the agent produce runnable implementation, executable patches, build commands, or code intended to be copied-and-run. All outputs generated from this prompt must be limited to documentation, design notes, non-executable pseudo-code, checklists, and reviewer-facing plans.

Please prepare a formal project specification that accomplishes the following (documentation-only):

1. Precisely update the project requirements: restate the requested feature using formal project-spec language, including scope, constraints, acceptance criteria, and measurable success metrics.

2. Produce an architecture plan for the feature: describe the high-level design, component responsibilities, data flows, external interfaces, dependencies, deployment considerations, and any required schema or API changes; include assumptions, risk notes, and migration considerations where applicable.

3. Provide an illustrative, non-executable example or reference sketch: include architecture diagrams, sequence descriptions, and pseudo-code or annotated snippets that are explicitly non-runnable. Do NOT include full runnable code, compilation commands, CI scripts, or implementation patches. The goal is to make the design and verification steps clear to a human reviewer, not to implement the feature.

4. Conclude by prompting the user for further guidance: explicitly ask the user to propose at least three additional feature requests or acceptance criteria. For each suggestion, request a priority (high/medium/low) and a one-line rationale. Also suggest next review steps and a concise set (2-4) of candidate follow-up tasks suitable for implementation by a developer.
