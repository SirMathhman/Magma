package org.example;

import static org.example.TestUtils.assertInvalid;
import static org.example.TestUtils.assertValid;

import org.junit.jupiter.api.Test;

public class InterpreterFunctionTest {
  @Test
  void fnDeclThenExprIsValid() {
    assertValid("let x : I32; x = 5; fn add(a : I32, b : I32) : I32 => 5; x", "5");
  }

  @Test
  void fnDeclMissingParensIsInvalid() {
    assertInvalid("let x : I32; fn add a : I32, b : I32) : I32 => 5; x");
  }

  @Test
  void fnDeclMissingArrowIsInvalid() {
    assertInvalid("let x : I32; fn add(a : I32, b : I32) : I32 5; x");
  }
}
