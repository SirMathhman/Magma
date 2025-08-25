package org.example;

import static org.example.TestUtils.assertInvalid;
import static org.example.TestUtils.assertValid;

import org.junit.jupiter.api.Test;

public class ForTest {
  @Test
  void forMissingParensIsInvalid() {
    assertInvalid("for true { }");
  }

  @Test
  void forMissingBracesIsInvalid() {
    assertInvalid("for (true) true");
  }

  @Test
  void forWithInitCondIncrAndBlockThenExpr() {
    assertValid("let x : I32; x = 5; for (let i = 0; true; i = 1) { } x", "5");
  }

  @Test
  void forNotAnExpressionInLetInitializer() {
    assertInvalid("let x = for (let i = 0; true; i = 1) { }; ");
  }
}
