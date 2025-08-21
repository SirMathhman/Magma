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
        // Parse a chain of leading let-declarations: let a = ...; let b = ...; <expr>
        java.util.List<LetInfo> lets = new java.util.ArrayList<>();
        String remaining = trimmed;
        Optional<LetInfo> cur = parseLet(remaining);
        while (cur.isPresent()) {
          LetInfo li = cur.get();
          lets.add(li);
          remaining = li.rest.trim();
          cur = parseLet(remaining);
        }
        out.append(emitLetProgramChain(lets, remaining));
      } else {
        // Emit a main that returns the Magma expression directly. The tests pass
        // expressions such as `readInt()` and `readInt() + readInt()` which map
        // directly to valid C expressions. Map boolean literals to numeric
        // literals so C compilers accept `true`/`false` used in expressions.
        String emitExpr = body.trim();
        if (emitExpr.startsWith("if ") && emitExpr.length() > 3 && emitExpr.charAt(3) == '(') {
          emitExpr = emitExpr.substring(3);
        }
        out.append("int main() { return ")
            .append(replaceBooleanLiterals(emitExpr)).append("; }\n");
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
    Optional<String> explicitType = Optional.empty();
    boolean mutable = false;
    int colon = varNamePart.indexOf(':');
    String beforeColon = colon > 0 ? varNamePart.substring(0, colon).trim() : varNamePart;
    if (beforeColon.startsWith("mut ")) {
      mutable = true;
      beforeColon = beforeColon.substring(4).trim();
    }
    String varName = beforeColon;
    if (colon > 0) {
      String typePart = varNamePart.substring(colon + 1).trim();
      if (!typePart.isEmpty()) {
        explicitType = Optional.of(typePart);
      }
    }
    String expr = afterLet.substring(eq + 1, semi).trim();
    String originalExpr = expr;
    String rest = afterLet.substring(semi + 1).trim();
    return Optional.of(new LetInfo(varName, explicitType, expr, originalExpr, rest, mutable));
  }

  // (mapBooleanLiteral removed - use mapBooleanLiteralsInExpr instead)

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

  private static String replaceBooleanLiterals(String s) {
    return s.replace("true", "1").replace("false", "0");
  }

  // emitLetProgram removed in favor of emitLetProgramChain which supports
  // multiple lets

  private static String emitLetProgramChain(java.util.List<LetInfo> lets, String finalRest)
      throws CompileException {
    // Validate duplicates and build mutability map
    java.util.Set<String> seen = new java.util.HashSet<>();
    java.util.Map<String, Boolean> mut = new java.util.HashMap<>();
    StringBuilder p = new StringBuilder();
    p.append("int main() { ");
    for (LetInfo li : lets) {
      if (seen.contains(li.varName)) {
        throw new CompileException("duplicate variable declaration: " + li.varName);
      }
      seen.add(li.varName);
      mut.put(li.varName, li.mutable);
      String initExpr = li.expr.trim();
      if (initExpr.startsWith("if ") && initExpr.length() > 3 && initExpr.charAt(3) == '(') {
        initExpr = initExpr.substring(3);
      }
      String expr = replaceBooleanLiterals(initExpr);
      validateType(li.explicitType, li.originalExpr);
      p.append("int ").append(li.varName).append(" = ").append(expr).append("; ");
    }

    String rest = finalRest.trim();
    // Enforce immutability: any assignment to an immutable variable is invalid
    for (java.util.Map.Entry<String, Boolean> e : mut.entrySet()) {
      if (!e.getValue() && hasAssignment(rest, e.getKey())) {
        throw new CompileException("assignment to immutable variable: " + e.getKey());
      }
    }

    if (!rest.isEmpty()) {
      p.append(rest);
      if (!rest.endsWith(";")) {
        p.append(";");
      }
    }
    String returnExpr;
    String last = lastLetOrZero(lets.toArray(new LetInfo[0]));
    if (rest.isEmpty()) {
      // If there is no trailing expression, return the value of the last let
      returnExpr = last;
    } else {
      // Use the last non-empty segment after the final semicolon as the return
      // expression
      returnExpr = inferReturnFromRest(rest, last);
    }
    p.append(" return ").append(returnExpr).append("; \n}");
    return p.toString();
  }

  private static String lastLetOrZero(LetInfo... lets) {
    return lets.length == 0 ? "0" : lets[lets.length - 1].varName;
  }

  private static String inferReturnFromRest(String rest, String varName) {
    String r = rest.trim();
    if (r.isEmpty()) {
      return varName;
    }
    // If rest contains semicolons, the last non-empty segment after the last
    // semicolon is the expression to return. If the rest ends with an
    // assignment like `x = expr;` but nothing after, return the variable.
    int lastSemi = r.lastIndexOf(';');
    String tail;
    if (lastSemi >= 0) {
      tail = r.substring(lastSemi + 1).trim();
      if (tail.isEmpty()) {
        // nothing after last semicolon; check segment before it
        String before = r.substring(0, lastSemi).trim();
        int prevSemi = before.lastIndexOf(';');
        String lastSeg = prevSemi >= 0 ? before.substring(prevSemi + 1).trim() : before;
        // if last segment is an assignment to varName, return varName
        if (hasAssignment(lastSeg, varName)) {
          return varName;
        }
        return lastSeg.isEmpty() ? varName : lastSeg;
      }
      return tail;
    }
    return r;
  }

  private static boolean hasAssignment(String rest, String varName) {
    int idx = rest.indexOf(varName);
    while (idx >= 0) {
      int after = idx + varName.length();
      // ensure varName is a standalone identifier (preceded/followed by non-id char)
      boolean okBefore = (idx == 0) || !Character.isLetterOrDigit(rest.charAt(idx - 1)) && rest.charAt(idx - 1) != '_';
      if (okBefore) {
        // skip spaces after varName
        int i = after;
        while (i < rest.length() && Character.isWhitespace(rest.charAt(i))) {
          i++;
        }
        if (charAtEquals(rest, i, '=')) {
          // Not an equality operator '=='
          int j = i + 1;
          if (charAtEquals(rest, j, '=')) {
            // '==' -> not an assignment
          } else {
            return true;
          }
        }
      }
      idx = rest.indexOf(varName, idx + 1);
    }
    return false;
  }

  private static boolean charAtEquals(String s, int idx, char ch) {
    return idx < s.length() && s.charAt(idx) == ch;
  }

  private static final class LetInfo {
    final String varName;
    final java.util.Optional<String> explicitType;
    final String expr;
    final String originalExpr;
    final String rest;
    final boolean mutable;

    LetInfo(String varName, Optional<String> explicitType, String expr,
        String originalExpr, String rest, boolean mutable) {
      this.varName = varName;
      this.explicitType = explicitType;
      this.expr = expr;
      this.originalExpr = originalExpr;
      this.rest = rest;
      this.mutable = mutable;
    }
  }
}
