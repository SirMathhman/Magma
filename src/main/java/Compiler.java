class Compiler {
  public static String compile(String input) {
    // Extract the expression after the extern declaration ("; ")
    String expr = "";
    if (input != null) {
      int semicolon = input.indexOf(';');
      if (semicolon >= 0 && semicolon + 1 < input.length()) {
        expr = input.substring(semicolon + 1).trim();
      } else {
        expr = input.trim();
      }
    }

    if (expr.isEmpty()) {
      expr = "0";
    }

    StringBuilder out = new StringBuilder();
    out.append("#include <stdio.h>\n");
    out.append("#include <stdlib.h>\n\n");
    // provide a readInt helper that reads an integer from stdin
    out.append("int readInt() {\n");
    out.append("    int v = 0;\n");
    out.append("    if (scanf(\"%d\", &v) != 1) return 0;\n");
    out.append("    return v;\n");
    out.append("}\n\n");

    out.append("int main(void) {\n");
    out.append("    return (");
    out.append(expr);
    out.append(");\n");
    out.append("}\n");

    return out.toString();
  }
}