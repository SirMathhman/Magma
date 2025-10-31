# Magma Compiler CLI Usage Guide

## Installation

Build the compiler from source:

```bash
cargo build --release
```

The binary will be available at `target/release/magma`.

## Basic Usage

### Compile a Magma program

```bash
magma compile <input.mg>
```

This compiles to TypeScript by default, creating `<input>.ts`.

### Specify target language

```bash
magma compile <input.mg> --target ts    # TypeScript (default)
magma compile <input.mg> --target js    # JavaScript
magma compile <input.mg> --target llvm  # LLVM IR
```

### Custom output file

```bash
magma compile input.mg --output output.ts
magma compile input.mg -o custom.js
```

## Debugging Commands

### Tokenize (Lexer output)

```bash
magma lex input.mg
```

Shows all tokens with location information:
```
Tokens from 'input.mg':
  fn at line 1 col 1
  add at line 1 col 4
  ( at line 1 col 7
  ...
```

### Parse (AST output)

```bash
magma parse input.mg
```

Shows abstract syntax tree structure:
```
AST from 'input.mg':
  Items: 2
    [1] FunctionDeclaration { ... }
    [2] FunctionDeclaration { ... }
```

## Information Commands

### Show version

```bash
magma --version
magma -v
```

Output: `Magma 0.1.0`

### Show help

```bash
magma --help
magma -h
magma help
```

Displays full usage documentation.

## Examples

### Example 1: Simple Function

**hello.mg:**
```magma
fn add(x : I32, y : I32) : I32 => x + y;

fn main() : I32 => add(5, 3);
```

**Compile:**
```bash
magma compile hello.mg --target ts
```

**Generated TypeScript (hello.ts):**
```typescript
function add(x: number, y: number): number {
    x + y;
}

function main(): number {
    add(5, 3);
}
```

### Example 2: Struct Definition

**point.mg:**
```magma
struct Point { x : I32, y : I32 }

fn distance(p : Point) : I32 => p.x + p.y;
```

**Compile to all backends:**
```bash
magma compile point.mg --target ts --output point.ts
magma compile point.mg --target js --output point.js
magma compile point.mg --target llvm --output point.ll
```

**TypeScript output:**
```typescript
interface Point {
    x: number;
    y: number;
}

function distance(p: Point): number {
    p.x + p.y;
}
```

**JavaScript output:**
```javascript
function Point(x, y) {
    this.x = x;
    this.y = y;
}

function distance(p) {
    p.x + p.y;
}
```

**LLVM IR output:**
```llvm
%Point = type { i32, i32 }

define i32 @distance(%Point* %p) {
    entry:
        ; ... IR code ...
}
```

## Error Handling

### Invalid input file

```bash
$ magma compile nonexistent.mg
Error reading file 'nonexistent.mg': No such file or directory
```

### Invalid target

```bash
$ magma compile test.mg --target rust
Error: invalid target 'rust'. Must be 'ts', 'js', or 'llvm'
```

### Syntax error in source

```bash
$ magma compile bad.mg
✗ Compilation failed with 1 error(s):
  [1] CompilationError { severity: Error, message: "Expected ':' but found '-'", span: Span { start: 15, end: 16, line: 1, column: 16 }, source: "" }
```

## Output Files

By default, the output filename matches the input with a different extension:

| Input | TypeScript Output | JavaScript Output | LLVM IR Output |
|-------|-------------------|--------------------|----------------|
| `hello.mg` | `hello.ts` | `hello.js` | `hello.ll` |
| `src/main.mg` | `src/main.ts` | `src/main.js` | `src/main.ll` |

All generated files include:
```
// Generated from Magma compiler
// DO NOT EDIT MANUALLY
```

## Compilation Statistics

The compiler reports statistics after successful compilation:

```bash
$ magma compile hello.mg
✓ Successfully compiled hello.mg to hello.ts
  Stats: 33 tokens, 2 AST nodes, 2 HIR exprs, 161 bytes output
```

Meaning:
- **33 tokens**: Total tokens produced by lexer
- **2 AST nodes**: Function definitions in AST
- **2 HIR exprs**: High-level IR expressions after lowering
- **161 bytes**: Size of generated TypeScript code

## Advanced Usage

### Batch Compilation

```bash
# Compile all Magma files to TypeScript
for f in *.mg; do
    magma compile "$f" --target ts
done
```

### TypeScript Compilation Chain

```bash
# Generate TypeScript from Magma
magma compile program.mg --target ts

# Compile TypeScript to JavaScript (requires TypeScript compiler)
tsc program.ts --outDir dist/

# Run the result
node dist/program.js
```

### LLVM Compilation Chain

```bash
# Generate LLVM IR from Magma
magma compile program.mg --target llvm --output program.ll

# Assemble LLVM IR (requires LLVM tools)
llvm-as program.ll -o program.bc

# Compile to native code
llc program.bc -o program.s

# Assemble and link
cc program.s -o program

# Run the result
./program
```

## Performance

Compilation times for typical programs (small functions/structs):

- **Lexing**: <1ms
- **Parsing**: <1ms
- **Semantic Analysis**: <1ms
- **Code Generation**: <1ms
- **Total**: ~1-4ms per program

Output sizes:

- **TypeScript**: 150-200 bytes per function (with type annotations)
- **JavaScript**: 100-150 bytes per function (no annotations)
- **LLVM IR**: 300-500 bytes per function (verbose IR format)

## Troubleshooting

### "magma: command not found"

Add the compiler to your PATH:
```bash
export PATH="$PATH:/path/to/magma/target/release"
```

Or use the full path:
```bash
/path/to/magma/target/release/magma compile input.mg
```

### Output not created

Ensure you have write permissions in the output directory:
```bash
ls -l .  # Check directory permissions
```

Specify an explicit output path:
```bash
magma compile input.mg --output /tmp/output.ts
```

### Type errors during compilation

Check your Magma syntax. Common issues:

- Missing type annotations on function parameters: `fn f(x : I32)` ✓
- Wrong operator usage: Use `=>` for function bodies, not `{}`
- Undefined functions or structs

For more details, use the lex and parse commands to debug:
```bash
magma lex input.mg    # Check tokenization
magma parse input.mg  # Check AST structure
```

## Language Quick Reference

### Function Definition

```magma
fn add(x : I32, y : I32) : I32 => x + y;
```

### Struct Definition

```magma
struct Point { x : I32, y : I32 }
```

### Type Annotations

```magma
let x : I32 = 42;           # Explicit annotation
let result = add(5, 3);     # Inferred from function signature
```

### Operators

```magma
x + y      # Addition
x - y      # Subtraction
x * y      # Multiplication
x / y      # Division
x % y      # Modulo
x == y     # Equality
x != y     # Inequality
x < y      # Less than
x > y      # Greater than
x && y     # Logical AND
x || y     # Logical OR
!x         # Logical NOT
```

### If Expression

```magma
if x > 0 then y else z  # Ternary-style conditional
```

## Support

For issues or questions:
1. Check the error message from `magma --help`
2. Run `magma lex` and `magma parse` for debugging
3. Review language specification in `LANGUAGE.md`
4. Check example programs in `examples/`
