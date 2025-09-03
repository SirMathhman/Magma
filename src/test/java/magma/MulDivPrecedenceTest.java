package magma;

import org.junit.jupiter.api.Test;

public class MulDivPrecedenceTest {
  @Test
  void mulAddMix() {
    TestHelpers.assertValid("5 * 3 + 1", "16");
  }

  @Test
  void addMulPrecedence() {
    TestHelpers.assertValid("1 + 5 * 3", "16");
  }

  @Test
  void mulWithParentheses() {
    TestHelpers.assertValid("4 * (3 + 2)", "20");
  }

  @Test
  void integerDivision() {
    TestHelpers.assertValid("10 / 2", "5");
  }

  @Test
  void divideZero() {
    TestHelpers.assertInvalid("10 / 0", """
        Division by zero.

        File: <virtual>

        1) 10 / 0
                ^""");
  }
}
