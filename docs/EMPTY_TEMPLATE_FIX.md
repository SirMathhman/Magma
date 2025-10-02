# Fix for Empty Template Declaration Bug

## Problem

Functions and structures with no type parameters were incorrectly generating empty `template<>` declarations in the
transpiled C++ code. For example:

```cpp
template<>
struct Main{};
template<>
void main_Main(char** args) {/*...*/}
```

This is incorrect C++ syntax—`template<>` is only for explicit template specialization. Non-generic functions and
structures should have no template declaration.

## Root Cause

The C++ code generation in `Lang.java` used the following pattern for both `Function()` and `CStructure()`:

```java
final Rule templateParams = Values("typeParameters", Prefix("typename ", Identifier()));
final Rule templateDecl = Prefix("template<", Suffix(templateParams, ">"));
final Rule maybeTemplate = Or(templateDecl, new StringRule(""));
```

When a function or structure had no type parameters:

1. `Values("typeParameters", ...)` would return `Ok("")` (empty string) for an empty or missing list
2. `Suffix(templateParams, ">")` would generate `"" + ">"` = `">"`
3. `Prefix("template<", ...)` would generate `"template<" + ">"` = `"template<>"`
4. The `Or` rule would succeed with `"template<>"` and return it

The fallback `new StringRule("")` never executed because the first alternative always succeeded.

## Solution

Created a new `NonEmptyListRule` that fails when a node list is empty or missing:

**File**: `src/main/java/magma/compile/rule/NonEmptyListRule.java`

- Returns an error if the specified node list is missing or empty
- Allows the `Or` rule to fall back to alternatives
- Delegates lexing and non-empty generation to the wrapped inner rule

Modified the template generation logic in both `Function()` and `CStructure()`:

**File**: `src/main/java/magma/compile/Lang.java`

```java
// Add template declaration only if type parameters exist (non-empty list)
final Rule templateParams = Values("typeParameters", Prefix("typename ", Identifier()));
final Rule templateDecl = NonEmptyList("typeParameters",
        Prefix("template<", Suffix(templateParams, ">" + System.lineSeparator())));
final Rule maybeTemplate = Or(templateDecl, Empty);
```

Key changes:

1. Wrap the template declaration in `NonEmptyList("typeParameters", ...)` to fail when typeParameters is empty
2. Change fallback from `new StringRule("")` to `Empty` to properly succeed with empty output

Also fixed a related issue where function parameters couldn't include function pointers:

- Updated `Function()` to accept `Or(CFunctionPointerDefinition(), CDefinition())` in params

## Verification

Run tests to verify:

```bash
mvn test -Dtest=DebugFunctionPointerTest
```

Expected output for a structure with no type parameters:

```cpp
struct TestClass{};
```

Expected output for a function with type parameter `R`:

```cpp
template<typename R>
void test_TestClass(R (*mapper)(char*)) {/*...*/}
```

## Files Modified

- `src/main/java/magma/compile/rule/NonEmptyListRule.java` (new file)
- `src/main/java/magma/compile/Lang.java` (Function(), CStructure() methods)

## Testing

The fix resolves failures in:

- `DebugFunctionPointerTest.testFunctionTypeTransformation` — validates function pointer generation with and without
  type parameters
- `CppGenerationTest` tests — verify complete C++ generation pipeline
- `RealSomeJavaTest` — tests with real `Option<T>` methods

All tests now correctly generate:

- No template declaration for functions/structures without type parameters
- Proper `template<typename T>` declarations for generic functions/structures
- Correct `template<typename R, typename T>` for multi-parameter generics
