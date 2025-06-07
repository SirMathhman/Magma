# Java to TypeScript Roadmap

This roadmap compares major Java features with their TypeScript counterparts as implemented by the Magma compiler. It enumerates what is currently converted and what remains unimplemented.

## Conversion Overview

| Java Feature | TypeScript Conversion | Status |
|--------------|----------------------|--------|
| Classes, Interfaces, Records | `class` and `interface` definitions with preserved `extends`/`implements` hierarchy | Implemented |
| Fields and Methods | Fields and function members with parameter and return types | Implemented |
| Generic Type Parameters | Generic parameters on classes and methods | Implemented |
| `static` Methods | `static` methods in TypeScript | Implemented |
| Primitive Types (`byte`, `short`, `int`, `long`, `float`, `double`) | `number` | Complete |
| `boolean` | `boolean` | Implemented |
| `char`, `String` | `string` | Implemented |
| Functional Interfaces (`Function`, `BiFunction`, `Supplier`) | Arrow function types | Implemented |
| Type Parameters in Records | Type parameters resolved for record constructors and fields | Implemented |
| Lambda Expressions | Currently emitted as `0` | Missing |
| `switch` Statements | Emitted as `0` with `case` blocks ignored | Missing |
| `instanceof` Checks | Emitted as `0` | Missing |
| `if`, `for`, `while` Headers | Always generate `if (true)` | Missing |
| Variadic Type Arguments & Complex Types | Replaced with `?` | Missing |

The [features document](features.md) provides additional details and will be updated as the compiler evolves.

## Module Overview

The compiler is organised into a few main packages:

- **`magma.ast`** – abstract syntax tree structures.
- **`magma.compile`** – compilation state and frames for generating TypeScript.
- **`magma.util`** – small collection-like utilities and result types.
- **`magma.Parser`** – parses Java source files.
- **`magma.Generator`** – generates TypeScript output from the AST.
- **`magma.Main`** – entry point used by `build.sh`.

These modules provide a high-level map when navigating the source tree.
