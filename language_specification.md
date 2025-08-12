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

### Struct Types
Structs are composite types that group multiple named fields together. The syntax is:

```plaintext
struct Point { x : I32, y : I32 }
```

This defines a struct named `Point` with two fields: `x` and `y`, both of type `I32`.

```plaintext
let c : U8; // a single character
let s : [U8; 16]; // a string of 16 characters

Note: Strings in Magma are not null terminated because their length is known at compile time. This makes them different from C strings, which use a null terminator to mark the end of the string.
```
- Composite types (e.g., arrays, structs, tuples)
- Type inference and conversion rules

### Array Types
Arrays are declared using the syntax `[Type; Length]`, with a semicolon separating the type and length. For example:

```plaintext
let x : [U8; 3];
```

This declares an array `x` of three unsigned 8-bit integers. Arrays have fixed length and element type.

### Tuple Types
Tuples are declared using the syntax `[Type1, Type2, ...]`, with a comma separating the types. For example:

```plaintext
let myTuple : [U8, U8];
```


Elements of tuples and arrays are accessed using index syntax:

```plaintext
let value = tuple[0]; // Access first element of a tuple
let item = array[2];  // Access third element of an array
```

Indices are zero-based.
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

### Function Syntax
Functions are defined using the following syntax:

```plaintext
fn empty(): Void => {}
fn accept(value : I32): Void => {}
fn pair(): [I32, I32] => { return [1, 2]; }
The third example shows that to return multiple values, a tuple must be used as the return type.

### Closures and Anonymous Functions
Closures and anonymous functions use the same syntax as regular functions. Functions can be nested, and closures can capture variables from their enclosing scope.

Example:

```plaintext
fn outer() => {
	fn inner() => {}
}
```

This example defines a function `outer` that contains a nested function `inner`.

### First-Class Functions and Closures
Functions and closures are first-class values in Magma. They can be assigned to variables, passed as arguments, and returned from other functions.

Example:

```plaintext
let f = fn(x : I32): I32 => { return x + 1; };
fn apply(func, value : I32): I32 => { return func(value); }
let result = apply(f, 10); // result is 11
```

### Function Type Annotations
Variables can be annotated with function types using the syntax `(ParameterTypes) => ReturnType`. For example:

```plaintext
let array : (I32) => Void;
```

This declares a variable `array` that holds a function taking an `I32` parameter and returning `Void`.

Type annotations for function variables can be inferred by the compiler when the context provides enough information. Explicit annotations are optional unless required for disambiguation or clarity.

### Function Overloading and Name Conflicts
Function overloading is not supported in Magma. Each function name must be unique within its scope.

Functions and variables cannot share names within the same scope. Module names are managed in a separate namespace and do not conflict with function or variable names.

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
### Struct Field Default Values
Struct fields can have default values. A default value may be a primitive or a closure that returns a value and takes no parameters.

Example:

```plaintext
struct Point {
	x : I32 = 0,
	y : I32 = || -> I32 { return 42; }
}
```

In this example, `x` defaults to 0, and `y` defaults to the result of a closure that returns 42.

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

## 14. Unresolved Questions
1. Should pattern matching be supported in variable declarations?
2. What are the requirements and rules for trait object safety?
3. What should the module/import system syntax and semantics be?
4. What extensibility mechanisms should be provided, given the absence of custom operators?
5. How should lifetime and borrow checking be designed for ownership-based memory management?
6. What is the syntax for accessing and mutating fields in structs?
7. Are there restrictions on the types of closures that can be used as default values for struct fields?
