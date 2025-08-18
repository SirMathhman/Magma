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
      // accept a leading integer and ignore trailing suffix (e.g. "5I32")
      java.util.regex.Matcher m = java.util.regex.Pattern.compile("^[+-]?\\d+").matcher(s);
      if (m.find()) {
        try {
          value = Integer.parseInt(m.group());
        } catch (NumberFormatException e) {
          value = 0;
        }
      } else {
        value = 0;
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
