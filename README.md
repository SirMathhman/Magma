# Magma (Maven project scaffold)

A minimal Maven project scaffold set up to match the local Java runtime.

## Java version

This project uses the Java major version detected on this machine (via `java --version`) — the `pom.xml` `java.version` property is set accordingly.

## Build & Run

Build:

```bash
mvn -q -DskipTests=false package
```

Run:

```bash
java -jar target/magma-0.1.0-SNAPSHOT.jar
```

Run tests:

```bash
mvn -q test
```

Notes:

- Maven is expected to be installed on your system (we detected `mvn -v` output). If Maven is not installed, follow instructions at https://maven.apache.org/install.html
- The project uses JUnit Jupiter for tests.
