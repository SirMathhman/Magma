# Compiler Features

The `Compiler` class supports a small but growing set of language constructs. Regular expressions and a minimal recursive parser keep the implementation straightforward while tests drive each addition.

output files; empty input results in `int main() {}` in the output. It also
supports a basic function syntax `fn name() => {}` which becomes
`void name() {}` in C. The pattern tolerates arbitrary whitespace so that
formatting differences – including newline and carriage return characters – do
not affect compilation. An optional explicit
return type such as `fn name(): Void => {}` is also recognized and produces
the same C output. Functions may declare a boolean return with
`fn name(): Bool => { return true; }` or `fn name(): Bool => { return false; }`.
These yield `int name() { return 1; }` or `int name() { return 0; }` in C so
that the output remains valid without extra headers. Numeric return types
like `U8` or `I64` translate to plain C integers such as `unsigned char` or
`long long` with a fixed body `return 0;`. Functions may also omit the
return type entirely when returning a numeric or boolean value. The compiler
infers `int` as the C return type in these cases. Multiple functions can appear one
per line, each translated in the same manner. Functions can also take
parameters written as `name: Type` separated by commas, so
`fn add(x: I32, y: I32)` yields `int add(int x, int y)` in the generated C.
Function bodies may also contain simple variable declarations of the form
`let name: I32 = 1;` which become `int name = 1;` in C. Only literal values
are accepted so the regular-expression parser remains straightforward.
Arrays can be declared with `let arr: [I32; 3] = [1, 2, 3];` resulting in
`int arr[] = {1, 2, 3};`.
When the type is omitted and the value is a boolean or integer literal, the
compiler infers the type and still emits `int` in the generated C code.
Typed variables may also be declared without an initializer, for example
`let value: I16;`. The compiler then emits an uninitialized C variable like
`short value;`.
Variables can also hold references to functions. Declaring
`let myEmpty: () => Void;` creates an uninitialized function pointer and
produces `void (*myEmpty)();` in the output C code. The pattern now accepts
parameter lists as well, so `let adder: (I32, I32) => I32;` results in
`int (*adder)(int, int);`. This keeps the regular expression approach while
supporting simple higher-order functions.
Function parameters may use the same syntax, so `fn run(cb: () => Void)`
becomes `void run(void (*cb)())` in the generated C.
Basic pointer types follow a similar pattern. A declaration like
`let reference: *I32 = &value;` results in `int* reference = &value;`.
Only the address-of operator was supported initially.  Pointers can now be
dereferenced with `*name`, so `let value: I32 = *reference;` copies the pointed
integer.  The compiler simply emits `*reference` in the C output and performs no
additional checks, keeping pointer usage explicit and easy to validate.
Assignment statements are supported when the variable is declared with
`mut` and the new value matches the original type.  Reassignment is written
simply as `name = 2;` and translates directly to the equivalent C statement.
Type aliases introduce new names for existing primitive types using
`type Name = I16;`. Aliases are resolved during compilation and do not
appear in the output.
Structures can be declared with `struct Name {x : I32;}` and translate to
the C form:

```c
struct Name {
int x;
};
```
Variables may use these structures directly. `let p: Name;` becomes
`struct Name p;` in the generated C code.
Struct fields may also reference functions. A declaration like
`struct Test { doSomething: () => Void }` produces a C field
`void (*doSomething)();` allowing simple callbacks without adding new
syntax to the language.
Generic structures like `struct Wrapper<T>` are monomorphized on use. A
declaration `let w: Wrapper<I32>;` produces a specialized `struct
Wrapper_I32` so the output remains explicit C.
A literal initializer like `let p = Name {1, 2};` expands to a declaration
followed by field assignments so the output stays easy to read. Constructors
may also be invoked at the global level, so `let car: Car = Car();` emits the
simple line `struct Car car = Car();` before any methods or functions.
It is even possible to define a class inline and instantiate it immediately:
`let value : Test = (class fn Test() => {})();`. The compiler registers the
struct and its constructor before emitting the initialization call.
The shorthand `class fn Name(x: Type)` defines both the struct and a
constructor function that assigns each parameter into a temporary `this`
value and returns it. Methods declared inside the block are flattened
into standalone functions like `void method_Name(struct Name this)` so
they behave just like inner functions with an explicit receiver. Inside these
methods the `this` value can be used in expressions, allowing `return this;`
to return the constructed struct.
This shorthand now supports a single type parameter. Using
`class fn Wrapper<T>(value: T) => {}` defers code generation until a concrete
type like `Wrapper<I32>` appears. At that point the compiler emits a specialized
struct and constructor named `Wrapper_I32`.
Objects build on this idea for the common case of a parameterless singleton.
Writing `object Config {}` generates the same struct/constructor pair as
`class fn Config() => {}` but the constructor caches a single instance using
static storage. Any statements inside the block run only on the first call so
initialization is deferred until needed.
Generic function declarations such as `fn malloc<T>() => {}` follow the same
approach. They are stored without emitting C code until invoked with a concrete
type.
Type parameters may appear anywhere a normal type is expected inside these
generic definitions. The compiler resolves them through a single `resolve_type`
helper when a concrete instantiation is seen, so pointers like `*T` or arrays
`[T; 4]` are handled transparently.
Field names inside these methods may also omit the `this.` prefix. The compiler
automatically rewrites `value` to `this.value` when it matches a struct field so
calls like `return value;` remain concise.
Enumerations are declared with `enum MyEnum { First, Second }` and become
`enum MyEnum { First, Second };` in the generated C. The member names keep
their original casing.
Struct enums extend this idea by generating a tagged union. A declaration such
as `struct enum Option { Some, None }` emits an auxiliary `enum OptionTag` and a
`struct Option` containing that tag along with a C `union` of the variant
structures. This keeps the feature a thin layer over plain C constructs.
Variants may specify parameters inside parentheses. These parameters become
fields of a struct named after the variant. For example
`struct enum Option { Some(value: I32), None }` produces:

```c
struct Some {
    int value;
};
struct Option {
    enum OptionTag tag;
    union {
        struct Some Some;
        struct None None;
    } data;
};
enum OptionTag { Some, None };
```
Only variants with parameters receive an explicit struct definition, keeping the
output minimal when no data is stored.

Instances of struct enums are created with `Enum.Variant(...)` syntax. A
variable may be annotated with either the parent enum type or a specific
variant such as `Option.Some`. Initialization sets the tag and any variant
fields directly.
Function and struct bodies are emitted with four-space indentation so the
generated code is easier to inspect.
Nested blocks written with `{` and `}` can be placed inside functions and
may nest arbitrarily. Each level of braces increases the indentation in the
generated C code.
Inner functions are flattened into new top-level declarations such as
`inner_outer`. When this happens an empty struct `outer_t` is emitted before
the functions to reserve space for eventual closure data. If the outer
function contains `let` declarations, those variables become fields of this
struct and are assigned within the outer function's body. This models a
simple closure environment without changing how callers invoke the inner
function.
Simple `if` statements written as `if (condition) { ... }` are translated
directly. Boolean literals in the condition become `1` or `0` to keep the
generated C self-contained.
`while` loops follow the same pattern and may contain any supported
statements. `break` and `continue` are recognized inside these loops
and translate directly to their C equivalents. Functions with non-`Void`
return types may include `return` statements anywhere within their bodies.
Basic comparisons `<`, `<=`, `>`, `>=`, and `==` require both sides to have
matching types; otherwise compilation fails. Nested `if` statements are
rejected when the conditions cannot all be true at the same time, preventing
dead code like `if (x > 10) { if (x < 10) { ... } }`.
Function calls written as `foo(1, bar);` are copied directly after
translating boolean literals to `1` or `0`. Each argument must either be a
literal or a previously declared variable; otherwise compilation fails. When
function parameters include numeric bounds such as `I32 > 10`, literal
arguments are validated against the bound at compile time.
Variables may also be initialized from other variables. Numeric
declarations can include bounds like `let y: I32 > 10 = x;`. The initializer
must satisfy the bound, which may be refined by surrounding `if` statements.
A declaration inside `if (x > 10)` can therefore use `x` as `> 10`.
Array elements can be accessed with `arr[i]` when `i` is bounded by
`arr.length` using a declaration like `let i: USize < arr.length = 0;`.
Out-of-range constants are rejected at compile time.
Struct literals may expose a field directly as `(Wrapper {100}).value`,
which compiles to the literal value without introducing a temporary
variable.
Expressions may be wrapped in any number of parentheses. The compiler
removes matching outer pairs before processing so `( (x) )` is treated the
same as `x`. Parentheses that alter precedence are preserved, so `(3 + 4) * 7`
remains grouped in the output. Arithmetic expressions involving only numeric
literals such as `1 + 2 * 3 - 4` are copied directly into the generated C
code. They are evaluated only when needed for bound checks, keeping the
parser minimal. Expressions may include calls to previously defined
functions, allowing constructs like `first(200 + second())`.

- `import foo;` inserts `#include <foo.h>` at the top of the generated
  C file. Any import name is accepted and simply maps to the
  corresponding header.

- `extern fn name(args): Type;` declares a function implemented elsewhere.
  No C code is emitted; the compiler simply stores the signature so calls can be
  type-checked. The `extern` keyword must be present with a trailing semicolon
  instead of an arrow and body.


- `.github/workflows/ci.yml` – GitHub Actions workflow that installs dependencies and runs `pytest`.
