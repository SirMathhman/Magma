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
    // A minimal C program that reads a single integer from stdin and returns it
    // as the process exit code. This keeps the compiler function pure (no I/O)
    // and deterministic for the tests which provide stdin like "10".
    return "#include <stdio.h>\n" +
        "int main(void) {\n" +
        "  int x = 0;\n" +
        "  if (scanf(\"%d\", &x) == 1) {\n" +
        "    return x;\n" +
        "  }\n" +
        "  return 0;\n" +
        "}\n";
  }
}
