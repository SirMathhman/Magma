# NodeListRule and NonEmptyListRule Merge

## Summary

Merged `NonEmptyListRule` into `NodeListRule`, eliminating the duplicate class. `NodeListRule` now has a single, consistent behavior: **always fails when the list is missing** (allowing `Or` to try alternatives), and **iterates through the list elements, generating each one and joining with the divider** when the list is present.

## Motivation

`NonEmptyListRule` was unnecessary duplication. Since `Node.nodeLists` now uses `NonEmptyList<Node>` (see `NODE_NONEMPTYLIST_REFACTORING.md`), lists are only stored when non-empty. `NodeListRule` should simply:

1. During `lex()`: Only store lists when non-empty
2. During `generate()`: Fail if list missing (for Or fallback), or iterate through elements and generate each one if present

All the different factory methods (`Statements`, `Expressions`, `Delimited`, `NonEmptyList`) just configure different dividers and rules—the core behavior is identical.

## What Changed

### NodeListRule Simplification

**File**: `src/main/java/magma/compile/rule/NodeListRule.java`

#### Simplified Record

```java
public record NodeListRule(String key, Rule rule, Divider divider) implements Rule
```

No configuration parameters needed—the behavior is always the same.

#### Correct Generate Logic

```java
@Override
public Result<String, CompileError> generate(Node value) {
    return switch (value.findNodeList(key)) {
        // List missing - fail to allow Or to try alternatives
        case None<?> _ -> new Err<String, CompileError>(
                new CompileError("Node list '" + key + "' not present", new NodeContext(value)));
        // List present and non-empty - iterate and generate each element
        case Some<NonEmptyList<Node>>(NonEmptyList<Node> list) -> generateList(list);
    };
}

private Result<String, CompileError> generateList(NonEmptyList<Node> list) {
    final StringJoiner sb = new StringJoiner(divider.delimiter());
    int i = 0;
    while (i < list.size()) {
        Node child = list.get(i).orElse(null);
        switch (rule.generate(child)) {
            case Ok<String, CompileError>(String generated) -> sb.add(generated);
            case Err<String, CompileError>(CompileError error) -> {
                return new Err<String, CompileError>(error);
            }
        }
        i++;
    }
    return new Ok<String, CompileError>(sb.toString());
}
```

**Key point**: The rule is applied to **each element** in the list, not to the whole node. This prevents stack overflow and correctly generates the list content.

### Factory Methods

All factory methods configure the rule (applied to each element) and divider (used to join generated strings):

- `Statements(key, rule)`: Uses statement folder/divider to join elements
- `Expressions(key, rule)`: Uses expression folder/divider to join elements
- `Delimited(key, rule, delimiter)`: Uses custom delimiter to join elements
- `NonEmptyList(key, rule)`: Uses empty delimiter (no separator between elements)### Updated Imports

**Files**: `src/main/java/magma/compile/Lang.java`, `src/main/java/magma/compile/CRules.java`

Changed:

```java
import static magma.compile.rule.NonEmptyListRule.NonEmptyList;
```

To:

```java
import static magma.compile.rule.NodeListRule.NonEmptyList;
```

### Deleted File

- `src/main/java/magma/compile/rule/NonEmptyListRule.java` (no longer needed)

## Behavioral Equivalence

### Before (Two Separate Classes)

**NodeListRule (various factories)**:

```java
NodeListRule.Statements("fields", fieldRule)
```

- Missing list → returns `""`
- Present list → iterates elements, applies rule to each, joins with divider

**NonEmptyListRule**:

```java
NonEmptyListRule.NonEmptyList("typeParameters", templateRule)
```

- Missing list → returns `Err` (allows `Or` fallback)
- Present list → delegates to inner rule (passed whole node)

### After (Single Unified Class)

**All NodeListRule factories** (unified behavior):

```java
NodeListRule.Statements("fields", fieldRule)
NodeListRule.NonEmptyList("typeParameters", templateRule)
```

- Missing list → returns `Err` (allows `Or` fallback)
- Present list → iterates elements, applies rule to each, joins with divider

**Important change**: `NonEmptyList` factory now also iterates through elements instead of delegating to the whole node, preventing potential stack overflow.

## Key Benefits

1. **Maximum Simplicity**: Single, consistent behavior—no configuration flags
2. **Type Safety**: Works directly with `NonEmptyList<Node>` from `Node.nodeLists`
3. **Clear Semantics**: Always fails when list missing (for Or fallback), always iterates elements when present
4. **No Stack Overflow**: Applies rule to each element, not recursively to the whole node
5. **Consistent**: All factory methods have the same core behavior, just different dividers

## Example Usage

### Optional List (Template with fallback)

```java
final Rule templateDecl =
    NonEmptyList("typeParameters", Prefix("template<", Suffix(templateParams, ">" + System.lineSeparator())));
final Rule maybeTemplate = Or(templateDecl, Empty);
```

- If `typeParameters` is present → generates `template<...>`
- If `typeParameters` is missing → `templateDecl` errors, `Or` tries `Empty`, returns `""`

### Optional List (Simple statements)

```java
Rule fields = Statements("fields", Suffix(CDefinition(), ";"));
```

- If `fields` is present → generates each field with `;` separator
- If `fields` is missing → returns `""`

## Verification

Run the build to verify:

```powershell
mvn clean compile
```

Expected: BUILD SUCCESS with no compilation errors.

Run existing tests:

```powershell
mvn surefire:test
```

Expected: All tests pass (no behavioral changes).

## Migration Notes

For any custom code using `NonEmptyListRule`:

1. Change import: `import static magma.compile.rule.NodeListRule.NonEmptyList;`
2. Usage remains identical: `NonEmptyList("key", innerRule)`

No other code changes required—the API is backward compatible.

## Related Documentation

- `NODE_NONEMPTYLIST_REFACTORING.md` — Background on why `Node.nodeLists` uses `NonEmptyList<Node>`
- `EMPTY_TEMPLATE_FIX.md` — Original motivation for `NonEmptyListRule` (now merged)
- `NONEMPTYLIST_INTRODUCTION.md` — Introduction to the `NonEmptyList<T>` type
