package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class InterpreterTest {
  @Test
  void error() {
    assertUndefined("test", """
        Undefined identifier.

        File: <virtual>

        1) test
           ^^^^""");
  }

  @Test
  void errorTwoLines() {
    assertUndefined("test\r\nother", """
        Undefined identifier.

        File: <virtual>

        1) test
           ^^^^
        2) other""");
  }

  @Test
  void integer() {
    switch (Interpreter.interpret("5", "")) {
      case Ok(var value) -> assertEquals("5", value);
      case Err(var error) -> fail("Did not expect an error: " + error.display());
    }
  }

  // Helper to avoid duplicated switch/assert logic across tests (prevents CPD
  // duplication)
  private void assertUndefined(String source, String expected) {
    switch (Interpreter.interpret(source, "")) {
      case Ok(var value) -> fail("Did not expect a value: " + value);
      case Err(var error) -> assertEquals(expected, error.display());
    }
  }
}
