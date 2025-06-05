# Project Specification

This document defines the high-level specification for the Magma project. Additional details may be added as development progresses.

## Modern C Language Features

The following list summarizes features found in the modern C programming language. These cover capabilities introduced over several versions of the ISO/IEC C standard (C89/C90, C99, C11, C17 and later revisions).

- **Basic Syntax and Types**
  - Fundamental data types: `char`, `int`, `float`, `double`, and related signed/unsigned variants.
  - Derived types including pointers, arrays, structures, unions and enumerations.
  - Storage class specifiers: `auto`, `register`, `static`, `extern`.
  - Type qualifiers: `const`, `volatile`, `restrict` and `_Atomic`.
  - Function prototypes with support for variable argument lists using `<stdarg.h>`.

- **Control Flow**
  - Selection statements: `if`, `else`, `switch`.
  - Iteration statements: `for`, `while`, `do`-`while`.
  - Jump statements: `goto`, `break`, `continue`, `return`.

- **Preprocessor Facilities**
  - File inclusion with `#include` and macro definition with `#define`.
  - Conditional compilation via `#if`, `#ifdef`, `#ifndef`, `#elif`, and `#endif`.
  - Macro stringizing and token pasting operations (`#` and `##`).

- **Memory Management**
  - Static storage duration objects via global or `static` declarations.
  - Automatic storage duration objects on the stack.
  - Dynamic memory management with `malloc`, `calloc`, `realloc`, and `free` from `<stdlib.h>`.

- **Language Enhancements from C99**
  - Inline functions (`inline` keyword).
  - Mixed declarations and code within a block.
  - Boolean type in `<stdbool.h>`.
  - `long long` integer type.
  - Variable-length arrays and designated initializers.
  - Single-line comments using `//` syntax.
  - Complex number types in `<complex.h>`.

- **Language Enhancements from C11 and Beyond**
  - `_Static_assert` for compile-time assertions.
  - `_Noreturn` function specifier.
  - `_Thread_local` storage specifier and minimal thread library `<threads.h>`.
  - Generic selections with `_Generic`.
  - Alignment control using `_Alignas` and `_Alignof`.
  - Atomic operations via `<stdatomic.h>`.

These features provide the foundation for writing portable and efficient C programs. Later revisions, such as C17 and ongoing C23 work, largely refine existing behavior and add optional library enhancements.
