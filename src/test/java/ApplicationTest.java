import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ApplicationTest {
  final String PRELUDE = """
      intrinsic fn readInt() : I32;""";

  @Test
  void empty() {
    assertValid("", "", 0);
  }

  private void assertValid(String input, String stdin, int expected) {
    try {
      int exit = Runner.run(PRELUDE + input, stdin);
      assertEquals(expected, exit);
    } catch (ApplicationException e) {
      fail(e);
    }
  }
}
