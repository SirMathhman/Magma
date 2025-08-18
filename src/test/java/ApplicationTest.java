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

  @Test
  void add() {
    assertValid("5+3", 8);
  }

  @Test
  void subtract() {
    assertValid("5-3", 2);
  }

  @Test
  void multiply() {
    assertValid("5*3", 15);
  }

  final String PRELUDE = "external fn readInt() : I32; external fn readString() : *CStr;";

  @Test
  void read() {
    assertValidWithPrelude("readInt()", "5", 5);
  }

  @Test
  void readAdd() {
    assertValidWithPrelude("readInt() + readInt()", "3\r\n4", 7);
  }

  @Test
  void let() {
    assertValidWithPrelude("let x = readInt(); x", "5", 5);
  }

  @Test
  void letMultiple() {
    assertValidWithPrelude("let x = readInt(); let y = readInt(); x + y", "5\r\n10", 15);
  }

  @Test
  void classTest() {
    assertValidWithPrelude("class fn Wrapper(value : I32) => {} let value = Wrapper(readInt()); value.value", "100",
        100);
  }

  @Test
  void method() {
    assertValidWithPrelude("class fn Empty() => {fn get() => readInt();} let value = Empty(); value.get()", "100", 100);
  }

  @Test
  void functionTest() {
    assertValidWithPrelude("fn get() => readInt(); get()", "100", 100);
  }

  @Test
  void string() {
    assertValidWithPrelude("readString().length", "test", 4);
  }

  @Test
  void stringIsEmptyTrue() {
    assertValidWithPrelude("readString().isEmpty()", "", 1);
  }

  @Test
  void stringIsEmptyFalse() {
    assertValidWithPrelude("readString().isEmpty()", "test", 0);
  }

  private void assertValid(String input, int expected) {
    // default overload: no stdin
    assertValid(input, "", expected);
  }

  private void assertValid(String input, String stdin, int expected) {
    try {
      int exit = Application.run(input, stdin);
      assertEquals(expected, exit);
    } catch (ApplicationException e) {
      fail(e);
    }
  }

  private void assertValidWithPrelude(String input, String stdin, int expected) {
    assertValid(PRELUDE + input, stdin, expected);
  }
}
