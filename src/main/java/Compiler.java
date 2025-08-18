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
}
