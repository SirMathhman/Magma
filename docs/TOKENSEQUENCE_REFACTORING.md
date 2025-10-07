# TokenSequence Refactoring Plan

## Overview

This document outlines the refactoring plan for `TokenSequence` to transition from String-based operations to a proper Token-based architecture. The goal is to make `TokenSequence` functionally equivalent to `List<Token>`, as noted in the interface comments.

## Current State

`TokenSequence` currently exposes many String-based methods inherited from legacy string manipulation patterns:

- `startsWith(String)`, `endsWith(String)` - string prefix/suffix checks
- `indexOf(String)`, `lastIndexOf(String)` - string search operations
- `equalsSlice(String)` - string equality comparison
- `charAt(int)` - character access (nonsensical for tokens)
- `split(String regex)` - regex-based splitting (will be replaced with proper division)
- `value()` - deprecated String accessor

## Refactoring Goals

1. **Introduce Token-based operations**: Replace String parameters with Token types
2. **Add list-like operations**: Provide `getFirst() -> Option<Token>` and similar
3. **Remove String-centric methods**: Phase out methods like `charAt()` and `value()`
4. **Preserve backwards compatibility**: Use deprecation to mark old APIs

## Phase 1: Token Interface Enhancement

### Action Items

1. Define concrete `Token` types in `magma.compile.rule.Token`:

   - Consider sealed hierarchy: `StringToken`, `IdentifierToken`, `OperatorToken`, etc.
   - OR: Keep Token as a marker interface and add methods to access token content

2. Add display/value methods to Token:
   ```java
   public interface Token {
       String display();  // How to render the token
       boolean matches(String value);  // Check if token matches a string
   }
   ```

## Phase 2: New TokenSequence Methods

### Add Token-based access methods

```java
// New methods to add to TokenSequence interface
Option<Token> getFirst();           // Get first token
Option<Token> getLast();            // Get last token
Option<Token> getAt(int index);     // Get token at index
Stream<Token> tokens();             // Stream all tokens
```

### Refactor existing String-based methods

The notes indicate these should eventually use Token:

```java
// Current (to be deprecated):
boolean startsWith(String slice);
boolean endsWith(String slice);
Option<Integer> indexOf(String infix);
Option<Integer> lastIndexOf(String infix);

// Future Token-based alternatives:
boolean startsWith(Token token);
boolean endsWith(Token token);
Option<Integer> indexOf(Token token);
Option<Integer> lastIndexOf(Token token);
```

**Note**: We'll need to decide whether to:

- Add new Token-based overloads alongside String versions
- Deprecate String versions and migrate call sites
- Keep both during transition period

## Phase 3: Implementation Strategy

### StringTokenSequence

Currently wraps a single `String value`. Two possible paths:

1. **Immediate full refactoring**: Parse `value` into `List<Token>` on construction

   - Requires tokenization logic
   - Breaking change to internal representation

2. **Gradual refactoring**: Keep String internally, implement Token methods via lazy parsing
   - Less disruptive initially
   - Still requires tokenization strategy

**Recommendation**: Start with option 2 (gradual) to minimize breakage.

### EmptyTokenSequence

Already represents an empty sequence. New Token methods return empty results (`None`, empty lists, etc.).

## Phase 4: Deprecation and Migration

### Methods to Deprecate

1. `String value()` - Already marked `@Deprecated` in notes
2. `Option<Character> charAt(int index)` - "nonsensical in a list of tokens" per notes
3. `List<TokenSequence> split(String regex)` - will become proper division operation

### Methods to Keep (for now)

- `String display()` - needed for serialization/output
- `equalsSlice(String)` - transitional; will evolve to `getFirst().matches(String)`

### Call Site Migration

Major usages to update:

- `PrefixRule.lex()` - uses `startsWith(prefix)` and `substring()`
- `SuffixRule.lex()` - uses `endsWith(suffix)` and `substring()`
- `FirstLocator` - uses `indexOf(infix)`
- `LastLocator` - uses `lastIndexOf(infix)`
- `NumberFilter` - uses `startsWith("-")`
- Various `Lang` types - use `endsWith()` for checks

## Phase 5: Testing Strategy

1. **Preserve existing tests**: All current tests must pass after refactoring
2. **Add Token-specific tests**: Test new Token methods in isolation
3. **Integration testing**: Verify compilation/deserialization still works end-to-end

Commands to verify:

```bash
mvn test                                    # All tests
mvn -Dtest=DeserializationDebugTest test   # Specific test class
```

## Migration Checklist

- [ ] Phase 1: Define Token types and enhance Token interface
- [ ] Phase 2: Add getFirst(), getLast(), getAt() to TokenSequence
- [ ] Phase 2: Implement new methods in StringTokenSequence
- [ ] Phase 2: Implement new methods in EmptyTokenSequence
- [ ] Phase 3: Add Token-based overloads for startsWith/endsWith/indexOf/lastIndexOf
- [ ] Phase 4: Mark String-based methods as @Deprecated
- [ ] Phase 5: Update major call sites (PrefixRule, SuffixRule, etc.)
- [ ] Phase 5: Run full test suite and verify
- [ ] Phase 6: Document changes in README/CHANGELOG

## Open Questions

1. **Tokenization strategy**: How should StringTokenSequence parse its String into Tokens?

   - Whitespace delimited?
   - Use existing lexer infrastructure from Lang?
   - Configurable/pluggable tokenizer?

2. **Token equality**: Should Token support structural equality? Value equality?

3. **Performance**: Will Token-based operations maintain performance of String-based ones?

4. **Breaking changes**: Can we afford breaking changes, or must we maintain full backwards compatibility?

## References

- `src/main/java/magma/compile/rule/TokenSequence.java` - Interface with refactoring notes
- `src/main/java/magma/compile/rule/StringTokenSequence.java` - Main implementation
- `src/main/java/magma/compile/rule/Token.java` - Currently empty marker interface
- `PrefixRule`, `SuffixRule`, `FirstLocator`, `LastLocator` - Major consumers of TokenSequence API

## Timeline

**Immediate (this session)**:

- Create this plan document ✓
- Define Token interface structure
- Add getFirst() method (simplest new addition)

**Short-term (next few sessions)**:

- Add remaining Token access methods
- Implement in concrete classes
- Begin deprecating String-based methods

**Long-term (future work)**:

- Full migration of call sites
- Remove deprecated methods
- Finalize Token hierarchy
