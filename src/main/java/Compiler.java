class Compiler {
  public static String compile(String input) {
    String expr = extractTrailingExpression(input);

    if (isReadIntCall(expr)) {
      return "#include <stdio.h>\n" +
          "int readInt(void) {\n" +
          "  int x = 0;\n" +
          "  if (scanf(\"%d\", &x) == 1) return x;\n" +
          "  return 0;\n" +
          "}\n" +
          "int main(void) {\n" +
          "  return readInt();\n" +
          "}\n";
    }

    int ret = expr.isEmpty() ? 0 : parseOperation(expr);

    return "#include <stdio.h>\n" +
        "int main(void) {\n" +
        "  return " + ret + ";\n" +
        "}\n";
  }

  // Pure: extract the trailing expression after any leading declarations or
  // earlier semicolons. Avoid regex; handle trailing semicolons and spaces.
  private static String extractTrailingExpression(String input) {
    if (input == null)
      return "";
    String s = input.trim();
    int len = s.length();
    int end = len - 1;

    // Skip trailing whitespace and semicolons
    while (end >= 0) {
      char c = s.charAt(end);
      if (c == ';' || Character.isWhitespace(c))
        end--;
      else
        break;
    }
    if (end < 0)
      return "";

    int prevSemi = s.lastIndexOf(';', end);
    if (prevSemi >= 0) {
      return s.substring(prevSemi + 1, end + 1).trim();
    }
    return s.substring(0, end + 1).trim();
  }

  // Pure: detect a no-arg call to readInt like "readInt()" (no regex).
  private static boolean isReadIntCall(String s) {
    if (s == null)
      return false;
    s = s.trim();
    if (!s.startsWith("readInt"))
      return false;
    int open = s.indexOf('(');
    int close = s.lastIndexOf(')');
    if (open <= 0 || close <= open)
      return false;
    String name = s.substring(0, open);
    if (!"readInt".equals(name))
      return false;
    String inside = s.substring(open + 1, close).trim();
    return inside.isEmpty();
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