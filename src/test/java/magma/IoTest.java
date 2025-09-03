package magma;

import org.junit.jupiter.api.Test;

public class IoTest {
  @Test
  void undefined() {
    TestHelpers.assertInvalid("readInt");
  }

  @Test
  void pass() {
    TestHelpers.assertValid("readInt()", "10", "10");
  }

  @Test
  void empty() {
    TestHelpers.assertValid("", "", "");
  }
}
