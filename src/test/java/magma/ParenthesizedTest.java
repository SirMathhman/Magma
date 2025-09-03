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
}
