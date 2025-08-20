package com.example.magma;

import java.util.Optional;

public final class Compiler {
  public static String compile(String source) {
    // Find the last non-empty trimmed line using pure string operations
    // (no IO utilities, no regexes).
    String lastLine = "";
    StringBuilder cur = new StringBuilder();
    for (int i = 0, n = source.length(); i < n; i++) {
      char c = source.charAt(i);
      if (c == '\r' || c == '\n') {
        String trimmed = cur.toString().trim();
        if (!trimmed.isEmpty()) {
          lastLine = trimmed;
        }
        cur.setLength(0);
      } else {
        cur.append(c);
      }
    }
    // Last segment after final newline (or whole string if no newlines).
    if (cur.length() > 0) {
      String trimmed = cur.toString().trim();
      if (!trimmed.isEmpty()) {
        lastLine = trimmed;
      }
    }

    // Remove a trailing semicolon if present.
    if (lastLine.endsWith(";")) {
      lastLine = lastLine.substring(0, lastLine.length() - 1);
    }

    String expr = Optional.of(lastLine).filter(s -> !s.isEmpty()).orElse("0");

    // Produce a minimal C program. We always provide a definition for
    // readInt() that reads an integer from stdin. The generated main will
    // return the value of the parsed expression.
    return "#include <stdio.h>\n" +
        "int readInt(void) {\n" +
        "  int x = 0;\n" +
        "  if (scanf(\"%d\", &x) != 1) return 0;\n" +
        "  return x;\n" +
        "}\n\n" +
        "int main(void) {\n" +
        "  return (" + expr + ");\n" +
        "}\n";
  }
}
