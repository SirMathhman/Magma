Ban on all for-loops (Checkstyle rule)

What changed

- Updated the Checkstyle rule in `config/checkstyle/checkstyle.xml` to ban all `for` loop constructs (AST token `LITERAL_FOR`). This flags both traditional indexed `for` loops and enhanced-for (foreach) loops.

Why

- To encourage modern, expressive iteration patterns: use `while` when you need index-based control, or Java Streams (forEach, map, filter) for declarative iteration.
- This aligns with the project's functional style (see `magma.option` and `magma.result`) and reduces mutation-heavy loop code.

How it works

- The rule is an `IllegalToken` check for token `LITERAL_FOR`. When any `for` loop is encountered during Checkstyle analysis, it will report an error with the message:

  "'for' loops are banned; use a 'while' loop or Streams (forEach/map) instead."

How to verify

- Run Checkstyle via Maven (from project root):

```powershell
mvn checkstyle:check
```

- Or run the full test/build which includes Checkstyle checks depending on CI configuration:

```powershell
mvn test
```

Notes and edge-cases

- This rule is intentionally strict and will flag enhanced-for (foreach) loops too. If you prefer to only ban indexed/traditional `for` loops and allow enhanced-for, we can refine the rule to use a regexp targeting `for` headers with semicolons.

Follow-ups

- If the team wants to allow enhanced-for loops, I can update the rule to only ban `for` loops that contain semicolons in their header (traditional indexed for-loops) and allow the enhanced-for / foreach form.
