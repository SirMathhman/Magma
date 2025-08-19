import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

  @Test
  void subtract() {
    assertValid("readInt() - readInt()", "5\r\n3", 2);
  }

  @Test
  void multiply() {
    assertValid("readInt() * readInt()", "5\r\n3", 15);
  }

  @Test
  void equals() {
    assertValid("readInt() == readInt()", "5\r\n5", 1);
  }

  @Test
  void notEquals() {
    assertValid("readInt() != readInt()", "5\r\n4", 1);
  }

  @Test
  void lessThan() {
    assertValid("readInt() < readInt()", "3\r\n4", 1);
  }

  @Test
  void lessThanEqual() {
    assertValid("readInt() <= readInt()", "3\r\n4", 1);
  }

  @Test
  void greaterThan() {
    assertValid("readInt() > readInt()", "4\r\n3", 1);
  }

  @Test
  void greaterThanEqual() {
    assertValid("readInt() >= readInt()", "4\r\n4", 1);
  }

  @Test
  void let() {
    assertValid("let x = readInt(); x", "100", 100);
  }

  @Test
  void letBool() {
    assertValid("let x = readInt() == 5; x", "5", 1);
  }

  @Test
  void letWithType() {
    assertValid("let x: I32 = readInt(); x", "100", 100);
  }

  @Test
  void letWithTypeBool() {
    assertValid("let x: Bool = readInt() == 5; x", "5", 1);
  }

  @Test
  void letMismatchedType() {
    assertInvalid("let x: Bool = 5;", "");
  }

  private void assertInvalid(String input, String stdin) {
    assertThrows(CompileException.class, () -> Runner.run(BEFORE_INPUT + input, stdin));
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