import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class ApplicationTest {
  @Test
  public void undefined() {
    assertThrows(CompileException.class, () -> Runner.run("test", ""));
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
    assertValid("intrinsic fn readInt() : I32; readInt()", "100", 100);
  }
}
