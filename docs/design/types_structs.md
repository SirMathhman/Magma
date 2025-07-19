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

### Generic Classes
The same monomorphization now applies to the `class fn` shorthand. A
declaration like `class fn Wrapper<T>(value: T) => {}` is stored without
emitting C immediately. When a concrete type such as `Wrapper<I32>` appears in
code, the compiler emits a specialized struct and constructor:

```
struct Wrapper_I32 {
    int value;
};
struct Wrapper_I32 Wrapper_I32(int value) {
    struct Wrapper_I32 this;
    this.value = value;
    return this;
}
```
Restricting the parameter `T` to numeric or boolean types avoids generating
complex C code and keeps the feature in line with existing generic structs.

Empty generic classes, such as `class fn Box<T>() => {}`, generate no C output
until instantiated. This keeps unused templates from cluttering the compiled
file while still validating their syntax at parse time.

### Generic Functions
Function declarations may also be parameterized. A definition like
`fn malloc<T>() => {}` is accepted but produces no C code until a concrete type
is used. Storing these templates without emitting them mirrors the generic
class behavior and keeps the output uncluttered.

### Function Fields
Struct fields can hold references to functions using the same arrow syntax as
variable declarations. This keeps the grammar uniform while enabling callbacks
without additional keywords. The compiler simply emits a function pointer field
in C, preserving the lightweight translation strategy.
The same notation now works for function parameters, keeping the grammar
consistent across declarations.

### Pointers
Variables may now store raw addresses using a leading `*` in the type.
Initially only taking the address of an existing variable was supported.
Dereferencing with `*name` is now available as well and compiles directly to
C. The compiler does not attempt safety checks here; keeping the feature small
avoids complicating the parsing logic while still permitting low-level
interactions when needed.

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

