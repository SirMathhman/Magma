package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Assertions;

public final class TestUtil {
  private TestUtil() {
  }

  public static void assertValid(String source, String expected) {
    assertValid(source, "", expected);
  }

  public static void assertValid(String source, String externalInput, String expected) {
    var interpreter = new Interpreter();
    var result = interpreter.interpret(source, externalInput);
    switch (result) {
      case Ok<String, InterpretError>(var actual) -> assertEquals(expected, actual);
      case Err<String, InterpretError>(var err) -> Assertions.fail(err.display());
    }
  }

  public static void assertValidWithPrelude(String afterPrelude, String externalInput, String expected) {
    final String PRELUDE = "intrinsic fn readInt() : I32; ";
    assertValid(PRELUDE + afterPrelude, externalInput, expected);
  }
}
