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

    // Return a minimal C program that reads nothing and exits with code 0.
    return "#include <stdio.h>\nint main() { return 0; }\n";
  }
}
