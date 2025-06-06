# Implementation Roadmap (Dependency Order)

This roadmap lists language features in an order where each builds on the prerequisites below it. Start from the top to reduce dependency complexity and implementation churn.

---

## 1. Primitive Types and Basic Expressions

* Integer, floating-point, boolean, char, \&Str (string) types
* Numeric, char, string, and boolean literals
* Basic arithmetic, logical, and comparison operators
* Variable and constant declarations (`let`, `mut`, initialization)
* Expression evaluation and statement sequencing

## 2. Control Flow

* if/else
* while, for, for...else loops
* break, continue, return statements
* Expression statements

## 3. Modules and Imports

* Module system and file structure
* Visibility modifiers (`public`, `local`, `private`)
* Import/export syntax
* Syntax fallback to C

## 4. Functions and Lambdas

* Named functions (`def`, arrow syntax)
* Lambdas and higher-order functions (including capture lists)
* Parameter passing and positional typing
* Global and inline constants

## 5. Structs and Tuples

* Struct definition and initialization
* Tuple definition, initialization, and field access
* class keyword as syntactic sugar for struct + constructor

## 6. Algebraic Data Types (ADTs)

* Enum/tagged union definitions with {} syntax for variants
* Pattern matching (`match` statement, exhaustiveness checking)
* Option, Result types

## 7. Arrays

* Fixed-size array types and literals
* Indexing and iteration
* Template/parameterized arrays

## 8. Ownership, Borrowing, and Lifetimes

* Unique ownership semantics
* Immutable and mutable borrows (references)
* Lifetime analysis (with elision)
* Compiler checks for borrowing/ownership rules

## 9. Type System Enhancements

* Nominal typing, type inference, and explicit casts
* Numeric literal suffixes and coercion rules
* Type aliases
* Template/generic types with monomorphization

## 10. Error Handling

* Result\<T, E> and error propagation
* `?` operator for concise error handling

## 11. Classes and Traits

* Trait syntax and dynamic dispatch (with Box/allocator)
* Inline/static trait dispatch for zero-cost abstraction
* Attribute/decorator syntax as higher-order functions
* Reflection (read-only metadata)

## 12. Async and Concurrency Model

* Promise type and callback-based async functions
* Interrupt-driven promise queue
* Async/await syntax desugaring

## 13. Program Startup, Entry, and Misc

* Entry file/module conventions
* Prohibition of recursion (enforcement)
* Inline keyword for constants
* Parameterized imports (`require`)
* Cyclic dependency detection and enforcement

## 14. Standard Library and I/O

* Importing and using standard libraries (e.g., Console)
* Concurrency and I/O primitives as APIs

---

**Note:** Many features can be prototyped in parallel, but ownership/borrowing, lifetime checks, and module/import logic are especially foundational for correctness and safe code reuse.
