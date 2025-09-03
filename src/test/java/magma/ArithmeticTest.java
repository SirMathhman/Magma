package magma;

import org.junit.jupiter.api.Test;

public class ArithmeticTest {
  @Test
  void integer() {
    TestHelpers.assertValid("5", "5");
  }

  @Test
  void add() {
    TestHelpers.assertValid("5 + 3", "8");
  }

  @Test
  void addArbitraryLength() {
    TestHelpers.assertValid("1 + 2 + 3", "6");
  }

  @Test
  void subtract() {
    TestHelpers.assertValid("5 - 3", "2");
  }

  @Test
  void addSubtractArbitraryMix() {
    TestHelpers.assertValid("1 + 2 - 3", "0");
  }

  @Test
  void mulAddMix() {
    TestHelpers.assertValid("5 * 3 + 1", "16");
  }

  @org.junit.jupiter.api.Test
  public void parenthesizedZero() {
    TestHelpers.assertValid("(0)", "0");
  }

  @Test
  void addMulPrecedence() {
    TestHelpers.assertValid("1 + 5 * 3", "16");
  }

  @Test
  void mulWithParentheses() {
    TestHelpers.assertValid("4 * (3 + 2)", "20");
  }
}
