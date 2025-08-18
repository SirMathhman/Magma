public class Compiler {
  /**
   * Compile the given source string into a C program. For now this returns
   * a minimal C program that simply exits with code 0.
   *
   * @param input source to compile (not used)
   * @return C source text
   */
  public static String compile(String input) {
    StringBuilder sb = new StringBuilder();
    sb.append("#include <stdio.h>\n");
    sb.append("int main(void) {\n");
    sb.append("    (void)printf(\"\");\n");
    sb.append("    return 0;\n");
    sb.append("}\n");
    return sb.toString();
  }
}
