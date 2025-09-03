package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public final class TestHelpers {
  private TestHelpers() {}

  public static void assertInvalid(String source, String expected) {
    switch (Interpreter.interpret(source, "")) {
      case Ok(var value) -> fail("Did not expect a value: " + value);
      case Err(var error) -> assertEquals(expected, error.display());
    }
  }

  public static void assertValid(String source, String expected) {
    switch (Interpreter.interpret(source, "")) {
      case Ok(var value) -> assertEquals(expected, value);
      case Err(var error) -> fail("Did not expect an error: " + error.display());
    }
  }
}
