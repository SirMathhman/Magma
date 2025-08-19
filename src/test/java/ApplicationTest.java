import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class ApplicationTest {
  private static final String BEFORE_INPUT = "extern fn readInt() : I32; ";

  @Test
  void pass() {
    assertValidWithPrelude("readInt()", "5", 5);
  }

  @Test
  void add() {
    assertValidWithPrelude("readInt() + readInt()", "3\r\n4", 7);
  }

  @Test
  void subtract() {
    assertValidWithPrelude("readInt() - readInt()", "5\r\n3", 2);
  }

  @Test
  void multiply() {
    assertValidWithPrelude("readInt() * readInt()", "5\r\n3", 15);
  }

  @Test
  void equals() {
    assertValidWithPrelude("readInt() == readInt()", "5\r\n5", 1);
  }

  @Test
  void notEquals() {
    assertValidWithPrelude("readInt() != readInt()", "5\r\n4", 1);
  }

  @Test
  void lessThan() {
    assertValidWithPrelude("readInt() < readInt()", "3\r\n4", 1);
  }

  @Test
  void lessThanEqual() {
    assertValidWithPrelude("readInt() <= readInt()", "3\r\n4", 1);
  }

  @Test
  void greaterThan() {
    assertValidWithPrelude("readInt() > readInt()", "4\r\n3", 1);
  }

  @Test
  void greaterThanEqual() {
    assertValidWithPrelude("readInt() >= readInt()", "4\r\n4", 1);
  }

  @Test
  void let() {
    assertValidWithPrelude("let x = readInt(); x", "100", 100);
  }

  @Test
  void letBool() {
    assertValidWithPrelude("let x = readInt() == 5; x", "5", 1);
  }

  @Test
  void letWithType() {
    assertValidWithPrelude("let x: I32 = readInt(); x", "100", 100);
  }

  @Test
  void letWithTypeBool() {
    assertValidWithPrelude("let x: Bool = readInt() == 5; x", "5", 1);
  }

  @Test
  void letMismatchedType() {
    assertInvalidWithPrelude("let x: Bool = 5;", "");
  }

  @Test
  void undefinedIdentifier() {
    assertInvalid("readInt", "");
  }

  @Test
  void undefinedCaller() {
    assertInvalid("readInt()", "");
  }

  @Test
  void unknownReturn() {
    assertInvalid("extern fn readInt() : Bool; readInt()", "");
  }

  @Test
  void invalidArgument() {
    assertInvalid("extern fn readInt() : I32; readInt(5)", "");
  }

  private void assertInvalidWithPrelude(String input, String stdin) {
    assertInvalid(BEFORE_INPUT + input, stdin);
  }

  private void assertInvalid(String input, String stdin) {
    assertThrows(CompileException.class, () -> Runner.run(input, stdin));
  }

  private void assertValidWithPrelude(String input, String stdin, int expected) {
    try {
      int exit = Runner.run(BEFORE_INPUT + input, stdin);
      assertEquals(expected, exit);
    } catch (ApplicationException e) {
      fail(e);
    }
  }
}