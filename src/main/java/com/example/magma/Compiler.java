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
    String src = stripPrelude(Optional.ofNullable(source).orElse(""));
    String body = src.replace("readInt()", "read_int()");

    StringBuilder sb = new StringBuilder();
    sb.append("#include <stdio.h>\n");
    sb.append("int read_int(void) { int x = 0; if (scanf(\"%d\", &x) == 1) return x; return 0; }\n");
    sb.append("int main(void) {\n");

    String finalExpr = processLets(body, sb);

    sb.append("  int result = (").append(finalExpr).append(");\n");

    sb.append("  return result;\n");
    sb.append("}\n");
    return sb.toString();
  }

  private static String stripPrelude(String src) {
    String prelude = "extern fn readInt() : I32;";
    int pIdx = src.indexOf(prelude);
    if (pIdx != -1) {
      src = src.substring(0, pIdx) + src.substring(pIdx + prelude.length());
    }
    return src.trim();
  }

  private static String processLets(String body, StringBuilder sb) {
    if (!body.startsWith("let ")) {
      return body.isEmpty() ? "0" : body;
    }

    String remaining = body;
    String lastVar = "_tmp";
    while (remaining.startsWith("let ")) {
      int semi = remaining.indexOf(';');
      if (semi == -1) {
        // malformed remainder, stop processing lets
        remaining = "";
        break;
      }
      String decl = remaining.substring(0, semi + 1).trim();
      remaining = remaining.substring(semi + 1).trim();

      int eq = decl.indexOf('=');
      int varStart = 4; // after "let "
      int colon = decl.indexOf(':', varStart);
      String varName = (colon != -1)
          ? decl.substring(varStart, colon).trim()
          : (eq != -1 ? decl.substring(varStart, eq).trim() : "_tmp");

      String rhs = "0";
      if (eq != -1) {
        rhs = decl.substring(eq + 1, decl.length() - 1).trim();
        if (rhs.isEmpty()) {
          rhs = "0";
        }
      }

      sb.append("  int ").append(varName).append(" = (").append(rhs).append(");\n");
      lastVar = varName;
    }
    return remaining.isEmpty() ? lastVar : remaining;
  }
}
