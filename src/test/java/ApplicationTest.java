import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class ApplicationTest {
  private static final String PRELUDE = "intrinsic fn readInt() : I32; ";

  @Test
  public void undefined() {
    assertThrows(CompileException.class, () -> Runner.run("readInt", ""));
  }

  private void assertValid(String source, String input, int expected) {
    try {
      assertEquals(expected, Runner.run(source, input));
    } catch (CompileException | RunException e) {
      fail(e);
    }
  }

  @Test
  void integer() {
    assertValid("0", "", 0);
  }

  @Test
  void pass() {
    assertValidWithPrelude("readInt()", "100", 100);
  }

  @Test
  void add() {
    assertValidWithPrelude("readInt() + readInt()", "100\r\n200", 300);
  }

  private void assertValidWithPrelude(String source, String input, int exitCode) {
    assertValid(PRELUDE + source, input, exitCode);
  }

  @Test
  void subtract() {
    assertValidWithPrelude("readInt() - readInt()", "n200\r\n100", 100);
  }

  @Test
  void multiply() {
    assertValidWithPrelude("readInt() * readInt()", "100\r\n200", 20000);
  }
}
