# Architecture change: support Bool type annotations in let-declarations

Goal
----
Allow let-declarations to be annotated with the `Bool` type and accept boolean
initializers such as `let x : Bool = true; x` which should evaluate to `true`.
# Architecture: Struct support (concise)

This document describes the minimal struct support recently added to the interpreter. It focuses on the design intent, affected files, runtime data shapes, error contracts, tests added, and quality gates required for merging.

Goal
----
Provide a small, safe representation for user-defined record types (structs) that supports:
- declaring named struct types with an ordered list of fields,
- creating struct literals (Type { <exprs> }), and
- accessing fields via the dot operator (value.field).

Design summary
--------------
- Representation: struct runtime values are encoded as strings with a stable prefix (STR_PREFIX) and a compact payload describing the type name and ordered field=value pairs. This keeps changes localized to the interpreter's existing value model.
- Declaration semantics: `struct Name { a : T, b : T }` registers `Name` and its ordered field list in the interpreter environment (`Env.structEnv`).
- Literal semantics: `Name { e1, e2 }` evaluates subexpressions e1/e2, checks the field count, and constructs the runtime encoded value.
- Member access: `expr.field` evaluates `expr` and, if it's a struct runtime value, extracts the requested field value or returns an error if unknown.
- Validation: the interpreter rejects
  - duplicate struct declarations (same name),
  - duplicate field names within a struct, and
  - struct literals with a field count mismatch.

Files / modules touched
-----------------------
- `src/main/java/magma/Interpreter.java`
  - Core parsing and evaluation for struct declarations, struct literals, and member access.
  - Adds `Env.structEnv: Map<String, List<String>>` to record field order for each struct type.
  - Adds helpers to parse field lists, validate duplicates, and encode/decode struct runtime values.
- `src/test/java/magma/InterpreterFeatureTest.java`
  - Acceptance tests covering inline-declaration+literal+field-access, declaration+let+field-access, duplicate-struct-name, duplicate-field-name, and struct-literal field-count mismatches.

Runtime data shapes / contract
-----------------------------
- Env.structEnv: Map<String, List<String>> — maps a struct type name to the ordered list of its field names.
- Struct runtime value encoding: STR_PREFIX + "Type|f1=v1|f2=v2...". Consumers should only rely on the public helpers in `Interpreter` to encode/decode these values.

Public input/output contract
----------------------------
- Input: a source string containing semicolon-separated statements and a final expression.
- Output: on success, `Result.Ok<String, InterpretError>` where the returned string is the textual representation of the final expression value (e.g., numeric literal, encoded struct, or raw field on access).
- Error cases (examples):
  - `InterpretError("duplicate struct declaration", source)` when a struct name is re-declared,
  - `InterpretError("duplicate struct field", context)` when a struct defines the same field name twice,
  - `InterpretError("struct literal field count mismatch", context)` when a literal provides the wrong number of fields,
  - `InterpretError("unknown struct type", context)` for a literal referring to an unknown type,
  - `InterpretError("invalid struct field access", context)` when a `.field` access name is malformed.

Tests added (acceptance)
------------------------
- `src/test/java/magma/InterpreterFeatureTest.java` (high-level acceptance tests):
  - inline struct literal and member access: `struct Wrapper { field : I32 } Wrapper { 100 }.field` => "100"
  - declaration + let + field access: `struct Wrapper { field : I32 } let value = Wrapper { 100 }; value.field` => "100"
  - duplicate struct declaration rejected: `struct Duplicate { ... } struct Duplicate { ... }` => invalid
  - duplicate struct fields rejected: `struct Point { x : I32, x : I32 }` => invalid
  - struct literal missing fields rejected when used as `let` initializer: `struct Point { x : I32, y : I32 } let test = Point {};` => invalid

Quality gates
-------------
- Tests: new acceptance tests are added under `src/test/java/...` and executed as part of the project's test suite.
- Build: `mvn package` must succeed (compilation, tests, Checkstyle/PMD must pass).
- Style: follow existing Checkstyle conventions (method-name patterns, disallowed literal `null`, cyclomatic complexity thresholds). The implementation keeps helper methods small to satisfy these rules.

Migration / compatibility notes
------------------------------
- Struct support is additive. Existing programs without structs are unchanged.
- Runtime representation of structs is internal to the interpreter; external callers should treat the encoded string as an opaque value except when using member access in source code.

Implementation notes / rationale
-------------------------------
- The interpreter uses a compact representation to avoid a broad refactor of the value model. This choice keeps the change small and low-risk.
- Duplicate-name and duplicate-field checks are performed at declaration time to provide fast, early feedback to users.

PR checklist (for this change)
-----------------------------
- [ ] Architecture: `docs/architecture.md` updated (this file).
- [ ] Tests: acceptance tests added under `src/test/java/...`.
- [ ] Tests: tests executed locally and are green.
- [ ] Build: `mvn package` completed successfully.
- [ ] Docs: documentation updated (this file). If further user-facing docs are required, add a short example to `README.md`.

If you want, I can also add a short example to `README.md` showing the declaration, construction, and field access patterns. Otherwise this file documents the design and quality gates for the change.
- Input: `source` string and `input` string (unused for these features).
- Success: `Result.Ok` with the stringified result (literal or computed sum).
- Failure: `Result.Err` for parse errors, type/width mismatches, unknown identifiers, or overflow.

Acceptance tests added
-----------------------
- `src/test/java/magma/InterpreterLetBindingTest.java`: verifies that an unannotated `let` binding followed by a variable lookup evaluates to the RHS value. Example: `let x = 3; x` should produce `"3"`.
 - `src/test/java/magma/InterpreterFeatureTest.java::literalTrue` - verifies that boolean literal `true` is accepted and returns `"true"`.

Quality gates & status
----------------------
- Tests added and executed. All tests and Checkstyle/PMD checks pass in this workspace (`mvn -DskipTests=false test` -> BUILD SUCCESS).

Notes and next steps
--------------------
- Behavior is intentionally minimal and conservative. If you want more expressions or a richer type system, we should replace the ad-hoc parsing with a small expression parser and add focused tests.

Documentation: `docs/architecture.md` updated to reflect the final change.

## Architecture change: multi-parameter function declarations and calls

Goal
----
Support function declarations with multiple parameters and function calls that pass multiple arguments. Example:
`fn first(a : I32, b : I32) : I32 => { return a; } first(100, 200)` should evaluate to `"100"`.

Files / Modules affected
------------------------
- `src/main/java/magma/Interpreter.java` — extend function parsing to accept comma-separated parameter lists and update call evaluation to bind multiple params.
- `src/test/java/magma/InterpreterFeatureTest.java` — add `fnTwoParamCall` acceptance test.

Design notes
------------
- Parameter lists are stored in `Env.fnParamNames` and `Env.fnParamTypes` as lists keyed by function name.
- `handleFnDecl` parses comma-separated parameters and records their names/types.
- `tryEvalFunctionCall` splits call arguments on commas, evaluates each argument in the caller environment, checks arity, creates a shallow copy of the caller `Env`, binds parameters into the function-local env, and evaluates the function body expression.
- Limitations: splitting on commas is simplistic and does not support nested comma-containing expressions in arguments or parameter defaults.

Tests added
-----------
- `src/test/java/magma/InterpreterFeatureTest.java::fnTwoParamCall` — verifies two-argument function declaration and call returns expected value.

Quality gates
-------------
- All tests must pass (`mvn test`) and Checkstyle must have 0 violations.

Documentation changes
---------------------
- README updated with a short example (see commit) describing `fn` syntax and the current limitation (no nested-commas in args).

Note: the function return type annotation is optional. The interpreter accepts `fn get() => { return 100; } get()` (no `: I32`), which returns `"100"`.

## Architecture change: inline structs and field access

Goal
----
Add a minimal, inline struct declaration and literal feature so tests can express
simple struct construction and field access. Example: `struct Wrapper { field : I32 } Wrapper { 100 }.field` => `"100"`.

Files / Modules affected
------------------------
- `src/main/java/magma/Interpreter.java` — add lightweight parsing for inline
  `struct` declarations, struct literals, and member access (`.`) evaluation.
- `src/test/java/magma/InterpreterFeatureTest.java` — add `structWrapperFieldAccess` test.

Contract
--------
- Input: inline struct declaration immediately followed by a struct literal and
  field access expression.
- Output: the field value as a string (e.g., `"100"`).
- Error modes: unterminated declaration/literal, unknown field, invalid
  struct literal, or mismatched field counts.

Design notes
------------
- Structs are stored in `Env.structEnv` as an ordered list of field names.
- Struct values are encoded as an internal string with the `@STR:` prefix and
  the form `@STR:Type|field=val|...` so the rest of the interpreter can keep
  using string-encoded runtime values.

Tests to add
------------
- `src/test/java/magma/InterpreterFeatureTest.java::structWrapperFieldAccess`
  — asserts `struct Wrapper { field : I32 } Wrapper { 100 }.field` evaluates to
  `"100"`.

Quality gates
-------------
- Add failing test (red) — done
- Implement struct parsing/evaluation — done
- Run `mvn -DskipTests=false test` until green

This is intentionally minimal to satisfy acceptance tests. If richer struct
semantics are needed, we should model runtime values with proper typed objects
instead of an encoded string format.

### Compact fn-body expression form

Goal
----
Allow a compact function body syntax where the arrow `=>` is followed by a single expression without braces or the `return` keyword. Example: `fn get() => 100; get()` should evaluate to `"100"`.

Files / Modules affected
------------------------
- `src/main/java/magma/Interpreter.java` — extend `extractFnReturnExpr` to accept a direct expression after `=>` in addition to block bodies and the `=> return <expr>;` form.
- `src/test/java/magma/InterpreterFeatureTest.java` — add `fnCompactNoRet` acceptance test.

Inputs / Outputs / Errors (contract)
-----------------------------------
- Input: a function declaration using the compact arrow form with a single expression (no braces), e.g. `fn get() => 100;`.
- Output: the function should be callable and return the evaluated expression as usual.
- Errors: invalid syntax (missing expression after `=>`) should produce an InterpretError.

Tests to add
------------
- `src/test/java/magma/InterpreterFeatureTest.java::fnCompactNoRet` — asserts `fn get() => 100; get()` evaluates to `"100"`.

Quality gates
-------------
- Add failing test (red) — done.
- Implement change and run `mvn -DskipTests=false test` until green — done.
- Ensure `mvn -DskipTests=false package` succeeds and Checkstyle/PMD pass — done.

### Invalid case: duplicate function parameter names

Goal
----
Ensure function declarations reject duplicate parameter names. Example invalid program:

  fn first(a : I32, a : I32) : I32 => { return first; } first(100, 200)

Files / Modules affected
------------------------
- `src/test/java/magma/InterpreterFeatureTest.java` — added `fnDupParamInvalid` test which asserts the program is invalid.

Notes
-----
The interpreter currently rejects duplicate parameter names (the test verifies this). No code changes were necessary; documentation was updated to record the invalid case and test added.

### Invalid case: wrong arity on function call

Goal
----
Ensure function calls with the wrong number of arguments are rejected. Example invalid program:

  fn pass(param : I32) : I32 => { return param; } pass()

Files / Modules affected
------------------------
- `src/test/java/magma/InterpreterFeatureTest.java` — added `fnWrongArityInvalid` test which asserts the program is invalid.

Notes
-----
The interpreter enforces argument-count checks during function call evaluation; the test verifies that calling a 1-arg function with zero arguments is rejected. No implementation change was required.

### Invalid case: argument type mismatch in calls

Goal
----
Ensure that function call arguments are compatible with the parameter annotated types. Example invalid program:

  fn pass(param : I32) : I32 => { return param; } pass(true)

Files / Modules affected
------------------------
- `src/main/java/magma/Interpreter.java` — `tryEvalFunctionCall` now validates evaluated argument values against parameter annotated types using existing suffix checks.
- `src/test/java/magma/InterpreterFeatureTest.java` — added `fnArgTypeMismatch` test which asserts the program is invalid.

Notes
-----
The interpreter now performs annotated-type validation for call arguments before binding them into the callee environment. The validation reuses `checkAnnotatedSuffix` and returns an InterpretError if the argument value is incompatible with the annotated type.

## Architecture change: array literals and indexing

Goal
----
Support a minimal form of array literals and indexing so simple expressions like `[1][0]` evaluate to `1`.

Files / Modules affected
------------------------
- `src/main/java/magma/Interpreter.java` — add parsing and evaluation for array literals (bracketed comma-separated lists) and indexing expressions of the form `<expr>[<expr>]` where the index evaluates to an integer literal.
- `src/test/java/magma/InterpreterFeatureTest.java` — add a failing acceptance test asserting `[1][0]` => `1`.

Inputs / Outputs / Errors (contract)
-----------------------------------
- Input: source string containing an array literal and an indexing expression.
- Output: on success, return the textual value of the indexed element (e.g., `"1"`).
- Errors: indexing out of bounds, invalid index expression, or invalid array literal syntax should produce an InterpretError.

Design notes
------------
- Representation: array literals will be represented as a comma-separated string wrapped with an internal marker (e.g., `@ARR:<elem1>|<elem2>|...`) in the runtime `valEnv` so the rest of the interpreter that expects string values can remain unchanged for now.
- Indexing: when evaluating `a[idx]`, evaluate `a` and `idx`; if `a` yields the internal array marker, parse the elements, evaluate `idx` as an integer, and return the requested element's value as a string.
- Limitations: this is intentionally minimal and suitable for acceptance tests; it is not a full typed array implementation.

Tests to add
------------
- `src/test/java/magma/InterpreterFeatureTest.java::arrayLiteralIndex` — asserts `"[1][0]"` evaluates to `"1"` (tests-first failing test).

Additional acceptance: assignment through an array index must respect the variable's mutability

Goal
----
Ensure that indexed assignment (e.g., `x[0] = 1`) is only allowed when the variable holding the array is mutable (declared with `let mut`). Assigning through an index when the variable was declared with an immutable `let` must be rejected.

Files / Modules affected
------------------------
- `src/main/java/magma/Interpreter.java` — `prepAssignOps` / `handleIdxAssign` to validate that indexed assignments require the base variable to be mutable when the base is a variable name (not a deref or reference target).
- `src/test/java/magma/InterpreterFeatureTest.java` — add failing test `indexedAssignLetInvalid` asserting `let x = [0]; x[0] = 1; x[0]` is invalid.

Inputs / Outputs / Errors (contract)
-----------------------------------
- Input: program string containing `let x = [0]; x[0] = 1; x[0]`.
- Output: interpretation must fail with an interpret error indicating assignment to immutable variable or invalid array assignment.

Tests to add
------------
- `src/test/java/magma/InterpreterFeatureTest.java::indexedAssignLetInvalid` — asserts `let x = [0]; x[0] = 1; x[0]` is invalid.

Quality gates
-------------
- Add failing test (red)
- Implement check in `Interpreter` to enforce mutability for indexed assignment
- Run `mvn -DskipTests=false test` until green

Quality gates
-------------
- Add failing test.
- Implement minimal evaluator changes in `Interpreter.java`.
- Run focused test and full build; fix Checkstyle/PMD as needed.

Migration / Future
------------------
This is an incremental step. If arrays are needed elsewhere, consider replacing the ad-hoc marker with a typed runtime value model.

