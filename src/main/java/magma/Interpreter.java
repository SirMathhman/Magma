package magma;

public class Interpreter {
  public static Result<String, InterpretError> interpret(String source, String input) {
    return new Err<String,InterpretError>(new InterpretError(source));
  }
}
