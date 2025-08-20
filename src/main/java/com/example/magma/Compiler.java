package com.example.magma;

import java.util.Optional;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

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
    Map<String, String> types = new HashMap<>();
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
      // basic Bool assignments are booleans. Also record the declared
      // or inferred type for later assignment checks.
      String declaredType;
      if (!typeStr.isEmpty()) {
        declaredType = typeStr;
        if ("I32".equals(typeStr)) {
          if ("true".equals(initExpr) || "false".equals(initExpr)) {
            throw new CompileException("Type mismatch: cannot assign boolean to I32");
          }
        } else if ("Bool".equals(typeStr)) {
          if (!("true".equals(initExpr) || "false".equals(initExpr))) {
            throw new CompileException("Type mismatch: Bool must be assigned a boolean literal");
          }
        }
      } else {
        // Infer type from initializer: boolean literals -> Bool, otherwise I32
        if ("true".equals(initExpr) || "false".equals(initExpr)) {
          declaredType = "Bool";
        } else {
          declaredType = "I32";
        }
      }

      // Map boolean literals to integers for C
      if ("true".equals(initExpr)) {
        initExpr = "1";
      } else if ("false".equals(initExpr)) {
        initExpr = "0";
      }

      // Validate any identifiers referenced in the initializer are declared
      // (e.g. `let x = y;` where y must already be declared).
      validateIdentifiers(initExpr, declared);

      if (declared.contains(name)) {
        throw new CompileException("Duplicate variable: " + name);
      }
      declared.add(name);
      if (isMutable) {
        mutable.add(name);
      }
      types.put(name, declaredType);
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
          String rhs = stmt.substring(assignIdx + 1).trim();
          validateAssignment(lhs, rhs, declared, mutable, types);
        }
        // For non-assignment expression statements, ensure identifiers exist
        if (stmt.indexOf('=') < 0) {
          validateIdentifiers(stmt, declared);
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
        String rhs = finalExpr.substring(assignIdx + 1).trim();
        validateAssignment(lhs, rhs, declared, mutable, types);
      }
      // Validate identifiers used in the final expression as well.
      validateIdentifiers(finalExpr, declared);
    }

    // If the final expression is a single bare identifier (e.g. "readInt")
    // then it must refer to a declared variable. Reject bare function
    // references or undeclared identifiers rather than emitting invalid C
    // (which would produce a compiler/runtime error).
    String feTrim = finalExpr.trim();
    if (!feTrim.isEmpty()) {
      // detect a single identifier token: starts with letter/_ then letters/digits/_
      // only
      char first = feTrim.charAt(0);
      if (Character.isLetter(first) || first == '_') {
        boolean allIdent = true;
        for (int i = 1; i < feTrim.length(); i++) {
          char c = feTrim.charAt(i);
          if (!(Character.isLetterOrDigit(c) || c == '_')) {
            allIdent = false;
            break;
          }
        }
        if (allIdent) {
          // it's a bare identifier token; ensure it's a declared variable
          if (!declared.contains(feTrim)) {
            throw new CompileException("Use of undefined identifier: " + feTrim);
          }
        }
      }
    }

    return preMain +
        "int main(void) {\n" +
        decls.toString() +
        body.toString() +
        "  return (" + finalExpr + ");\n" +
        "}\n";
  }

  private static void validateAssignment(String lhs, String rhs, Set<String> declared, Set<String> mutable,
      Map<String, String> types) {
    if (!declared.contains(lhs)) {
      throw new CompileException("Assignment to undeclared variable: " + lhs);
    }
    if (!mutable.contains(lhs)) {
      throw new CompileException("Assignment to immutable variable: " + lhs);
    }
    // Ensure any identifiers referenced on the RHS are declared.
    validateIdentifiers(rhs, declared);
    String declaredType = types.get(lhs);
    if (declaredType != null) {
      if ("I32".equals(declaredType)) {
        if ("true".equals(rhs) || "false".equals(rhs)) {
          throw new CompileException("Type mismatch: cannot assign boolean to I32");
        }
      } else if ("Bool".equals(declaredType)) {
        if (!("true".equals(rhs) || "false".equals(rhs))) {
          throw new CompileException("Type mismatch: Bool must be assigned a boolean literal");
        }
      }
    }
  }

  private static void validateIdentifiers(String expr, Set<String> declared) {
    if (expr == null || expr.trim().isEmpty()) {
      return;
    }
    // Scan the string for identifier-like tokens (start with letter or '_', then
    // letters/digits/_)
    int n = expr.length();
    StringBuilder token = new StringBuilder();
    for (int i = 0; i <= n; i++) {
      char c = i < n ? expr.charAt(i) : '\0';
      if (Character.isLetter(c) || c == '_') {
        token.append(c);
        continue;
      }
      if (Character.isDigit(c)) {
        if (token.length() > 0) {
          token.append(c);
          continue;
        }
        // digit starting token -> numeric literal, skip
        while (i < n && Character.isDigit(expr.charAt(i)))
          i++;
        i--; // adjust because loop will i++
        token.setLength(0);
        continue;
      }
      // non-identifier char ends a token
      if (token.length() > 0) {
        String t = token.toString();
        // Check whether this identifier is immediately used as a call, i.e.
        // followed (possibly after whitespace) by '('. If a declared variable
        // is used as a function call, that's a compile error (e.g. let x=1; x()).
        boolean isCall = false;
        int j = i;
        while (j < n && Character.isWhitespace(expr.charAt(j)))
          j++;
        if (j < n && expr.charAt(j) == '(') {
          isCall = true;
        }

        // allowed identifiers: boolean literals and builtins
        if (!"true".equals(t) && !"false".equals(t) && !"readInt".equals(t)) {
          if (!declared.contains(t)) {
            throw new CompileException("Use of undefined identifier: " + t);
          }
        }

        // If this token is a call and the identifier is a declared variable,
        // that's an error: calling a non-function.
        if (isCall && declared.contains(t)) {
          throw new CompileException("Call of non-function identifier: " + t);
        }

        token.setLength(0);
      }
      // otherwise skip this character
    }
  }
}
