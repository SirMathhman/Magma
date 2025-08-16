Magma
=====

This project contains a small Java utility. Added components:

- `pom.xml` — Maven build file. Sources are under `src/java` (non-standard layout) so the POM sets `<sourceDirectory>` accordingly.
- `config/checkstyle/checkstyle.xml` — Checkstyle configuration that enforces Cyclomatic Complexity per method to be <= 10.

How to run checks (requires Maven):

1. Install Maven (https://maven.apache.org/install.html) if not already installed.
2. From the project root, run:

```powershell
mvn -q verify
```

The `maven-checkstyle-plugin` runs during the `verify` phase and will fail the build if any method exceeds cyclomatic complexity of 10.

Notes
- The Checkstyle config is minimal and targets only the `CyclomaticComplexity` check. You can extend it with other modules as needed.
- If you want the check to be applied during `compile`, change the plugin's execution phase in `pom.xml`.
