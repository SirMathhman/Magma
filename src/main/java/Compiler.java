public class Compiler {
  private static final java.util.regex.Pattern LEADING_INT = java.util.regex.Pattern.compile("^[+-]?\\d+");

  /**
   * Compile the given source string into a C program. For now this returns
   * a minimal C program that simply exits with code computed from the
   * simple expression in the input.
   *
   * @param input source to compile
   * @return C source text
   */
  public static String compile(String input) {
    int value = 0;
    if (input == null || input.isEmpty()) {
      return buildC(value);
    }

    String s = input.trim();

    // If the source uses read(), generate a C program that reads an int from stdin
    // and returns it. This keeps behavior simple for the tests which provide
    // stdin directly to the application. If there are multiple read() calls we
    // generate C that reads each occurrence into a variable and substitutes
    // them into the expression so expressions like "read() + read()" work
    // as the tests expect.
    if (s.contains("read()")) {
      // If the prelude is present at the start, strip it so we only operate on
      // the actual expression.
      final String PRELUDE = "external fn read<T>() : T;";
      String expr = s;
      if (expr.startsWith(PRELUDE)) {
        expr = expr.substring(PRELUDE.length()).trim();
      }
      return buildCReadExpression(expr);
    }
    ParseState st = parseLeadingInt(s);
    value = st.value;
    value = evaluateExpression(s, st.pos, value);

    return buildC(value);
  }

  private static class ParseState {
    final int value;
    final int pos;

    ParseState(int value, int pos) {
      this.value = value;
      this.pos = pos;
    }
  }

  private static ParseState parseLeadingInt(String s) {
    int value = 0;
    int pos = 0;
    java.util.regex.Matcher m = LEADING_INT.matcher(s);
    if (m.find()) {
      try {
        value = Integer.parseInt(m.group());
      } catch (NumberFormatException e) {
        value = 0;
      }
      pos = m.end();
    }
    return new ParseState(value, pos);
  }

  private static int evaluateExpression(String s, int pos, int value) {
    final int len = s.length();
    while (pos < len) {
      pos = skipWhitespace(s, pos);
      if (pos >= len) {
        break;
      }
      char op = s.charAt(pos);
      if (op != '+' && op != '-' && op != '*') {
        break;
      }
      pos++;
      pos = skipWhitespace(s, pos);
      if (pos >= len) {
        break;
      }
      IntParse parsed = parseNextNumber(s, pos);
      if (!parsed.found) {
        break;
      }
      value = applyOperation(value, op, parsed.value);
      pos = parsed.endPos;
    }
    return value;
  }

  private static final class IntParse {
    final boolean found;
    final int value;
    final int endPos;

    IntParse(boolean found, int value, int endPos) {
      this.found = found;
      this.value = value;
      this.endPos = endPos;
    }
  }

  private static IntParse parseNextNumber(String s, int pos) {
    java.util.regex.Matcher m2 = LEADING_INT.matcher(s.substring(pos));
    if (!m2.find()) {
      return new IntParse(false, 0, pos);
    }
    try {
      int num = Integer.parseInt(m2.group());
      return new IntParse(true, num, pos + m2.end());
    } catch (NumberFormatException e) {
      return new IntParse(false, 0, pos + m2.end());
    }
  }

  private static int applyOperation(int value, char op, int num) {
    if (op == '+') {
      return value + num;
    }
    if (op == '-') {
      return value - num;
    }
    // assume '*'
    return value * num;
  }

  private static int skipWhitespace(String s, int pos) {
    final int len = s.length();
    while (pos < len && Character.isWhitespace(s.charAt(pos))) {
      pos++;
    }
    return pos;
  }

  private static String buildC(int value) {
    StringBuilder sb = new StringBuilder();
    sb.append("#include <stdlib.h>\n");
    sb.append("int main(void) {\n");
    sb.append("    return ").append(value).append(";\n");
    sb.append("}\n");
    return sb.toString();
  }

  /**
   * Build C program that reads one or more ints and evaluates the expression
   * provided in terms of those read() calls. We replace each occurrence of
   * read() with a temporary variable name (r0, r1, ...) and emit scanf calls
   * to populate them, then return the evaluated expression.
   */
  private static String buildCReadExpression(String expr) {
    java.util.List<Integer> positions = findReadPositions(expr);
    StringBuilder sb = new StringBuilder();
    sb.append("#include <stdio.h>\n");
    sb.append("#include <stdlib.h>\n");
    sb.append("int main(void) {\n");

    int n = positions.size();
    sb.append(buildReadVariables(n));
    sb.append(buildScanfIf(n));

    // build the return expression by replacing read() with r{i}
    String replaced = replaceReadsWithVars(expr, n);
    sb.append(buildReturnOrLet(replaced));

    if (n > 0) {
      sb.append("    }\n");
      sb.append("    return 0;\n");
    }
    sb.append("}\n");
    return sb.toString();
  }

  private static java.util.List<Integer> findReadPositions(String expr) {
    java.util.List<Integer> positions = new java.util.ArrayList<>();
    int idx = 0;
    while (true) {
      int p = expr.indexOf("read()", idx);
      if (p == -1)
        break;
      positions.add(p);
      idx = p + 6;
    }
    return positions;
  }

  // Pure helpers: return string fragments instead of mutating a StringBuilder.
  private static String buildReadVariables(int n) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < n; i++) {
      sb.append("    int r").append(i).append(" = 0;\n");
    }
    return sb.toString();
  }

  private static String buildScanfIf(int n) {
    if (n <= 0)
      return "";
    StringBuilder sb = new StringBuilder();
    sb.append("    if (");
    for (int i = 0; i < n; i++) {
      if (i > 0)
        sb.append(" && ");
      sb.append("scanf(\"%d\", &r").append(i).append(") == 1");
    }
    sb.append(") {\n");
    return sb.toString();
  }

  private static String replaceReadsWithVars(String expr, int n) {
    String replaced = expr;
    for (int i = 0; i < n; i++) {
      replaced = replaced.replaceFirst("read\\(\\)", "r" + i);
    }
    return replaced;
  }

  private static String buildReturnOrLet(String replaced) {
    StringBuilder sb = new StringBuilder();
    String trimmed = replaced.trim();
    if (trimmed.startsWith("let ") && trimmed.contains(";")) {
      int semi = trimmed.indexOf(';');
      String binding = trimmed.substring(0, semi).trim();
      String rest = trimmed.substring(semi + 1).trim();
      String decl = binding.replaceFirst("^let\\s+", "int ");
      sb.append("        ").append(decl).append(";\n");
      if (rest.isEmpty()) {
        sb.append("        return 0;\n");
      } else {
        sb.append("        return ").append(rest).append(";\n");
      }
    } else {
      sb.append("        return ").append(replaced).append(";\n");
    }
    return sb.toString();
  }
}
