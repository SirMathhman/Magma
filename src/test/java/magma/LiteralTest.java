package magma;

import org.junit.jupiter.api.Test;

public class LiteralTest {
  @Test
  void empty() {
    TestUtil.assertValid("", "");
  }

  @Test
  void boolTrue() {
    TestUtil.assertValid("true", "true");
  }

  @Test
  void integerLiteral() {
    TestUtil.assertValidWithPrelude("readInt()", "100", "100");
  }
}
