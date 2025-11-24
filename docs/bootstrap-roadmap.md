# Magma Bootstrap Roadmap

## Project Goal

Magma is a **self-hosted compiler** for a new programming language (Magma, with Rust/Kotlin/TypeScript-inspired syntax). Java is used only as a bootstrap — the current compiler in `Main.java` transpiles itself to C++, and eventually the C++ version will replace the Java version. Then the language syntax will transition from Java-like to Magma-specific.

**Key Constraints:**

- Zero external dependencies (no Maven libs, minimal C++ stdlib)
- Java SDK features avoided (building custom primitives: `Option`, `Result`, `Iter`, `List`)
- Focus is on getting the compiler to compile itself, not full Java compatibility

## Current State

- **Java compiler works**: `Main.java` successfully transpiles to `src/main/windows/magma/Main.cpp`
- **C++ doesn't compile yet**: 100+ compilation errors due to incomplete transformations
- **Development approach**: "Compiler-driven development" — placeholders show what needs work, not concerned with runtime behavior yet

## Critical Missing Transformations (Priority Order)

### 1. Method References (~40% of placeholders)

**Pattern:** `iter().map(CType::generate)`  
**Current Output:** `F? { alloc(CType), F?Table { generate }}`  
**Problem:** Method references aren't converted to proper function pointer structs  
**Impact:** Hundreds of broken `.map()`, `.filter()`, etc. calls

### 2. Interface Method Dispatch

**Pattern:** `state.append(next)` where `state` is an interface type  
**Current Output:** `/*Cannot access member 'append' in 'Identifier[value=State]', not an object.*/`  
**Problem:** Interface vtable dispatch logic incomplete  
**Impact:** Can't call methods on interface instances

### 3. Switch Expressions / Pattern Matching

**Pattern:** `switch(value) { case Some(var x) -> ... }`  
**Current Output:** `return _switch;` (literal stub)  
**Problem:** Pattern matching transformation not implemented  
**Impact:** Control flow broken for ADT matching (critical for `Option`, `Result`, sealed types)

### 4. Lambda Type Inference

**Pattern:** Lambda parameters and return types inferred from context  
**Current Output:** `/*TODO: resolve lambda return type*/ lambda0(void* _ref, /*TODO: resolve type of lambda param*/ element)`  
**Problem:** Type resolution context for lambdas incomplete  
**Impact:** Many anonymous functions have unknown types

### 5. `@Actual` Native Implementations

**Missing Types:** `JavaList`, `JavaPath`, `Streams`, string operations  
**Problem:** These are JVM-only helpers that need C++ implementations  
**Impact:** Core data structures unavailable in generated code

## Type System Implementation Status

### Complete:

- Java primitives → C++ primitives (`int`, `boolean`, `void`, `char`)
- Java `record` → C++ `struct`
- Java `interface` → C++ `struct` with vtable (function pointers)
- Java `sealed interface` → C++ `struct` with tagged union

### Incomplete:

- Generics (partially working via `typeParameters`)
- Lambdas (syntax present, type inference broken)
- Method references (not transformed)
- Pattern matching (not implemented)
- Exception handling (not attempted)
- Inheritance via `extends` (explicitly skipped)

## Memory Model (Work in Progress)

- `String` → `char*` (plan to `malloc`)
- References use `_ref` pointer conventions
- Stack-based allocation with manual control
- Object lifetimes/ownership model: TBD

## Bootstrap Path Forward

1. **Fix method reference transformation** → Unblock functional programming patterns
2. **Fix interface method dispatch** → Enable polymorphism
3. **Implement switch expressions** → Enable pattern matching on ADTs
4. **Improve lambda type inference** → Clean up anonymous function signatures
5. **Provide minimal `@Actual` C++ implementations** → Enable core data structures
6. **Iterate until `Main.cpp` compiles** → Achieve self-hosting milestone
7. **Verify `Main.cpp` can transpile itself** → Bootstrap complete
8. **Introduce Magma syntax** → Transition away from Java

## Language Design Notes (Magma)

- **Not Java**: Java is temporary scaffolding
- **Inspiration**: Rust (safety, ownership?), Kotlin (conciseness), TypeScript (type system?)
- **Object Model**: Simpler than Java (no full OOP complexity)
- **Core Types**: Records, sealed types, pattern matching, functional primitives
- **Minimal Runtime**: Custom implementations, no dependency on standard libraries

## Development Workflow

1. Edit `Main.java` to fix transformations
2. Run Java compiler: `./mvnw exec:java` (regenerates `Main.cpp`)
3. Try to compile C++: `./build.ps1` (CMake) or direct `clang++/g++`
4. Observe errors, identify placeholder categories
5. Repeat

**Testing:** "If it self-transpiles and the C++ compiles, it works"
