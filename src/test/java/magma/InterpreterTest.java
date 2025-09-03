package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class InterpreterTest {
  @Test
  void empty() {
    var interpreter = new Interpreter();
    var result = interpreter.interpret("");
    switch (result) {
      case Ok<String, InterpretError>(var actual) -> assertEquals("", actual);
      case Err<String, InterpretError>(var err) -> fail(err.display());
    }
  }

  @Test
  void readInt() {
    var interpreter = new Interpreter();
    var result = interpreter.interpret("intrinsic fn readInt() : I32; readInt()", "100");
    switch (result) {
      case Ok<String, InterpretError>(var actual) -> assertEquals("100", actual);
      case Err<String, InterpretError>(var err) -> fail(err.display());
    }
  }

  @Test
  void readIntSum() {
    var interpreter = new Interpreter();
    var result = interpreter.interpret("intrinsic fn readInt() : I32; readInt() + readInt()", "10\r\n20");
    switch (result) {
      case Ok<String, InterpretError>(var actual) -> assertEquals("30", actual);
      case Err<String, InterpretError>(var err) -> fail(err.display());
    }
  }
}
