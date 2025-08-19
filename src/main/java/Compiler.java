class Compiler {
  public static String compile(String input) throws CompileException {
    boolean hasPrelude = input != null && input.indexOf(';') >= 0;
    String expr = extractExpr(input);
    String[] parts = buildDeclAndRet(expr, hasPrelude, input);
    return buildC(parts[0], parts[1]);
  }

  private static String[] buildDeclAndRet(String expr, boolean hasPrelude, String input) throws CompileException {
    String decl = "";
    String retExpr = (expr == null || expr.isEmpty()) ? "0" : expr;

    validateIdentifiers(expr, hasPrelude);

    // If the prelude declares readInt as returning Bool, using it as the top-level
    // return
    // expression (e.g. "readInt()") is considered invalid for this tiny compiler.
    if (hasPrelude) {
      String preludeType = getPreludeReturnType(input);
      if ("Bool".equals(preludeType)) {
        String t = (expr == null) ? "" : expr.trim();
        if (t.equals("readInt()")) {
          throw new CompileException("Invalid return: readInt declared as Bool");
        }
      }
    }

    LetBinding lb = parseLetBinding(expr);
    if (lb != null) {
      processLetBinding(lb);
      decl = "    int " + lb.id + " = (" + lb.init + ");\n";
      retExpr = lb.after.isEmpty() ? "0" : lb.after;
    }

    return new String[] { decl, retExpr };
  }

  private static void validateIdentifiers(String expr, boolean hasPrelude) throws CompileException {
    if (expr == null)
      return;
    String t = expr.trim();
    if (t.isEmpty() || t.startsWith("let "))
      return;
    // if there's no prelude, identifiers should be considered undefined
    String cleaned = expr;
    if (hasPrelude) {
      // remove allowed identifiers and then fail if any letter remains
      cleaned = expr.replace("readInt", "").replace("true", "").replace("false", "");
    }
    for (int i = 0; i < cleaned.length(); i++) {
      if (Character.isLetter(cleaned.charAt(i))) {
        // find the token for error message
        int j = i;
        while (j < cleaned.length() && (Character.isLetterOrDigit(cleaned.charAt(j)) || cleaned.charAt(j) == '_'))
          j++;
        String tok = cleaned.substring(i, j);
        throw new CompileException("Unknown identifier: " + tok);
      }
    }
  }

  private static void processLetBinding(LetBinding lb) throws CompileException {
    if (lb.declaredType != null && lb.declaredType.equals("Bool") && !isBooleanInit(lb.init)) {
      throw new CompileException("Type mismatch: cannot assign non-bool to Bool");
    }
  }

  private static String buildC(String decl, String retExpr) {
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
    // support optional type annotation after identifier, e.g. 'x: I32'
    String declaredType = null;
    int colon = idPart.indexOf(':');
    if (colon >= 0) {
      declaredType = idPart.substring(colon + 1).trim();
      idPart = idPart.substring(0, colon).trim();
    }
    String initPart = trimmed.substring(eq + 1, semi).trim();
    String after = trimmed.substring(semi + 1).trim();
    if (idPart.isEmpty() || initPart.isEmpty())
      return null;
    // allow empty 'after' (e.g. `let x: Bool = 5;`) — caller will handle default
    return new LetBinding(idPart, initPart, after, declaredType);
  }

  private static final class LetBinding {
    final String id;
    final String init;
    final String after;
    final String declaredType;

    LetBinding(String id, String init, String after, String declaredType) {
      this.id = id;
      this.init = init;
      this.after = after;
      this.declaredType = declaredType;
    }
  }

  private static boolean isBooleanInit(String init) {
    if (init == null)
      return false;
    String t = init.trim();
    if (t.equals("true") || t.equals("false"))
      return true;
    // simple heuristic: presence of comparison operators yields boolean
    return t.contains("==") || t.contains("!=") || t.contains("<") || t.contains(">") || t.contains("<=")
        || t.contains(">=");
  }

  private static String getPreludeReturnType(String input) {
    if (input == null)
      return null;
    int semicolon = input.indexOf(';');
    if (semicolon <= 0)
      return null;
    String pre = input.substring(0, semicolon).trim();
    // expected form: extern fn readInt() : Type
    int colon = pre.indexOf(':');
    if (colon < 0)
      return null;
    String type = pre.substring(colon + 1).trim();
    return type.isEmpty() ? null : type;
  }
}