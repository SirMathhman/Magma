package magma;

import org.junit.jupiter.api.Test;

public class LetExtrasTest {
  @Test
  void letInitWithThreeReadInts() {
    TestHelpers.assertValid("let x = readInt() + readInt() + readInt(); x", "10\r\n20\r\n30", "60");
  }

  @Test
  void letInitWithThreeReadIntsSubtraction() {
    TestHelpers.assertValid("let x = readInt() - readInt() - readInt(); x", "10\r\n20\r\n30", "-40");
  }
}
