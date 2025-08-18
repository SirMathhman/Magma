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
      // If the input contains '+' treat it as a sum of terms. For each term,
      // accept only a leading integer and ignore any trailing suffix (e.g. "5I32").
      String[] parts = s.split("\\+");
      java.util.regex.Pattern leadingInt = java.util.regex.Pattern.compile("^[+-]?\\d+");
      for (String part : parts) {
        java.util.regex.Matcher m = leadingInt.matcher(part.trim());
        if (m.find()) {
          try {
            value += Integer.parseInt(m.group());
          } catch (NumberFormatException e) {
            // ignore overflow
          }
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
