# Magma (minimal Maven project)

This is a minimal Maven project scaffolded to match the installed Java version on this machine (OpenJDK 24).

Files added:

- `pom.xml` - Maven project file targeting Java 24
- `src/main/java/magma/App.java` - Simple Hello World application
- `README.md` - This file

Additional files:

- `src/main/java/magma/Interpreter.java` - Empty placeholder class for future interpreter features

Build & run

If you have Maven installed, build with:

    mvn -DskipTests package

Then run the JAR from the `target` directory (artifactId-version.jar):

    java -jar target/magma-0.1.0-SNAPSHOT.jar

If Maven is not installed, you can compile and run directly with javac/java:

    javac -d out src/main/java/magma/App.java
    java -cp out magma.App
