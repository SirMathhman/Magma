# C Features and Safety Recommendations

This page outlines common features of the C programming language and how Magma plans to handle them safely. The guidance takes inspiration from Rust's approach to memory safety and concurrency.

- **Manual memory management (`malloc`/`free`)**
  - Magma will prefer scoped allocations with automatic cleanup to avoid leaks and double frees.
- **Pointer arithmetic**
  - Exposed through safe containers and iterators to prevent out‑of‑bounds access.
- **Macros**
  - Limited to hygienic macros or generics to preserve type safety.
- **`union` types**
  - Replaced by tagged unions (enums with data) to prevent undefined access.
- **`goto` statements**
  - Discouraged in favor of structured control flow; exceptions only for low-level error handling.
- **Arrays and indexing**
  - Bounds checked by default to eliminate buffer overruns. Index values must be
    compile-time bounded by the array length, written as `let i: USize < arr.length`.
- **Variable length arrays**
  - Use dynamic vectors which manage capacity safely.
- **Integer overflow**
  - Explicitly checked and either saturates or raises an error.
- **Function pointers**
  - Wrapped in high-level traits to ensure signatures match.
- **Concurrency primitives**
  - Built using ownership and borrowing rules to avoid data races.

The goal is to provide familiar C capabilities while enforcing strong safety guarantees.
