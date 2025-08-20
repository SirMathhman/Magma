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

    // If the expression is a simple let-binding like
    // let x = <init>; <rest>
    // translate it into C by declaring the variable then returning the
    // rest expression. We only use plain string operations (no regex).
    String preMain = "#include <stdio.h>\n" +
        "int readInt(void) {\n" +
        "  int x = 0;\n" +
        "  if (scanf(\"%d\", &x) != 1) return 0;\n" +
        "  return x;\n" +
        "}\n\n";

    // Support multiple sequential let-bindings such as
    // let x = ...; let y = ...; expr
    // by extracting each binding and emitting a C declaration before the
    // final return. Use only string operations.
    StringBuilder decls = new StringBuilder();
    while (expr.startsWith("let ")) {
      int eq = expr.indexOf('=');
      int sem = expr.indexOf(';', eq >= 0 ? eq : 0);
      if (!(eq > 0 && sem > eq)) {
        break; // malformed let; fall back to returning whatever remains
      }
      String name = expr.substring(4, eq).trim();
      String initExpr = expr.substring(eq + 1, sem).trim();
      decls.append("  int ").append(name).append(" = (").append(initExpr).append(");\n");
      expr = expr.substring(sem + 1).trim();
    }

    String finalExpr = expr.isEmpty() ? "0" : expr;
    return preMain +
        "int main(void) {\n" +
        decls.toString() +
        "  return (" + finalExpr + ");\n" +
        "}\n";
  }
}
