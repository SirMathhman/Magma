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
    assertValid(PRELUDE + "readInt()", "100", 100);
  }

  @Test
  void add() {
    assertValid(PRELUDE + "readInt() + readInt()", "100\r\n200", 300);
  }

  @Test
  void subtract() {
    assertValid(PRELUDE + "readInt() - readInt()", "n200\r\n100", 100);
  }

  @Test
  void multiply() {
    assertValid(PRELUDE + "readInt() * readInt()", "100\r\n200", 20000);
  }

  @Test
  void let() {
    assertValid(PRELUDE + "let x = readInt(); x", "100", 100);
  }

  @Test
  void letWithExplicitType() {
    assertValid(PRELUDE + "let x: I32 = readInt(); x", "100", 100);
  }

  @Test
  void letMultiple() {
    assertValid(PRELUDE + "let x = readInt(); let y = readInt(); x + y", "100\r\n200", 300);
  }

}
