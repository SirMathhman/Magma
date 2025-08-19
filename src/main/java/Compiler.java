class Compiler {
  public static String compile(String input) {
    // Interpret the input as either a decimal integer literal or a
    // simple addition expression like "a + b". Empty or invalid -> 0.
    int ret = 0;
    if (input != null) {
      String s = input.trim();
      if (!s.isEmpty()) {
        // Handle simple addition without using regex.
        int plusIdx = s.indexOf('+');
        if (plusIdx >= 0) {
          String left = s.substring(0, plusIdx).trim();
          String right = s.substring(plusIdx + 1).trim();
          try {
            int l = Integer.parseInt(left);
            int r = Integer.parseInt(right);
            ret = l + r;
          } catch (NumberFormatException e) {
            // fall through to default 0
          }
        } else {
          try {
            ret = Integer.parseInt(s);
          } catch (NumberFormatException e) {
            // leave ret = 0
          }
        }
      }
    }

    return "#include <stdio.h>\n" +
        "int main(void) {\n" +
        "  return " + ret + ";\n" +
        "}\n";
  }
}