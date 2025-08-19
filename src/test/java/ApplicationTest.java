import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ApplicationTest {
  private static final String BEFORE_INPUT = "extern fn readInt() : I32; ";

  @Test
  void valid() {
    assertValid("", "", 0);
  }

  @Test
  void integer() {
    assertValid("5", "", 5);
  }

  @Test
  void add() {
    assertValid("2 + 3", "", 5);
  }

  @Test
  void subtract() {
    assertValid("5 - 3", "", 2);
  }

  @Test
  void multiply() {
    assertValid("2 * 3", "", 6);
  }

  @Test
  void read() {
    assertValid("readInt();", "100", 100);
  }

  @Test
  void readAdd() {
    assertValid("readInt() + readInt();", "1\r\n2", 3);
  }

  @Test
  void readSubtract() {
    assertValid("readInt() - readInt();", "5\r\n3", 2);
  }

  @Test
  void readMultiply() {
    assertValid("readInt() * readInt();", "2\r\n3", 6);
  }

  @Test
  void let() {
    assertValid("let x = readInt(); x", "100", 100);
  }

  @Test
  void letTwo() {
    assertValid("let x = readInt(); let y = x; y", "100", 100);
  }

  @Test
  void letThree() {
    assertValid("let x = readInt(); let y = x; let z = y; z", "100", 100);
  }

  @Test
  void function() {
    assertValid("fn get() => readInt(); get()", "100", 100);
  }

  @Test
  void letBeforeFunction() {
    assertValid("let x = 0; fn get() => readInt(); get()", "100", 100);
  }

  @Test
  void letWithinFunction() {
    assertValid("fn get() => { let x = readInt(); x }; get()", "100", 100);
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