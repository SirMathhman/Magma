```markdown
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

## Testing and conformance

- The implementation should include a test harness that compiles Magma test programs, generates C, compiles the generated C, and verifies runtime behavior.

## Notes and alternatives

- This document shows one practical approach. Implementers can choose other backends or runtime designs; if so, they should document differences and map language guarantees to their implementation choices.
