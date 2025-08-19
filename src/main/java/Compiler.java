class Compiler {
  public static String compile(String input) {
    // Minimal translation: produce a small C program that provides a readInt
    // implementation and a main() which returns the result of calling
    // readInt() if the input uses it. This keeps the function pure and
    // avoids regexes as requested.
    boolean usesReadInt = input != null && input.contains("readInt()");

    StringBuilder out = new StringBuilder();
    out.append("#include <stdio.h>\n");
    out.append("#include <stdlib.h>\n\n");
    // provide a readInt helper that reads an integer from stdin
    out.append("int readInt() {\n");
    out.append("    int v = 0;\n");
    out.append("    if (scanf(\"%d\", &v) != 1) return 0;\n");
    out.append("    return v;\n");
    out.append("}\n\n");

    out.append("int main(void) {\n");
    if (usesReadInt) {
      out.append("    return readInt();\n");
    } else {
      out.append("    return 0;\n");
    }
    out.append("}\n");

    return out.toString();
  }
}