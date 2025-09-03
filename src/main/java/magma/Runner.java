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

    // Use Compiler to compile the input and map compile errors to run errors.
    Compiler compiler = new Compiler();
    Result<String, CompileError> compileResult = compiler.compile(in);

    // Use Java pattern matching for switch (JDK 17+ preview features may be
    // required for some pattern matching features depending on the JDK). For
    // sealed Result, match on the concrete record types.
    return switch (compileResult) {
      case Result.Ok(var value) -> Result.ok(value);
      case Result.Err(var error) -> Result.err(new RunError(error.message(), in));
      default -> Result.err(new RunError("Unknown result variant", in));
    };
  }
}
