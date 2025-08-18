import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ApplicationTest {
  final String PRELUDE = """
      intrinsic fn readInt() : I32; """;

  @Test
  void empty() {
    assertValid("", "", 0);
  }

  @Test
  void read() {
    assertValid("readInt()", "5", 5);
  }

  @Test
  void add() {
    assertValid("readInt() + readInt()", "3\r\n4", 7);
  }

  @Test
  void subtract() {
    assertValid("readInt() - readInt()", "5\r\n3", 2);
  }

  @Test
  void multiply() {
    assertValid("readInt() * readInt()", "3\r\n4", 12);
  }

  @Test
  void let() {
    assertValid("let x = readInt(); x", "5", 5);
  }

  @Test
  void letMultiple() {
    assertValid("let x = readInt(); let y = readInt(); x", "5\r\n10", 5);
  }

  @Test
  void letAndAdd() {
    assertValid("let x = readInt(); let y = readInt(); x + y", "5\r\n10", 15);
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
