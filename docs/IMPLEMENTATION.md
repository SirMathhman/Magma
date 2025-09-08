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

## Variable mutability (`mut`) — parser and typechecker guidance

- Parser / AST:
  - The parser shall accept an optional `mut` modifier in local `let` declarations and shall record a `mutable` boolean on the `LocalVarDecl` AST node.
  - Local declarations without `mut` but without an initializer (for example `let x : I32;`) shall be represented with `mutable = true` internally for the purposes of later assignment checks (that is, treated as an assignable but uninitialized local in the frontend's analysis).
  - The parser shall reject declarations that include `mut` but lack an initializer (for example `let mut x : I32;`) and shall emit a clear diagnostic indicating that `mut` requires an initializer.

- Typechecker / semantic rules:
  - The typechecker shall enforce that assignments to a variable declared with an initializer are permitted only when the declaration was marked `mut`.
  - The typechecker shall permit assignments to variables declared without an initializer (uninitialized locals) regardless of whether `mut` was written; these locals shall be tracked for definite-assignment until initialized.
  - Attempts to assign to an immutable initialized binding (for example `let x = 0; x = 1;`) shall produce a clear diagnostic indicating the binding is immutable and suggest adding `mut` if mutation was intended.
  - Attempts to read an uninitialized local before it has been assigned on every control-flow path shall produce a definite-assignment error.

  Compound-assignment lowering and enforcement

  - Parser / AST:
    - The parser shall recognize compound-assignment tokens `+=`, `-=`, `*=`, `/=`, `%=` and construct a `CompoundAssign` AST node with `lhs`, `op`, and `rhs` children.
    - The parser shall not accept `++` or `--` tokens as valid operators; encountering them shall produce a clear syntax error and a suggestion to use `x += 1` or `x -= 1` instead.

  - Typechecking and semantic checks:
    - The typechecker shall verify that the `lhs` of a `CompoundAssign` is an assignable variable (either declared `mut` with an initializer, or an uninitialized local tracked as assignable). Assignments to immutable-initialized variables shall be an error.
    - The typechecker shall ensure the underlying binary operation (`lhs <op> rhs`) is valid for the operand types and shall report a type error if not.

  - Lowering:
    - The lowering pass shall translate a `CompoundAssign(lhs, op, rhs)` into an explicit sequence equivalent to `lhs = lhs <op> rhs`, taking care to evaluate `lhs` exactly once when `lhs` is an expression with side-effects (for example, array indexing or field access). For simple variable lhs the lowering is straightforward.
    - Example lowering (simple variable):

        /* Magma */
        let mut x = 0;
        x += 30;

        /* Generated C (sketch) */
        int32_t x = 0;
        x = x + 30;

    - When `lhs` is a more complex lvalue (for example `arr[i]`), the lowering shall evaluate the address/index expressions once and store temporaries as needed to preserve semantics.

  Revision history

  - 2025-09-08 — Add compound-assignment operators `+=|-=`|`*=`|`/=`|`%=` and forbid `++`/`--`; document parsing, lowering, and diagnostics — user
- Lowering and runtime:
  - The lowering pass shall reflect mutability in the generated code by emitting variables as C locals as usual. There is no runtime representation difference between mutable and immutable locals in the C reference backend beyond compile-time enforcement (both lower to C local variables); however, the frontend must ensure that immutability rules are enforced before lowering.

Revision history

- 2025-09-08 — Require initializer when `mut` is present in local declarations; document parser diagnostic for `mut` without initializer — user

Revision history

- 2025-09-08 — Document `mut` declaration semantics: `let mut x = ...` mutable initialized binding, `let x = ...` immutable initialized binding, and `let x : Type;` uninitialized assignable binding; add parser/AST and typechecker guidance — user

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

## Array types and array literals

- Parser / AST:
  - The parser shall accept fixed-size array type annotations with the concrete syntax `[ type ';' integer ]` and shall represent them in the AST as `ArrayType(element_type, length)` nodes.
  - The parser shall accept array literals in the form `[ e1, e2, ..., eN ]` and shall construct an `ArrayLiteral` AST node containing the list of element expressions.

- Typechecking / semantic checks:
  - The typechecker shall verify that an array literal used where a fixed-size array type is expected has exactly the same number of elements as the statically-known length. A mismatch in element count shall be a type error.
  - When an array literal does not have an explicit target type (for example when used in a `let` binding with inference), the compiler shall attempt to infer an array type `[T; N]` where `T` is the common type of all elements and `N` is the element count; if the elements are not uniformly typed the typechecker shall report an error.
  - Each element expression shall be type-checked against the array element type `T`, and implicit/explicit coercions shall be applied only if documented by the implementation.

- Lowering to C (C reference backend guidance):
  - Fixed-size Magma arrays `[T; N]` shall lower to C fixed-size arrays where possible. For example, `[U8; 3]` shall lower to `uint8_t arr[3];` or an initialized form `uint8_t arr[3] = {1, 2, 3};` when an initializer is present.
  - The C backend shall emit aggregate initializers for array literals when available and shall ensure correct element ordering and representation.
  - For array types whose element type maps to a runtime-managed heap type (for example `string`), the backend shall lower the array to an array of the runtime pointer type and ensure appropriate runtime initialization is performed (for example calling `magma_string_new` for string literals as part of initialization) or require explicit runtime helper calls in generated code.

- Examples (Magma -> C sketch):

    /* Magma */
    fn main() -> int {
      let x : [U8; 3] = [1, 2, 3];
      return 0;
    }

    /* Generated C (sketch) */
    #include <stdint.h>

    int32_t magma_main(void) {
      uint8_t x[3] = {1, 2, 3};
      return 0;
    }

Revision history

- 2025-09-08 — Add fixed-size array types `[T; N]` and array literal syntax with parser/typechecker/lowering guidance; include C-backend lowering sketch — user

## Pointer types, address-of, and dereference

- Parser / AST:
  - The parser shall accept pointer type annotations using the concrete syntax `* type` and shall represent them in the AST as `PointerType(element_type)` nodes.
  - The parser shall accept address-of expressions `& lvalue` and dereference expressions `* expression` and shall create `AddressOf` and `Deref` AST nodes respectively.

- Typechecking / semantic checks:
  - The typechecker shall allow assignment of an address-of expression to a matching pointer type. For example `let y : *I32 = &x;` is valid when `x` is an `I32` lvalue.
  - The typechecker shall ensure that dereference expressions `*p` are applied only to expressions whose type is a pointer `*T`. The resulting type of `*p` shall be `T`.
  - The typechecker shall not, in the MVP, attempt to prove pointer validity or detect aliasing; these are runtime/back-end concerns or future static analyses.

- Lowering to C (C reference backend guidance):
  - The C backend shall map Magma pointer types `*T` to C pointer types corresponding to the lowered element type (for example `*I32` -> `int32_t *`).
  - The address-of operator `&` shall lower to C `&` when applied to C-addressable lvalues and the dereference operator `*` shall lower to C `*` as expected. Example lowering:

    /* Magma */
    fn main() -> int {
      let x : I32 = 0;
      let y : *I32 = &x;
      let z : I32 = *y;
      return z;
    }

    /* Generated C (sketch) */
    #include <stdint.h>

    int32_t magma_main(void) {
      int32_t x = 0;
      int32_t * y = &x;
      int32_t z = *y;
      return z;
    }

  - The backend shall document that dereferencing invalid or null pointers is undefined behavior unless the implementation inserts runtime checks; such checks are optional in the MVP but may be recommended for debug builds.

Revision history

- 2025-09-08 — Add pointer types `*T`, address-of `&` and dereference `*` semantics and C-backend lowering guidance — user

## While statements: parsing, typing, and lowering

- Parser / AST:
  - The parser shall recognize `while` statements with the concrete syntax `while ( <expression> ) <statement>` and shall construct a `WhileStmt` AST node with `cond` and `body` children.

- Typechecking:
  - The typechecker shall require the `cond` expression to have type `Bool` for `WhileStmt` nodes.
  - The typechecker shall run definite-assignment checks conservatively across `while` loops: variables assigned only within the loop body shall not be considered definitely assigned after the loop unless proven otherwise.

- Lowering to C (C reference backend guidance):
  - The C backend shall lower `while` statements to C `while` loops directly, ensuring the condition is evaluated before each iteration and that semantics for `break`/`continue` (if supported) are preserved.
  - Example lowering (Magma -> C sketch):

      /* Magma */
      fn main() -> int {
        let mut i: I32 = 0;
        while (i < 10) {
          i += 1;
        }
        return i;
      }

      /* Generated C (sketch) */
      #include <stdint.h>
      #include <stdbool.h>

      int32_t magma_main(void) {
        int32_t i = 0;
        while (i < 10) {
          i = i + 1;
        }
        return i;
      }

Revision history

- 2025-09-08 — Add normative and implementation guidance for `while` statements; include lowering sketch and definite-assignment note — user

## For statements: parsing, typing, and lowering

- Parser / AST:
  - The parser shall recognize a C-style `for` statement with concrete syntax `for ( <for-init> ; <condition>? ; <post>? ) <statement>` and shall construct a `ForStmt` AST node with `init`, `cond`, `post`, and `body` children. The `init` child may be a `LocalVarDecl` node, an `ExpressionStmt` node, or `null` to indicate an empty initializer.
  - A `LocalVarDecl` used in a `for` initializer shall introduce local bindings that are scoped to the `ForStmt` node.

- Typechecking / semantic checks:
  - The typechecker shall require the `cond` expression (when present) to have type `Bool` for `ForStmt` nodes.
  - The typechecker shall perform definite-assignment analysis conservatively across `for` loops: variables assigned only in the loop body or only in the post expression shall not be considered definitely assigned after the loop unless statically provable.
  - Assignments to variables declared in the `init` shall be treated as initializing those variables for use within the loop body and post expression; those variables shall not be visible after the `ForStmt` completes.

- Lowering to C (C reference backend guidance):
  - The C backend may lower a Magma `for` directly to a C `for` loop when the initializer, condition, and post expressions can be represented as C expressions/statements. For example, a Magma `for (let mut i = 0; i < 100; i += 1) { ... }` can lower to `for (int32_t i = 0; i < 100; i = i + 1) { ... }` in generated C. The backend shall ensure scoping of `i` matches Magma semantics by placing the declaration in the C `for` initializer.
  - Alternatively, if the backend or lowering pipeline prefers a simpler lowering, the `for` may be transformed into an equivalent `while` with an explicit initializer and post expression, taking care to preserve scoping. Example lowering (transform to `while`):

      /* Magma */
      for (let mut i = 0; i < 100; i += 1) {
        body();
      }

      /* Lowered C sketch (while form) */
      int32_t i = 0; /* scoped to surrounding function - frontend must ensure name uniqueness */
      while (i < 100) {
        body();
        i = i + 1;
      }

  - When lowering to C `for`, the backend shall generate the initializer as a declaration in the `for` initializer so that the variable's scope is limited to the loop. When lowering to `while` the backend shall generate a unique temporary name or introduce a nested block to contain the initializer to mimic lexical scoping.

Revision history

- 2025-09-08 — Add normative and implementation guidance for C-style `for` statements; include parser/AST, typechecking, scoping notes, and lowering sketches — user

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
