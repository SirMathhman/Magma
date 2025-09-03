package magma;

import org.junit.jupiter.api.Test;

public class ParenthesizedTest {
  @Test
  void parenthesizedReadInt() {
    TestHelpers.assertValid("(readInt())", "42", "42");
  }

  @Test
  void parenthesizedReadIntPlusOne() {
    TestHelpers.assertValid("(readInt() + 1)", "41", "42");
  }

  @Test
  void parenthesizedLiteralPlusReadInt() {
    TestHelpers.assertValid("(1 + readInt())", "41", "42");
  }

  @Test
  void parenthesizedTwoReadInts() {
    TestHelpers.assertValid("(readInt() + readInt())", "10\r\n20", "30");
  }

  @Test
  void readIntPlusParenReadInt() {
    TestHelpers.assertValid("readInt() + (readInt())", "10\r\n20", "30");
  }
}
