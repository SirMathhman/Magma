class Compiler {
  public static String compile(String input) {
    String expr = extractExpr(input);
    String decl = "";
    String retExpr = expr.isEmpty() ? "0" : expr;

    // attempt to desugar a simple let-binding
    LetBinding lb = parseLetBinding(expr);
    if (lb != null) {
      decl = "    int " + lb.id + " = (" + lb.init + ");\n";
      retExpr = lb.after;
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
    if (!decl.isEmpty()) {
      out.append(decl);
    }
    out.append("    return (");
    out.append(retExpr);
    out.append(");\n");
    out.append("}\n");

    return out.toString();
  }

  private static String extractExpr(String input) {
    if (input == null)
      return "";
    int semicolon = input.indexOf(';');
    if (semicolon >= 0 && semicolon + 1 < input.length()) {
      return input.substring(semicolon + 1).trim();
    }
    return input.trim();
  }

  private static LetBinding parseLetBinding(String expr) {
    if (expr == null)
      return null;
    String trimmed = expr.trim();
    if (!trimmed.startsWith("let "))
      return null;
    int eq = trimmed.indexOf('=');
    int semi = trimmed.indexOf(';', eq >= 0 ? eq : 0);
    if (eq <= 0 || semi <= eq)
      return null;
    String idPart = trimmed.substring(4, eq).trim();
    String initPart = trimmed.substring(eq + 1, semi).trim();
    String after = trimmed.substring(semi + 1).trim();
    if (idPart.isEmpty() || initPart.isEmpty() || after.isEmpty())
      return null;
    return new LetBinding(idPart, initPart, after);
  }

  private static final class LetBinding {
    final String id;
    final String init;
    final String after;

    LetBinding(String id, String init, String after) {
      this.id = id;
      this.init = init;
      this.after = after;
    }
  }
}