public class Compiler {
  /**
   * Compile the given source string into a C program. For now this returns
   * a minimal C program that simply exits with code 0.
   *
   * @param input source to compile (not used)
   * @return C source text
   */
  public static String compile(String input) {
    int value = 0;
    if (input != null && !input.isEmpty()) {
      String s = input.trim();
      // Parse a simple expression with + and - operators. For each term we
      // accept only a leading integer and ignore any trailing suffix (e.g. "5I32").
      java.util.regex.Pattern leadingInt = java.util.regex.Pattern.compile("^[+-]?\\d+");

      int pos = 0;
      java.util.regex.Matcher m = leadingInt.matcher(s);
      if (m.find()) {
        try {
          value = Integer.parseInt(m.group());
        } catch (NumberFormatException e) {
          value = 0;
        }
        pos = m.end();
      } else {
        // no leading integer -> value stays 0
        pos = 0;
      }

      // process remaining + or - operations
      while (pos < s.length()) {
        // skip whitespace
        while (pos < s.length() && Character.isWhitespace(s.charAt(pos)))
          pos++;
        if (pos >= s.length())
          break;
        char op = s.charAt(pos);
        if (op != '+' && op != '-')
          break;
        pos++;
        // skip whitespace
        while (pos < s.length() && Character.isWhitespace(s.charAt(pos)))
          pos++;
        if (pos >= s.length())
          break;

        java.util.regex.Matcher m2 = leadingInt.matcher(s.substring(pos));
        if (m2.find()) {
          try {
            int num = Integer.parseInt(m2.group());
            if (op == '+')
              value += num;
            else
              value -= num;
          } catch (NumberFormatException e) {
            // ignore overflow and continue
          }
          pos += m2.end();
        } else {
          // no integer after operator; stop parsing
          break;
        }
      }
    }

    StringBuilder sb = new StringBuilder();
    sb.append("#include <stdlib.h>\n");
    sb.append("int main(void) {\n");
    sb.append("    return ").append(value).append(";\n");
    sb.append("}\n");
    return sb.toString();
  }
}
