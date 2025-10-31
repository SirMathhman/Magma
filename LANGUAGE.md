# Magma Language Specification (Frozen)

This document defines the complete syntax and semantics of the Magma language. All three backends (TypeScript, JavaScript, LLVM IR) must implement these semantics identically.

## Core Language Features

### Functions

```magma
fn add(a : I32, b : I32) : I32 => a + b;
fn greet(name : String) : String => "Hello, " + name;
```

**Semantics:**

- Type annotations required for parameters and return type
- Single expression body (functional style)
- Arrow (`=>`) separates signature from body
- Semicolon terminates declaration

### Structs

```magma
struct Point {
  x : I32,
  y : I32
}

struct User {
  name : String,
  age : I32,
  active : Bool
}
```

**Semantics:**

- Named product types
- Fields with explicit type annotations
- Immutable by default
- Support field access: `point.x`

### Classes (Constructor Functions)

```magma
class fn Point(x : I32, y : I32) => {
  Point { x: x, y: y }
}
```

**Semantics:**

- Syntactic sugar for constructor functions
- Must return instance of same-named struct
- Block body with statements and expressions

### Variables and Binding

```magma
let x : I32 = 5;
let mut y : I32 = 10;
let name = "Magma";  // Type inferred from literal
```

**Semantics:**

- `let` creates immutable binding
- `let mut` creates mutable binding
- Type annotations optional for local variables (inferred)
- Required for function parameters and struct fields

### Ownership Model

#### Immutable Borrow (`&T`)

```magma
fn read(data : &String) : Void => /* read only */;
```

**Semantics:**

- Multiple immutable references allowed
- No mutation through reference
- Borrowed value cannot be moved while borrowed

#### Mutable Borrow (`&mut T`)

```magma
fn modify(data : &mut String) : Void => /* mutate */;
```

**Semantics:**

- Only one mutable reference allowed at a time
- Mutable reference allows mutation
- Exclusive access guaranteed

#### Move Semantics

```magma
let x = String::new();
let y = x;  // x moved to y, x no longer valid
```

**Semantics:**

- Non-Copy types move by default
- Copy types (primitives, etc.) are copied
- Moving invalidates original binding

### Generic Types (Monomorphization)

```magma
struct Box<T> {
  value : T
}

fn unwrap<T>(box : Box<T>) : T => box.value;
```

**Semantics:**

- Monomorphization at compile time
- Each concrete type instantiation generates specialized code
- Type parameter constraints optional initially (no trait bounds required)
- Same code in all three backends for same specialization

### Type System

#### Primitives

- `Bool` - true/false
- `I32` - signed 32-bit integer
- `I64` - signed 64-bit integer
- `F32` - 32-bit floating point
- `F64` - 64-bit floating point
- `String` - Unicode string
- `Void` - no value (unit type)

#### Composite Types

- `(T, U)` - tuples
- `[T]` - arrays (fixed-size)
- `Vec<T>` - dynamic vectors
- `Option<T>` - nullable type
- `Result<T, E>` - error handling

#### Result and Error Handling

```magma
fn safe_parse(s : String) : Result<I32, String> =>
  /* returns Ok(value) or Err(error_message) */;

fn caller() : Result<Void, String> =>
  let value = safe_parse("42")?;  // Propagate error with ?
```

**Semantics:**

- `Ok(value)` - success case
- `Err(error)` - error case
- `?` operator unwraps or propagates error
- All three backends must handle identically

### Control Flow

#### If/Else Expressions

```magma
let result = if x > 0 {
  "positive"
} else {
  "non-positive"
};
```

**Semantics:**

- Expressions (not statements) - return values
- Both branches must have same type
- Optional `else` for `Bool` branch

#### Loops

```magma
let mut i = 0;
while i < 10 {
  /* body */
  i = i + 1;
}

for item in collection {
  /* process item */
}
```

**Semantics:**

- `while` loops for condition-based iteration
- `for` loops for collection iteration
- `break` exits loop
- `continue` skips to next iteration

#### Pattern Matching

```magma
match value {
  Ok(x) => /* handle x */,
  Err(e) => /* handle e */
}
```

**Semantics:**

- Exhaustive matching required
- Pattern binds variables
- Supports destructuring structs, enums, tuples

### Method Calls and Field Access

```magma
let text = "hello";
let len = text.len();        // Method call
let first = text[0];         // Array indexing
let x = point.x;             // Field access
```

**Semantics:**

- Method calls use dot notation
- Automatic receiver handling (borrowing when needed)
- Field access on structs and tuples

### Operators

#### Arithmetic

- `+` (addition)
- `-` (subtraction)
- `*` (multiplication)
- `/` (division)
- `%` (modulo)

#### Comparison

- `==` (equality)
- `!=` (inequality)
- `<` (less than)
- `<=` (less than or equal)
- `>` (greater than)
- `>=` (greater than or equal)

#### Logical

- `&&` (logical AND)
- `||` (logical OR)
- `!` (logical NOT)

#### Bitwise

- `&` (bitwise AND)
- `|` (bitwise OR)
- `^` (bitwise XOR)
- `<<` (left shift)
- `>>` (right shift)

**Semantics:**

- Standard precedence and associativity
- Short-circuit evaluation for `&&` and `||`

### Comments

```magma
// Single-line comment
/* Multi-line comment */
```

## Semantic Requirements

### Type Checking

- Bidirectional type inference
- Required annotations: function parameters, function returns, struct fields
- Inferred annotations: local variable bindings, expressions
- Type unification algorithm for inference

### Borrow Checking

- Verify ownership rules at compile time
- Detect use-after-move violations
- Prevent data races (exclusive mutable access)
- Support borrowing with lifetime tracking (implicit for now)

### Monomorphization

- Collect all concrete type instantiations during semantic analysis
- Generate specialized code for each instantiation
- All backends produce identical specialized code

### Error Handling Semantics

- `Result<T, E>` type tracks fallible operations
- `?` operator in functions returning `Result` propagates errors
- Automatic `Ok` wrapping at function end if applicable

## Module System

- File-to-module mapping: `src/foo/bar.mg` → module `foo.bar`
- Package system: directory structure defines module hierarchy
- Module visibility: public by default, `priv` keyword for private

## Code Generation Contracts

### All Three Backends Must:

1. Produce human-readable output (proper indentation, meaningful names)
2. Preserve source location metadata for debugging
3. Handle ownership semantics consistently
4. Produce identical behavior for equivalent Magma source
5. Support all language features identically

### TypeScript Backend Produces:

- `.ts` files with idiomatic TypeScript
- `.d.ts` type declaration files
- Source maps for debugging
- Output ready for `tsc` compilation

### JavaScript Backend Produces:

- `.js` files with idiomatic JavaScript
- Ownership semantics as runtime values (GC-compatible)
- Source maps for debugging
- Output ready for Node.js/bundlers

### LLVM Backend Produces:

- `.ll` text files (human-readable LLVM IR)
- Proper LLVM type system mapping
- Function signatures and control flow
- Source location metadata
- Output ready for `llvm-as` + linker

## Future Extensions (Out of Scope for v1.0)

- Async/await
- Macros
- Trait bounds and associated types
- Lifetime annotations (implicit for now)
- Module re-exports
- Visibility modifiers beyond `priv`
- Const evaluation
