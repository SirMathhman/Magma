package magma;

/**
 * The Compiler class for Magma. This is a placeholder for future compiler logic.
 */
import magma.result.Result;
import magma.result.Ok;
import magma.result.Err;

/**
 * The Compiler class for Magma. This is a placeholder for future compiler
 * logic.
 */
public class Compiler {
  /**
   * Compiles the given source code string.
   * 
   * @param source The source code to compile.
   * @return Result containing compiled output or a CompileError.
   */
  public Result<String, CompileError> compile(String source) {
    if (source == null || source.isEmpty()) {
      return new Err<>(new CompileError("Source code is empty."));
    }
    // First validate the source by interpreting it. If interpretation fails,
    // return a CompileError wrapping the interpret error.
    var interp = Interpreter.interpret(source);
    if (interp instanceof magma.result.Err) {
      // InterpretError is stored as the error value in Err
      var interpretError = ((magma.result.Err<?, magma.InterpretError>) interp).error();
      return new Err<>(new CompileError(interpretError.display()));
    }
    // Use the interpreted result (string) as the output to print from C.
    String value = ((magma.result.Ok<String, ?>) interp).value();
    // Only allow simple literal outputs (integers or booleans) to be compiled into C
  if (!(value != null && (value.matches("-?\\d+") || "true".equals(value) || "false".equals(value)))) {
      return new Err<>(new CompileError("Only literal integer or boolean expressions can be compiled in tests"));
    }
    // Produce a minimal C program that prints the interpreted value.
    // Escape backslashes and double quotes for embedding in a C string literal.
    String escaped = value.replace("\\", "\\\\").replace("\"", "\\\"");
    String cProgram = "#include <stdio.h>\n" +
        "int main(void) {\n" +
        "    printf(\"%s\", \"" + escaped + "\");\n" +
        "    return 0;\n" +
        "}\n";
    return new Ok<>(cProgram);
  }
}
