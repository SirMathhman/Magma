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
definitions.

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

## Documentation Practice
When a new feature is introduced, ensure the relevant documentation is updated to capture why the feature exists and how it fits into the design.

## Continuous Integration
A lightweight CI pipeline runs tests on every push and pull request using GitHub Actions. The goal is to keep feedback fast and avoid regressions as features grow. The workflow installs dependencies from `requirements.txt` and executes `pytest` to honor our test-driven approach. Keeping the pipeline small adheres to simple design and ensures developers focus on the code rather than infrastructure.
