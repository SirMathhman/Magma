package magma;

/**
 * Test utility class for running Magma code in tests.
 */
public class Runner {
  /**
   * Runs the given Magma code and returns the compilation result.
   * 
   * @param code The code to run.
   * @return Result containing compiled output or a CompileError.
   */
  public static magma.result.Result<String, CompileError> run(String code) {
    // For tests, just interpret the code and return its result. This avoids
    // relying on an external C compiler/runtime during unit tests.
    var interp = Interpreter.interpret(code);
    if (interp instanceof magma.result.Ok<String, ?> ok) {
      return new magma.result.Ok<>(ok.value());
    }
    if (interp instanceof magma.result.Err) {
      var ie = ((magma.result.Err<?, magma.InterpretError>) interp).error();
      return new magma.result.Err<>(new CompileError(ie.display()));
    }
    return new magma.result.Err<>(new CompileError("Unknown result"));
  }
}