# Architecture Guide

This document provides a comprehensive overview of Magma's architecture, explaining how the different components work together to transpile Java to C++.

## High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                         Main.java                            │
│                    (Orchestration Layer)                     │
└──────────────┬──────────────────────────────────────────────┘
               │
               ├──> Read Java source files
               │
               v
┌─────────────────────────────────────────────────────────────┐
│                      Compiler.compile()                      │
│                   (Compilation Pipeline)                     │
└──────────────┬──────────────────────────────────────────────┘
               │
               │ Step 1: Lexing & Parsing
               v
┌─────────────────────────────────────────────────────────────┐
│              JRoot().lex(source) → Node                      │
│      (Parser rules defined in Lang.java + rule/)            │
└──────────────┬──────────────────────────────────────────────┘
               │
               │ Step 2: Deserialization
               v
┌─────────────────────────────────────────────────────────────┐
│      JavaSerializer.deserialize(JRoot.class, node)          │
│           (Converts Node → Java AST records)                │
└──────────────┬──────────────────────────────────────────────┘
               │
               │ Step 3: Transformation
               v
┌─────────────────────────────────────────────────────────────┐
│              Transformer.transform(JRoot)                    │
│              (Converts Java AST → C++ AST)                  │
└──────────────┬──────────────────────────────────────────────┘
               │
               │ Step 4: C++ Generation
               v
┌─────────────────────────────────────────────────────────────┐
│           CRules.generate(CRoot) → String                    │
│              (Generates C++ source code)                     │
└──────────────┬──────────────────────────────────────────────┘
               │
               └──> Write .cpp files
```

## Core Components

### 1. Parsing Infrastructure (`magma.compile`)

#### Node (`Node.java`)

The intermediate representation for parsed data. A `Node` is a tree structure with three types of children:

```java
public class Node {
    Map<String, String> strings;          // Simple key-value pairs
    Map<String, Node> nodes;              // Single nested objects
    Map<String, List<Node>> nodeLists;    // Arrays of nested objects
    String type;                          // Tag/type identifier
}
```

**Example:**

```java
// Java: public record Person(String name) {}
Node personNode = new Node()
    .retype("record")
    .withString("name", "Person")
    .withNodeList("params", List.of(
        new Node().retype("definition")
            .withString("name", "name")
            .withString("type", "String")
    ));
```

#### Rule System (`magma.compile.rule`)

Parsing is defined using composable rules:

```java
// Grammar rule for a method
private static Rule Method() {
    return Tag("method",
        Suffix(
            First(
                Node("definition", JDefinition()),  // Method signature
                "{",
                Statements("body", JMethodSegment())  // Method body
            ),
            "}"
        )
    );
}
```

**Rule Types:**

| Rule                   | Purpose                     | Example                  |
| ---------------------- | --------------------------- | ------------------------ |
| `Tag("name", rule)`    | Labels the result node      | `Tag("method", ...)`     |
| `String("text")`       | Matches literal text        | `String("public")`       |
| `First(r1, r2, ...)`   | Matches rules in sequence   | `First(type, name)`      |
| `Suffix(rule, "text")` | Matches rule then literal   | `Suffix(body, "}")`      |
| `Node("field", rule)`  | Stores result in node field | `Node("body", ...)`      |
| `Nodes("field", rule)` | Stores list in node field   | `Nodes("children", ...)` |
| `Or(r1, r2, ...)`      | Tries alternatives          | `Or(ifStmt, whileStmt)`  |

#### Lang.java

Defines both the AST structure (as records and sealed interfaces) and the parsing rules.

**Sealed Interface Hierarchy:**

```java
sealed interface JavaRootSegment
    permits Invalid, Import, JStructure, Package, Whitespace, BlockComment

sealed interface JStructure extends JavaRootSegment, JStructureSegment
    permits Interface, JClass, RecordNode

sealed interface JStructureSegment
    permits BlockComment, Field, Invalid, JDefinition, ..., Method, Whitespace

sealed interface JMethodSegment
    permits BlockComment, Break, Catch, Invalid, JAssignment, ...
```

This creates a type-safe tree where the compiler knows all possible node types at each level.

### 2. Serialization System (`JavaSerializer.java`)

Converts between `Node` (untyped tree) and Java records (typed ADT).

#### Deserialization

```java
Result<T, CompileError> deserialize(Class<T> clazz, Node node)
```

**Process:**

1. **Type Resolution:**

   - For records: Use the class directly
   - For sealed interfaces: Find the subclass with matching `@Tag` annotation

2. **Field Mapping:**

   - Get record components via reflection
   - For each component, read corresponding Node field
   - Type conversion: `String` → `String`, `Node` → nested object, `List<Node>` → list

3. **Validation:**

   - Track all consumed fields
   - Verify no leftover fields remain (field consumption validation)
   - Verify type matches (string vs node vs list)
   - Verify required fields are present

4. **Construction:**
   - Invoke record constructor with all field values
   - Return `Ok(instance)` or `Err(error)`

**Example:**

```java
@Tag("method")
public record Method(
    JDefinition definition,
    Option<NonEmptyList<JDefinition>> params,
    Option<String> body
) implements JStructureSegment {}

// Deserialization automatically:
// - Maps "definition" field to JDefinition
// - Maps "params" field to Option<NonEmptyList<JDefinition>>
// - Maps "body" field to Option<String>
// - Validates all node fields were consumed
```

#### Type Handling

| Java Type         | Node Representation           | Validation                          |
| ----------------- | ----------------------------- | ----------------------------------- |
| `String`          | `node.strings.get("field")`   | Must be string, not node/list       |
| `RecordType`      | `node.nodes.get("field")`     | Recursive deserialization           |
| `List<T>`         | `node.nodeLists.get("field")` | Deserialize each element            |
| `NonEmptyList<T>` | `node.nodeLists.get("field")` | Must have ≥1 elements               |
| `Option<T>`       | Any field                     | Missing → None, Present → Some      |
| Sealed interface  | `node.type` matches `@Tag`    | Tag must match a permitted subclass |

#### Serialization

```java
Result<Node, CompileError> serialize(Class<T> clazz, T value)
```

Inverse process: Record → Node. Uses reflection to read record components and populate Node fields.

### 3. Result & Option Types

Functional error handling replaces exceptions with explicit types.

#### Result<T, E>

```java
sealed interface Result<T, E> permits Ok<T, E>, Err<T, E>

record Ok<T, E>(T value) implements Result<T, E>
record Err<T, E>(E error) implements Result<T, E>
```

**Usage Pattern:**

```java
Result<String, CompileError> result = compile(source);

if (result instanceof Ok<String, CompileError>(String code)) {
    writeFile(code);
} else if (result instanceof Err<String, CompileError>(CompileError err)) {
    System.err.println("Compilation failed: " + err);
}
```

**Methods:**

```java
result.map(value -> transform(value))        // Transform Ok value
result.mapError(err -> wrapError(err))       // Transform Err value
result.flatMap(value -> nextOperation(value)) // Chain operations
```

#### Option<T>

```java
sealed interface Option<T> permits Some<T>, None<T>

record Some<T>(T value) implements Option<T>
record None<T>() implements Option<T>
```

**Usage:**

```java
Option<String> name = person.name();

if (name instanceof Some<String>(String value)) {
    System.out.println("Name: " + value);
} else {
    System.out.println("Name not provided");
}
```

### 4. AST Structure

#### Java AST

```
JRoot
├── children: Option<NonEmptyList<JavaRootSegment>>
    ├── Package(location)
    ├── Import(location)
    └── JStructure (Interface | JClass | RecordNode)
        ├── name: String
        ├── typeParameters: Option<List<Identifier>>
        └── children: List<JStructureSegment>
            ├── Field(definition, value)
            ├── Method(definition, params, body)
            └── JStructure (nested class)
```

#### C++ AST

```
CRoot
├── children: Option<NonEmptyList<CRootSegment>>
    ├── Structure(name, fields, typeParameters)
    │   └── fields: Option<NonEmptyList<CDefinition>>
    └── Function(name, returnType, params, body)
        ├── params: Option<NonEmptyList<CParameter>>
        └── body: NonEmptyList<CFunctionSegment>
```

### 5. Transformation (`Transformer.java`)

Converts Java AST to C++ AST.

**Key Mappings:**

| Java                  | C++                   |
| --------------------- | --------------------- |
| `JClass`              | `Structure`           |
| `Method`              | `Function`            |
| `String` type         | `char*` type          |
| `JInvocation`         | `CInvocation`         |
| Field access `.field` | Field access `.field` |

**Process:**

1. Transform `JRoot.children` → `CRoot.children`
2. For each `JStructure`:
   - Create `Structure` with name and type parameters
   - Transform methods to functions
   - Transform fields to C definitions
3. For each `Method`:
   - Create `Function` with mangled name (`methodName_ClassName`)
   - Transform body statements
   - Transform parameters

### 6. Code Generation (`CRules.java`)

Converts C++ AST back to text.

```java
Result<String, CompileError> generate(CRoot root)
```

Traverses the AST and emits C++ syntax:

```cpp
// Structure
struct ClassName {
    Type fieldName;
};

// Function
ReturnType functionName_ClassName(ParamType param) {
    // body
}
```

## Data Flow Example

Let's trace a simple Java method through the entire pipeline:

### Input (Java source)

```java
public class Greeter {
    public void greet(String name) {
        System.out.println("Hello " + name);
    }
}
```

### Step 1: Lexing → Node

```java
Node {
    type: "root",
    nodeLists: {
        "children": [
            Node {
                type: "class",
                strings: { "name": "Greeter" },
                nodeLists: {
                    "children": [
                        Node {
                            type: "method",
                            nodes: {
                                "definition": Node {
                                    type: "definition",
                                    strings: { "name": "greet", "type": "void" }
                                }
                            },
                            nodeLists: {
                                "params": [ /* ... */ ],
                                "body": [ /* ... */ ]
                            }
                        }
                    ]
                }
            }
        ]
    }
}
```

### Step 2: Deserialization → JRoot

```java
JRoot(
    children: Some(NonEmptyList(
        JClass(
            modifiers: Some("public"),
            name: "Greeter",
            children: List(
                Method(
                    definition: JDefinition(name: "greet", type: Identifier("void")),
                    params: Some(NonEmptyList(JDefinition(name: "name", type: Identifier("String")))),
                    body: Some("System.out.println(\"Hello \" + name)")
                )
            ),
            typeParameters: None,
            interfaces: None
        )
    ))
)
```

### Step 3: Transformation → CRoot

```java
CRoot(
    children: Some(NonEmptyList(
        Structure(
            name: "Greeter",
            fields: None,
            typeParameters: None
        ),
        Function(
            name: "greet_Greeter",
            returnType: Identifier("void"),
            params: Some(NonEmptyList(CDefinition(name: "name", type: Identifier("char*")))),
            body: NonEmptyList(Placeholder("System.out.println(\"Hello \" + name)"))
        )
    ))
)
```

### Step 4: Code Generation → C++ source

```cpp
struct Greeter {};

void greet_Greeter(char* name) {
    /*System.out.println("Hello " + name)*/
}
```

## Validation Layers

Magma has multiple validation layers ensuring correctness:

### 1. Compile-Time (Java type system)

- Sealed interfaces prevent invalid AST nodes
- Pattern matching ensures exhaustive case handling
- `NonEmptyList<T>` prevents empty collections at type level

### 2. Parse-Time (Lexer/Parser)

- Rules validate syntax
- `Invalid` nodes capture unparseable sections

### 3. Deserialization-Time (JavaSerializer)

- **Field consumption validation**: No leftover fields
- **Type mismatch validation**: String vs node vs list
- **Unknown tag validation**: `@Tag` must match sealed interface subclass
- **Required field validation**: Non-optional fields must be present
- **NonEmptyList validation**: Lists must have ≥1 elements

### 4. Transformation-Time (Transformer)

- Type conversions validated
- AST structure validated

## Error Handling Philosophy

Magma uses **explicit error handling** via `Result<T, E>`:

1. **No exceptions for expected failures** — Use `Result` instead
2. **Contextual errors** — Every `CompileError` includes context (node, file, location)
3. **Early failure** — Fail fast and propagate errors up the call stack
4. **Informative messages** — Errors explain what went wrong and where

**Example Error:**

```
Incomplete deserialization for 'Method': leftover fields [extraField]
This indicates a mismatch between the Node structure and the target ADT.
Context: Node(type: method, fields: [definition, params, body, extraField])
```

## Extensibility Points

### Adding New AST Nodes

1. Define record in `Lang.java`
2. Add to appropriate sealed interface `permits` clause
3. Add `@Tag("yourTag")` annotation
4. Define parsing rule
5. Add transformation logic
6. Add code generation

### Adding New Validation

1. Add validation method in `JavaSerializer.java`
2. Call during deserialization
3. Return `Option<CompileError>` on failure
4. Add tests

### Adding New Transformations

1. Pattern match on Java AST node type
2. Create corresponding C++ AST node
3. Handle nested transformations
4. Add tests

## Performance Considerations

- **Lazy evaluation**: Rules use lazy evaluation to avoid unnecessary parsing
- **Minimal copying**: Records are immutable but shared
- **Stream processing**: Files processed as streams where possible
- **Caching**: (Future) Parsed ASTs could be cached

## Testing Strategy

1. **Unit tests**: Individual components (rules, deserialization, transformations)
2. **Integration tests**: Full pipeline (source → C++)
3. **Validation tests**: Error cases and edge cases
4. **Roundtrip tests**: Serialize → deserialize → equals original

See [../README.md](../README.md) and [docs/INDEX.md](INDEX.md) for test documentation.
