# Magma

A self-hosting Java-to-C++ transpiler.

## Overview

Magma compiles its own Java source code (`Main.java`) into C++ code (`Main.cpp`). The project demonstrates a functional programming approach to compiler construction within Java.

## Prerequisites

### For Java Development

- Java 24 JDK
- Maven 3.6+

### For C++ Compilation

- CMake 3.20 or higher
- A C++20 compatible compiler:
  - **Windows**: MSVC (Visual Studio 2019+), Clang, or GCC
  - **Linux**: GCC 10+, Clang 11+
  - **macOS**: Clang 11+ (Xcode 12+)

## Building the Java Compiler

### Using Maven Wrapper (Recommended)

**Windows:**

```powershell
.\mvnw -DskipTests package
```

**Unix/Linux/macOS:**

```bash
./mvnw -DskipTests package
```

### Running the Transpiler

To regenerate the C++ code from Java:

```powershell
.\mvnw exec:java
```

Or directly:

```powershell
java -cp target/classes magma.Main
```

This reads `src/main/java/magma/Main.java` and writes to `src/main/windows/magma/Main.cpp`.

## Building the Generated C++ Code

### Using CMake (Recommended)

#### Windows (PowerShell)

```powershell
# Build in Release mode
.\build.ps1

# Build in Debug mode
.\build.ps1 -BuildType Debug

# Clean build
.\build.ps1 -Clean

# Build and run
.\build.ps1 -Run
```

#### Unix/Linux/macOS

```bash
# Make the script executable (first time only)
chmod +x build.sh

# Build in Release mode
./build.sh

# Build in Debug mode
./build.sh Debug

# Clean build
./build.sh --clean

# Build and run
./build.sh --run
```

#### Manual CMake Build

```bash
# Create build directory
mkdir build
cd build

# Configure
cmake .. -DCMAKE_BUILD_TYPE=Release

# Build
cmake --build . --config Release

# The executable will be in build/bin/magma (or magma.exe on Windows)
```

### Using Compiler Directly

**With Clang:**

```bash
clang++ -std=c++20 src/main/windows/magma/Main.cpp -O2 -o magma
```

**With GCC:**

```bash
g++ -std=c++20 src/main/windows/magma/Main.cpp -O2 -o magma
```

**With MSVC:**

```cmd
cl /std:c++20 /EHsc /O2 src\main\windows\magma\Main.cpp
```

**Note:** The generated C++ file can be very large. Compilation may require significant memory and time.

## Project Structure

```
Magma/
├── src/
│   └── main/
│       ├── java/
│       │   └── magma/
│       │       └── Main.java          # Source compiler (Java)
│       └── windows/
│           └── magma/
│               └── Main.cpp           # Generated C++ code
├── build/                             # CMake build directory (gitignored)
├── target/                            # Maven build directory (gitignored)
├── CMakeLists.txt                     # CMake configuration
├── build.ps1                          # Windows build script
├── build.sh                           # Unix build script
├── pom.xml                            # Maven configuration
└── README.md                          # This file
```

## Development Workflow

1. **Modify the Java compiler** in `src/main/java/magma/Main.java`
2. **Regenerate C++**: Run `.\mvnw exec:java` or `java -cp target/classes magma.Main`
3. **Compile the C++**: Run `.\build.ps1` or `./build.sh`
4. **Validate**: Ensure the generated C++ compiles successfully

The canonical test is that `Main.java` successfully self-transpiles and the produced C++ compiles without errors.

## Architecture

- **Single-File Compiler**: The entire compiler logic is in `Main.java`
- **Functional Style**: Uses custom functional primitives (`Option`, `Result`, `Iter`, `List`)
- **AST-Based**: Java AST (`J*` types) → C++ AST (`C*` types) → Code generation
- **Self-Hosting**: The compiler compiles itself

### Key Components

- **Lexing/Parsing**: Custom ad-hoc parsing using `State`, `Folder`, and `divide` methods
- **AST Construction**: Parses Java into `JType`, `JExpression`, `JDeclaration` nodes
- **Transformation**: Converts Java AST to C++ AST (`CType`, `CExpression`, `CDeclaration`)
- **Code Generation**: Calls `generate()` on C++ nodes to produce source code

## Contributing

When adding language features:

1. Add new `J*` types for Java AST nodes
2. Add corresponding `C*` types for C++ AST nodes
3. Update transformation helpers: `transformType`, `transformExpression`, `transformInvocation`
4. Test by regenerating and compiling the C++ output

All changes should be made to `Main.java` - do not manually edit `Main.cpp` as it is generated code.

## License

See project documentation for license information.
