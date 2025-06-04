# Magma

This repository contains a simple Java program that generates a PlantUML file.

## Usage

Compile and run the program using the Java compiler:

```bash
javac GenerateDiagram.java
java GenerateDiagram
```

Executing the program creates a file named `diagram.puml` in the same directory.
The file contains a minimal example of a PlantUML diagram.

## Running tests

The project follows a test-driven development approach using JUnit 5. To run the
tests, download the JUnit Platform console standalone jar and execute it after
compiling the sources:

```bash
# download the console runner (only needed once)
curl -L -o junit-platform-console-standalone.jar \
  https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar

# compile source and test files
javac -cp junit-platform-console-standalone.jar:. GenerateDiagram.java tests/GenerateDiagramTest.java

# run the tests
java -jar junit-platform-console-standalone.jar -cp junit-platform-console-standalone.jar:. --scan-class-path
```
