package org.example;

import static org.example.TestUtils.assertValid;

import org.junit.jupiter.api.Test;

public class ClosureTest {
  @Test
  void nestedFnOnThisIsCallable() {
    assertValid("fn outer() => {fn inner() => 100; this} outer().inner()", "100");
  }

  @Test
  void closureCapturesParamAndLocal() {
    // outer returns `this` which contains both a parameter `a` and a local `b`.
    // inner should close over both and produce their sum.
    assertValid(
        "fn outer(a : I32) => { let b : I32; b = 5; fn inner() => a + b; this } outer(10).inner()",
        "15");
  }
}
