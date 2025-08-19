import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ApplicationTest {
  private static final String BEFORE_INPUT = "extern fn readInt() : I32; ";

  @Test
  void integer() {
    assertValid("readInt()", "5", 5);
  }

  @Test
  void let() {
    assertValid("let x = readInt(); x", "100", 100);
  }

  @Test
  void functionTest() {
    assertValid("fn get() => readInt(); get()", "100", 100);
  }

  @Test
  void structureTest() {
    assertValid("struct Wrapper { field : I32 } let value = Wrapper {readInt()}; value.field", "100", 100);
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