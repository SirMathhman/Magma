# Magma

Magma is a code generation and transpilation tool that can process input code and transform it into different output formats. It uses a functional programming approach with robust error handling patterns.

## Overview

Magma provides a flexible framework for parsing, transforming, and generating code. It can be used for:

- Generating Java code from a simplified syntax
- Converting between different programming languages
- Processing and transforming code structures

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

Magma accepts a custom syntax that can be transformed into Java or C code. The syntax supports:

- Class and method definitions
- Function calls and expressions
- Control structures (if, else, etc.)
- Various operators and literals

### Example

Input:
```
class Example {
    void main() {
        println("Hello, World!");
    }
}
```

Output (Java):
```java
public class Example {
    public void main() {
        System.out.println("Hello, World!");
    }
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