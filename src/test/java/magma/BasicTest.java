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
  void subtract() {
    TestHelpers.assertValid("readInt() - readInt()", "100\r\n200", "-100");
  }
}
