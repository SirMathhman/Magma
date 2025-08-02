# Magma Project

A Java utility library for working with two-dimensional maps.

## Project Structure

- `src/java/magma/` - Source code
- `test/java/magma/` - JUnit 5 tests
- `target/` - Build artifacts (created by Maven)

## Building and Testing with Maven

This project uses Maven for building and dependency management.

### Prerequisites

- Java JDK 8 or higher installed
- Java added to your PATH environment variable
- Maven installed (optional, as the project includes Maven wrapper)

### Building the Project

To build the project, run:

```
# Using Maven wrapper (no Maven installation required)
mvnw.cmd clean package

# Or if you have Maven installed
mvn clean package
```

This will:
1. Clean previous build artifacts
2. Download required dependencies (including JUnit 5)
3. Compile the main source code
4. Compile and run the test source code
5. Package the application into a JAR file

### Running Tests

Tests are automatically run during the build process. To run only the tests:

```
# Using Maven wrapper
mvnw.cmd test

# Or if you have Maven installed
mvn test
```

This will:
1. Compile the main source code if needed
2. Compile and run all JUnit 5 tests
3. Generate test reports in the `target/surefire-reports` directory

### Running the Application

To run the application after building:

```
java -jar target/magma-1.0-SNAPSHOT.jar
```

## Current Features

- `MapUtils.processTwoDimensionalMap()` - A utility method for processing two-dimensional maps

## Development

To add new tests:
1. Create a new test class in the `test/java/magma` directory
2. Run the tests using `run-tests.bat`

To add new functionality:
1. Add or modify classes in the `src/java/magma` directory
2. Add corresponding tests in the `test/java/magma` directory
3. Build and test using the provided scripts