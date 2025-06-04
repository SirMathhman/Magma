## Features

- Parses Java sources to find classes, interfaces and records
- Detects inheritance and dependency relationships
- Generates a PlantUML diagram summarizing the relations
- Creates TypeScript stubs that match the Java hierarchy
- Provides helper scripts for building, running and testing
- Uses a small `Result` type for explicit error handling
- Handles generic type arguments when converting return values
- Preserves method type parameters on generated methods
- Preserves the `static` modifier on generated methods
- Includes a `check-ts.sh` utility to validate the TypeScript output
