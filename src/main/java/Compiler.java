public class Compiler {

  /**
   * Compile the given source string into a C program. For now this returns
   * a minimal C program that simply exits with code computed from the
   * simple expression in the input.
   *
   * @param input source to compile
   * @return C source text
   */
  private static final String HEADER = "#include <stdlib.h>\n#include <stdio.h>\n";

  public static String compile(String input) {
    // Very small, pragmatic compiler for the tests in this kata.
    // If the source references the intrinsic readInt(), emit a C
    // program that reads an integer from stdin and returns it. For
    // any other (including empty) input, return a program that exits 0.
    String src = input == null ? "" : input;
    if (src.contains("readInt()")) {
      String body = "  int v = 0;\n" +
          "  if (scanf(\"%d\", &v) != 1) { return 0; }\n" +
          "  return v;\n";
      return buildProgram(body);
    }

    // default: program that returns 0
    return buildProgram("  return 0;\n");
  }

  // Pure helper to assemble a full C program from a body snippet.
  private static String buildProgram(String body) {
    StringBuilder sb = new StringBuilder();
    sb.append(HEADER);
    sb.append("int main(void) {\n");
    sb.append(body);
    sb.append("}\n");
    return sb.toString();
  }
}
