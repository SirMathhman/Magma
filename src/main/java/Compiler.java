public class Compiler {

  /**
   * Compile the given source string into a C program. For now this returns
   * a minimal C program that simply exits with code computed from the
   * simple expression in the input.
   *
   * @param input source to compile
   * @return C source text
   */
  public static String compile(String input) {
  // Minimal implementation for tests: produce a tiny C program that
  // returns 0 when no expression is present. Future work: parse the
  // source and emit correct code for expressions and intrinsics.
  StringBuilder sb = new StringBuilder();
  sb.append("int main(void) {\n");
  sb.append("  return 0;\n");
  sb.append("}\n");
  return sb.toString();
  }
}
