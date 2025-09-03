package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class InterpreterTest {
  @Test
  public void empty() {
    var interpreter = new Interpreter();
    var result = interpreter.interpret("");
    switch (result) {
      case Ok<String, InterpretError>(var actual) -> assertEquals("", actual);
      case Err<String, InterpretError>(var err) -> fail(err.display());
    }
  }
}
