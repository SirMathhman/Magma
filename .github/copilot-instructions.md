Always update the repository documentation (README, docs/, CHANGELOG, or relevant files) after completing a task.

Be explicit about what changed and why, and include any commands required to build or test the changes.

Tests-first requirement:
Always write automated tests and run them before implementing a change. Include the test commands and test results (success/failure) in your change notes.

The use of the null literal is banned in this project. Use java.util.Optional as the alternative to null.

Avoid regular expressions for parsing or logic that encodes structure; regexes often hide complexity that becomes duplicated elsewhere — prefer explicit parsing helpers or small scanners so duplication is visible and consolidatable.

Method names should be 30 characters or less to keep APIs concise and readable; prefer shorter, descriptive names and consolidate helpers when similar logic repeats.
