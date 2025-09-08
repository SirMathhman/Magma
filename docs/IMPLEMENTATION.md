# Magma Implementation Guide (C reference)

Status: Informative (implementation guidance, not part of the language spec)

This document describes one concrete implementation strategy for the Magma language: a reference code generator that emits ISO C (C11) and a small runtime in C. This guide is intended for compiler implementers who choose C as a backend. It is not normative for the language.

# Magma Implementation Guide (C reference)

Status: Informative (implementation guidance, not part of the language spec)

This document describes one concrete implementation strategy for the Magma language: a reference code generator that emits ISO C (C11) and a small runtime in C. This guide is intended for compiler implementers who choose C as a backend. It is not normative for the language.

## Scope and goals

- Provide clear, reproducible guidance for lowering Magma constructs to C.
- Keep generated C readable and debuggable to aid debugging and learning.
- Provide a minimal runtime (allocation, strings, basic error handling) that can be replaced or extended.

## Project layout (suggested)

- runtime/
  - magma_runtime.h  -- public runtime API used by generated code
  - magma_runtime.c  -- allocator, string helpers, basic runtime glue
- gen/                -- codegen templates and helpers (compiler side)
- examples/           -- small Magma programs and generated C

## Representation and ABI

- Value types (e.g., `int`, `float`, `bool`) map to fixed C types: `int32_t`, `double`, `bool` (from <stdbool.h>). Use <stdint.h> types for portability.
- Heap values (strings, arrays, heap-allocated structs) are represented by pointers to runtime-managed structs. The runtime header defines opaque typedefs like `magma_string_t`.
- All generated functions should use a clear naming scheme to avoid symbol collisions, for example: `magma_modulename_funcname` or `magma__modulename__funcname`.

## Generated C patterns

- Modules and headers: for each Magma module produce a header (`modname.h`) with type and function declarations and a C file (`modname.c`) with definitions.
- Structs: Magma `type` declarations map to C `struct` definitions. If a struct contains heap values, the C struct fields hold runtime-managed pointers.
- Functions: Magma `fn` maps to a C function. For functions that return non-void values, return the C equivalent type. For functions that may fail or need rich error semantics, consider returning a runtime `magma_result_t` struct (value + status) — the runtime can provide helper macros.
- Locals: locals with inferred types generate local C variables with explicit C types.

## Memory management and runtime API

- The reference runtime provides minimal primitives:
  - magma_alloc(size_t) / magma_free(void*)
  - magma_string_new(const char*) / magma_string_free(magma_string_t*)
  - optional: reference counting helpers `magma_rc_inc`, `magma_rc_dec`
- The runtime should be small and well-documented so implementers can swap in a GC or other strategy later.

## Strings

- Represent strings as a heap struct containing length and char* data. Expose helpers in the runtime header for creating, freeing, and comparing strings.

## Closures and function values

- Non-capturing function literals can be lowered to plain function pointers.
- Capturing closures require a closure environment struct plus a function that takes an environment pointer as its first argument. To model this in C:
  - Define an environment struct with captured variables.
  - Define a trampoline function with signature `return_type trampoline(env_t* env, /* original params */)`.
  - Represent the closure value as a struct containing `env_t* env` and a function pointer to the trampoline (or a generic `void*` context + `void (*fun)(void*)` pairing).

## Error handling

- For functions that may return errors, the generator can produce C functions that return a `magma_result_t` (value + error flag/message) or use an out-parameter for error information. The runtime should provide helpers to create and inspect `magma_result_t` values.

## Interop (FFI)

- Provide a small `extern`/FFI model where the compiler accepts declarations that map to C functions and types. Generated code should include the appropriate headers and translate calls to the external ABI.

## Build and integration

- Generated code should be self-contained and include a small header that documents required runtime symbols.
- Provide a small Makefile or build manifest that shows how to compile generated C together with the runtime into an executable using `gcc`/`clang`.

## Examples

- Hello World: generator emits `main` that calls `magma_io_println("Hello, Magma!")` implemented in the runtime.

Top-level expression programs:

  The reference C backend may accept a Magma source file that is a single top-level expression with an optional integer width suffix (MVP: `I32`). Such a source file shall be lowered to a generated `main` function that returns the expression's value as an `int32_t`.

  Example source and generated C:

  Magma source:

    5I32

  Generated C (sketch):

    #include "magma_runtime.h"
    int32_t magma_main(void) {
      return 5;
    }

    int main(int argc, char** argv) {
      int32_t r = magma_main();
      /* Map to process exit code: use low-order 8 bits by default */
      return (int)(r & 0xff);
    }

  Implementations targeting other backends should document how `int` return values are mapped to the platform's process exit mechanism. The C reference uses the low-order 8 bits of the returned `int32_t` to produce a POSIX-compatible exit code.

## Testing and conformance

- The implementation should include a test harness that compiles Magma test programs, generates C, compiles the generated C, and verifies runtime behavior.

## Notes and alternatives

- This document shows one practical approach. Implementers can choose other backends or runtime designs; if so, they should document differences and map language guarantees to their implementation choices.

## Lowering of local `let` statements with `I32`

- For local `let` statements that include an explicit `I32` annotation (for example `let x : I32 = 0;`), the C reference backend shall lower the binding to a local `int32_t` variable in the generated function. Example lowering sketch:

    /* Magma */
    fn main() -> int {
      let x : I32 = 0;
      return x;
    }

    /* Generated C (sketch) */
    #include <stdint.h>
    int32_t magma_main(void) {
      int32_t x = 0; /* lowered from let x : I32 = 0; */
      return x;
    }

- The backend shall perform a type-check to ensure the initializer's value is compatible with the annotated `I32` type and shall emit a diagnostic if the initializer cannot be represented as a 32-bit signed integer where required.

## Integer width annotations mapping and lowering

- The C reference backend shall support lowering the following integer width annotations to the corresponding C fixed-width types:

  - `U8` -> `uint8_t`
  - `U16` -> `uint16_t`
  - `U32` -> `uint32_t`
  - `U64` -> `uint64_t`
  - `I8` -> `int8_t`
  - `I16` -> `int16_t`
  - `I32` -> `int32_t`
  - `I64` -> `int64_t`

- Example lowering (Magma -> generated C sketch):

    /* Magma */
    fn main() -> int {
      let u: U8 = 10;
      let i: I32 = -1;
      return (int) i;
    }

    /* Generated C (sketch) */
    #include <stdint.h>
    uint8_t magma_u = 10; /* example global or lowered local */
    int32_t magma_i = -1;

    int32_t magma_main(void) {
      uint8_t u = 10; /* lowered from let u : U8 = 10; */
      int32_t i = -1; /* lowered from let i : I32 = -1; */
      return i;
    }

- The backend shall enforce representability checks for initializers (for example, warn or error if an initializer value does not fit in the annotated width or signedness). The implementation guide shall document whether these checks occur at parse-time or during type-checking and the exact diagnostic messages produced.

## Default integer literal and initializer semantics

- By default, unannotated integer literals and inferred integer locals shall be treated as 32-bit signed integers. The C reference backend shall lower such unannotated integers to `int32_t`. For example, `let x = 0;` shall lower to `int32_t x = 0;` in generated C unless context requires a different type.

- If a `let` binding includes an explicit integer width annotation (for example `let x : U8 = 0;`), the initializer shall be interpreted in the context of that annotated type and lowered to the corresponding fixed-width C type (for example `uint8_t`). That is, `0` in `let x : U8 = 0;` becomes a `U8` initializer rather than being treated first as `I32` and then converted.

- The backend shall perform representability checking for annotated initializers and shall emit diagnostics when initializers are out-of-range for the annotated type (for example, assigning `-1` to `U8` or `256` to `U8`). The implementation guide shall document whether the compiler performs implicit conversions or reports precise errors for mixed-signedness cases.

## Booleans

- Magma boolean literals `true` and `false` shall be supported and shall have the `Bool` type.

- The C reference backend shall lower Magma `Bool` to C's `bool` type from `<stdbool.h>` (or `_Bool` if `<stdbool.h>` is not available). Example lowering sketch:

    /* Magma */
    fn main() -> int {
      let b: Bool = true;
      if (b) {
        return 0;
      }
      return 1;
    }

    /* Generated C (sketch) */
    #include <stdbool.h>
    int32_t magma_main(void) {
      bool b = true; /* lowered from let b : Bool = true; */
      if (b) {
        return 0;
      }
      return 1;
    }

- The backend shall ensure boolean expressions are represented by `bool` in generated function signatures and local variable declarations where applicable. When interoperating with other types, implementations shall define conversion semantics (for example, whether numeric `0` maps to `false` and any non-zero maps to `true`) and document these rules.

## If statements: lowering and definite-assignment

- Magma `if` statements shall be lowered directly to C `if`/`else` constructs where possible. The backend shall ensure the condition expression is lowered to a `bool` value (or an expression that produces `bool`) before emitting the C `if`.

- Example lowering (Magma -> generated C sketch):

    /* Magma */
    fn main() -> int {
      let x : I32;
      if (true) x = 3; else x = 5;
      return x;
    }

    /* Generated C (sketch) */
    #include <stdint.h>
    #include <stdbool.h>

    int32_t magma_main(void) {
      int32_t x; /* local declared without initializer */
      if (true) {
        x = 3;
      } else {
        x = 5;
      }
      return x;
    }

- Definite-assignment checks shall be performed by the frontend: the compiler shall reject lowering to C when a local might be read before being assigned on some control-flow path. Implementations may choose to initialize locals to a default value in generated C as a fallback, but doing so must be documented and the frontend shall still emit a warning if this masks a probable uninitialized-use bug.

## If expressions: parsing, typing, and lowering

- Parsing and AST:
  - The parser shall accept `if` in expression position where the concrete syntax is `if ( <expression> ) <expression> else <expression>` and shall construct an `IfExpr` AST node with `cond`, `then_expr`, and `else_expr` children.
  - The parser shall treat `if` followed by a statement or block (without an `else` expression) as a statement-form `IfStmt` (existing behavior) rather than an expression.

  - The parser and typechecker shall treat `if` used in expression position without an `else` as a syntax/type error: `else` is required for the expression form. Implementations shall report a clear diagnostic when an `if` expression lacks an `else` branch.

- Type checking:
  - The typechecker shall require the condition to be `Bool` for `IfExpr` nodes and shall type-check both branch expressions. Both branches shall have the same type or a documented coercion shall be applied; otherwise the typechecker shall report an incompatible-branch error.
  - For `let` bindings with an explicit annotation (for example `let x : I32 = if (cond) 3 else 5;`), the compiler shall verify that the branch expressions are representable in the annotated type and shall report an error if not.

- Lowering to C (C reference backend guidance):
  - The C backend shall lower `IfExpr` nodes into a sequence that evaluates the condition and then assigns the selected branch value to a temporary variable of the appropriate C type, finally using that temporary where the original expression appeared. This avoids duplicating branch side-effects and ensures single evaluation semantics per branch.
  - Example lowering sketch (Magma -> generated C):

      /* Magma */
      fn main() -> int {
        let x : I32 = if (true) 3 else 5;
        return x;
      }

      /* Generated C (sketch) */
      #include <stdint.h>
      #include <stdbool.h>

      int32_t magma_main(void) {
        int32_t __tmp_if_0;
        if (true) {
          __tmp_if_0 = 3;
        } else {
          __tmp_if_0 = 5;
        }
        int32_t x = __tmp_if_0;
        return x;
      }

  - The backend shall generate unique temporary names for nested or multiple `if` expressions and shall ensure correct scoping so that temporaries do not conflict with user identifiers.
  - The backend shall preserve evaluation order and side-effects: only the selected branch shall be evaluated, and evaluation of the condition shall occur before any branch expression.

Assumptions:

- The C backend lowers `if` expressions to an explicit temporary and `if`/`else` statement; alternative backends may use other lowering strategies (for example, SSA-based IR or a synthetic conditional operator) as long as semantics are preserved.

Revision history

- 2025-09-08 — Require `else` for `if` when used as an expression and document parser/typechecker diagnostics for missing `else` — user

## Revision history

- 2025-09-08  Document lowering of `if` statements to C `if`/`else` and require definite-assignment checks for uninitialized locals  user

## Comparison and logical operator lowering

- The C reference backend shall map Magma comparison and logical operators to the corresponding C operators where possible:

  - `==` -> `==`
  - `!=` -> `!=`
  - `<`  -> `<`
  - `<=` -> `<=`
  - `>`  -> `>`
  - `>=` -> `>=`
  - `!`  -> `!`

- For `string` equality (`==` / `!=`) the backend shall call a runtime helper (for example `magma_string_eq`) that compares contents; generated C shall include the appropriate runtime header.
- For numeric comparisons, the backend shall emit direct C comparisons. The backend shall ensure operands are of compatible numeric kinds (signed vs unsigned and width); mixing signed and unsigned comparisons without an explicit cast shall be diagnosed or lowered via a documented promotion rule.
- For boolean negation `!`, the backend shall use the C `!` operator on `bool` values.

## Revision history

- 2025-09-08  Document mapping of Magma comparison and logical operators to C and note string-equality runtime helper  user

## Revision history

- 2025-09-08  Specify default unannotated integer literals and inferred locals as `I32` and clarify annotated-initializer lowering semantics  user

## Revision history

- 2025-09-08  Add support for integer width annotations `U8|U16|U32|U64|I8|I16|I32|I64` and map them to C fixed-width types in the C reference backend  user
## Revision history

- 2025-09-08  Document lowering of `let x : I32 = 0;` to `int32_t` locals in the C reference backend  assistant
