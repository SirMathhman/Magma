package magma;

import org.junit.jupiter.api.Test;

public class LetTest {
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

  @Test
  void letInvalidWithMismatchedType() {
    TestHelpers.assertInvalid("let x : I32 = true;");
  }
}
