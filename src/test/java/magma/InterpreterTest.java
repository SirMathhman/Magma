package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class InterpreterTest {
  @Test
  void empty() {
    assertValid("", "");
  }

  @Test
  void readInt() {
    assertValid("intrinsic fn readInt() : I32; readInt()", "100", "100");
  }

  @Test
  void readIntSum() {
    assertValid("intrinsic fn readInt() : I32; readInt() + readInt()", "10\r\n20", "30");
  }

  private static void assertValid(String source, String expected) {
    assertValid(source, "", expected);
  }

  private static void assertValid(String source, String externalInput, String expected) {
    var interpreter = new Interpreter();
    var result = interpreter.interpret(source, externalInput);
    switch (result) {
      case Ok<String, InterpretError>(var actual) -> assertEquals(expected, actual);
      case Err<String, InterpretError>(var err) -> fail(err.display());
    }
  }
}
