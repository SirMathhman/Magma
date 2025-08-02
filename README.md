# Magma Project

A Java utility library for working with two-dimensional maps.

## Project Structure

- `src/java/magma/` - Source code
- `test/java/magma/` - JUnit 5 tests
- `build/` - Build artifacts (created by build script)
  - `classes/` - Compiled main classes
  - `test-classes/` - Compiled test classes
  - `lib/` - JUnit 5 dependencies
  - `test-reports/` - Test execution reports

## Building and Testing on Windows

This project includes Windows batch scripts for consistent building and testing.

### Prerequisites

- Java JDK 8 or higher installed
- Java added to your PATH environment variable

### Building the Project

To build the project, run:

```
build.bat
```

This script will:
1. Create necessary build directories
2. Download JUnit 5 dependencies if they don't exist
3. Compile the main source code
4. Compile the test source code

### Running Tests

To run the tests, execute:

```
run-tests.bat
```

This script will:
1. Check if the project is built, and build it if necessary
2. Run all JUnit 5 tests
3. Generate test reports in the `build/test-reports` directory
4. Display test results in the console

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