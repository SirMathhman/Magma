class Compiler {
  public static String compile(String input) {
    // Interpret the input as a decimal integer literal and produce a C
    // program that returns that integer. Empty or invalid input -> 0.
    int ret = 0;
    if (input != null) {
      String s = input.trim();
      if (!s.isEmpty()) {
        try {
          ret = Integer.parseInt(s);
        } catch (NumberFormatException e) {
          // leave ret = 0 for invalid numbers
        }
      }
    }

    return "#include <stdio.h>\n" +
        "int main(void) {\n" +
        "  return " + ret + ";\n" +
        "}\n";
  }
}