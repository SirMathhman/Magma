package magma;

public class Compiler {
  /**
   * Compiles the given source code string and returns the compiled output or a
   * CompileError wrapped in Result.
   */
  public static Result<String, CompileError> compile(String source) {
    return Result.ok(source);
  }

  // emitIfProgram moved to CompilerUtil
}
