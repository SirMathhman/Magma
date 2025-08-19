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
  final StringBuilder globalDecls = new StringBuilder();
    final StringBuilder typeDecls = new StringBuilder();
    String lastExpr = "";
    String lastDeclName = "";
  }

  // Pure: parse semicolon-separated segments extracting let/fn declarations
  // and the final expression. Keeps logic small and testable.
  private static ProgramParts parseSegments(String t) {
    return parseSegments(t, false);
  }

  private static ProgramParts parseSegments(String t, boolean inFunction) {
    ProgramParts parts = new ProgramParts();
    if (t == null || t.isEmpty())
      return parts;

    String[] segs = splitTopLevel(t);
    for (String part : segs) {
      String p = part.trim();
      if (p.isEmpty())
        continue;
      if (tryParseStruct(parts, p, inFunction))
        continue;
      if (tryParseLet(parts, p, inFunction))
        continue;
      if (tryParseFn(parts, p, inFunction))
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

  private static boolean tryParseLet(ProgramParts parts, String p, boolean inFunction) {
    if (!p.startsWith("let "))
      return false;
    int eq = p.indexOf('=');
    if (eq < 0)
      return true; // treated as handled but malformed
    String name = p.substring(4, eq).trim();
    String init = p.substring(eq + 1).trim();
    // handle struct constructor: TypeName { .. }
    int braceIdx = init.indexOf('{');
    if (braceIdx > 0) {
      String typeName = init.substring(0, braceIdx).trim();
      String inner = init.substring(braceIdx + 1).trim();
      if (inner.endsWith("}"))
        inner = inner.substring(0, inner.length() - 1).trim();
      ProgramParts innerParts = parseSegments(inner, inFunction);
      String value;
      if (!innerParts.lastExpr.isEmpty())
        value = innerParts.lastExpr;
      else if (!innerParts.lastDeclName.isEmpty())
        value = innerParts.lastDeclName;
      else
        value = "0";
      if (inFunction) {
        parts.decls.append("  struct ").append(typeName).append(" ").append(name).append(" = { ").append(value)
            .append(" };\n");
      } else {
        // declare global variable and assign inside main
        parts.globalDecls.append("struct ").append(typeName).append(" ").append(name).append(";\n");
        parts.decls.append("  ").append(name).append(" = (struct ").append(typeName).append("){ ").append(value)
            .append(" };\n");
      }
      parts.lastDeclName = name;
      return true;
    }

    if (inFunction) {
      parts.decls.append("  int ").append(name).append(" = ").append(init).append(";\n");
    } else {
      // declare global and assign in main
      parts.globalDecls.append("int ").append(name).append(";\n");
      parts.decls.append("  ").append(name).append(" = ").append(init).append(";\n");
    }
    parts.lastDeclName = name;
    return true;
  }

  private static boolean tryParseStruct(ProgramParts parts, String p, boolean inFunction) {
    if (!p.startsWith("struct "))
      return false;
    int nameStart = 7; // after "struct "
    int braceIdx = p.indexOf('{', nameStart);
    if (braceIdx < 0)
      return true; // malformed but handled
    String name = p.substring(nameStart, braceIdx).trim();
    int endBrace = findMatchingBrace(p, braceIdx);
    if (endBrace < 0)
      return true; // malformed

    String inner = p.substring(braceIdx + 1, endBrace).trim();
    buildStructDef(parts, name, inner);

    // merge any trailing text after the struct declaration
    if (endBrace + 1 < p.length()) {
      String rest = p.substring(endBrace + 1).trim();
      if (!rest.isEmpty())
        mergePartsFromString(parts, rest, inFunction);
    }
    return true;
  }

  private static int findMatchingBrace(String s, int openIndex) {
    int depth = 0;
    for (int i = openIndex; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == '{')
        depth++;
      else if (c == '}') {
        depth--;
        if (depth == 0)
          return i;
      }
    }
    return -1;
  }

  private static void buildStructDef(ProgramParts parts, String name, String inner) {
    String[] fields = inner.split(",");
    StringBuilder sb = new StringBuilder();
    sb.append("struct ").append(name).append(" {\n");
    for (String f : fields) {
      String ft = f.trim();
      if (ft.isEmpty())
        continue;
      int colon = ft.indexOf(":");
      String fieldName = colon >= 0 ? ft.substring(0, colon).trim() : ft;
      sb.append("  int ").append(fieldName).append(";\n");
    }
    sb.append("};\n");
    parts.typeDecls.append(sb.toString());
  }

  private static void mergePartsFromString(ProgramParts parts, String rest, boolean inFunction) {
    ProgramParts extra = parseSegments(rest, inFunction);
    if (extra.typeDecls.length() > 0)
      parts.typeDecls.append(extra.typeDecls);
    if (extra.globalDecls.length() > 0)
      parts.globalDecls.append(extra.globalDecls);
    if (extra.fnDecls.length() > 0)
      parts.fnDecls.append(extra.fnDecls);
    if (extra.decls.length() > 0)
      parts.decls.append(extra.decls);
    if (!extra.lastExpr.isEmpty())
      parts.lastExpr = extra.lastExpr;
    if (!extra.lastDeclName.isEmpty())
      parts.lastDeclName = extra.lastDeclName;
  }

  private static boolean tryParseFn(ProgramParts parts, String p, boolean inFunction) {
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
      ProgramParts innerParts = parseSegments(inner, true);
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
    // append any generated type declarations (structs)
    if (parts.typeDecls.length() > 0) {
      sb.append(parts.typeDecls.toString());
    }
    // append any global variable declarations so functions can reference them
    if (parts.globalDecls.length() > 0) {
      sb.append(parts.globalDecls.toString());
    }
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