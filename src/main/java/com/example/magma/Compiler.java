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

    // collect let declarations early so we can emit them inside main after
    // structures (typedefs) are emitted
    String[] letsCollected = collectLets(body);
    String letDecls = letsCollected[0];
    String bodyNoLets = letsCollected[1];

    StringBuilder sb = new StringBuilder();
    sb.append("#include <stdio.h>\n");
    sb.append("int read_int(void) { int x = 0; if (scanf(\"%d\", &x) == 1) return x; return 0; }\n");

    // emit any top-level structures and function definitions first
    String afterStructs = processStructures(bodyNoLets, sb);
    String afterFns = processFunctions(afterStructs, sb);

    sb.append("int main(void) {\n");

    // append collected let declarations inside main
    if (!letDecls.isEmpty()) {
      sb.append(letDecls);
    }

    String finalExpr = afterFns.isEmpty() ? "0" : afterFns;
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

  // processLets was removed; collectLets is used instead to extract declarations

  /**
   * Extract all top-level let declarations from the body. Returns a two-element
   * array where index 0 is the C declarations text (each ending with newline)
   * and index 1 is the remaining body with lets removed.
   */
  private static String[] collectLets(String body) {
    String remaining = body == null ? "" : body.trim();
    StringBuilder decls = new StringBuilder();

    int idx;
    while ((idx = remaining.indexOf("let ")) != -1) {
      int semi = remaining.indexOf(';', idx);
      if (semi == -1) {
        break;
      }
      String decl = remaining.substring(idx, semi + 1).trim();
      int eq = decl.indexOf('=');
      int varStart = 4;
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

      int brace = rhs.indexOf('{');
      if (brace != -1) {
        String structName = rhs.substring(0, brace).trim();
        int endBrace = rhs.lastIndexOf('}');
        String inner = endBrace != -1 ? rhs.substring(brace + 1, endBrace).trim() : "0";
        decls.append("  ").append(structName).append(" ").append(varName).append(" = {").append("(").append(inner)
            .append(")").append("};\n");
      } else {
        decls.append("  int ").append(varName).append(" = (").append(rhs).append(");\n");
      }

      remaining = (remaining.substring(0, idx) + remaining.substring(semi + 1)).trim();
    }

    return new String[] { decls.toString(), remaining };
  }

  private static String processFunctions(String body, StringBuilder sb) {
    String remaining = body;
    while (remaining.startsWith("fn ")) {
      int arrow = remaining.indexOf("=>");
      int semi = remaining.indexOf(';');
      if (arrow == -1 || semi == -1 || arrow > semi) {
        // malformed function, stop
        break;
      }

      // expected form: fn name() : I32 => expr;
      String header = remaining.substring(0, arrow).trim();
      // extract function name
      int nameStart = 3; // after "fn "
      int paren = header.indexOf('(', nameStart);
      String name = (paren != -1) ? header.substring(nameStart, paren).trim() : "_fn";

      String expr = remaining.substring(arrow + 2, semi).trim();
      // map any read_int occurrences already done by caller

      sb.append("int ").append(name).append("(void) { return (").append(expr).append("); }\n");

      remaining = remaining.substring(semi + 1).trim();
    }
    return remaining;
  }

  private static String processStructures(String body, StringBuilder sb) {
    String remaining = body;
    int idx;
    while ((idx = remaining.indexOf("structure ")) != -1) {
      // move to the start of the structure declaration
      remaining = remaining.substring(0, idx) + remaining.substring(idx);
      remaining = remaining.trim();
      int braceOpen = remaining.indexOf('{');
      int braceClose = remaining.indexOf('}');
      int semi = remaining.indexOf(';');
      if (braceOpen == -1 || braceClose == -1) {
        break; // malformed
      }

      String header = remaining.substring(0, braceOpen).trim();
      int nameStart = "structure".length();
      String name = header.substring(nameStart).trim();

      String bodyContent = remaining.substring(braceOpen + 1, braceClose).trim();
      int colon = bodyContent.indexOf(':');
      String fieldName = colon != -1 ? bodyContent.substring(0, colon).trim() : "field";

      sb.append("typedef struct { int ").append(fieldName).append("; } ").append(name).append(";\n");

      int removeEnd = (semi == -1) ? (braceClose + 1) : (semi + 1);
      // remove the processed structure declaration from the remaining text
      remaining = remaining.substring(removeEnd).trim();
    }
    return remaining;
  }
}
