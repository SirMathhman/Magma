# Magma: A Self-Hosted Java-to-TypeScript Compiler

Magma is a self-hosted compiler that translates Java source code to TypeScript. It's written in Java and can compile its
own source code to TypeScript, demonstrating the concept of self-hosting.

## Features

- Translates Java classes to TypeScript classes
- Translates Java interfaces to TypeScript interfaces
- Handles fields with appropriate type mapping
- Handles methods with appropriate parameter and return type mapping
- Supports inheritance (extends) and interface implementation (implements)
- Supports constructors, including super constructor calls
- Self-hosting capability (can compile itself)

## Java-to-TypeScript Type Mapping

| Java Type                | TypeScript Type |
|--------------------------|-----------------|
| String                   | string          |
| int, long, float, double | number          |
| boolean                  | boolean         |
| char                     | string          |
| void                     | void            |
| Object                   | any             |

## Project Structure

- `src/java/com/magma/compiler/`: Source code for the compiler
    - `JavaToTypeScriptCompiler.java`: Basic compiler implementation
    - `JavaToTypeScriptCompilerV2.java`: Enhanced compiler with support for interfaces, inheritance, and constructors
    - `SelfHostedCompiler.java`: Self-hosted compiler that can read and compile Java source files
    - `Main.java`: Basic test cases
    - `MainV2.java`: Enhanced test cases including self-hosting test

## Usage

### Basic Usage

```
// Create an instance of the compiler
JavaToTypeScriptCompiler compiler = new JavaToTypeScriptCompiler();

// Compile Java code to TypeScript
String javaCode = "public class Example { private int value; }";
String typeScriptCode = compiler.compile(javaCode);
System.out.println(typeScriptCode);
```

### Advanced Usage

```
// Create an instance of the enhanced compiler
JavaToTypeScriptCompilerV2 compiler = new JavaToTypeScriptCompilerV2();

// Compile Java code with interfaces and inheritance
String javaCode = "public interface Printable { void print(); }\n" + "public class Example implements Printable {\n" +
									"    public void print() { System.out.println(\"Hello\"); }\n" + "}";
String typeScriptCode = compiler.compile(this.javaCode);
System.out.println(typeScriptCode);
```

### Self-Hosting Usage

```
// Create an instance of the self-hosted compiler
SelfHostedCompiler compiler = new SelfHostedCompiler();

// Compile a Java file to TypeScript
compiler.compileFile("path/to/input.java","path/to/output.ts");

// Compile all Java files in a directory
compiler.compileDirectory("path/to/input/dir","path/to/output/dir");

// Compile the compiler itself
compiler.compileSelf("path/to/output/dir");
```

## Development Approach

This project was developed following Test-Driven Development (TDD) principles and Kent Beck's 4 rules of simple design:

1. All tests pass
2. No duplicates
3. Simple intent
4. No code that doesn't qualify for the first three

The development process followed these steps:

1. Write a test for a specific feature
2. Implement the minimal code to make the test pass
3. Refactor the code to improve design while keeping tests passing
4. Repeat for each new feature

## Limitations

- Method bodies are not translated; only method signatures are preserved
- Generic types are not fully supported
- Annotations are not preserved in the TypeScript output
- Complex Java language features (e.g., inner classes, enums) are not supported

## Future Improvements

- Add support for translating method bodies
- Add support for generic types
- Add support for annotations
- Add support for more Java language features
- Improve error handling and reporting
- Add command-line interface for easier usage

## License

This project is open source and available under the MIT License.