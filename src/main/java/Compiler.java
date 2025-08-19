class Compiler {
  public static String compile(String input) {
    String expr = extractTrailingExpression(input);
    if (expr.contains("readInt")) {
      return generateReadIntProgram(expr);
    }

    int ret = expr.isEmpty() ? 0 : parseOperation(expr);

    return "#include <stdio.h>\n" +
        "int main(void) {\n" +
        "  return " + ret + ";\n" +
        "}\n";
  }

  // Pure: generate C program that implements readInt() and returns the
  // provided expression (which may contain one or more readInt() calls).
  private static String generateReadIntProgram(String expr) {
    String header = "#include <stdio.h>\n" +
        "int readInt(void) {\n" +
        "  int x = 0;\n" +
        "  if (scanf(\"%d\", &x) == 1) return x;\n" +
        "  return 0;\n" +
        "}\n";

    String t = expr.trim();
    if (t.startsWith("let ")) {
      // Handle one or more semicolon-separated let declarations followed by
      // an optional final expression. Convert each `let NAME = INIT` into a
      // C declaration and return the final expression (or last declared
      // variable when no expression is present).
      String[] parts = t.split(";");
      StringBuilder decls = new StringBuilder();
      String lastExpr = "";
      String lastDeclName = "";

      for (String part : parts) {
        String p = part.trim();
        if (p.isEmpty())
          continue;
        if (p.startsWith("let ")) {
          int eq = p.indexOf('=');
          if (eq < 0)
            continue; // ignore malformed let
          String name = p.substring(4, eq).trim();
          String init = p.substring(eq + 1).trim();
          decls.append("  int ").append(name).append(" = ").append(init).append(";\n");
          lastDeclName = name;
        } else {
          // non-let segment is the final expression to return
          lastExpr = p;
        }
      }

      StringBuilder sb = new StringBuilder();
      sb.append(header);
      sb.append("int main(void) {\n");
      sb.append(decls.toString());

      if (lastExpr.isEmpty()) {
        if (lastDeclName.isEmpty()) {
          sb.append("  return 0;\n");
        } else {
          sb.append("  return ").append(lastDeclName).append(";\n");
        }
      } else {
        sb.append("  return ").append(lastExpr).append(";\n");
      }

      sb.append("}\n");
      return sb.toString();
    }

    return header +
        "int main(void) {\n" +
        "  return " + expr + ";\n" +
        "}\n";
  }

  // Pure: extract the trailing expression after any leading declarations or
  // earlier semicolons. Avoid regex; handle trailing semicolons and spaces.
  private static String extractTrailingExpression(String input) {
    if (input == null)
      return "";
    String s = input.trim();
    if (s.isEmpty())
      return "";

    String[] parts = s.split(";");
    String letExpr = reconstructLetExpression(parts);
    if (letExpr != null)
      return letExpr;

    String candidate = lastNonEmptySegment(parts);
    if (candidate.isEmpty())
      return "";

    if (!looksLikeDeclaration(candidate))
      return candidate;

    // find last non-declaration segment
    for (int i = parts.length - 1; i >= 0; i--) {
      String t = parts[i].trim();
      if (t.isEmpty())
        continue;
      if (!looksLikeDeclaration(t))
        return t;
    }

    return "";
  }

  private static String lastNonEmptySegment(String[] parts) {
    for (int i = parts.length - 1; i >= 0; i--) {
      String t = parts[i].trim();
      if (!t.isEmpty())
        return t;
    }
    return "";
  }

  private static String reconstructLetExpression(String[] parts) {
    for (int i = 0; i < parts.length; i++) {
      String t = parts[i].trim();
      if (t.startsWith("let ")) {
        StringBuilder sb = new StringBuilder();
        for (int j = i; j < parts.length; j++) {
          if (sb.length() > 0)
            sb.append("; ");
          sb.append(parts[j].trim());
        }
        return sb.toString().trim();
      }
    }
    return null;
  }

  private static boolean looksLikeDeclaration(String s) {
    return s.contains("extern") || s.contains("fn");
  }

  // Pure: determine whether the expression is binary (+, -, *) or a lone integer.
  private static int parseOperation(String s) {
    int plusIdx = s.indexOf('+');
    if (plusIdx >= 0)
      return parseBinary(s, plusIdx, '+');

    int minusIdx = s.indexOf('-');
    if (minusIdx >= 0)
      return parseBinary(s, minusIdx, '-');

    int mulIdx = s.indexOf('*');
    if (mulIdx >= 0)
      return parseBinary(s, mulIdx, '*');

    return parseIntOrZero(s);
  }

  // Pure: parse a binary operation around the given operator index.
  private static int parseBinary(String s, int opIdx, char op) {
    String left = s.substring(0, opIdx).trim();
    String right = s.substring(opIdx + 1).trim();
    try {
      int l = Integer.parseInt(left);
      int r = Integer.parseInt(right);
      switch (op) {
        case '+':
          return l + r;
        case '-':
          return l - r;
        case '*':
          return l * r;
        default:
          return 0;
      }
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  // Pure: parse integer or return 0 on failure.
  private static int parseIntOrZero(String s) {
    try {
      return Integer.parseInt(s);
    } catch (NumberFormatException e) {
      return 0;
    }
  }
}