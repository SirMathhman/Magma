# Magma

Magma is a self-hosted compiler that translates Java code to C. It uses a functional programming approach with robust error handling patterns.

## Overview

Magma provides a specialized framework for compiling Java source code into C. Key features include:

- Complete Java to C translation capabilities
- Self-hosted design (the compiler is written in the same language it compiles)
- Support for Java language constructs in the generated C code
- Functional approach to parsing and code generation

The project uses functional programming patterns like Option and Result for robust error handling without exceptions.

## Installation

### Prerequisites

- Java 21 or higher
- A Java IDE (IntelliJ IDEA recommended)

### Building from Source

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/Magma.git
   ```

2. Open the project in your IDE

3. Build the project using your IDE's build tools or with the command:
   ```
   javac -d out/production/Magma src/java/magma/*.java src/java/magma/*/*.java
   ```

## Usage

### Basic Usage

To use Magma for code generation, you can call the `Main.main()` method with appropriate arguments:

```java
public static void main(String[] args) {
    if (args.length < 2) {
        System.err.println("Usage: java magma.Main <input_file> <output_file>");
        return;
    }
    
    final var source = Paths.get(args[0]);
    final var target = Paths.get(args[1]);
    
    Main.readString(source).match(input -> {
        final var output = Main.compile(input);
        return Main.writeString(target, output);
    }, Some::new).ifPresent(Throwable::printStackTrace);
}
```

### Input Format

Magma accepts standard Java code as input and transforms it into equivalent C code. The compiler supports:

- Java class and method definitions
- Object-oriented constructs translated to C structures and functions
- Control structures (if, else, loops, etc.)
- Java operators and expressions
- Basic Java standard library functionality

### Example

Input (Java):
```java
class Example {
    void main() {
        System.out.println("Hello, World!");
    }
}
```

Output (C):
```c
struct Example {
    // Class fields would be here
};

void Example_main(struct Example* this) {
    printf("Hello, World!\n");
}
```

## Architecture

Magma is built around several key components:

- **Main**: Contains the core logic for parsing and compilation
- **DivideState**: Manages the state during parsing and tokenization
- **Node classes**: Represent different code elements (parameters, methods, etc.)
- **Option pattern**: Handles optional values (Some, None)
- **Result pattern**: Handles operation results (Ok, Err)

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.