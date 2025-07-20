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

All of the number handling code&mdash;from type mapping to range math&mdash;now
lives in `magma.numbers` so the main compiler loop remains readable.
