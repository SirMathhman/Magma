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

  @Test
  void let() {
    TestHelpers.assertValid("let x = readInt(); x", "10", "10");
  }

  @Test
  void letWithExplicitType() {
    TestHelpers.assertValid("let x: I32 = readInt(); x", "10", "10");
  }

  @Test
  void letMultiple() {
    TestHelpers.assertValid("let x = readInt(); let y = x; y", "10", "10");
  }

  @Test
  void letInvalidWithSameName() {
    TestHelpers.assertInvalid("let x = 0; let x = 0;");
  }
}
