package org.example;

import static org.example.TestUtils.assertValid;

import org.junit.jupiter.api.Test;

public class ClosureTest {
  @Test
  void nestedFnOnThisIsCallable() {
    assertValid("fn outer() => {fn inner() => 100; this} outer().inner()", "100");
  }
}
