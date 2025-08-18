import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ApplicationTest {
  @Test
  void empty() throws Exception {
    assertValid("", 0);
  }

  @Test
  void integer() throws Exception {
    assertValid("5", 5);
  }

  @Test
  void integerWithSuffix() {
    assertValid("5I32", 5);
  }

  @Test
  void add() {
    assertValid("5+3", 8);
  }

  @Test
  void subtract() {
    assertValid("5-3", 2);
  }

  @Test
  void multiply() {
    assertValid("5*3", 15);
  }

  private void assertValid(String input, int expected) {
    try {
      int exit = Application.run(input);
      assertEquals(expected, exit);
    } catch (ApplicationException e) {
      fail(e);
    }
  }
}
