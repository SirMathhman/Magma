# Implementation Roadmap (Indented Breakdown)

This breakdown expands each major feature area into smaller dependent parts for clarity and granular implementation.

---

## 1. Primitive Types and Basic Expressions

* Types:

  * Integer types:

    * Can parse literals: I8, I16, I32, I64, U8, U16, U32, U64
    * Can store and print integer values
    * Can perform arithmetic and assignment for each size/signedness
    * Can compare equality and order
  * Floating-point types:

    * Can parse F32, F64 literals (decimal, scientific)
    * Can store, print, and do basic arithmetic
    * Can compare floats (==, !=, <, >, <=, >=)
  * Boolean:

    * Can parse `true` and `false`
    * Can use in logical operations and conditionals
  * Char:

    * Can parse char literals (e.g., 'a', '
      ')
    * Can compare and print chars
  * \&Str (string reference):

    * Can parse, print, and compare string literals
    * Can handle escape sequences
* Literals:

  * Integer literals:

    * Can parse and distinguish decimal, hex, octal, binary
    * Can handle numeric suffixes for explicit type
  * Floating-point literals:

    * Can parse both notation styles
    * Can check correct type (F32, F64)
  * Boolean literals:

    * Unit test parsing and use in expressions
  * Char literals:

    * Check escapes, compare, print
  * String literals:

    * Can parse, store, print, and compare
    * Unit test all escape cases
  * Raw string literals:

    * Can parse and store literal text (no escapes)
    * Can print and compare raw strings
* Operators:

  * Arithmetic operators:

    * Unit test `+`, `-`, `*`, `/`, `%` for all numeric types
  * Logical operators:

    * Unit test `&&`, `||`, `!` on booleans
  * Comparison operators:

    * Unit test all comparisons on all types (numeric, char, bool, string)
  * Bitwise operators:

    * Unit test `&`, `|`, `^`, `~`, `<<`, `>>` on integer types
  * Assignment and compound assignment:

    * Unit test each compound operator for correctness
    * Test assignment and value update
* Declarations and Evaluation:

  * let for variable/constant declaration:

    * Can declare, assign, and retrieve values
    * Compiler enforces initialization
    * Shadowing is not allowed; test compile errors
  * mut for mutability:

    * Can mutate only if declared mutable; test compile errors otherwise
  * All variables must be initialized:

    * Test compile error for uninitialized variable
  * Expression evaluation and statement sequencing:

    * Can parse and execute simple sequences of expressions and statements

## 2. Control Flow

* Conditionals:

  * if statements:

    * Can parse and execute single if
    * Can parse and execute if-else
    * Test with various boolean conditions
* Loops:

  * while loops:

    * Can parse, execute, and break out of while
    * Test loop with 0, 1, many iterations
  * for loops:

    * Can parse initialization, condition, increment
    * Can iterate over numeric range and arrays
    * Test loop variable scope and value updates
  * for...else loops:

    * Can execute else only if loop completes without break
    * Test with and without break
* Jump Statements:

  * break:

    * Exits loops; test correct exit point
  * continue:

    * Skips to next loop iteration; test control flow
  * return:

    * Exits function; test return value
* Expression statements:

  * Can parse and execute statements like assignments, function calls

## 3. Modules and Imports

* Modules/files as units of code organization:

  * Can parse and recognize modules/files
  * Can group related code and symbols
* Visibility:

  * public:

    * Public symbol can be accessed from anywhere; test cross-module access
  * local:

    * Local symbol can be accessed within solution; test internal access
  * private:

    * Private symbol can only be accessed in same file/module; test visibility errors
* Imports/exports:

* import syntax:

    * Uses `import Child from parent.Child;` format for default imports
    * Can import single symbol
    * Can import multiple/group symbols
    * Can import with parameterization (`require`)
    * Example compiler in `src/magma/Main.java` rewrites Java imports into this format
  * export syntax:

    * Can export symbol for use elsewhere
* Parameterized imports (`require`):

  * Module can declare required parameters
  * Can import and supply dependency
  * Error if parameter not provided or mismatched
* C syntax fallback for unspecified features:

  * Test that basic C syntax (comments, operators) is parsed as expected

## 4. Functions and Lambdas

* Function declaration:

  * def keyword:

    * Can declare function with name and parameters
    * Can return value
    * Test arity and type errors
  * Arrow (=>) syntax:

    * Can use expression body
    * Can use block body
    * Can mix expressions and statements
* Parameters:

  * Positional:

    * Parameters passed in correct order
  * Explicitly typed:

    * Type errors caught at compile time
  * No named/default/variadic params (except via templates):

    * Test compile error if misused
* Lambdas/closures:

  * Basic: () => {}:

    * Can declare and execute anonymous function
  * Capture lists:

    * \[value] (move): test move semantics in closure
    * \[\&value] (borrow): test borrow semantics in closure
  * Implicit capture for other variables:

    * Closure captures by default; test variable visibility and lifetime
* First-class functions:

  * Can assign function to variable
  * Can pass as argument to other function
  * Can return from function
* Global and inline constants:

  * let and mut for constants: test compile-time assignment
  * inline for compile-time inlining: test inlining in generated code

## 5. Structs and Tuples

* Structs:

  * Definition:

    * Can parse struct definitions with fields
    * Can enforce unique field names and correct types
  * Instantiation:

    * Can create instance and assign field values
    * Can test error on missing or extra fields
  * Field access:

    * Can access and mutate (if allowed) fields via dot notation
* Tuples:

  * Definition:

    * Can parse and check tuple types
  * Instantiation:

    * Can create and use tuple values
  * Positional access:

    * Can access elements by .0, .1, etc.
* `class def` syntax:

  * `class def <Name>() => {}` sugar for struct + constructor: test class desugaring

## 6. Algebraic Data Types (ADTs)

* Enums/tagged unions:

  * enum keyword:

    * Can declare enum with variants
    * Can use template parameters
  * struct-like syntax for variants:

    * Can declare variants with named fields
    * Can instantiate and match on each variant
  * template parameters:

    * Can use generics in ADT definitions
* Pattern matching:

  * match statement:

    * Can match on enum value
    * Can bind and destructure variant fields
    * Exhaustiveness checking:

      * Error if match is non-exhaustive
    * Optional \_ catch-all:

      * Can provide catch-all branch
* Built-in types:

  * Option<T> (Some, None): test all option usage
  * Result\<T, E> (Ok, Err): test all result/error patterns

## 7. Arrays

* Fixed-size array definition:

  * Can parse and define \[T; N]
  * Test array type errors (e.g., size mismatch)
* Array literals and initialization:

  * Can create arrays from literals
  * All elements must match type
* Indexing:

  * Can access element by index
  * Error if out-of-bounds (at runtime or compile time if possible)
* Iteration:

  * Can loop over array with for
* Template arrays:

  * Can define function with array length as template parameter
  * Monomorphize function per array size

## 8. Ownership, Borrowing, and Lifetimes

* Ownership semantics:

  * Each value has unique owner; test move/copy semantics
* Borrowing:

  * Immutable borrows:

    * Can create and use immutable references
    * Compiler prevents modification
  * Mutable borrows:

    * Can create and use mutable references
    * Compiler ensures unique mutable reference
  * & for references:

    * \&T and \&mut T syntax and checks
  * Lifetime annotations:

    * Can annotate explicit lifetimes
    * Lifetime elision for simple cases
    * Compiler error for use-after-free or dangling references
* Compiler checks:

  * Prevent double free, use-after-free, and data race errors

## 9. Type System Enhancements

* Nominal typing:

  * Structs, enums, traits use names as type IDs; test for type mismatches
* Type inference:

  * Can omit obvious types and infer correctly
* Explicit casts:

  * Can cast from one type to another with explicit syntax
  * Error if unsafe or invalid cast
* Numeric literal suffixes:

  * Suffixes parsed and correct type assigned
  * Error for mismatched type
* Coercion rules:

  * Can implicitly coerce from larger to smaller types
  * Error for invalid coercion
* Type aliases:

  * Can define new type as alias for another
* Templates/generics:

  * Can define and instantiate generic types/functions
  * Monomorphization at compile time verified

## 10. Error Handling

* Result\<T, E> type:

  * Can create and use Ok/Err
  * Can propagate errors explicitly
* ? operator:

  * Propagates error or returns unwrapped value
  * Error if used outside function returning Result
* No panics/unwraps:

  * Compile error if panic/unwrap attempted

## 11. Classes and Traits

* Trait syntax:

  * Can define trait with method signatures
  * No inheritance allowed; compile error if attempted
* Dynamic dispatch:

  * Can create trait object with Box<S>
  * Method table/vtable works for dynamic calls
  * Requires allocator; error if none available
* Static dispatch:

  * inline annotation triggers monomorphization
  * Can call trait statically
* Decorator syntax:

  * @Decorator as higher-order function
  * Syntactic sugar expands as expected
* Reflection:

  * Can inspect type and field metadata at runtime (read-only)

## 12. Async and Concurrency Model

* Promise type:

  * Can define Promise<T> as callback signature
* Async function syntax:

  * Can declare async def
  * Implicitly returns Promise<T>
* Promise queue:

  * Can enqueue and dequeue promises
  * Only one processed per interrupt
* Interrupt-driven execution:

  * Can trigger interrupt and process queued promise
* Async/await desugaring:

  * Can expand async/await into callbacks
  * Unit tests verify correct execution order and state

## 13. Program Startup, Entry, and Misc

* Entry file/module:

  * Can run program from specified entry file
* Recursion prohibition:

  * Compiler detects and errors on recursive function calls
* Inline keyword for constants:

  * Constants marked inline are inlined in generated code
* Parameterized imports:

  * Can declare, require, and supply parameters in imports
* Cyclic dependency detection:

  * Compiler errors on cycles in import graph

## 14. Standard Library and I/O

* Importing standard libraries:

  * Can import standard APIs like Console
* I/O primitives:

  * Can print, read, etc., via API calls
* Concurrency primitives:

  * Can use concurrency API functions (not language keywords)

## 15. Diagnostics & Testing

* Compiler diagnostics:

  * Error messages:

    * Unit test clear, descriptive errors for type mismatches
    * Errors for out-of-bounds, uninitialized variables, use-after-free
    * Errors for visibility violations (private/local/public)
    * Errors for recursion, cyclic imports, orphaned expects without actuals
    * Errors for invalid casts or coercions
  * Warning messages:

    * Warning for unused imports, unused variables
    * Warning for unreachable code or dead branches
  * Line/column reporting:

    * Unit test that errors report accurate file, line, and column
* Tooling hooks:

  * Reflection metadata:

    * Unit test that reflection returns correct type names, field lists
  * Public API enumeration:

    * Unit test that modules expose correct public symbols
* Documentation support:

  * Docstrings:

    * If supported via comments, test that doc comments attach to functions/types
* Code formatting:

  * Pretty-printing/formatter (optional):

    * Unit test that formatted output matches canonical style
* Test harness and assertion support:

  * Function testing:

    * Unit test invoking user-defined functions and verifying output
  * Assertion behavior:

    * If assertions available in test harness, test that failures report correctly
* Edge cases:

  * Unicode handling:

    * Test string and char handling with multi-byte characters, emojis
  * Numeric limits:

    * Test min/max values for all numeric types
  * Complex data structures:

    * Test borrow checker and stack for deeply nested structs or enums
  * Large arrays:

    * Test boundary checks and iteration over large fixed-size arrays
  * Import cycles:

    * Test compile error on cyclic module dependencies
* Concurrency & async edge cases:

  * Empty promise queue:

    * Test interrupt when queue is empty
  * Full queue behavior:

    * Test behavior when too many promises are queued (if limit applies)
  * Concurrent borrow interactions:

    * Test borrow rules when async tasks and interrupts interact with data
* Reflection tests:

  * Verify all fields and methods appear in reflection data for each type
* Desugaring verification:

  * `class def` sugar:

    * Test that class definitions desugar to struct + constructor correctly
  * Decorator sugar:

    * Test that @Decorator usage expands to higher-order function call
  * Async/await sugar:

    * Test that async functions and await desugar to callback sequences
* Performance checks (optional):

  * Inlining verification:

    * Test that inline constants are inlined in generated code
  * Operation benchmarks:

    * Measure basic math and loop performance against expected baselines
