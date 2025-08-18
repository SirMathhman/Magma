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

  private void assertValid(String input, int expected) {
    try {
      int exit = Application.run(input);
      assertEquals(expected, exit);
    } catch (ApplicationException e) {
      fail(e);
    }
  }
}
