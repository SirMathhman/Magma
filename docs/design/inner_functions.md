### Flattening Inner Functions
Nested function declarations are flattened into top-level functions. When a
function `inner` appears inside `outer`, the compiler generates a new C
function named `inner_outer` before emitting `outer` itself. When this happens
the compiler also declares an empty struct `outer_t` to reserve space for future
closure environments. The generated inner function receives this struct as a
`this` parameter so that captured variables could be threaded through later
without changing call sites. This approach keeps the regular-expression driven
parser viable while sidestepping the complexity of capturing lexical scope.
Future iterations may introduce proper closures, but for now flattening
preserves simplicity and ensures each function remains a standalone unit while
hinting at potential context storage.

The next increment stores outer-scope declarations inside that generated struct
when inner functions are present. Moving variables into the `*_t` struct keeps
their lifetime explicit and prepares for real closures without rewriting call
sites. Only top-level `let` statements are captured for now, which keeps the
implementation straightforward while demonstrating how lexical scope might be
preserved.

The struct now also holds the outer function's parameters whenever an inner
function is present. These parameters are assigned into the environment struct at
the start of the outer function. This keeps the function's call signature
unchanged while allowing inner functions to access the values, bringing the
design a step closer to real closures without complicating the parser.

Allowing variables to reference functions continues this incremental
approach. The parser recognizes `let myEmpty: () => Void;` and emits the
C declaration `void (*myEmpty)();`. The same pattern now accepts simple
parameter lists, so `let adder: (I32, I32) => I32;` becomes
`int (*adder)(int, int);`. Validation remains lightweight, preserving the
regex-based implementation while enabling basic higher-order abstractions.

Originally these captured variables required an explicit type annotation. This
kept the struct generation simple but forced verbose declarations. The compiler
now infers boolean or numeric types when an initializer is present, allowing
`let x = 100;` inside an outer function that defines an inner function. The
environment struct still stores the resolved C type, so the flattening approach
remains unchanged while user code becomes less cluttered.

Tests later revealed that this flattening step emitted the same `*_t` struct
twice: once when the inner function was seen and again when the outer function
completed.  The compiler now records the need for the struct during parsing and
emits it only once when processing the outer function.  This keeps the generated
C concise while preserving room for captured variables.

