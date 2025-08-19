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
    String text = Optional.ofNullable(source).orElse("");
    String escaped = text.replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n");

    // Generated C tries to parse 'a + b' first, then a single integer, then
    // falls back to 0. This keeps Java logic minimal and avoids duplicated
    // fragments that trigger CPD.
    return String.format("""
        /* compiled output: %s */
        #include <stdlib.h>
        #include <stdio.h>

        int main(void) {
          int a = 0, b = 0;
          if (sscanf("%s", " %%d + %%d", &a, &b) == 2) {
            return a + b;
          }
          if (sscanf("%s", " %%d", &a) == 1) {
            return a;
          }
          return 0;
        }
        """, text, escaped, escaped);
  }
  // No extra helpers required; compile() returns a compact C program.
}
