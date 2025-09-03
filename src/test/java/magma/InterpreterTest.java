package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class InterpreterTest {
  @Test
  void error() {
    assertInvalid("test", """
        Undefined identifier.

        File: <virtual>

        1) test
           ^^^^""");
  }

  @Test
  void errorTwoLines() {
    assertInvalid("test\r\nother", """
        Undefined identifier.

        File: <virtual>

        1) test
           ^^^^
        2) other""");
  }

  @Test
  void integer() {
    assertValid("5", "5");
  }

  @Test
  void add() {
    assertValid("5 + 3", "8");
  }

  @Test
  void trueTest() {
    assertValid("true", "true");
  }

  @Test
  void falseTest() {
    assertValid("false", "false");
  }

  @Test
  void addRequiresIntLeft() {
    assertInvalid("true + 1", """
        Addition requires integer on the left-hand side.

        File: <virtual>

        1) true + 1
           ^^^^""");
  }

  // Helper to avoid duplicated switch/assert logic across tests (prevents CPD
  // duplication)
  private void assertInvalid(String source, String expected) {
    switch (Interpreter.interpret(source, "")) {
      case Ok(var value) -> fail("Did not expect a value: " + value);
      case Err(var error) -> assertEquals(expected, error.display());
    }
  }

  private void assertValid(String source, String expected) {
    switch (Interpreter.interpret(source, "")) {
      case Ok(var value) -> assertEquals(expected, value);
      case Err(var error) -> fail("Did not expect an error: " + error.display());
    }
  }
}
