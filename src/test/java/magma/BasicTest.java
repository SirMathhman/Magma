package magma;

import org.junit.jupiter.api.Test;

public class BasicTest {
  @Test
  void empty() {
    TestHelpers.assertValid("", "", "");
  }

  @Test
  void pass() {
    TestHelpers.assertValid("readInt()", "100", "100");
  }

  @Test
  void integer() {
    TestHelpers.assertValid("5", "", "5");
  }

  @Test
  void add() {
    TestHelpers.assertValid("readInt() + readInt()", "100\r\n200", "300");
  }

  @Test
  void addRequiresNumLeft() {
    TestHelpers.assertInvalid("true + 1");
  }

  @Test
  void subtract() {
    TestHelpers.assertValid("readInt() - readInt()", "100\r\n200", "-100");
  }

  @Test
  void booleanLiteralTrue() {
    TestHelpers.assertValid("true", "", "true");
  }

  @Test
  void simpleIfLiteralTrue() {
    TestHelpers.assertValid("if (true) 3 else 5", "", "3");
  }

  @Test
  void eqNumbers() {
    TestHelpers.assertValid("5 == 3", "", "false");
  }

  @Test
  void lessThanWithReadInt() {
    TestHelpers.assertValid("3 < readInt()", "5", "true");
  }
}
