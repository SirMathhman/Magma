package magma;

import org.junit.jupiter.api.Test;

public class ArithmeticTest {
  @Test
  void add() {
    TestHelpers.assertValid("readInt() + readInt()", "10\r\n20", "30");
  }

  @Test
  void subtract() {
    TestHelpers.assertValid("readInt() - readInt()", "10\r\n20", "-10");
  }

  @Test
  void multiply() {
    TestHelpers.assertValid("readInt() * readInt()", "10\r\n20", "200");
  }

  @Test
  void divide() {
    TestHelpers.assertValid("readInt() / readInt()", "10\r\n20", "0");
  }

  @Test
  void modulo() {
    TestHelpers.assertValid("readInt() % readInt()", "30\r\n20", "10");
  }
}
