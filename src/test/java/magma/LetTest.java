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
  void letMutable() {
    TestHelpers.assertValid("let mut x = 0; x = readInt(); x", "10", "10");
  }

  @Test
  void letInvalidWithSameName() {
    TestHelpers.assertInvalid("let x = 0; let x = 0;");
  }

  @Test
  void letInvalidWithMismatchedType() {
    TestHelpers.assertInvalid("let x : I32 = true;");
  }

  @Test
  void letInvalidAssignToImmutable() {
    TestHelpers.assertInvalid("let x = 0; x = readInt();");
  }

  @Test
  void postIncrement() {
    TestHelpers.assertValid("let mut x = readInt(); x++; x", "4", "5");
  }
}
