# Magma - Sample Maven project

This is a minimal Maven project scaffold generated to match the local Java version detected on this machine.

Key points:
- Java release: 24 (detected via `java --version`)
- Uses Maven Compiler Plugin with `<release>` set to 24
- Includes a simple `App` class and a JUnit 5 test

How to build:

```powershell
mvn -DskipTests package
```

To run tests:

```powershell
mvn test
```

Run the app:

```powershell
java -jar target/magma-sample-0.1.0-SNAPSHOT.jar
```
