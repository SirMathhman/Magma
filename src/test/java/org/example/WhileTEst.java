package org.example;

import static org.example.TestUtils.assertInvalid;
import static org.example.TestUtils.assertValid;

import org.junit.jupiter.api.Test;

public class WhileTEst {
  @Test
  void whileThenExpr() {
    assertValid("let x : I32; x = 5; while (true) { } x", "5");
  }

  @Test
  void whileNotAnExpressionInLetInitializer() {
    assertInvalid("let x = while (true) { };");
  }

  @Test
  void whileMissingParensIsInvalid() {
    assertInvalid("let x : I32; while true { } x");
  }

  @Test
  void whileMissingBracesIsInvalid() {
    assertInvalid("let x : I32; while (true) x");
  }
}
