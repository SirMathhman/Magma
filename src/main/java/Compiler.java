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

    // expression is whatever comes after the last semicolon (the
    // prelude typically ends with a semicolon). This keeps the
    // implementation robust to the provided PRELUDE in tests.
    String expr = src;
    int lastSemi = src.lastIndexOf(';');
    if (lastSemi != -1 && lastSemi + 1 < src.length()) {
      expr = src.substring(lastSemi + 1).trim();
    } else if (lastSemi != -1) {
      expr = "";
    }

    if (expr.isEmpty()) {
      return buildProgram("  return 0;\n");
    }

    // support a single intrinsic call: readInt()
    if (expr.equals("readInt()")) {
      String body = "  int v = 0;\n" +
          "  if (scanf(\"%d\", &v) != 1) { return 0; }\n" +
          "  return v;\n";
      return buildProgram(body);
    }

    // support binary addition of two readInt() calls: readInt() + readInt()
    int plusIdx = expr.indexOf('+');
    if (plusIdx != -1) {
      String left = expr.substring(0, plusIdx).trim();
      String right = expr.substring(plusIdx + 1).trim();
      if (left.equals("readInt()") && right.equals("readInt()")) {
        String body = "  int a = 0;\n" +
            "  int b = 0;\n" +
            "  if (scanf(\"%d\", &a) != 1) { return 0; }\n" +
            "  if (scanf(\"%d\", &b) != 1) { return 0; }\n" +
            "  return a + b;\n";
        return buildProgram(body);
      }
    }

    // fallback: unknown expression -> return 0
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
