package magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterLogicTest {
  @Test
  void interpretLogicalAndReturnsTrue() {
    Interpreter interpreter = new Interpreter();
    Result<String, InterpretError> expected = Result.ok("true");
    Result<String, InterpretError> actual = interpreter.interpret("true && true", "");
    assertEquals(expected, actual);
  }

  @Test
  void interpretIfWithLogicalAndConditionReturnsThenBranch() {
    Interpreter interpreter = new Interpreter();
    Result<String, InterpretError> expected = Result.ok("3");
    Result<String, InterpretError> actual = interpreter.interpret("if (true && true) 3 else 5", "");
    assertEquals(expected, actual);
  }
}
