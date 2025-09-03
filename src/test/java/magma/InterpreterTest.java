package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterTest {

  @Test
  public void interpret_integerLiteral_returnsNumber() {
    Interpreter interpreter = new Interpreter();
    assertEquals(new Ok<String, InterpretError>("123"), interpreter.interpret("123"));
    assertEquals(new Ok<String, InterpretError>("-42"), interpreter.interpret("-42"));
    assertEquals(new Ok<String, InterpretError>("7"), interpreter.interpret("+7"));
  }

  @Test
  public void interpret_invalidInputs_returnErrorMessages() {
    Interpreter interpreter = new Interpreter();
    assertEquals(new Err<String, InterpretError>(new InterpretError("empty input")), interpreter.interpret(""));
    assertEquals(new Err<String, InterpretError>(new InterpretError("invalid integer")), interpreter.interpret("+"));
    assertEquals(new Err<String, InterpretError>(new InterpretError("invalid character: x")),
        interpreter.interpret("12x3"));
  }
}
