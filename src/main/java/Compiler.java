class Compiler {
  public static String compile(String input) {
    String expr = extractTrailingExpression(input);

    // If the *input* mentions readInt anywhere (even in a function body or
    // declaration), generate a program that includes the readInt helper and
    // converts declarations (let/fn) to C before returning the final expr.
    if (input != null && input.contains("readInt")) {
      return generateReadIntProgram(input);
    }

    int ret = expr.isEmpty() ? 0 : parseOperation(expr);

    return "#include <stdio.h>\n" +
        "int main(void) {\n" +
        "  return " + ret + ";\n" +
        "}\n";
  }

  // Helper container for parsed program parts.
  private static final class ProgramParts {
    final StringBuilder decls = new StringBuilder();
    final StringBuilder fnDecls = new StringBuilder();
    String lastExpr = "";
    String lastDeclName = "";
  }

  // Pure: parse semicolon-separated segments extracting let/fn declarations
  // and the final expression. Keeps logic small and testable.
  private static ProgramParts parseSegments(String t) {
    ProgramParts parts = new ProgramParts();
    if (t == null || t.isEmpty())
      return parts;

    String[] segs = splitTopLevel(t);
    for (String part : segs) {
      String p = part.trim();
      if (p.isEmpty())
        continue;
      if (tryParseLet(parts, p))
        continue;
      if (tryParseFn(parts, p))
        continue;
      if (tryIgnoreExtern(p))
        continue;

      parts.lastExpr = p;
    }
    return parts;
  }

  // Split on semicolons that are at top-level (not inside braces {}).
  private static String[] splitTopLevel(String s) {
    java.util.List<String> parts = new java.util.ArrayList<>();
    if (s == null || s.isEmpty())
      return new String[0];
    StringBuilder cur = new StringBuilder();
    int depth = 0;
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == '{') {
        depth++;
        cur.append(c);
      } else if (c == '}') {
        depth = Math.max(0, depth - 1);
        cur.append(c);
      } else if (c == ';' && depth == 0) {
        parts.add(cur.toString());
        cur.setLength(0);
      } else {
        cur.append(c);
      }
    }
    if (cur.length() > 0)
      parts.add(cur.toString());
    return parts.toArray(new String[0]);
  }

  private static boolean tryParseLet(ProgramParts parts, String p) {
    if (!p.startsWith("let "))
      return false;
    int eq = p.indexOf('=');
    if (eq < 0)
      return true; // treated as handled but malformed
    String name = p.substring(4, eq).trim();
    String init = p.substring(eq + 1).trim();
    parts.decls.append("  int ").append(name).append(" = ").append(init).append(";\n");
    parts.lastDeclName = name;
    return true;
  }

  private static boolean tryParseFn(ProgramParts parts, String p) {
    if (!p.startsWith("fn "))
      return false;
    int nameStart = 3; // after "fn "
    int parenIdx = p.indexOf('(', nameStart);
    if (parenIdx < 0)
      return true; // handled but malformed
    String name = p.substring(nameStart, parenIdx).trim();
    int arrow = p.indexOf("=>", parenIdx);
    if (arrow < 0)
      return true; // handled but malformed
    String body = p.substring(arrow + 2).trim();
    // strip trailing semicolon if present
    if (body.endsWith(";"))
      body = body.substring(0, body.length() - 1).trim();

    // support block bodies: { let x = readInt(); x }
    if (body.startsWith("{") && body.endsWith("}")) {
      String inner = body.substring(1, body.length() - 1).trim();
      ProgramParts innerParts = parseSegments(inner);
      StringBuilder f = new StringBuilder();
      f.append("int ").append(name).append("(void) {\n");
      // append declarations inside function
      f.append(innerParts.decls.toString());
      // determine return expression
      String ret;
      if (innerParts.lastExpr.isEmpty()) {
        ret = innerParts.lastDeclName.isEmpty() ? "0" : innerParts.lastDeclName;
      } else {
        ret = innerParts.lastExpr;
      }
      f.append("  return ").append(ret).append(";\n");
      f.append("}\n");
      parts.fnDecls.append(f.toString());
      return true;
    }

    parts.fnDecls.append("int ").append(name).append("(void) { return ").append(body).append("; }\n");
    return true;
  }

  private static boolean tryIgnoreExtern(String p) {
    return p.startsWith("extern ");
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

    // Parse semicolon-separated segments into parts used to build the C program.
    ProgramParts parts = parseSegments(t);

    StringBuilder sb = new StringBuilder();
    sb.append(header);
    // append any generated functions
    if (parts.fnDecls.length() > 0) {
      sb.append(parts.fnDecls.toString());
    }
    sb.append("int main(void) {\n");
    sb.append(parts.decls.toString());

    if (parts.lastExpr.isEmpty()) {
      if (parts.lastDeclName.isEmpty()) {
        sb.append("  return 0;\n");
      } else {
        sb.append("  return ").append(parts.lastDeclName).append(";\n");
      }
    } else {
      sb.append("  return ").append(parts.lastExpr).append(";\n");
    }

    sb.append("}\n");
    return sb.toString();
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