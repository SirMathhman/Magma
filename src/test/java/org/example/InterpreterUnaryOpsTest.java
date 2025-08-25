package org.example;

import org.junit.jupiter.api.Test;

public class InterpreterUnaryOpsTest {

  @Test
  void unaryMinusSimple() {
    TestUtils.assertValid("-5", "-5");
  }

  @Test
  void unaryMinusBindsTighterThanMul() {
    TestUtils.assertValid("-2 * 3", "-6");
  }

  @Test
  void doubleUnaryMinus() {
    TestUtils.assertValid("--5", "5");
  }

  @Test
  void unaryNotSimple() {
    TestUtils.assertValid("!true", "false");
  }

  @Test
  void doubleUnaryNot() {
    TestUtils.assertValid("!!true", "true");
  }

  @Test
  void notPrecedenceOverAndOr() {
    TestUtils.assertValid("true || !false && false", "true");
  }

  @Test
  void notRequiresBoolean() {
    TestUtils.assertInvalid("!1");
  }

  @Test
  void minusRequiresNumber() {
    TestUtils.assertInvalid("-true");
  }
}
