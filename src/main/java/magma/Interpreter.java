package magma;

public class Interpreter {
  // Interpret the given input using the provided context/source and
  // return either an Ok value with the result string or an Err with an
  // InterpretError.
  public Result<String, InterpretError> interpret(String input, String context) {
    // Simple behavior for now: if both input and context are empty, return Ok("").
    if ("".equals(input) && "".equals(context)) {
      return Result.ok("");
    }

    // Placeholder fallback — explicit Err to avoid null/throw per project rules.
    return new Result.Err<>(new InterpretError("interpret not implemented"));
  }
}
