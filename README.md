Magma - tiny interpreter (test harness)

This repository contains a minimal `Interpreter` and a JUnit 5 test verifying handling of an `intrinsic fn readInt()` call.

How to run tests (requires Maven):

```powershell
mvn test
```

If Maven isn't available, you can compile and run manually, but using Maven will download JUnit and run the tests automatically.

What changed:
- Added a `pom.xml` with JUnit 5 dependency.
- Converted `InterpreterTest` to a JUnit 5 test.
- Implemented minimal `interpret` behavior in `Interpreter` to satisfy the test.

PMD and CPD
-----------
This project now includes the Maven PMD plugin which runs both PMD and CPD during the `verify` phase and will fail the build on violations.

To run PMD/CPD via Maven:

```powershell
mvn verify
```

You can tune CPD sensitivity by editing the `minimumTokens` property in `pom.xml`.
Magma - tiny interpreter (test harness)

This repository contains a minimal `Interpreter` and a JUnit 5 test verifying handling of an `intrinsic fn readInt()` call.

How to run tests (requires Maven):

```powershell
mvn test
```

If Maven isn't available, you can compile and run manually, but using Maven will download JUnit and run the tests automatically.

What changed:
- Added a `pom.xml` with JUnit 5 dependency.
- Converted `InterpreterTest` to a JUnit 5 test.
- Implemented minimal `interpret` behavior in `Interpreter` to satisfy the test.
