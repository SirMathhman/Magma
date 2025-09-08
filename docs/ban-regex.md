Policy: Ban use of java.util.regex
=================================

Summary
-------

For this compiler project we ban the use of `java.util.regex` (both imports and fully-qualified uses) in the source tree. Regular expressions are powerful but brittle for parsing programming-language constructs; prefer explicit, small parsers or proper lexer/parser implementations.

Rationale
---------

- Regexes can silently accept malformed inputs or reject valid ones when whitespace or nesting changes.
- They encourage gluing parsing logic via pattern matching rather than building robust AST-producing code.
- For a compiler project, clarity and correctness are preferred; small, explicit parsers are easier to reason about and test.

How to follow this policy
-------------------------

- Do not add `import java.util.regex.*;` or `import java.util.regex.Pattern;` to new or existing files.
- Do not use fully-qualified `java.util.regex.Pattern` or `java.util.regex.Matcher` names.
- When you need to parse small, well-defined patterns (for tests or CLI conveniences), implement minimal manual parsing functions or extend the proper lexer/parser components.

Notes
-----

This policy is enforced automatically by Checkstyle for the `magma-core` module. The rules live in `java/magma-core/checkstyle.xml` and the plugin is configured in `java/magma-core/pom.xml` to fail the build if `java.util.regex` is referenced.

If you need an exception for a specific file, open an issue and explain why; the preferred approach is to refactor the code to avoid regex usage.
