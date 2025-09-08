# Magma — Java + Maven Initial Architecture Plan

Last updated: 2025-09-08

## Purpose

This document formalizes an initial Java + Maven architecture for the Magma compiler project. It restates the requested feature as a formal specification, describes a concrete multi-module Maven layout, component responsibilities, data flows, interfaces, dependencies, and acceptance criteria.

---

## 1) Formal project specification (scope, constraints, acceptance)

Summary
- Add an initial Java-based implementation and project layout for the Magma compiler using Maven as the build system. The goal is to create a clear, idiomatic Java project structure that mirrors the compiler pipeline (lexer, parser, AST, typechecker, IR, codegen, CLI), is easy to build and test locally and in CI, and integrates with the existing repo docs and tests.

Scope
- Create a multi-module Maven project scaffold in a new `java/` or `src-java/` directory (recommend `java/`).
- Implement module boundaries to host future Java implementations of compiler stages.
- Provide a parent `pom.xml` and module `pom.xml` files with sensible defaults (Java 17+), unit testing (JUnit 5), and reviewable dependency management.
- Provide a minimal `magmac` CLI module that can be executed (jar) and accepts a `.mg` input and emits a `.c` file (initially stubbed; later implemented).
- Provide integration with `docs/` and `tests/` by documenting the Maven build and providing sample commands.

Constraints
- Do not change or remove existing non-Java code in the repository.
- Keep initial Java implementation minimal and low-risk (no heavy external runtime assumptions).
- Prefer standard, widely-supported Maven plugins and Java LTS (Java 17 or 21 depending on CI). Default to Java 17 to maximize compatibility unless the project prefers later LTS.
- Avoid introducing large binary dependencies; keep initial dependencies small.

Acceptance criteria (measurable)
- A new `java/` directory exists at project root containing a multi-module Maven project with a parent `pom.xml` and at least these modules: `magma-core`, `magma-cli`, `magma-codegen`, and `magma-tests`.
- Running `mvn -f java/pom.xml -q -DskipTests=false clean verify` in the repo root (or `cd java && mvn verify`) returns a successful build (for the scaffold this means the build succeeds with stub code and unit-test placeholders passing).
- The `magma-cli` module produces an executable JAR (or a main-class jar) that, when run, prints a short help message and returns exit code 0.
- Minimal example integration documented in `docs/quickstart.md` (or this document) showing how to build and run the Java tool.
- CI config (optional, follow-up) can run `maven verify` and run unit tests.

Success metrics
- Build reproducibility: clean build succeeds on Linux and Windows in CI within 2 minutes.
- Developer onboarding: a new contributor can run the build and run `magma-cli --help` in under 5 minutes following the README.
- Incremental implementation: each compiler stage added later targets a single module and unit-tests cover it.

---

## 2) High-level design

Design summary
- Use a Maven multi-module layout to separate concerns and speed up incremental builds.
- Use lightweight interfaces between compiler stages expressed as Java interfaces and immutable AST/IR data classes.
- Keep the public API surface minimal and stable: the CLI calls into a Compiler API that coordinates stages.

Suggested modules (Maven artifact ids and responsibilities)

- Parent project: `com.sirmathhman:magma` (packaging: pom)
  - `magma-core` (artifactId: `magma-core`) — core compiler types: AST nodes, tokens, lexer, parser interfaces, typechecker interfaces, common utilities.
  - `magma-codegen` (artifactId: `magma-codegen`) — IR representations and C code emitter. Depends on `magma-core`.
  - `magma-cli` (artifactId: `magma-cli`) — command-line front-end. Depends on `magma-core` and `magma-codegen`. Contains `Main` with `--help` and `compile` command.
  - `magma-test-fixtures` (artifactId: `magma-test-fixtures`) — shared test utilities and sample `.mg` inputs. Test-scoped dependency.
  - `magma-integration` or `magma-tests` (artifactId: `magma-tests`) — integration tests that compile small `.mg` inputs, call CLI, and assert C emitted or exit codes. Depends on `magma-cli`.

Maven coordinates and Java version
- GroupId: `com.sirmathhman` (or `io.sirmathhman.magma` as preferred)
- Version: `0.1.0-SNAPSHOT` initially.
- Java source/target: 17 (configurable in parent POM via maven-compiler-plugin properties).

Build & plugin choices
- maven-compiler-plugin for Java compilation.
- maven-surefire-plugin / maven-failsafe for unit/integration tests.
- maven-assembly-plugin or maven-shade-plugin (optional) to create an executable jar for `magma-cli`.
- jacoco-maven-plugin (optional) for code coverage.

Dependencies
- JUnit 5 for tests.
- (Optional) ANTLR4 or similar if choosing generated parser approach. If repo already contains a custom lexer/parser in other languages, consider porting rather than adding heavy ANTLR at first.
- SLF4J + simple logger (or none) for logging abstractions.

---

## 3) Package / class responsibilities (suggested Java packages)

Top-level Java package: `com.sirmathhman.magma`.

- com.sirmathhman.magma.core
  - Token, TokenType
  - Lexer (interface) and a minimal `StubLexer` implementation
  - Parser (interface), `AstNode` base class or sealed hierarchy (if using Java sealed types later)
  - Types: `MType` (i32, i64, f32, f64, bool, PtrType)
  - TypeChecker (interface)

- com.sirmathhman.magma.ir
  - IR nodes (`IrNode`, `IrFunction`, `IrInstr`)
  - Lowering utilities: AST -> IR

- com.sirmathhman.magma.codegen
  - CodeEmitter (interface)
  - CEmitter (implements CodeEmitter)
  - CodeGenOptions

- com.sirmathhman.magma.cli
  - Main (public static void main)
  - CLI parser (simple args parsing class)
  - CompilerFacade or CompilerService that coordinates core -> ir -> codegen

- com.sirmathhman.magma.test
  - Fixtures for inputs and helper assertions to run CLI and compile the emitted C with system compiler if desired.

Interfaces / Contracts (tiny contract examples)
- CompilerService.compile(InputStream src, Path outC) -> Result (success/fail + diagnostics)
- Lexer.tokenize(CharSequence src) -> List<Token>
- Parser.parse(List<Token>) -> AstModule
- TypeChecker.check(AstModule) -> CheckedModule or Diagnostic list
- CodeEmitter.emit(CheckedModule, OutputStream) -> void

Error modes
- Recoverable diagnostics: the typechecker and parser collect diagnostics and return them; the CLI reports them and returns non-zero exit code.
- Fatal errors: I/O failures, internal exceptions — CLI returns non-zero and logs stack traces under debug flag.

---

## 4) Data flow and sequence (compile pipeline)

High level flow (steps)
1. CLI reads source `.mg` file.
2. Lexer tokenizes into tokens.
3. Parser builds AST.
4. Typechecker runs over AST and emits diagnostics or `CheckedModule`.
5. Lowering transforms AST into IR.
6. Code generator (`CEmitter`) emits `.c` file.
7. Optionally call system C compiler (gcc/clang) to produce an executable (out of scope initially, possible follow-up task).

ASCII sequence diagram

CLI -> Lexer: read source
Lexer -> Parser: tokens
Parser -> TypeChecker: AST
TypeChecker -> Lowering: Checked AST
Lowering -> CodeEmitter: IR
CodeEmitter -> FileSystem: write .c

---

## 5) Maven layout example (multi-module)

Project tree (recommended)

java/pom.xml                     # parent pom (packaging pom)
java/magma-core/pom.xml
java/magma-codegen/pom.xml
java/magma-cli/pom.xml
java/magma-tests/pom.xml

Parent pom responsibilities
- Declare common properties: java.version=17, encoding, plugin management (compiler, surefire), dependencyManagement for shared dependencies.
- Declare modules section.

Minimal parent pom snippet (illustrative)

- groupId: com.sirmathhman
- artifactId: magma
- version: 0.1.0-SNAPSHOT
- packaging: pom

Each child module should declare a dependency on the parent via <parent> and only minimal module-specific properties.

---

## 6) CI, build & local dev considerations

Local dev
- Developers should be able to run:
  - mvn -f java/pom.xml -DskipTests=false clean verify
  - cd java/magma-cli && mvn package
  - java -jar magma-cli/target/magma-cli-0.1.0-SNAPSHOT.jar --help

CI
- Add a small GitHub Actions job (follow-up) to run `mvn -f java/pom.xml -DskipTests=false -B clean verify` on Linux and Windows.
- Cache Maven repository and attach test artifacts.

OS toolchain
- The initial Java build should not require a system C compiler; tests that call gcc/clang should be optional and guarded behind an environment variable (e.g., `WITH_NATIVE=1`).

---

## 7) Tests and quality gates

Unit tests
- Each module uses JUnit 5.
- `magma-core` tests: tokenization, small parser cases, canonical AST shapes.
- `magma-codegen` tests: given a tiny IR, expect generated C string snippets.

Integration tests
- `magma-tests` runs the CLI on small `.mg` programs kept as fixtures, asserts expected `.c` output patterns and exit codes.

Quality gates
- `mvn verify` must run unit tests and fail on regressions.
- Optional code coverage threshold via jacoco.

---

## 8) Deployment and packaging

- The `magma-cli` module produces a runnable jar. Optionally create platform-specific launchers later.
- If the toolchain requires a native C compiler invocation, provide an opt-in Maven profile that runs an external process (not recommended in the initial iteration).

---

## 9) Assumptions and trade-offs

Assumptions
- Java is a comfortable language choice for future contributors.
- The repository will host both the original code (non-Java) and the new Java implementation; cross-language coherence is a documentation responsibility.

Trade-offs
- Using ANTLR speeds parser development but adds generated sources; a handwritten parser is lighter but more code to maintain.
- Multi-module Maven layout increases initial setup overhead but improves incremental build times and separation of concerns.

---

## 10) Risks and mitigations

Risk: introducing heavy dependencies or complexity.
- Mitigation: keep initial modules small; avoid ANTLR until a clear need.

Risk: duplicating compiler behavior across languages.
- Mitigation: prioritize port-first approach—start with minimal behavior parity and write tests that assert emitted C for small programs.

Risk: CI friction on Windows for native deps.
- Mitigation: avoid native dependencies in early tests; guard native steps behind opt-in env vars.

---

## 11) Small incremental roadmap (next concrete steps)

1. Create `java/` parent pom with module placeholders and a minimal `magma-cli` main class that prints help (this doc maps to acceptance criteria).
2. Add `magma-core` with a `Token`, `Lexer` and a `StubLexer` and unit tests for tokenization.
3. Add `magma-codegen` with a minimal C emitter that can emit "hello" program for a hard-coded IR.
4. Add `magma-tests` with a fixture that runs `magma-cli --help` and asserts exit code 0.

---

## 12) Next steps & requests for the maintainer

Please confirm:
- Preferred Java LTS version (17 or 21).
- Whether to add ANTLR now or postpone to a follow-up.
- Preferred Maven coordinates (groupId) if not `com.sirmathhman`.

If confirmed, I will implement step 1 (scaffold parent POM and magma-cli stub) and run `mvn -f java/pom.xml -DskipTests=false clean verify` to verify the scaffold.


---

Appendix: brief examples and pseudo-contracts

- CompilerService contract
  - Input: path to `.mg` source (String or Path)
  - Output: path to emitted `.c` file on success or list of diagnostics on failure
  - Errors: IO errors, parsing/type errors (returned as diagnostics)

- Example pseudo-code (non-runnable sketch)

  // Main
  var src = Files.readString(inputPath);
  var tokens = LexerFactory.create().tokenize(src);
  var ast = new Parser().parse(tokens);
  var checked = TypeChecker.check(ast);
  if (checked.hasErrors()) { printDiagnostics(); return 1; }
  var ir = Lowering.lower(checked);
  CodeEmitter.emitC(ir, outputPath);
  return 0;


---

Document author: automated architecture planner

End of file.
