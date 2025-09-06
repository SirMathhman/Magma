Do not use the null literal in source code.

Use java.util.Optional for absent values and prefer methods that return Optional instead of returning null.

Checkstyle is configured (see config/checkstyle/checkstyle.xml) to flag any occurrence of the literal "null" as an error. The Checkstyle plugin is disabled by default in `pom.xml` to avoid surprising CI runs.

To run Checkstyle and fail on violations locally:

```powershell
mvn -Dcheckstyle.skip=false -Dcheckstyle.failOnViolation=true checkstyle:check
```

Rationale: Optional makes absence explicit and reduces NPE risk; this project enforces that pattern with Checkstyle.
