package com.example.magma;

import java.util.Optional;

/**
 * Simple Compiler utility.
 * Provides a pure function-like method to "compile" a string input to a string
 * output.
 */
public final class Compiler {
  private Compiler() {
    // utility class - prevent instantiation
  }

  /**
   * Compile the given source text to a result string.
   * This implementation is intentionally trivial: it returns the input wrapped to
   * indicate compilation.
   *
   * @param source non-null source text
   * @return compiled representation
   */
  public static String compile(String source) {
    if (source == null) {
      source = "";
    }

    // Remove a simple prelude declaration like: extern fn readInt() : I32;
    String body = source.replaceAll("(?m)extern\\s+fn\\s+readInt\\s*\\(\\s*\\)\\s*:\\s*I32\\s*;", "");
    body = body.trim();

    // Map the language-level readInt() to a C helper read_int()
    body = body.replace("readInt()", "read_int()");

    // Emit a tiny C program that defines read_int(), evaluates the expression
    // and returns the result as the process exit code.
    StringBuilder sb = new StringBuilder();
    sb.append("#include <stdio.h>\n");
    sb.append("int read_int(void) { int x = 0; if (scanf(\"%d\", &x) == 1) return x; return 0; }\n");
    sb.append("int main(void) {\n");
    sb.append("  int result = (");
    sb.append(body.isEmpty() ? "0" : body);
    sb.append(");\n");
    sb.append("  return result;\n");
    sb.append("}\n");
    return sb.toString();
  }
}
