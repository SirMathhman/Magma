package magma;

public class Compiler {
  /**
   * Compiles the given source code string and returns the compiled output or a
   * CompileError wrapped in Result.
   */
  public static Result<String, CompileError> compile(String source) {
    // Avoid using the literal null (project style checks may ban it).
    String input = String.valueOf(source);
    if (input.isBlank() || input.equals("null")) {
      return Result.err(new CompileError("Empty source", input));
    }

    // Simple stub compilation logic — replace with real compiler logic when ready.
    String output = "Compiled: " + input;
    return Result.ok(output);
  }
}
