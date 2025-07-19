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

### While Loops and Returns
Control flow now includes `while` loops alongside existing `if` statements. The
loop parser mirrors the conditional logic so arbitrary statements can appear
within the loop body, including nested blocks. Functions with non-`Void` return
types are compiled through the same block parser, allowing `return` statements
to appear anywhere rather than being fixed to a single `return 0;` form. This
keeps the implementation uniform while still validating basic type correctness.

### Break and Continue
`break` and `continue` provide early exits and iteration skipping for loops.
The parser recognizes these statements with small regular expressions so the
overall block parser stays uncomplicated. Tests ensure they behave as expected
without extra context tracking, and the compiler simply copies them into the
generated C code.

### Condition Handling Cleanup
As more control flow features appeared, the comparisons used in `if` and `while`
statements duplicated logic for validating operand types and translating boolean
literals to `1` or `0`. A small helper now converts these conditions into C
syntax. This keeps the block parser shallow and avoids repeating nested
expressions.

### Type Conversion Helpers
As the compiler grew, repeated checks converted Magma types to C types and
translated booleans to `1` or `0`. The new `c_type_of` and `bool_to_c`
helpers centralize these conversions so all parts of the compiler share the
same implementation. This reduces duplication and makes further changes
easier to reason about.

### Return Statement Helper
As constructors generated by `class fn` and regular function bodies both needed
to emit `return` statements, code duplication crept in. The `emit_return`
helper now produces these statements with or without a value so that every
return site follows the same formatting logic. This keeps the output uniform
and the compiler simpler to maintain.

### Type Aliases
Type aliases allow new names for existing primitive types using syntax like
`type MyAlias = I16;`. Aliases are resolved during compilation so they do not
appear in the generated C code. This keeps the compiler's internal type handling
simple while letting source files adopt clearer domain terminology.

### Generic Structs
Templates introduce parameterized structs without complicating the runtime
model. Each use of a generic struct instantiates a concrete version with a
monomorphized name. This approach sidesteps template expansion at C compile
time and keeps the generated code explicit. Tests drive the instantiation logic
so that only valid numeric and boolean substitutions are accepted.

### Struct Literal Field Access
Struct literals now allow immediate access to a field using syntax like
`(Wrapper {100}).value`. When all field values are literals, the compiler
substitutes the selected field's value directly. This avoids generating a
temporary variable and keeps the parser simple while enabling a familiar
dot-notation.

### Enumerations
Enums follow the same minimalist approach. A declaration like
`enum MyEnum { First, Second }` translates directly to C with no additional
analysis. Keeping the case of each member intact avoids surprising the user
and fits the philosophy of emitting plain C constructs whenever possible.

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
C declaration `void (*myEmpty)();`. Supporting only the simplest function
pointer form keeps validation trivial while paving the way for higher-order
abstractions. Future steps can expand the pattern to handle parameters and
return types once real use cases emerge.

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

## Documentation Practice
When a new feature is introduced, ensure the relevant documentation is updated to capture why the feature exists and how it fits into the design.

## Continuous Integration
A lightweight CI pipeline runs tests on every push and pull request using GitHub Actions. The goal is to keep feedback fast and avoid regressions as features grow. The workflow installs dependencies from `requirements.txt` and executes `pytest` to honor our test-driven approach. Keeping the pipeline small adheres to simple design and ensures developers focus on the code rather than infrastructure.
