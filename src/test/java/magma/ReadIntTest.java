package magma;

import org.junit.jupiter.api.Test;

public class ReadIntTest {
  @Test
  void readInt() {
    TestUtil.assertValidWithPrelude("readInt()", "100", "100");
  }

  @Test
  void add() {
    TestUtil.assertValidWithPrelude("readInt() + readInt()", "10\r\n20", "30");
  }

  @Test
  void letReadIntVariants() {
    TestUtil.assertValidWithPrelude("let x = readInt(); x", "42", "42");
    TestUtil.assertValidWithPrelude("let mut x = 0; x = readInt(); x", "7", "7");
  }

  @Test
  void twoLetsReadInt() {
    TestUtil.assertValidWithPrelude("let x = readInt(); let y = readInt(); x + y", "3\r\n4", "7");
  }
}
