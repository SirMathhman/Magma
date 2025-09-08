<!-- GitHub Copilot / Assistant instructions for contributors -->
# Copilot / Assistant Instructions

These instructions are written for automated assistants (GitHub Copilot and similar tools). They are prescriptive and enforceable: generated or modified code must follow them exactly.

High-level intent

- Primary audience: automated code generators (Copilot) and automated maintainers. Human maintainers read this for reference.
- Scope: All application/business logic must live under the `magma.app` package. Do not place domain logic elsewhere.
- Goal: The codebase must remain translatable toward native/C output. Avoid language features and runtime behaviors that cannot be reliably converted.

Rules (absolute — do not violate)

- No use of the literal `null`. Use `Optional<T>` or the repository `Result<T, E>` pattern for absent values or errors.
- Do not import or use `java.util.regex` anywhere.
- Do not use `throw` or `throws`. Use `Result`-style error returns.
- Do not use wildcard generics (`?`, `? extends`, `? super`). Use explicit type parameters or bounded generics.
- Do not use raw `Object` types. Use concrete types or proper generics.
- Avoid explicit casts. Prefer pattern matching, polymorphism, or helper converters.

Static-analysis and build constraints

- Java target: JDK 24 with preview features enabled (`--enable-preview`). Generated code must compile under JDK 24.
- Cyclomatic complexity: keep per-method complexity <= 15.
- Method parameter limit: 3 parameters maximum.
- CPD (copy/paste detector) token threshold: 50 tokens.
- JaCoCo coverage: 100% for INSTRUCTION, BRANCH, LINE, METHOD, and CLASS. Tests are centrally managed; do not add tests unless explicitly requested.

Code placement and structure

- Business logic: everything representing domain behavior must be inside `magma.app`.
- Utilities: small, well-specified utilities that are safe for C-translation may live in `magma.util` or `magma.support`. Avoid generic `helpers` or `utils` at the repo root without documentation.
- Keep packages shallow and cohesive. Prefer clear, explicit package names.

Error handling and data shapes

- Use `Result<T, E>` for operations that can fail. The `Result` type expresses success or a typed error without throwing.
- Use `Optional<T>` only to represent optional values; prefer `Result` when an error case must be communicated.

Good vs Bad examples (follow these patterns)

- Optional vs null

Good:

    // Good: return Optional
    public Optional<String> findNameById(int id) {
        // ...implementation...
    }

Bad:

    // Bad: returns null
    public String findNameById(int id) {
        return null;
    }

- Result pattern vs throw

Good:

    // Good: return Result
    public Result<User, Error> loadUser(String id) {
        // ...implementation...
    }

Bad:

    // Bad: throws an exception
    public User loadUser(String id) throws UserNotFoundException {
        // ...implementation...
    }

- Avoid regex

Good:

    // Good: explicit checks or simple parsers
    public boolean isAlpha(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!Character.isLetter(c)) return false;
        }
        return true;
    }

Bad:

    // Bad: uses java.util.regex
    public boolean isAlpha(String s) {
        return s.matches("^[A-Za-z]+$");
    }

Developer verification (Windows / PowerShell)

- Development environment: Windows 11 with PowerShell. Local verification commands assume PowerShell.
- To run checks locally:

```powershell
mvn test
mvn clean test -q
```

- There is no CI; maintainers run local checks before merging.

Maintenance and exceptions

- This document is strict. Any exception to these rules must be documented in this file with rationale and must be approved and recorded by a maintainer.
- When rules change, add a short changelog entry at the top with the date and author.

Behavior for automated assistants

- Refuse to generate code that violates the bans above. If a requested change conflicts with these rules, surface the conflict clearly and suggest compliant alternatives.
- Keep generated commit messages and PR descriptions explicit about why code was added and how it complies with these rules.

When in doubt, prefer conservative, simple code patterns that map cleanly to native/C semantics and explicitly document any non-obvious decisions.
