# Magma

A compiler for the Magma programming language that targets C.

## Requirements

- Java 24 or later
- Maven 3.6+

## Build

```bash
mvn compile
```

## Test

```bash
mvn test
```

## Run

```bash
mvn exec:java -Dexec.args="input.magma output.c"
```

Or after building:

```bash
java -cp target/classes magma.Main input.magma output.c
```

## Usage

```
magma <input-file> <output-file>
```

- `<input-file>`: Path to the Magma source file
- `<output-file>`: Path to the generated C output file
