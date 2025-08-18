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
      try {
        value = Integer.parseInt(input.trim());
      } catch (NumberFormatException e) {
        // non-integer input -> default to 0
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
