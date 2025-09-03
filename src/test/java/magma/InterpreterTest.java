package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterTest {

  @Test
  public void interpret_integerLiteral_returnsNumber() {
    Interpreter interpreter = new Interpreter();
    assertEquals("123", interpreter.interpret("123"));
    assertEquals("-42", interpreter.interpret("-42"));
    assertEquals("7", interpreter.interpret("+7"));
  }

  @Test
  public void interpret_invalidInputs_returnErrorMessages() {
    Interpreter interpreter = new Interpreter();
    assertEquals("error: empty input", interpreter.interpret(""));
    assertEquals("error: invalid integer", interpreter.interpret("+"));
    assertEquals("error: invalid character: x", interpreter.interpret("12x3"));
  }
}
