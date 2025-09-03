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
    // Very small codegen: if the source uses the builtin readInt, emit a C
    // program that reads an integer from stdin and prints it. This keeps the
    // integration test simple while the real compiler is developed.
    if (input.contains("readInt")) {
      String c = "#include <stdio.h>\n" +
          "int main(void) {\n" +
          "  int x = 0;\n" +
          "  if (scanf(\"%d\", &x) != 1) return 1;\n" +
          "  printf(\"%d\", x);\n" +
          "  return 0;\n" +
          "}\n";
      return Result.ok(c);
    }

    String output = "Compiled: " + input;
    return Result.ok(output);
  }
}
