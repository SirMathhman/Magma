# Feature Specification: Magma → C compiler

**Feature Branch**: `001-title-magma-to`  
**Created**: 2025-09-08  
**Status**: Draft  
**Input**: User description: "A compiler from my custom programming language, Magma, to C."

## Execution Flow (main)
```
1. Parse user description from Input
   → If empty: ERROR "No feature description provided"
2. Extract key concepts from description
   → Identify: actors, actions, data, constraints
3. For each unclear aspect:
   → Mark with [NEEDS CLARIFICATION: specific question]
4. Fill User Scenarios & Testing section
   → If no clear user flow: ERROR "Cannot determine user scenarios"
5. Generate Functional Requirements
   → Each requirement must be testable
   → Mark ambiguous requirements
6. Identify Key Entities (if data involved)
7. Run Review Checklist
   → If any [NEEDS CLARIFICATION]: WARN "Spec has uncertainties"
   → If implementation details found: ERROR "Remove tech details"
8. Return: SUCCESS (spec ready for planning)
```

---

## ⚡ Quick Guidelines
- ✅ Focus on WHAT users need and WHY
- ❌ Avoid HOW to implement (no tech stack, APIs, code structure)
- 👥 Written for business stakeholders, not developers

### Section Requirements
- **Mandatory sections**: Must be completed for every feature
- **Optional sections**: Include only when relevant to the feature
- When a section doesn't apply, remove it entirely (don't leave as "N/A")

### For AI Generation
When creating this spec from a user prompt:
1. **Mark all ambiguities**: Use [NEEDS CLARIFICATION: specific question] for any assumption you'd need to make
2. **Don't guess**: If the prompt doesn't specify something (e.g., "login system" without auth method), mark it
3. **Think like a tester**: Every vague requirement should fail the "testable and unambiguous" checklist item
4. **Common underspecified areas**:
   - User types and permissions
   - Data retention/deletion policies  
   - Performance targets and scale
   - Error handling behaviors
   - Integration requirements
   - Security/compliance needs

---

## User Scenarios & Testing *(mandatory)*

### Primary User Story
As a Magma language user, I want to compile my Magma source files into readable, idiomatic C code so that I can build and link the output with standard C toolchains and run the resulting programs on systems that support C.

### Acceptance Scenarios
1. **Given** a valid Magma source file, **When** the user runs the compiler, **Then** the compiler emits one or more C source files and a return code 0 indicating success.
2. **Given** an invalid Magma source file (syntax or semantic errors), **When** the user runs the compiler, **Then** the compiler prints human-readable error messages with file/line references and returns a non-zero exit code; no C output files are produced.
3. **Given** Magma source that depends on external libraries or platform-specific features, **When** the compiler cannot map constructs to C, **Then** it reports clear [NEEDS CLARIFICATION: expected handling of foreign/library bindings] notes.

### Edge Cases
- Large source files: ensure memory usage and compile time are reasonable (performance targets unspecified). [NEEDS CLARIFICATION: target performance/scale]
- Ambiguous language features or underspecified semantics in Magma: compiler must surface clear diagnostics with a description of the ambiguity. [NEEDS CLARIFICATION: how strict should the compiler be when semantics are underspecified?]
- Platform-specific behavior (endianness, word size) must be defined for generated C or documented as assumptions. [NEEDS CLARIFICATION: supported target platforms/ABIs]

## Requirements *(mandatory)*

### Functional Requirements
- **FR-001**: The compiler MUST accept one or more Magma source files as input and produce equivalent C source file(s) and/or headers as output.
- **FR-002**: The compiler MUST report syntax and semantic errors with clear messages including file and line numbers.
- **FR-003**: The compiler MUST preserve Magma program semantics (as specified by the Magma language reference). [NEEDS CLARIFICATION: provide or link to Magma language reference if available]
- **FR-004**: The generated C MUST be buildable with a standard C compiler (e.g., gcc/clang) on supported targets, producing equivalent runtime behavior for supported programs.
- **FR-005**: The compiler MUST provide a command-line interface with standard flags: input files, output directory, verbosity/log level, and a --help option. [NEEDS CLARIFICATION: desired CLI flag names and additional flags (optimization levels, debug info)]
- **FR-006**: For undefined or unmappable language features, the compiler MUST emit informative diagnostics and either provide a compile-time error or a configurable fallback. [NEEDS CLARIFICATION: default policy for unmappable constructs]
- **FR-007**: The compiler SHOULD support generating one C file per Magma module, or an option to emit a single combined C translation unit.
- **FR-008**: The compiler SHOULD optionally emit debug information or mapping data (e.g., comments or a source map) to make debugging the generated C easier.

### Key Entities *(include if feature involves data)*
- **Magma AST**: Internal representation of parsed Magma code used for semantic analysis and code generation.
- **Symbol Table**: Records declarations, scopes, and types for semantic checks and name resolution.
- **Codegen Unit**: Represents a translation unit that will be emitted as a C source or header file.

### Key Entities *(include if feature involves data)*
- **[Entity 1]**: [What it represents, key attributes without implementation]
- **[Entity 2]**: [What it represents, relationships to other entities]

---


## Review & Acceptance Checklist
*GATE: Automated checks run during main() execution*

### Content Quality
- [ ] No implementation details that prescribe internal code structure (the spec may reference C as the target language because that's the user value)
- [ ] Focused on user value and business needs
- [ ] Written for technical and non-technical stakeholders who need to understand capability and acceptance
- [ ] All mandatory sections completed

### Requirement Completeness
- [ ] All [NEEDS CLARIFICATION] markers resolved (or explicitly accepted as outstanding risks)
- [ ] Requirements are testable: unit testable compiler behavior and end-to-end compilation tests
- [ ] Success criteria: example Magma programs compile and run producing expected outputs; error cases produce deterministic diagnostics
- [ ] Scope: translate Magma to portable C for supported subset; does not include implementing a Magma runtime unless required
- [ ] Dependencies/Assumptions: availability of Magma language reference; supported C toolchains (gcc/clang); target platforms/ABIs to be defined

---

## Execution Status
*Updated by main() during processing*

- [x] User description parsed
- [x] Key concepts extracted
- [x] Ambiguities marked
- [x] User scenarios defined
- [x] Requirements generated
- [x] Entities identified
- [ ] Review checklist passed

---
