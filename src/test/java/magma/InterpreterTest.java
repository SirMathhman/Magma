package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class InterpreterTest {
  @Test
  void undefined() {
    switch (Interpreter.interpret("test", "")) {
      case Ok(var value) -> {
        fail("Did not expect a value: " + value);
      }
      case Err(var error) -> {
        assertEquals("""
            Undefined identifier.

            1) test
               ^^^^""", error.display());
      }
    }
  }
}
