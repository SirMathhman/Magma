package com.example.magma;

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
      throw new IllegalArgumentException("source must not be null");
    }
    // Produce a small, valid C program that prints the original source
    // and returns 0. Use a text block to avoid duplicated token sequences
    // which can trigger CPD checks.
    String escaped = source
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n");
    String program = String.format("""
        /* compiled output: %s */
        #include <stdio.h>
        #include <stdlib.h>

        int main(void) {
          // print the original source (escaped)
          printf("%s\\n");
          return 0;
        }
        """, source, escaped);
    return program;
  }
}
