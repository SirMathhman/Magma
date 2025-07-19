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

### Bounded Parameter Types
Function parameters may include numeric bounds such as `I32 > 10`. These
constraints are checked only when arguments are compile-time literals so the
implementation stays simple. Calls with literals that violate the bound cause
compilation to fail, while variables bypass the check since their values are not
known. This strikes a balance between early feedback and keeping the parser
lightweight.

### Bounded Variables
Variables can now be initialized from other variables, lifting the earlier
restriction that only literals were allowed. Numeric declarations may include
bounds like `let y: I32 > 10 = x;`. When a bound is given, the initializer's
effective range is checked against it. `if` statements contribute to this
effective range: inside `if (x > 10)` the variable `x` is treated as `> 10`
for the body. Assignments fail only when the initializer's range does not fit
within the declared bound. This keeps the parser lightweight while allowing
simple flow-sensitive checks.

### Array Indexing with Bounds
Array access now requires the index to be a compile-time bounded value.
Arrays are declared with a fixed size such as `let arr: [U64; 2] = [100, 200];`.
An index variable must specify a bound that references the array length,
written `let i: USize < arr.length = 0;`.  The compiler verifies the bound and
rejects out-of-range constants or variables without the appropriate constraint.
This keeps indexing safe without adding a runtime check while remaining simple
enough for the regular-expression based parser.

### Parenthesized Expressions
The parser now accepts arbitrary balanced parentheses around expressions. Rather
than building a full expression grammar, the compiler repeatedly strips matching
outer pairs before evaluating the expression. This recursive approach keeps the
implementation small while allowing familiar grouping such as `((value))`. When
parentheses influence precedence&mdash;for example `(3 + 4) * 7`&mdash;the grouping
is preserved in the generated C so the semantics remain intact.

### Arithmetic Expressions
Variable declarations and assignments may now use simple arithmetic made up of
numeric literals. Instead of introducing a full expression parser, a tiny helper
checks that the expression only contains digits, parentheses, and the `+`, `-`,
`*`, or `/` operators. If valid, the expression is copied verbatim into the
generated C code. Bounds are still enforced when literals appear by evaluating
the expression in Python. Parentheses may be used within these expressions to
control precedence, so `(3 + 4) * 7` remains exactly as written. This keeps the
implementation compact while enabling common calculations like `1 + 2 * 3 - 4`.

Mixing function calls within these expressions is now supported as long as the
called functions were parsed earlier in the file. The code uses Python's `ast`
module to validate that only arithmetic operators, variables, and known
function calls appear. Boolean literals within such expressions are translated
to `1` or `0` so the generated C code stays self-contained. Constant folding
still only happens when an expression reduces to pure numerics, keeping the
parser small while allowing idioms like `first(200 + second())`.

## Documentation Practice
When a new feature is introduced, ensure the relevant documentation is updated to capture why the feature exists and how it fits into the design.

## Continuous Integration
A lightweight CI pipeline runs tests on every push and pull request using GitHub Actions. The goal is to keep feedback fast and avoid regressions as features grow. The workflow installs dependencies from `requirements.txt` and executes `pytest` to honor our test-driven approach. Keeping the pipeline small adheres to simple design and ensures developers focus on the code rather than infrastructure.
