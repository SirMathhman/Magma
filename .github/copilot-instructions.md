Always update project documentation at the end of each task.

When completing any change, ensure README, CHANGELOG, and relevant docs are updated so the next developer has clear context. Update or add examples, usage notes, and any migration steps as needed.

Thanks for keeping the repo documented.

Additional behavior:

- Whenever the user types a message starting with the command `/spec ` followed by any text (for example `/spec implement foo`), the assistant should execute the instructions contained in the repository file `.github/prompts/spec.md` and follow them as the user's request. The assistant should treat the contents of `.github/prompts/spec.md` as the authoritative prompt for handling `/spec` invocations.

Notes:

- The assistant may assume the `.github/prompts/spec.md` file is present and authoritative; it does not need to re-open the file on every `/spec` invocation unless the file has been modified during the same session.
- If `.github/prompts/spec.md` contains steps that require repository edits, the assistant should follow the normal repository editing workflow (create a todo, mark in-progress, apply patches, run quick verification) and report progress back to the user.
