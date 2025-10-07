# Documentation Index

This directory contains detailed documentation for specific features, fixes, and refactorings in the Magma project. Each document provides context, implementation details, and verification steps for a particular change or feature.

## Comprehensive Guides

**Start here if you're new to the project:**

- **[ARCHITECTURE.md](ARCHITECTURE.md)** — Complete architecture overview: components, data flow, AST structure, validation layers, and extensibility points
- **[DEVELOPER_GUIDE.md](DEVELOPER_GUIDE.md)** — Practical guide for common development tasks: adding nodes, debugging, testing patterns, and troubleshooting
- **[INDEX.md](INDEX.md)** (this file) — Categorized index of all feature documentation

These guides complement the [main README](../README.md), which provides project overview and quick start instructions.

## Core Features

### Validation & Type Safety

- **[TYPE_MISMATCH_VALIDATION.md](TYPE_MISMATCH_VALIDATION.md)** — Detects when `Option<String>` fields encounter nodes or lists instead of strings, preventing silent data loss
- **[UNKNOWN_TAG_VALIDATION.md](UNKNOWN_TAG_VALIDATION.md)** — Validates that `@Tag` values in deserialized nodes match permitted sealed interface subclasses
- **[../FIELD_VALIDATION_FEATURE.md](../FIELD_VALIDATION_FEATURE.md)** — Ensures 1:1 correspondence between Node fields and ADT fields during deserialization (leftover field detection)

### Type System & ADTs

- **[NONEMPTYLIST_REFACTORING.md](NONEMPTYLIST_REFACTORING.md)** — Introduction of `NonEmptyList<T>` to eliminate the "present but empty" invalid state
- **[RESULT_TYPE_ERROR_HANDLING.md](RESULT_TYPE_ERROR_HANDLING.md)** — Patterns and best practices for using `Result<T, E>` throughout the codebase
- **[OPTION_NONEMPTYLIST_DESERIALIZATION_FIX.md](OPTION_NONEMPTYLIST_DESERIALIZATION_FIX.md)** — Fixes for deserializing `Option<NonEmptyList<T>>` fields correctly

### Architecture & AST

- **[PLACEHOLDER_STATEMENT_FIX.md](PLACEHOLDER_STATEMENT_FIX.md)** — Introduces `JMethodSegment` and `CFunctionSegment` sealed interfaces for function body architecture
- **[METHOD_BODY_TYPE_FIX.md](METHOD_BODY_TYPE_FIX.md)** — Fixes method body type mismatches between parser and data model
- **[TRY_DIRECT_PERMITTED_SUBCLASSES_REFACTORING.md](TRY_DIRECT_PERMITTED_SUBCLASSES_REFACTORING.md)** — Refactoring of sealed interface deserialization logic

## Code Quality & Tooling

### Checkstyle & Refactoring

- **[CHECKSTYLE_COMPLIANCE_FIX.md](CHECKSTYLE_COMPLIANCE_FIX.md)** — General checkstyle compliance fixes and improvements
- **[ONE_LOOP_PER_METHOD_CHECKSTYLE.md](ONE_LOOP_PER_METHOD_CHECKSTYLE.md)** — Enforcement of "one loop per method" rule by extracting nested loops into helper methods
- **[SPLITTER_REFACTORING.md](SPLITTER_REFACTORING.md)** — Refactoring of string splitting utilities

### Serialization & Transformation

- **[JAVA_SERIALIZER_RENAME.md](JAVA_SERIALIZER_RENAME.md)** — Renaming of `Serialize` class to `JavaSerializer` for clarity
- **[CPP_GENERATION_FIX.md](CPP_GENERATION_FIX.md)** — Fixes for C++ code generation from Java AST
- **[EMPTY_TEMPLATE_FIX.md](EMPTY_TEMPLATE_FIX.md)** — Handling of empty template/generic type parameters

## Performance & Debugging

- **[JROOT_PERFORMANCE_ISSUE.md](JROOT_PERFORMANCE_ISSUE.md)** — Investigation and fix for `JRoot` deserialization performance issues
- **[NODE_OUTPUT_TRUNCATION.md](NODE_OUTPUT_TRUNCATION.md)** — Truncation of large `Node.toString()` output for debugging

## Testing & Compilation

- **[TEST_COMPILATION_FIX.md](TEST_COMPILATION_FIX.md)** — Fixes for test compilation errors (missing Stream methods, etc.)
- **[FLATMAP_IMPLEMENTATION.md](FLATMAP_IMPLEMENTATION.md)** — Implementation of `flatMap` for `HeadedStream` and other collections
- **[HEADEDSTREAM_FLATMAP_TEST.md](HEADEDSTREAM_FLATMAP_TEST.md)** — Tests for `HeadedStream.flatMap` functionality

## Document Categories

### By Topic

| Topic              | Documents                                                                                     |
| ------------------ | --------------------------------------------------------------------------------------------- |
| **Validation**     | TYPE_MISMATCH_VALIDATION, UNKNOWN_TAG_VALIDATION, FIELD_VALIDATION_FEATURE                    |
| **Type System**    | NONEMPTYLIST_REFACTORING, RESULT_TYPE_ERROR_HANDLING, OPTION_NONEMPTYLIST_DESERIALIZATION_FIX |
| **AST & Parsing**  | PLACEHOLDER_STATEMENT_FIX, METHOD_BODY_TYPE_FIX, EMPTY_TEMPLATE_FIX                           |
| **Serialization**  | JAVA_SERIALIZER_RENAME, TRY_DIRECT_PERMITTED_SUBCLASSES_REFACTORING                           |
| **Code Quality**   | CHECKSTYLE_COMPLIANCE_FIX, ONE_LOOP_PER_METHOD_CHECKSTYLE, SPLITTER_REFACTORING               |
| **Performance**    | JROOT_PERFORMANCE_ISSUE, NODE_OUTPUT_TRUNCATION                                               |
| **Testing**        | TEST_COMPILATION_FIX, HEADEDSTREAM_FLATMAP_TEST                                               |
| **Transformation** | CPP_GENERATION_FIX, FLATMAP_IMPLEMENTATION                                                    |

### By Impact Level

**High Impact (Breaking Changes):**

- NONEMPTYLIST_REFACTORING — Changes ADT field types, requires migration
- FIELD_VALIDATION_FEATURE — New validation may break code relying on ignored fields
- TYPE_MISMATCH_VALIDATION — New errors for previously silent failures

**Medium Impact (API Changes):**

- JAVA_SERIALIZER_RENAME — Class name change
- OPTION_NONEMPTYLIST_DESERIALIZATION_FIX — Behavior change for edge cases
- METHOD_BODY_TYPE_FIX — Grammar/model alignment

**Low Impact (Internal):**

- ONE_LOOP_PER_METHOD_CHECKSTYLE — Internal refactoring
- SPLITTER_REFACTORING — Internal utilities
- NODE_OUTPUT_TRUNCATION — Debugging improvement

## Reading Guide

### For New Contributors

**Recommended reading order:**

1. [../README.md](../README.md) — Project overview and quick start
2. [ARCHITECTURE.md](ARCHITECTURE.md) — System architecture and components
3. [DEVELOPER_GUIDE.md](DEVELOPER_GUIDE.md) — Common development tasks
4. [RESULT_TYPE_ERROR_HANDLING.md](RESULT_TYPE_ERROR_HANDLING.md) — Understanding Result<T, E> patterns
5. [../FIELD_VALIDATION_FEATURE.md](../FIELD_VALIDATION_FEATURE.md) — Core validation behavior
6. [PLACEHOLDER_STATEMENT_FIX.md](PLACEHOLDER_STATEMENT_FIX.md) — AST architecture example

### For Understanding Validation

1. [../FIELD_VALIDATION_FEATURE.md](../FIELD_VALIDATION_FEATURE.md) — Field consumption validation
2. [TYPE_MISMATCH_VALIDATION.md](TYPE_MISMATCH_VALIDATION.md) — Type validation
3. [UNKNOWN_TAG_VALIDATION.md](UNKNOWN_TAG_VALIDATION.md) — Tag validation
4. [NONEMPTYLIST_REFACTORING.md](NONEMPTYLIST_REFACTORING.md) — Semantic type constraints

### For Working with AST

1. [ARCHITECTURE.md](ARCHITECTURE.md) — AST structure and transformation pipeline
2. [DEVELOPER_GUIDE.md](DEVELOPER_GUIDE.md) — Adding new AST nodes (step-by-step)
3. [PLACEHOLDER_STATEMENT_FIX.md](PLACEHOLDER_STATEMENT_FIX.md) — Function body architecture
4. [METHOD_BODY_TYPE_FIX.md](METHOD_BODY_TYPE_FIX.md) — Parser/model alignment
5. [EMPTY_TEMPLATE_FIX.md](EMPTY_TEMPLATE_FIX.md) — Generic type handling
6. [CPP_GENERATION_FIX.md](CPP_GENERATION_FIX.md) — C++ generation

### For Debugging Issues

1. [DEVELOPER_GUIDE.md](DEVELOPER_GUIDE.md) — Debugging techniques and troubleshooting
2. [NODE_OUTPUT_TRUNCATION.md](NODE_OUTPUT_TRUNCATION.md) — Better debug output
3. [JROOT_PERFORMANCE_ISSUE.md](JROOT_PERFORMANCE_ISSUE.md) — Performance investigation
4. [TYPE_MISMATCH_VALIDATION.md](TYPE_MISMATCH_VALIDATION.md) — Type error messages

## Document Format

Each document typically follows this structure:

1. **Summary** — Brief description of the change/feature
2. **Problem/Motivation** — What issue was being addressed
3. **Solution** — How it was solved (with code examples)
4. **Implementation Details** — Technical specifics
5. **Verification** — How to test/verify the change
6. **Files Modified** — List of affected files
7. **Testing** — Test results and commands

## Contributing Documentation

When adding a new feature or making a significant change:

1. Create a new markdown file in `docs/` with a descriptive name (use SCREAMING_SNAKE_CASE)
2. Follow the standard document format above
3. Add an entry to this index under the appropriate category
4. Update [../README.md](../README.md) if it's a major feature
5. Include:
   - What changed and why
   - How to verify (commands to run and expected results)
   - File references with backticks (e.g., `src/main/java/magma/compile/Lang.java`)
   - Code examples showing before/after behavior

Keep documentation concise (3-8 sentences per section) and actionable.
