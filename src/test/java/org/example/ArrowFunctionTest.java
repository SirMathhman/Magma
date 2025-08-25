package org.example;

import static org.example.TestUtils.assertValid;

import org.junit.jupiter.api.Test;

public class ArrowFunctionTest {
  @Test
  void arrowFunctionAssignedAndCalled() {
    assertValid("let func = () => 100; func()", "100");
  }
}
