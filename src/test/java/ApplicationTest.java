import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ApplicationTest {
  private static final String BEFORE_INPUT = "";

  @Test
  void valid() {
    assertValid("", "", 0);
  }

  @Test
  void integer() {
    assertValid("5", "", 5);
  }

  @Test
  void add() {
    assertValid("2 + 3", "", 5);
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
