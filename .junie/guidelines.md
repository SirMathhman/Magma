Follow this process:

- Write the test
- Implement the test
- Remove duplicate code
- A single concept should be handled in a single place. We don't want the same concept to be handled by two different
  branches of code.
- Don't be afraid to separate a class into smaller ones.
- All CheckStyle errors should be resolved.
- If a feature seems too complex, break it into smaller features.
- Avoid putting logging statements or printlns everywhere. Instead, add tests to verify smaller pieces of behavior. If
  you can't add those tests, then the code ought to be refactored.

Notes:

You are required to:

- Build using `build.bat`
- Build using `test.bat`