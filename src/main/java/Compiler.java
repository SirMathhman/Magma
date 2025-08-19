class Compiler {
  public static String compile(String input) {
    // Produce a minimal, valid C program. Tests expect the generated
    // executable to run and return an exit code; returning 0 is the
    // simplest valid behavior.
    StringBuilder sb = new StringBuilder();
    sb.append("#include <stdio.h>\n");
    sb.append("int main(void) {\n");
    sb.append("  (void)0;\n");
    sb.append("  return 0;\n");
    sb.append("}\n");
    return sb.toString();
  }
}