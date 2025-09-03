package magma;

import org.junit.jupiter.api.Test;

public class LiteralTest {
  @Test
  void booleanLiteralFalse() {
    TestHelpers.assertValid("false", "", "false");
  }
}
