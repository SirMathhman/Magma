
Magma - tiny interpreter (test harness)

This repository contains a minimal `Interpreter` and a JUnit 5 test verifying handling of an `intrinsic fn readInt()` call.

How to run tests (requires Maven):

```powershell
mvn test
```

What the build runs
-------------------
- Unit tests (JUnit 5)
- Checkstyle
- PMD (static analysis)
- CPD (duplicate code detection)

All static checks (Checkstyle, PMD, and CPD) are bound to the `test` phase in `pom.xml`, so they run whenever you run `mvn test`.

The project is configured to analyze both main and test sources for these checks.

CPD sensitivity and intent
--------------------------
The CPD threshold is currently set via `<minimumTokens>` in `pom.xml` (presently 100). This value is intentionally low to encourage emergent
design and early refactoring rather than serving as a blunt copy/paste detector. In practice this means:

- Small duplicated fragments may be flagged to prompt thinking about abstraction.
- The goal is to encourage developers to refactor early and consolidate common patterns, not to punish incidental similarity.

If you find CPD too noisy for certain directories or file types, we can adjust `minimumTokens`, exclude paths, or add a separate ruleset for test code.

Verification performed
----------------------
I verified the configuration locally by running the following commands and observing successful builds:

```powershell
mvn -DskipTests=false test    # runs tests + Checkstyle + PMD + CPD (test sources included)
mvn verify                   # runs the full lifecycle including the checks (also works)
```

If you want a lighter local iteration, we can add a Maven profile to skip static checks during rapid development.

Recent changes
--------------
- Added tests for typed let-binding (`let x : I32 = readInt(); x`) and other small interpreter test cases (addition, subtraction, multiplication, untyped let).
- The interpreter currently recognizes `intrinsic fn readInt() : I32;` and simple expressions composed of `readInt()` calls and `let` bindings.

- Added a unit test that asserts a duplicate `let` declaration produces an interpreter error: `let x : I32 = readInt(); let x : I32 = 0;` and implemented a simple duplicate-declaration check in `Interpreter`.

- Enforced a new Checkstyle rule: methods may have at most 3 parameters. See `config/checkstyle/checkstyle.xml`.
- Reordered Maven plugins so Checkstyle runs before PMD/CPD during the `test` phase. This helps surface style issues earlier.

How to run the interpreter tests (skip CPD/PMD when iterating)
-------------------------------------------------------------

If CPD/PMD checks are noisy during development you can run the interpreter tests only and skip PMD/CPD checks (PowerShell):

```powershell
mvn "-Dpmd.skip=true" -Dtest=InterpreterTest test
```

How to run tests
----------------
Use the normal Maven test command (this runs tests + static checks):

```powershell
mvn -DskipTests=false test
```

New: array literal and indexing support
-------------------------------------
The interpreter now supports fixed-size array type annotations and
array literals for I32, plus indexing in final expressions. Example:

	let array : [I32; 3] = [1, 2, 3]; array[0]

You can run the focused test for this feature with:

```powershell
mvn -Dtest=InterpreterTest#arrayLiteralIndex test
```
