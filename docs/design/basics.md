# Design Reasoning

The compiler currently works with a single input file and a single output file.
This decision keeps the interface familiar and mirrors traditional compilers,
which simplifies future command-line integration.  For now, non-empty input was
initially echoed back with a `compiled:` prefix while the empty-file case
generated an empty `main` function in C.  This minimal behavior allowed tests to
drive the implementation while leaving room for the real compilation pipeline to
evolve.  The first extension to the grammar introduces a simple function form
`fn name() => {}` that compiles directly to `void name() {}` in C.  Supporting
this syntax keeps the compiler behavior obvious while providing a stepping stone
toward a richer language.

The next step generalizes this translation to handle multiple functions in a
single file. Rather than introduce a full parser, each line is examined with a
regular expression. Only lines that match the simple function pattern are
translated; anything else falls back to the placeholder behavior. This keeps the
compiler's implementation straightforward and allows the tests to drive future
grammar additions.

The grammar now accepts an explicit `Void` return type written as
`fn name(): Void => {}`.  The compiler still emits `void` in the generated C
code but parsing the annotation prepares the ground for richer type handling
without complicating the current translation scheme.

As another small step, a boolean return can be expressed using
`fn name(): Bool => { return true; }` or `fn name(): Bool => { return false; }`.
To keep the generated C portable without additional headers, the compiler emits
`int` and `1` or `0` rather than `bool` and `true`/`false`:
`int name() { return 1; }` or `int name() { return 0; }`. Keeping the body fixed
lets the regular-expression approach continue working while hinting at how types
and function bodies will eventually evolve.

Numeric return types such as `U8` or `I32` map directly to plain C integers like
`unsigned char` or `int`. The body is limited to `return 0;` so the same regular
expression can parse these functions without growing more complicated. Using
standard C types avoids introducing additional headers at this stage.
Return type inference is kept deliberately simple: when a function omits the
type annotation but returns a numeric or boolean value, the compiler assumes an
`int` result. This avoids another grammar production while still allowing terse
examples like `fn first() => { return 100; }`.

The translation relies on a single regular expression. To keep this simple
approach viable as code gets reformatted, the regex now tolerates arbitrary
whitespace in function declarations. Initially this only covered spaces and
tabs because the compiler split input into lines. This restriction meant each
function had to occupy a single line. The parser now scans the entire source
so newlines and carriage returns are treated like any other whitespace.
Without this flexibility, casual reformatting would fail the compile step and
undermine the project's forgiving early-stage design.

The next feature introduces simple variable declarations using `let` inside
functions.  Each declaration is validated against the declared type so that
obvious mismatches are caught early without a full type checker.  Booleans are
again translated to plain `int` values to keep the generated C self-contained
while numeric types reuse the existing mapping table.  Allowing only literals as
values keeps the regular-expression parser manageable and continues the theme of
small incremental steps.

Array declarations extend this idea without introducing complex parsing. The
compiler recognizes expressions like `let nums: [I32; 3] = [1, 2, 3];` and emits
`int nums[] = {1, 2, 3};`. Only literal values are allowed so the same
regular-expression approach can verify the element count and type without a full
type checker. Keeping the transformation straightforward preserves the project's
focus on small, test-driven increments.

Variable declarations may now omit the type when assigned a boolean or numeric
literal. In these cases the compiler infers `Bool` or `I32` and still emits a
plain `int` in C. This inference keeps the source terse while retaining the
simple regular-expression parser. Assignments to the `Void` type are rejected so
that meaningless variables do not slip through the translation.

Typed declarations may now omit an initializer entirely. Providing the type is
enough information for the compiler to emit an uninitialized C variable, so
`let value: I16;` becomes `short value;`. This keeps the parser simple because no
additional expression analysis is required.

Assignment statements build on this by requiring a `mut` keyword on the
original declaration.  Without `mut` the compiler rejects reassignments so that
immutable values stay predictable.  The same simple literal checks ensure the
assigned value matches the variable's type.  Booleans remain translated to
`int` as `1` or `0`, while numeric variables accept only decimal literals.  This
keeps the implementation small while establishing a basic form of type safety.

The next increment introduces `struct` declarations such as `struct Point {x : I32; y : I32}`.
Only boolean and numeric field types are accepted, mirroring the existing scalar
support.  Each field is translated directly to its C equivalent, resulting in
`struct Point {int x; int y;};`.  By parsing structures with another regular
expression, the compiler avoids a full parser while still allowing clear data
definitions.  Variables may now use these structures as types. A declaration
like `let p: Point;` expands to `struct Point p;` in C, keeping the layout
explicit without introducing a new type system.

Structure literals provide a way to initialize these types without a complex
parser. A statement like `let myPoint = Point {3, 4};` becomes:
`struct Point myPoint; myPoint.x = 3; myPoint.y = 4;` in the generated C. The
compiler expands the literal into individual assignments so the output remains
simple and avoids nested initializer syntax.

To reduce boilerplate further, `class fn Name(x: Type, ...) => {}` acts as a
shorthand for declaring a struct and a constructor. The parameter list becomes
the struct fields, and the generated function populates a temporary `this`
value before returning it. This keeps the source compact without complicating
the regex-driven parser.

Methods placed inside this `class fn` block are flattened into separate
functions that take the struct as their first parameter. This mirrors the
existing approach for inner functions so the implementation stays uniform
while enabling a basic method syntax without real member lookups. The
receiver is exposed as a regular variable named `this`, making it possible to
return or pass the value directly with statements like `return this;`.

Function declarations now accept parameters written as `name: Type` separated
by commas.  The same regular-expression approach parses these parameters,
limiting them to boolean and numeric types so the implementation stays small.
Parameters translate directly to their C equivalents, maintaining the
incremental strategy of introducing features only as tests require them.

As the code generator grew, the output originally placed entire
functions and structures on single lines. This kept the implementation
minimal but made the resulting C hard to read. The compiler now emits
newlines with four-space indentation inside each block so that generated
code resembles hand-written C. This formatting step clarifies the output
without altering the underlying logic.

Allowing nested `{` and `}` blocks required moving beyond a single regular
expression for function bodies. A small recursive parser now tracks brace depth
so blocks can appear within blocks. This keeps the implementation straightforward
while opening the door to more complex control flow constructs later.

The next step introduces simple conditional execution using `if (condition)`
statements. Conditions are copied directly so the syntax mirrors C, but boolean
literals are still converted to `1` or `0` to keep the generated code free of
extra headers. The same recursive block parser handles the body, letting nested
`if` constructs work without additional logic.

Comparison operators `<`, `<=`, `>`, `>=`, and `==` are validated so that both
operands share the same type. This conservative check prevents accidental mixes
of boolean and numeric values while keeping the parser straightforward.  When a
mismatch occurs, compilation fails rather than producing questionable C code.

Nested conditionals are also checked for impossible combinations. If an inner
`if` statement contradicts the outer condition&mdash;for example `if (x > 10)`
followed by `if (x < 10)`&mdash;the compiler rejects the program. Detecting these
trivial dead paths keeps the generated code honest without requiring a complex
optimizer.

Function calls are now recognized as stand-alone statements. Arguments may be
literals or previously declared variables, and boolean values are converted to
`1` or `0` so the generated C remains header-free. Unknown variables cause
compilation to fail, but the callee's signature is not yet validated. This keeps
the parser small while letting tests drive interaction between functions.

Imports follow the same lightweight approach. When the source contains a line
like `import foo;`, the compiler simply emits `#include <foo.h>` in the output.
There is no verification of the module name; the assumption is that the
developer provides a valid header. This keeps the implementation minimal while
allowing the build system or later stages of the compiler to supply the proper
files.

External function declarations follow this philosophy as well. A line like
`extern fn exit(code: I32);` merely records the signature so calls can be
validated. No code is emitted, and the compiler does not verify that the symbol
exists. Requiring the `extern` keyword helps avoid accidentally omitting a
function body.

To keep interoperability simple, extern functions may accept parameters of type
`Any`. This special type corresponds to `void*` and skips type validation when
calling the function. Limiting `Any` to extern parameter lists prevents it from
creeping into normal code while still enabling foreign interfaces.

A small convenience wrapper allows the compiler to be executed directly with
``python src/magma/__init__.py``. When run in this manner the module detects the
absence of a package context and adjusts ``sys.path`` so the ``magma`` package
resolves correctly. This provides a quick way to compile the example file under
``working/`` without installing the package first and keeps experimentation
lightweight.

