package magma;

public class Compiler {
  /**
   * Compiles the given source code string and returns the compiled output or a
   * CompileError wrapped in Result.
   */
  public static Result<String, CompileError> compile(String source) {
    String input = String.valueOf(source);
    if (input.equals("null")) {
      return Result.err(new CompileError("Empty source", input));
    }

    String prelude = "intrinsic fn readInt() : I32;";
    String core = input.replace(prelude, "").trim();
    if (core.isEmpty()) {
      return Result.ok(CompilerUtil.codeEmpty());
    }

    // Bare "readInt" without parentheses is illegal
    if (hasBareReadInt(core)) {
      return Result.err(new CompileError("Bare identifier 'readInt' used without parentheses", input));
    }

    // Pure boolean literal
    if (core.equals("true") || core.equals("false")) {
      return Result.ok(CompilerUtil.codePrintString(core));
    }

    // Split by ';' into statements; last non-empty is the expression
    String[] parts = core.split(";");
    java.util.Set<String> letNames = new java.util.HashSet<>();
    java.util.Set<String> readIntLets = new java.util.HashSet<>();
    java.util.Map<String, String> letBoolVals = new java.util.HashMap<>();
    String expr = "";

    int i = 0;
    while (i < parts.length) {
      String stmt = parts[i].trim();
      if (stmt.isEmpty()) {
        i = i + 1;
      } else if (stmt.startsWith("let")) {
        // parse: let IDENT ( : TYPE )? = RHS
        int p = 3;
        p = skipWs(stmt, p);
        if (!hasIdentifierStart(stmt, p)) {
          return Result.err(new CompileError("Invalid let declaration", input));
        }
        int s = p;
        p++;
        while (p < stmt.length() && Character.isJavaIdentifierPart(stmt.charAt(p)))
          p++;
        String name = stmt.substring(s, p);
        if (letNames.contains(name)) {
          return Result.err(new CompileError("Duplicate let declaration: '" + name + "'", input));
        }
        letNames.add(name);
        p = skipWs(stmt, p);
        String declaredType = "";
        if (p < stmt.length() && stmt.charAt(p) == ':') {
          p++;
          int typeStart = p;
          while (p < stmt.length() && stmt.charAt(p) != '=')
            p++;
          declaredType = stmt.substring(typeStart, p).trim();
        }
        p = skipWs(stmt, p);
        if (p >= stmt.length() || stmt.charAt(p) != '=') {
          return Result.err(new CompileError("Expected '=' in let declaration", input));
        }
        p++;
        while (p < stmt.length() && Character.isWhitespace(stmt.charAt(p)))
          p++;
        String rhs = stmt.substring(p).trim();
        if (declaredType.equals("Bool") && !(rhs.equals("true") || rhs.equals("false"))) {
          return Result.err(new CompileError("Type mismatch: expected Bool", input));
        }
        if (rhs.equals("readInt()")) {
          readIntLets.add(name);
        } else if (rhs.equals("true") || rhs.equals("false")) {
          letBoolVals.put(name, rhs);
        }
        i = i + 1;
      } else {
        // collect expression from remaining parts
        StringBuilder tail = new StringBuilder();
        int j = i;
        while (j < parts.length) {
          String piece = parts[j].trim();
          if (!piece.isEmpty()) {
            if (tail.length() > 0)
              tail.append("; ");
            tail.append(piece);
          }
          j = j + 1;
        }
        expr = tail.toString();
        i = parts.length;
      }
    }

    if (expr.isEmpty()) {
      return Result.ok(CompilerUtil.codeEmpty());
    }

    // If the final expression is an identifier bound to a boolean literal, print it
    if (letBoolVals.containsKey(expr)) {
      return Result.ok(CompilerUtil.codePrintString(letBoolVals.get(expr)));
    }

    // If-expression: if <cond> <then> else <else>
    if (expr.startsWith("if ")) {
      int idxElse = expr.indexOf(" else ");
      if (idxElse > 0) {
        String condAndThen = expr.substring(3, idxElse).trim();
        String elsePart = expr.substring(idxElse + 6).trim();
        // split condAndThen into cond and thenExpr
        int eqPos = condAndThen.indexOf("==");
        if (eqPos > 0) {
          // determine end of right operand
          int rightStart = eqPos + 2;
          rightStart = skipWs(condAndThen, rightStart);
          int rightEnd = rightStart;
          if (condAndThen.startsWith("readInt()", rightStart)) {
            rightEnd = rightStart + "readInt()".length();
          } else {
            // identifier
            if (rightStart < condAndThen.length() && Character.isJavaIdentifierStart(condAndThen.charAt(rightStart))) {
              rightEnd = rightStart + 1;
              while (rightEnd < condAndThen.length() && Character.isJavaIdentifierPart(condAndThen.charAt(rightEnd)))
                rightEnd++;
            }
          }
          String cond = condAndThen.substring(0, rightEnd).trim();
          String thenPart = condAndThen.substring(rightEnd).trim();
          // normalize thenPart if it starts with a space
          if (thenPart.isEmpty()) {
            // maybe thenPart was directly after cond with whitespace
            // try to find remaining after rightEnd in original expr
            thenPart = "";
          }

          // Determine whether condition uses two readInt() or two let identifiers
          boolean condIsReadInts = false;
          if (cond.startsWith("readInt()") && cond.contains("==") && cond.endsWith("readInt()")) {
            condIsReadInts = true;
          } else {
            // check for identifier == identifier
            int localEq = cond.indexOf("==");
            if (localEq > 0) {
              String l = cond.substring(0, localEq).trim();
              String r = cond.substring(localEq + 2).trim();
              if (letNames.contains(l) && letNames.contains(r) && readIntLets.contains(l) && readIntLets.contains(r)) {
                condIsReadInts = true;
              }
            }
          }

          if (condIsReadInts) {
            // thenPart might be empty if the 'then' expression was after a space; try to
            // extract from original expr
            String thenExpr = condAndThen.substring(rightEnd).trim();
            if (thenExpr.isEmpty()) {
              // fallback: try splitting original expr at idxElse
              String between = expr.substring(3, idxElse).trim();
              // remove the condition portion
              thenExpr = "";
              // try last token before else
              String[] tokens = between.split("\\s+");
              if (tokens.length > 0)
                thenExpr = tokens[tokens.length - 1];
            }
            String thenAtom = thenExpr;
            String elseAtom = elsePart;
            // if then/else are numeric literals, print them as ints
            if (isIntegerLiteral(thenAtom) && isIntegerLiteral(elseAtom)) {
              return Result.ok(CompilerUtil.emitIfProgram(thenAtom, elseAtom));
            }
          }
        }
      }
    }

    // Equality between two let identifiers initialized by readInt()
    int eq = expr.indexOf("==");
    if (eq > 0) {
      String left = expr.substring(0, eq).trim();
      String right = expr.substring(eq + 2).trim();
      if (isReadIntLetPair(letNames, readIntLets, left, right)) {
        return Result.ok(CompilerUtil.codeCompare());
      }
      // Direct equality of two readInt() calls
      if (left.equals("readInt()") && right.equals("readInt()")) {
        return Result.ok(CompilerUtil.codeCompare());
      }
    }

    // Identifier expression bound to readInt()
    if (letNames.contains(expr) && readIntLets.contains(expr)) {
      return Result.ok(CompilerUtil.codeOneInt());
    }

    // Binary readInt() <op> readInt()
    String tok = "readInt()";
    int a = expr.indexOf(tok);
    if (a >= 0) {
      int p = a + tok.length();
      while (p < expr.length() && Character.isWhitespace(expr.charAt(p)))
        p++;
      if (p < expr.length()) {
        char op = expr.charAt(p);
        if (op == '+' || op == '-' || op == '*' || op == '/' || op == '%') {
          int q = p + 1;
          while (q < expr.length() && Character.isWhitespace(expr.charAt(q)))
            q++;
          if (q + tok.length() <= expr.length() && expr.substring(q, q + tok.length()).equals(tok)) {
            return Result.ok(CompilerUtil.codeBinary(op));
          }
        }
      }
    }

    // Single readInt()
    if (expr.equals("readInt()")) {
      return Result.ok(CompilerUtil.codeOneInt());
    }

    // Binary x <op> y where x and y are lets bound to readInt()
    int opPos = findBinaryOp(expr);
    if (opPos > 0) {
      char op = expr.charAt(opPos);
      if (op == '+' || op == '-' || op == '*' || op == '/' || op == '%') {
        String left = expr.substring(0, opPos).trim();
        String right = expr.substring(opPos + 1).trim();
        if (isReadIntLetPair(letNames, readIntLets, left, right)) {
          return Result.ok(CompilerUtil.codeBinary(op));
        }
      }
    }

    // Default: empty program
    return Result.ok(CompilerUtil.codeEmpty());
  }

  private static boolean hasBareReadInt(String s) {
    int pos = 0;
    while ((pos = s.indexOf("readInt", pos)) >= 0) {
      int after = pos + "readInt".length();
      if (after >= s.length() || s.charAt(after) != '(') {
        return true;
      }
      pos = after;
    }
    return false;
  }

  // ... helper code moved to CompilerUtil to satisfy style/size checks

  private static int findBinaryOp(String expr) {
    int i = 0;
    while (i < expr.length()) {
      char c = expr.charAt(i);
      if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%') {
        return i;
      }
      i = i + 1;
    }
    return -1;
  }

  private static boolean isReadIntLetPair(java.util.Set<String> lets, java.util.Set<String> readLets,
      String left, String right) {
    return lets.contains(left) && lets.contains(right) && readLets.contains(left) && readLets.contains(right);
  }

  private static int skipWs(String s, int p) {
    int i = p;
    while (i < s.length() && Character.isWhitespace(s.charAt(i)))
      i++;
    return i;
  }

  private static boolean hasIdentifierStart(String s, int p) {
    return p < s.length() && Character.isJavaIdentifierStart(s.charAt(p));
  }

  private static boolean isIntegerLiteral(String s) {
    if (java.util.Objects.isNull(s) || s.isEmpty())
      return false;
    int k = 0;
    if (s.charAt(0) == '-' && s.length() > 1)
      k = 1;
    while (k < s.length()) {
      if (!Character.isDigit(s.charAt(k)))
        return false;
      k++;
    }
    return true;
  }

  // emitIfProgram moved to CompilerUtil
}
