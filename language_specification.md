# Programming Language Specification

## 1. Introduction
- Purpose and goals of the language
- Target audience and use cases

## 2. Lexical Structure
- Character set and encoding
- Tokens: identifiers, keywords, literals, operators, delimiters
- Comments and whitespace rules

## 3. Syntax
- Grammar rules (BNF or EBNF)
- Statement and expression structure
- Block and scope rules

- Primitive types:
	- Unsigned integers: U8, U16, U32, U64
	- Signed integers: I8, I16, I32, I64
	- Floating point: F32, F64
	- Boolean: Bool
- Characters are represented as type U8.
- Strings are represented as fixed-size arrays of U8, with their length defined at compile time. For example:

```plaintext
let c : U8; // a single character
let s : [U8; 16]; // a string of 16 characters

Note: Strings in Magma are not null terminated because their length is known at compile time. This makes them different from C strings, which use a null terminator to mark the end of the string.
```
- Composite types (e.g., arrays, structs, tuples)
- Type inference and conversion rules

### Array Types
Arrays are declared using the syntax `[Type; Length]`. For example:

```plaintext
let x : [U8; 3];
```

This declares an array `x` of three unsigned 8-bit integers. Arrays have fixed length and element type.
- Primitive types:
	- Unsigned integers: U8, U16, U32, U64
	- Signed integers: I8, I16, I32, I64
	- Floating point: F32, F64
	- Boolean: Bool
	- (Other types such as string may be supported)
- Composite types (e.g., arrays, structs, tuples)
- Type inference and conversion rules

## 5. Variables
- Declaration and initialization
- Scope and lifetime
- Mutability rules

### Variable Declarations
Variables are immutable by default. To declare a mutable variable, use the `mut` keyword:

```plaintext
let <identifier> = <expression>;
let <identifier> : <type> = <expression>;
let mut <identifier> = <expression>;
let mut <identifier> : <type> = <expression>;
```

- `let` is the keyword for variable declaration.
- `mut` (optional) makes the variable mutable.
- `<identifier>` is the variable name, following the rules for identifiers.
- `<type>` (optional) specifies the variable's type explicitly (e.g., `I32`, `F64`, `String`).
- `<expression>` is the value assigned to the variable.
- A semicolon (`;`) terminates the declaration statement.

Examples:

```plaintext
let counter = 0;
let pi : F64 = 3.14;
let name : String = "Alice";
let x : I32 = 100;
let mut total = 100;
let mut value : I32 = 42;
```

Variables must be declared before use. Type inference is supported, but explicit types can be provided for clarity or type safety.

## 6. Control Flow
- Conditional statements (if, else, switch)
- Loops (for, while, do-while)
- Jump statements (break, continue, return)

## 7. Functions
- Definition and invocation
- Parameters and return values
- Recursion and higher-order functions


- Organization of code
- Magma uses TypeScript-style `import` and `export` keywords for module organization and visibility.

## 8. Modules
- Organization of code
- Magma uses TypeScript-style `import` and `export` keywords for module organization and visibility.
- Module packaging and directory structure follow Java conventions, where code is organized into packages that map to directory hierarchies.

Example:

```plaintext
// src/math/add.magma
export fn add(a : I32, b : I32) : I32 {
	return a + b;
}

// src/main.magma
import { add } from "math.add";

fn main() {
	let result = add(2, 3);
}
```

Packages are represented by directories, and module paths use dot notation similar to Java.
- Organization of code
- Magma uses TypeScript-style `import` and `export` keywords for module organization and visibility.

Example:

```plaintext
// math.magma
export fn add(a : I32, b : I32) : I32 {
	return a + b;
}

// main.magma
import { add } from "./math.magma";

fn main() {
	let result = add(2, 3);
}
```

## 9. Error Handling
Error handling in Magma uses Result types, which are implemented as a tagged union of `Ok` and `Err`. Functions that may fail return a Result type, which must be handled explicitly by the caller. Panic is not allowed in the language; all errors must be managed through Result values.

Example:

```plaintext
Result<T> = Ok(T) | Err(String)

fn divide(a : I32, b : I32) : Result<I32> {
	if b == 0 {
		return Err("Division by zero");
	}
	return Ok(a / b);
}
```

The caller must check the Result and handle errors appropriately.

## 10. Memory Management
Memory management in Magma will be handled through borrowing and ownership. Each value has a single owner, and references to values must obey borrowing rules to prevent data races and ensure safety. The language will enforce lifetimes and borrow checking at compile time.

Unresolved details include the exact rules for lifetimes, mutable and immutable borrows, and how ownership is transferred between functions and data structures.

## 11. Standard Library
- Built-in functions and modules


## 12. Sample Programs
- Example code snippets

## 13. Traits

Traits in Magma are used to define shared behavior for types. A trait specifies a set of required methods that a type must implement to satisfy the trait. Traits enable polymorphism and code reuse without inheritance.

Trait objects are dynamically sized, meaning their size is not known at compile time and they must be handled via references or pointers.

Example:

```plaintext
trait Display {
	fn to_string(self) : String;
}

struct Point {
	x : I32,
	y : I32
}

impl Display for Point {
	fn to_string(self) : String {
		return "Point(" + self.x + ", " + self.y + ")";
	}
}
```

Types can implement multiple traits, and traits can be used as bounds for generic functions.

## Note on Result Type Metadata
The tagged union Result type in Magma should not have additional metadata or error codes. It consists only of `Ok` and `Err` variants, with `Err` containing a string message.

## Note on Operators
Operator overloading and custom operators do not exist in Magma. All operators have fixed, built-in semantics and cannot be redefined or extended by user code.
