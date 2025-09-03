package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

public class Runner {
  /**
   * Runs the given input and returns output or a RunError wrapped in Result.
   */
  public static Result<String, RunError> run(String input) {
    String in = String.valueOf(input);
    if (in.isBlank()) {
      return Result.err(new RunError("Empty input", in));
    }

    // Use Compiler to compile the input and map compile errors to run errors.
    Result<String, CompileError> compileResult = Compiler.compile(in);

    // Use pattern-matching switch to handle Result variants and write compiled
    // output to a temp file when compilation succeeds.
    return switch (compileResult) {
      case Result.Ok(var value) -> {
        String compiled = value;
        try {
          Path tmp = Files.createTempFile("magma-compiled-", ".txt");
          Files.writeString(tmp, compiled, StandardCharsets.UTF_8);
          yield Result.ok(compiled);
        } catch (IOException e) {
          yield Result.err(new RunError(e.getMessage(), in));
        }
      }
      case Result.Err(var error) -> Result.err(new RunError(error.message(), in));
      default -> Result.err(new RunError("Unknown result variant", in));
    };
  }
}
