package org.example;

import static org.example.TestUtils.assertInvalid;
import static org.example.TestUtils.assertValid;

import org.junit.jupiter.api.Test;

/**
 * Semantic tests for closures (arrow and inline fn), focusing on calls, arity,
 * captures via `this`, and invalid cases.
 */
public class ClosureSemanticsTest {

  @Test
  void arrowClosureWithParamsReturnsSum() {
    assertValid("let add = (a : I32, b : I32) => a + b; add(2, 3)", "5");
  }

  @Test
  void arrowClosureWithReturnTypeAnnotationWorks() {
    assertValid("let id = (x : I32) : I32 => x; id(42)", "42");
  }

  @Test
  void arrowClosureWrongArityTooFewIsInvalid() {
    assertInvalid("let add = (a : I32, b : I32) => a + b; add(1)");
  }

  @Test
  void arrowClosureWrongArityTooManyIsInvalid() {
    assertInvalid("let add = (a : I32, b : I32) => a + b; add(1, 2, 3)");
  }

  @Test
  void closureRefersUndefinedVarIsInvalid() {
    // Closure body references 'x', which is not in scope when invoked directly
    assertInvalid("let f = () => x; f()");
  }

  @Test
  void closureStoredOnThisCapturesFieldsAndCalls() {
    // Build an object via `this` that contains fields a, b, and a closure 'clos'
    // which uses a and b. Calling obj.clos() should compute a + b.
    String prog =
        "fn outer(a : I32) => { let b : I32; b = 7; let clos = () => a + b; this } outer(5).clos()";
    assertValid(prog, "12");
  }

  @Test
  void objectFieldClosureWithParamUsesCapturedField() {
    // A closure field with one parameter should be callable: a captured from 'this'
    String prog =
        "fn maker(a : I32) => { let mul = (x : I32) => a * x; this } maker(3).mul(4)";
    assertValid(prog, "12");
  }

  @Test
  void objectFieldClosureWrongArityIsInvalid() {
    String prog =
        "fn maker(a : I32) => { let mul = (x : I32) => a * x; this } maker(3).mul()";
    assertInvalid(prog);
  }

  @Test
  void callingNonCallableFieldIsInvalid() {
    // 'x' is a numeric field; attempting to call it should fail with undefined function
    assertInvalid("fn outer() => { let x = 1; this } outer().x() ");
  }
}
