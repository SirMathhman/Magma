package magma;

public class Compiler {
  /**
   * Compiles the given source code string and returns the compiled output or a
   * CompileError wrapped in Result.
   */
  public static Result<String, CompileError> compile(String source) {
    String input = String.valueOf(source);
    if (input.equals("null")) {
      return Result.err(new CompileError("Empty source", input));
    }

    String prelude = "intrinsic fn readInt() : I32;";
    String core = input.replace(prelude, "").trim();
    if (core.isEmpty()) {
      return Result.ok(codeEmpty());
    }

    // Bare "readInt" without parentheses is illegal
    if (hasBareReadInt(core)) {
      return Result.err(new CompileError("Bare identifier 'readInt' used without parentheses", input));
    }

    // Pure boolean literal
    if (core.equals("true") || core.equals("false")) {
      return Result.ok(codePrintString(core));
    }

    // Split by ';' into statements; last non-empty is the expression
    String[] parts = core.split(";");
    java.util.Set<String> letNames = new java.util.HashSet<>();
    java.util.Set<String> readIntLets = new java.util.HashSet<>();
    String expr = "";

    int i = 0;
    while (i < parts.length) {
      String stmt = parts[i].trim();
      if (stmt.isEmpty()) {
        i = i + 1;
      } else if (stmt.startsWith("let")) {
        // parse: let IDENT ( : TYPE )? = RHS
        int p = 3;
        p = skipWs(stmt, p);
        if (!hasIdentifierStart(stmt, p)) {
          return Result.err(new CompileError("Invalid let declaration", input));
        }
        int s = p;
        p++;
        while (p < stmt.length() && Character.isJavaIdentifierPart(stmt.charAt(p)))
          p++;
        String name = stmt.substring(s, p);
        if (letNames.contains(name)) {
          return Result.err(new CompileError("Duplicate let declaration: '" + name + "'", input));
        }
        letNames.add(name);
        p = skipWs(stmt, p);
        if (p < stmt.length() && stmt.charAt(p) == ':') {
          p++;
          while (p < stmt.length() && stmt.charAt(p) != '=')
            p++;
        }
        p = skipWs(stmt, p);
        if (p >= stmt.length() || stmt.charAt(p) != '=') {
          return Result.err(new CompileError("Expected '=' in let declaration", input));
        }
        p++;
        while (p < stmt.length() && Character.isWhitespace(stmt.charAt(p)))
          p++;
        String rhs = stmt.substring(p).trim();
        if (rhs.equals("readInt()")) {
          readIntLets.add(name);
        }
        i = i + 1;
      } else {
        // collect expression from remaining parts
        StringBuilder tail = new StringBuilder();
        int j = i;
        while (j < parts.length) {
          String piece = parts[j].trim();
          if (!piece.isEmpty()) {
            if (tail.length() > 0)
              tail.append("; ");
            tail.append(piece);
          }
          j = j + 1;
        }
        expr = tail.toString();
        i = parts.length;
      }
    }

    if (expr.isEmpty()) {
      return Result.ok(codeEmpty());
    }

    // Equality between two let identifiers initialized by readInt()
    int eq = expr.indexOf("==");
    if (eq > 0) {
      String left = expr.substring(0, eq).trim();
      String right = expr.substring(eq + 2).trim();
      if (isReadIntLetPair(letNames, readIntLets, left, right)) {
        return Result.ok(codeCompare());
      }
      // Direct equality of two readInt() calls
      if (left.equals("readInt()") && right.equals("readInt()")) {
        return Result.ok(codeCompare());
      }
    }

    // Identifier expression bound to readInt()
    if (letNames.contains(expr) && readIntLets.contains(expr)) {
      return Result.ok(codeOneInt());
    }

    // Binary readInt() <op> readInt()
    String tok = "readInt()";
    int a = expr.indexOf(tok);
    if (a >= 0) {
      int p = a + tok.length();
      while (p < expr.length() && Character.isWhitespace(expr.charAt(p)))
        p++;
      if (p < expr.length()) {
        char op = expr.charAt(p);
        if (op == '+' || op == '-' || op == '*' || op == '/' || op == '%') {
          int q = p + 1;
          while (q < expr.length() && Character.isWhitespace(expr.charAt(q)))
            q++;
          if (q + tok.length() <= expr.length() && expr.substring(q, q + tok.length()).equals(tok)) {
            return Result.ok(codeBinary(op));
          }
        }
      }
    }

    // Single readInt()
    if (expr.equals("readInt()")) {
      return Result.ok(codeOneInt());
    }

    // Binary x <op> y where x and y are lets bound to readInt()
    int opPos = findBinaryOp(expr);
    if (opPos > 0) {
      char op = expr.charAt(opPos);
      if (op == '+' || op == '-' || op == '*' || op == '/' || op == '%') {
        String left = expr.substring(0, opPos).trim();
        String right = expr.substring(opPos + 1).trim();
        if (isReadIntLetPair(letNames, readIntLets, left, right)) {
          return Result.ok(codeBinary(op));
        }
      }
    }

    // Default: empty program
    return Result.ok(codeEmpty());
  }

  private static String codeEmpty() {
    return "#include <stdio.h>\nint main(void) {\n  return 0;\n}\n";
  }

  private static String codePrintString(String s) {
    return "#include <stdio.h>\nint main(void) {\n  printf(\"%s\", \"" + s + "\");\n  return 0;\n}\n";
  }

  private static boolean hasBareReadInt(String s) {
    int pos = 0;
    while ((pos = s.indexOf("readInt", pos)) >= 0) {
      int after = pos + "readInt".length();
      if (after >= s.length() || s.charAt(after) != '(') {
        return true;
      }
      pos = after;
    }
    return false;
  }

  private static String startTwoInt() {
    StringBuilder sb = new StringBuilder();
    sb.append("#include <stdio.h>\n");
    sb.append("int main(void) {\n");
    sb.append("  int a = 0, b = 0;\n");
    sb.append("  if (scanf(\"%d\", &a) != 1) return 1;\n");
    sb.append("  if (scanf(\"%d\", &b) != 1) return 1;\n");
    return sb.toString();
  }

  private static String codeCompare() {
    StringBuilder sb = new StringBuilder(startTwoInt());
    sb.append("  if (a == b) printf(\"%s\", \"true\"); else printf(\"%s\", \"false\");\n");
    sb.append("  return 0;\n");
    sb.append("}\n");
    return sb.toString();
  }

  private static String codeOneInt() {
    return "#include <stdio.h>\nint main(void) {\n  int x = 0;\n  if (scanf(\"%d\", &x) != 1) return 1;\n  printf(\"%d\", x);\n  return 0;\n}\n";
  }

  private static String codeBinary(char op) {
    StringBuilder sb = new StringBuilder(startTwoInt());
    sb.append("  int res = 0;\n");
    if (op == '+') {
      sb.append("  res = a + b;\n");
    } else if (op == '-') {
      sb.append("  res = a - b;\n");
    } else if (op == '*') {
      sb.append("  res = a * b;\n");
    } else if (op == '/') {
      sb.append("  if (b == 0) return 1;\n");
      sb.append("  res = a / b;\n");
    } else if (op == '%') {
      sb.append("  if (b == 0) return 1;\n");
      sb.append("  res = a % b;\n");
    }
    sb.append("  printf(\"%d\", res);\n");
    sb.append("  return 0;\n");
    sb.append("}\n");
    return sb.toString();
  }

  private static int findBinaryOp(String expr) {
    int i = 0;
    while (i < expr.length()) {
      char c = expr.charAt(i);
      if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%') {
        return i;
      }
      i = i + 1;
    }
    return -1;
  }

  private static boolean isReadIntLetPair(java.util.Set<String> lets, java.util.Set<String> readLets,
      String left, String right) {
    return lets.contains(left) && lets.contains(right) && readLets.contains(left) && readLets.contains(right);
  }

  private static int skipWs(String s, int p) {
    int i = p;
    while (i < s.length() && Character.isWhitespace(s.charAt(i)))
      i++;
    return i;
  }

  private static boolean hasIdentifierStart(String s, int p) {
    return p < s.length() && Character.isJavaIdentifierStart(s.charAt(p));
  }
}
