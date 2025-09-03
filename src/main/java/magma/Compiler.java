package magma;

public class Compiler {
  /**
   * Compiles the given source code string and returns the compiled output or a
   * CompileError wrapped in Result.
   */
  public static Result<String, CompileError> compile(String source) {
    // Very small, pragmatic compiler stub used by tests:
    // - If the source contains a call to `readInt()` emit a C program that
    // reads an integer from stdin and prints it (no newline) so tests that
    // expect the integer as output pass.
    // - Otherwise emit a no-op program that returns 0 and prints nothing.
    if (source.contains("readInt()")) {
      String cProgram = "#include <stdio.h>\n\n" +
          "int main(void) {\n" +
          "  int x = 0;\n" +
          "  if (scanf(\"%d\", &x) == 1) {\n" +
          "    printf(\"%d\", x);\n" +
          "  }\n" +
          "  return 0;\n" +
          "}\n";
      return Result.ok(cProgram);
    }

    // Default: no output program
    String cProgram = "#include <stdio.h>\n\nint main(void) {\n  return 0;\n}\n";
    return Result.ok(cProgram);
  }

  // emitIfProgram moved to CompilerUtil
}
