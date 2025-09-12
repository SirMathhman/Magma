PR: Support methods in objects returned by `this`

Summary
-------
This PR adds support for returning `this` from a block where `this` may include inner functions declared in that block. Methods can be invoked on the returned object using `obj.method()` (no-arg methods only in this change).

Files changed
-------------
- src/main/java/magma/Interpreter.java
  - Added METHOD_PREFIX encoding for method entries in `this`.
  - Added tryEvalMethodCall/evalMethodInvoke and helpers makeCapturedEnv/shallowCopyEnv/findFieldValue.
  - Adjusted function call parsing (findOpenParen) to avoid misparsing nested calls.
- src/test/java/magma/InterpreterWrapperThisTest.java
  - New test confirming `fn Wrapper() => {fn get() => 100; this} Wrapper().get()` evaluates to `100`.
- docs/architecture.md
  - Documented the design and helper functions added.

Quality gates run
-----------------
- mvn -DskipTests=false package (local): BUILD SUCCESS
  - Tests run: 67, Failures: 0
  - Checkstyle: 0 violations
  - PMD CPD: no duplicate blocks reported

Notes for reviewers
-------------------
- Methods are encoded with `@MTH:name:bodyExpr`; bodyExpr is re-evaluated in a captured environment when invoked.
- No argument support for methods in this change; calling with arguments will produce an error.
- This is intentionally minimal to satisfy the acceptance test. Consider a follow-up to introduce proper Function values if more features are required.

Runtime/diagnostics
-------------------
- Improved the "invalid assignment lhs" runtime error to include the offending LHS fragment (e.g., the invalid token or expression) instead of repeating the entire source text. This makes assignment errors easier to locate during debugging.
