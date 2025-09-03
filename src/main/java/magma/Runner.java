package magma;

public class Runner {
  /**
   * Runs the given input and returns output or a RunError wrapped in Result.
   */
  public Result<String, RunError> run(String input) {
    String in = String.valueOf(input);
    if (in.isBlank()) {
      return Result.err(new RunError("Empty input", in));
    }

    // stub execution logic
    String output = "Ran: " + in;
    return Result.ok(output);
  }
}
