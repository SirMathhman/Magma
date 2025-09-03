package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class InterpreterTest {
  @Test
  void undefined() {
  assertUndefined("test", """
    Undefined identifier.

    File: <virtual>

    1) test
       ^^^^""");
  }

  @Test
  void twoLines() {
    assertUndefined("test\r\nother", """
        Undefined identifier.

        File: <virtual>

        1) test
           ^^^^
        2) other""");
  }

  // Helper to avoid duplicated switch/assert logic across tests (prevents CPD duplication)
  private void assertUndefined(String source, String expected) {
    switch (Interpreter.interpret(source, "")) {
      case Ok(var value) -> fail("Did not expect a value: " + value);
      case Err(var error) -> assertEquals(expected, error.display());
    }
  }
}
