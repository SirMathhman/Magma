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
      // Parse simple form: let NAME = INIT; BODY
      int eq = t.indexOf('=');
      if (eq < 0) {
        // malformed let - fall back to returning 0
        return "#include <stdio.h>\nint main(void) {\n  return 0;\n}\n";
      }
      String name = t.substring(4, eq).trim();
      int semi = t.indexOf(';', eq);
      if (semi < 0) {
        // no semicolon separating init and body - treat rest as body
        semi = eq;
      }
      String init = t.substring(eq + 1, semi).trim();
      String body = "";
      if (semi < t.length() - 1)
        body = t.substring(semi + 1).trim();

      // Build program: include readInt, declare variable, return body (or variable)
      StringBuilder sb = new StringBuilder();
      sb.append(header);
      sb.append("int main(void) {\n");
      sb.append("  int ").append(name).append(" = ").append(init).append(";\n");
      if (body.isEmpty()) {
        sb.append("  return ").append(name).append(";\n");
      } else {
        sb.append("  return ").append(" ").append(body).append(";\n");
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