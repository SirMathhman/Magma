package magma;

public class Interpreter {
  // Interpret the given input using the provided context/source and
  // return either an Ok value with the result string or an Err with an
  // InterpretError.
  public Result<String, InterpretError> interpret(String input, String context) {
    return Result.ok("");
  }
}
 