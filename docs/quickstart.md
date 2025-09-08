# Quickstart — Magma to C (example)

This quickstart shows a tiny Magma program, the expected generated C, and how you would compile and run it once the compiler emits C source.

## Example Magma program

Save as `examples/hello.mg`:

    fn main() -> i32 {
        let x: i32 = 2 + 3;
        print(x);
        return 0;
    }

Notes:
- `print` prints an integer followed by a newline.

## Expected generated C

    #include <stdio.h>

    int main(void) {
        int x = 2 + 3;
        printf("%d\n", x);
        return 0;
    }

## Compile & run (once you have the generated C file)

On Windows with gcc (MinGW) or a Unix-like shell:

    gcc -std=c11 -O2 -o hello hello.c
    ./hello

On Windows with MSVC (Developer Command Prompt):

    cl /EHsc /O2 hello.c
    hello.exe

## Roadmap for tooling

1. Implement a `magmac` tool that takes `.mg` files and emits `.c` files.
2. Provide command-line flags for target C dialect, optimization, and output paths.
3. Add tests under `tests/` to compare generated C with expected output.

## Contributing examples

Add example Magma files to `examples/` and include the expected `.c` in `examples/generated/` to help validate code generation as the compiler is developed.

## Small-expression CLI usage

The repository includes a tiny CLI stub (`magma.CLI`) that can evaluate very small expressions passed as a single argument. For example, running the CLI with the argument `5+3` will cause the tool to parse and evaluate the expression and exit with code `8` (the sum), which is useful for quick end-to-end checks in tests.

Note: this is a lightweight development convenience and not a full REPL.
