package magma;

import org.junit.jupiter.api.Test;

public class EqualityTest {
  @Test
  void readIntEquals() {
    TestUtil.assertValidWithPrelude("readInt() == readInt()", "5\r\n5", "true");
    TestUtil.assertValidWithPrelude("readInt() == readInt()", "5\r\n6", "false");
  }
}
