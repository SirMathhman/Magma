package magma;

import org.junit.jupiter.api.Test;

public class ControlFlowTest {
  @Test
  void equalsTrue() {
    TestHelpers.assertValid("readInt() == readInt()", "10\r\n10", "true");
  }

  @Test
  void ifTrue() {
    TestHelpers.assertValid("if readInt() == readInt() 10 else 20", "10\r\n10", "10");
  }

  @Test
  void ifFalse() {
    TestHelpers.assertValid("if readInt() == readInt() 10 else 20", "10\r\n20", "20");
  }
}
