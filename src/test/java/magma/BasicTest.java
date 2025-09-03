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
}
