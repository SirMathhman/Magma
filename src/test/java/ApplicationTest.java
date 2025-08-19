import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ApplicationTest {
  private static final String BEFORE_INPUT = "extern fn readInt() : I32; ";

  @Test
  void pass() {
    assertValid("readInt()", "5", 5);
  }

  @Test
  void add() {
    assertValid("readInt() + readInt()", "3\r\n4", 7);
  }

  private void assertValid(String input, String stdin, int expected) {
    try {
      int exit = Runner.run(BEFORE_INPUT + input, stdin);
      assertEquals(expected, exit);
    } catch (ApplicationException e) {
      fail(e);
    }
  }
}