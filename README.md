# magma

Minimal Maven project with package `magma` and JUnit 5 tests.

Requirements
- Java 24 (detected via `java --version` on this machine)
- Maven

Build and test

Run tests:

```powershell
mvn test
```

Run the application:

```powershell
mvn -q exec:java -Dexec.mainClass="magma.App"
```
