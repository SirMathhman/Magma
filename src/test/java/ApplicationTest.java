import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ApplicationTest {
  final String BEFORE_INPUT = """
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

  @Test
  void testTrue() {
    assertValid("true", "", 1);
  }

  @Test
  void testFalse() {
    assertValid("false", "", 0);
  }

  @Test
  void ifTestTrue() {
    assertValid("if (true) { 5 } else { 10 }", "5", 5);
  }

  @Test
  void ifTestFalse() {
    assertValid("if (false) { 5 } else { 10 }", "10", 10);
  }

  @Test
  void comparison() {
    assertValid("readInt() == 5", "5", 1);
  }

  @Test
  void ifWithCallTrue() {
    assertValid("if (readInt() == 5) { 10 } else { 20 }", "5", 10);
  }

  @Test
  void ifWithCallFalse() {
    assertValid("if (readInt() == 5) { 10 } else { 20 }", "6", 20);
  }

  @Test
  void array() {
    assertValid("let array = [readInt()]; array[0]", "100", 100);
  }

  @Test
  void assignment() {
    assertValid("let mut x = 100; x = readInt(); x", "20", 20);
  }

  @Test
  void function() {
    assertValid("fn get() => readInt(); get()", "100", 100);
  }

  @Test
  void struct() {
    assertValid("struct Wrapper {value: I32} let wrapper = Wrapper { readInt() }; wrapper.value", "100", 100);
  }

  @Test
  void structWithTwoFields() {
    assertValid(
        "struct Wrapper {first: I32, second: I32} let wrapper = Wrapper { readInt(), readInt() }; wrapper.first", "100\r\n200",
        100);
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
