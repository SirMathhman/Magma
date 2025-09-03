package magma;

import org.junit.jupiter.api.Test;

public class LetTest {
  @Test
  void let() {
    TestHelpers.assertValid("let x : I32 = readInt(); x", "10", "10");
  }

  @Test
  void multipleLetInvalidWithSameNames() {
    TestHelpers.assertInvalid("let x : I32 = readInt(); let x : I32 = readInt(); x");
  }

  @Test
  void letWithImplicitType() {
    TestHelpers.assertValid("let x = readInt(); x", "10", "10");
  }

  @Test
  void multipleLetAdd() {
    TestHelpers.assertValid("let x = readInt(); let y = readInt(); x + y", "10\r\n20", "30");
  }

  @Test
  void letInitWithBool() {
    TestHelpers.assertValid("let x : Bool = true; x", "", "true");
  }

  @Test
  void letInitWithBoolFalse() {
    TestHelpers.assertValid("let x : Bool = false; x", "", "false");
  }

  @Test
  void letInitWithBoolImplicit() {
    TestHelpers.assertValid("let x = true; x", "", "true");
  }

  @Test
  void letInitWithReadIntPlusLiteral() {
    TestHelpers.assertValid("let x = readInt() + 1; x", "10", "11");
  }

  @Test
  void letInitWithLiteralPlusReadInt() {
    TestHelpers.assertValid("let x = 1 + readInt(); x", "10", "11");
  }
}
