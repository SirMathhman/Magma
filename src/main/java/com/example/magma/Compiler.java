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
    String src = Optional.ofNullable(source).orElse("");

    // Extract the final expression after the last semicolon. This keeps the
    // generated C simple: we only try to evaluate the last expression in the
    // source (the prelude may contain declarations that shouldn't be parsed
    // as the expression to evaluate).
    String expr = src.trim();
    int lastSemi = expr.lastIndexOf(';');
    if (lastSemi >= 0 && lastSemi + 1 < expr.length()) {
      expr = expr.substring(lastSemi + 1).trim();
    }

    String escaped = expr.replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n");

    // Generated C evaluates a few simple expression shapes. It also supports
    // the special readInt() name which reads a single integer from stdin.
    return String.format("""
        /* compiled output: %s */
        #include <stdlib.h>
        #include <stdio.h>
        #include <string.h>

        int main(void) {
          int a = 0, b = 0;
          /* handle readInt() which reads a single int from stdin */
          if (strcmp("%2$s", "readInt()") == 0) {
            int v = 0;
            if (scanf("%%d", &v) == 1) {
              return v;
            }
            return 0;
          }
          if (sscanf("%2$s", " %%d + %%d", &a, &b) == 2) {
            return a + b;
          }
          if (sscanf("%2$s", " %%d - %%d", &a, &b) == 2) {
            return a - b;
          }
          if (sscanf("%2$s", " %%d * %%d", &a, &b) == 2) {
            return a * b;
          }
          if (sscanf("%2$s", " %%d", &a) == 1) {
            return a;
          }
          return 0;
        }
        """, src, escaped);
  }
  // No extra helpers required; compile() returns a compact C program.
}
