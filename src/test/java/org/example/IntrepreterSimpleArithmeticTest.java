package org.example;

import static org.example.TestUtils.assertValid;

import org.junit.jupiter.api.Test;

public class IntrepreterSimpleArithmeticTest {
  @Test
  void additionSimple() {
    assertValid("1 + 2", "3");
  }

  @Test
  void subtractionSimple() {
    assertValid("5 - 2", "3");
  }

  @Test
  void multiplicationSimple() {
    assertValid("4 * 6", "24");
  }

  @Test
  void precedenceMultiplicationOverAddition() {
    assertValid("2 + 3 * 4", "14");
  }

  @Test
  void leftAssociativityForSubtraction() {
    assertValid("10 - 2 - 3", "5");
  }

  @Test
  void whitespaceRobustness() {
    assertValid(" 7*  8 + 1 ", "57");
  }
}
