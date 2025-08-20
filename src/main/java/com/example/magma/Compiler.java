package com.example.magma;

import java.util.Optional;
import java.util.HashSet;
import java.util.Set;

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
    Set<String> declared = new HashSet<>();
    Set<String> mutable = new HashSet<>();
    while (expr.startsWith("let ")) {
      int eq = expr.indexOf('=');
      int sem = expr.indexOf(';', eq >= 0 ? eq : 0);
      if (!(eq > 0 && sem > eq)) {
        break; // malformed let; fall back to returning whatever remains
      }
      String namePart = expr.substring(4, eq).trim();
      boolean isMutable = false;
      if (namePart.startsWith("mut ")) {
        isMutable = true;
        namePart = namePart.substring(4).trim();
      }
      int colonIdx = namePart.indexOf(':');
      String name = colonIdx >= 0 ? namePart.substring(0, colonIdx).trim() : namePart;
      String typeStr = colonIdx >= 0 ? namePart.substring(colonIdx + 1).trim() : "";
      String initExpr = expr.substring(eq + 1, sem).trim();

      // Simple type checks: ensure booleans aren't assigned to I32 and
      // basic Bool assignments are booleans.
      if (!typeStr.isEmpty()) {
        if ("I32".equals(typeStr)) {
          if ("true".equals(initExpr) || "false".equals(initExpr)) {
            throw new CompileException("Type mismatch: cannot assign boolean to I32");
          }
        } else if ("Bool".equals(typeStr)) {
          if (!("true".equals(initExpr) || "false".equals(initExpr))) {
            throw new CompileException("Type mismatch: Bool must be assigned a boolean literal");
          }
        }
      }

      // Map boolean literals to integers for C
      if ("true".equals(initExpr)) {
        initExpr = "1";
      } else if ("false".equals(initExpr)) {
        initExpr = "0";
      }

      if (declared.contains(name)) {
        throw new CompileException("Duplicate variable: " + name);
      }
      declared.add(name);
      if (isMutable) {
        mutable.add(name);
      }
      decls.append("  int ").append(name).append(" = (").append(initExpr).append(");\n");
      expr = expr.substring(sem + 1).trim();
    }

    // The final expression and body will be computed below after handling
    // statements; leave expr intact for parsing.
    // Split remaining expr into statements separated by ';'. Any statements
    // (assignments) before the final expression are emitted as statements in
    // the function body; the last segment (if any) becomes the returned
    // expression. Validate assignments against mutability rules.
    StringBuilder body = new StringBuilder();
    String remaining = expr;
    String lastSegment = null;
    while (true) {
      int idx = remaining.indexOf(';');
      if (idx < 0) {
        lastSegment = remaining.trim();
        break;
      }
      String stmt = remaining.substring(0, idx).trim();
      if (!stmt.isEmpty()) {
        // validate assignment statements like: name = expr
        int assignIdx = stmt.indexOf('=');
        if (assignIdx > 0) {
          String lhs = stmt.substring(0, assignIdx).trim();
          validateAssignment(lhs, declared, mutable);
        }
        body.append("  ").append(stmt).append(";\n");
      }
      remaining = remaining.substring(idx + 1).trim();
      if (remaining.isEmpty()) {
        lastSegment = "";
        break;
      }
    }

    String finalExpr = (lastSegment == null || lastSegment.isEmpty()) ? "0" : lastSegment;
    if ("true".equals(finalExpr)) {
      finalExpr = "1";
    } else if ("false".equals(finalExpr)) {
      finalExpr = "0";
    } else {
      // If final expression is an assignment, validate mutability too.
      int assignIdx = finalExpr.indexOf('=');
      if (assignIdx > 0) {
        String lhs = finalExpr.substring(0, assignIdx).trim();
        validateAssignment(lhs, declared, mutable);
      }
    }

    return preMain +
        "int main(void) {\n" +
        decls.toString() +
        body.toString() +
        "  return (" + finalExpr + ");\n" +
        "}\n";
  }

  private static void validateAssignment(String lhs, Set<String> declared, Set<String> mutable) {
    if (!declared.contains(lhs)) {
      throw new CompileException("Assignment to undeclared variable: " + lhs);
    }
    if (!mutable.contains(lhs)) {
      throw new CompileException("Assignment to immutable variable: " + lhs);
    }
  }
}
