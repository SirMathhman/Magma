package org.example;

import static org.example.TestUtils.assertInvalid;
import static org.example.TestUtils.assertValid;

import org.junit.jupiter.api.Test;

public class IntrepreterExpressionTest {
  @Test
  void interpretShouldThrowOnTypedOperands() {
    assertInvalid("5I64 + 0U8");
  }

  @Test
  void interpretShouldRejectPlainPlusTyped() {
    assertInvalid("8 + 10U32");
  }

  @Test
  void multiplicationHasPrecedenceOverAdditionAndSubtraction() {
    assertValid("3 + 4 * 7", "31");
  }

  @Test
  void parenthesesHavePrecedenceOverMultiplication_leftGrouped() {
    assertValid("(3 + 4) * 2", "14");
  }

  @Test
  void parenthesesHavePrecedenceOverMultiplication_rightGrouped() {
    assertValid("3 * (4 + 2)", "18");
  }

  @Test
  void interpretShouldAllowLetInitializedWithAddition() {
    // let binding initialized with an addition, then referenced
    assertValid("let x = 5 + 7; x", "12");
  }
}
