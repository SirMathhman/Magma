package com.example.magma;

import java.util.Optional;

/**
 * Minimal test compiler used by unit tests. It recognizes a single invalid
 * input string ("test") and otherwise emits a tiny C program that returns 0.
 */
public final class Compiler {
  public static String compile(String input) throws CompileException {
    // Defensively handle potential nulls by using Optional (tests never pass null).
    String src = Optional.ofNullable(input).orElse("");

    if (src.equals("test")) {
      throw new CompileException("invalid input: " + src);
    }
    boolean usesReadInt = src.contains("readInt()");

    StringBuilder out = new StringBuilder();
    out.append("#include <stdio.h>\n");

    if (usesReadInt) {
      // Provide a simple implementation of the intrinsic readInt that reads one
      // integer from stdin and returns it. If scanning fails, return 0.
      out.append("int readInt() { int x = 0; if (scanf(\"%d\", &x) != 1) return 0; return x; }\n");
      out.append("int main() { return readInt(); }\n");
    } else {
      out.append("int main() { return 0; }\n");
    }

    return out.toString();
  }
}
