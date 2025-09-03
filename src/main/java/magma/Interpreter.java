package magma;

public class Interpreter {
  public static Result<String, InterpretError> interpret(String source, String input) {
    // Trim whitespace and handle a single integer literal as a value
    String trimmed = source.strip();
    // Accept plain integers only (optional leading + or - and digits)
    if (trimmed.matches("[+-]?\\d+")) {
      return new Ok<String, InterpretError>(trimmed);
    }
    return new Err<String, InterpretError>(new InterpretError(source));
  }
}
