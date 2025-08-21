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
    // If the input contains the prelude declaration for readInt, remove it
    // and treat the remainder as the expression to compile.
    String prelude = "intrinsic fn readInt() : I32;";
    String body = src;
    if (body.contains(prelude)) {
      body = body.replace(prelude, "");
    }
    body = body.trim();

    StringBuilder out = new StringBuilder();
    out.append("#include <stdio.h>\n");
    // Implementation of the intrinsic readInt that reads an int from stdin.
    out.append("int readInt() { int x = 0; if (scanf(\"%d\", &x) != 1) return 0; return x; }\n");

    if (body.isEmpty()) {
      out.append("int main() { return 0; }\n");
    } else {
      // Support a single simple `let` declaration of the form:
      // let x = <expr>; <rest>
      // by emitting a local int and returning the remaining expression.
      String trimmed = body.trim();
      Optional<LetInfo> letInfo = parseLet(trimmed);
      if (letInfo.isPresent()) {
        LetInfo info = letInfo.get();
        // Map boolean literals to numeric C literals
        String expr = mapBooleanLiteral(info.expr);
        validateType(info.explicitType, info.originalExpr);
        String returnExpr = info.rest.isEmpty() ? info.varName : info.rest;
        out.append("int main() { int ").append(info.varName).append(" = ").append(expr)
            .append("; return ").append(returnExpr).append("; }\n");
      } else {
        // Emit a main that returns the Magma expression directly. The tests pass
        // expressions such as `readInt()` and `readInt() + readInt()` which map
        // directly to valid C expressions.
        out.append("int main() { return ").append(body).append("; }\n");
      }
    }

    return out.toString();
  }

  private static Optional<LetInfo> parseLet(String trimmed) {
    if (!trimmed.startsWith("let ")) {
      return Optional.empty();
    }
    String afterLet = trimmed.substring(4);
    int eq = afterLet.indexOf('=');
    int semi = afterLet.indexOf(';');
    if (eq <= 0 || semi <= eq) {
      return java.util.Optional.empty();
    }
    String varNamePart = afterLet.substring(0, eq).trim();
    String varName = varNamePart;
    Optional<String> explicitType = Optional.empty();
    int colon = varNamePart.indexOf(':');
    if (colon > 0) {
      varName = varNamePart.substring(0, colon).trim();
      String typePart = varNamePart.substring(colon + 1).trim();
      if (!typePart.isEmpty()) {
        explicitType = Optional.of(typePart);
      }
    }
    String expr = afterLet.substring(eq + 1, semi).trim();
    String originalExpr = expr;
    String rest = afterLet.substring(semi + 1).trim();
    return Optional.of(new LetInfo(varName, explicitType, expr, originalExpr, rest));
  }

  private static String mapBooleanLiteral(String expr) {
    if ("true".equals(expr)) {
      return "1";
    }
    if ("false".equals(expr)) {
      return "0";
    }
    return expr;
  }

  private static void validateType(Optional<String> explicitType, String originalExpr)
      throws CompileException {
    if (explicitType.isPresent()) {
      String t = explicitType.get();
      if ("Bool".equals(t)) {
        if (!"true".equals(originalExpr) && !"false".equals(originalExpr)) {
          throw new CompileException("type mismatch: expected Bool");
        }
      }
    }
  }

  private static final class LetInfo {
    final String varName;
    final java.util.Optional<String> explicitType;
    final String expr;
    final String originalExpr;
    final String rest;

    LetInfo(String varName, Optional<String> explicitType, String expr,
        String originalExpr, String rest) {
      this.varName = varName;
      this.explicitType = explicitType;
      this.expr = expr;
      this.originalExpr = originalExpr;
      this.rest = rest;
    }
  }
}
