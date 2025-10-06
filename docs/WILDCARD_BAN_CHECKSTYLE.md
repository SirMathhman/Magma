# Wildcard Type Parameter Ban via CheckStyle

## What Changed

Added a CheckStyle rule to ban the use of wildcard type parameters (`?`) in the codebase.

## Why

Wildcard type parameters (`Class<?>`, `Option<?>`, `List<?>`, etc.) can make code less explicit and harder to reason about. The project prefers explicit type parameters that clearly communicate the types being used, improving code clarity and type safety.

## How to Verify

Run CheckStyle to see violations:

```powershell
mvn checkstyle:check
```

Expected behavior: CheckStyle will report violations like:

```
[ERROR] C:\...\JavaSerializer.java:49: Wildcard type parameter ? is banned; use explicit type parameters instead.
```

## Configuration Details

**File**: `config/checkstyle/checkstyle.xml`

Added rule:

```xml
<!-- Ban wildcard type parameters -->
<module name="RegexpSinglelineJava">
    <property name="format" value="&lt;\s*\?" />
    <property name="message"
        value="Wildcard type parameter '?' is banned; use explicit type parameters instead." />
    <property name="ignoreComments" value="true" />
</module>
```

The regex pattern `<\s*?` matches:

- `<` followed by optional whitespace and then `?`
- This catches patterns like `Class<?>`, `Option<?>`, `List<?>`, etc.
- Comments containing this pattern are ignored

## Examples

❌ **Banned patterns:**

```java
Class<?> clazz = ...
Option<?> option = ...
List<?> list = ...
Result<?, CompileError> result = ...
```

✅ **Preferred alternatives:**

```java
Class<Object> clazz = ...
Option<String> option = ...
List<Node> list = ...
Result<Object, CompileError> result = ...
```

## Impact

This rule currently flags 154 violations across the codebase (as of October 5, 2025). These violations represent opportunities to improve type explicitness. The existing code continues to compile and run; the CheckStyle rule serves as a guide for future code quality improvements.

## Related Rules

This rule complements the existing bans on:

- `null` literals (use `Option` instead)
- `throw` statements (use `Result` or `Option` for error handling)
- Multiple loops per method (enforce simpler control flow)
