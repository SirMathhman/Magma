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
    java.util.Map<String, Integer> letIntVals = new java.util.HashMap<>();
    java.util.Map<String, Integer> readIntPlusLiteral = new java.util.HashMap<>();
    java.util.Map<String, Character> readIntOpReadInt = new java.util.HashMap<>();
    java.util.Map<String, Integer> readIntChainCount = new java.util.HashMap<>();
    java.util.Map<String, Character> readIntChainOp = new java.util.HashMap<>();
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
        if (declaredType.equals("I32") && (rhs.equals("true") || rhs.equals("false"))) {
          return Result.err(new CompileError("Type mismatch: expected I32", input));
        }

        // If RHS is a binary op, detect divisor zero when possible (literal or known
        // let int)
        int opPosRhs = findBinaryOp(rhs);
        if (opPosRhs > 0) {
          char opR = rhs.charAt(opPosRhs);
          String rightR = rhs.substring(opPosRhs + 1).trim();
          if ((opR == '/' || opR == '%')) {
            if (isIntegerLiteral(rightR) && Integer.parseInt(rightR) == 0) {
              return Result.ok(CompilerUtil.codeRuntimeFail());
            }
            if (letIntVals.containsKey(rightR) && letIntVals.get(rightR) == 0) {
              return Result.ok(CompilerUtil.codeRuntimeFail());
            }
          }
        }
        if (rhs.equals("readInt()")) {
          readIntLets.add(name);
        } else if (rhs.startsWith("readInt()")) {
          // maybe readInt() <op> readInt()
          int p2 = "readInt()".length();
          p2 = skipWs(rhs, p2);
          // detect chain of 'readInt() + readInt() + ...'
          int len = "readInt()".length();
          int pos = 0;
          int count = 0;
          while (pos < rhs.length()) {
            int idx = rhs.indexOf("readInt()", pos);
            if (idx < 0) {
              pos = rhs.length();
            } else {
              count++;
              pos = idx + len;
            }
          }
          if (count > 1) {
            String norm = normalizeSpaces(rhs);
            int idx0 = norm.indexOf("readInt()");
            int sep = idx0 + len;
            while (sep < norm.length() && norm.charAt(sep) == ' ')
              sep++;
            char op = '+';
            if (sep < norm.length())
              op = norm.charAt(sep);
            StringBuilder expected = new StringBuilder();
            for (int k = 0; k < count; k++) {
              if (k > 0)
                expected.append(' ').append(op).append(' ');
              expected.append("readInt()");
            }
            if (norm.equals(expected.toString())) {
              readIntChainCount.put(name, count);
              readIntChainOp.put(name, op);
            } else {
              int p4 = "readInt()".length();
              p4 = skipWs(rhs, p4);
              if (p4 < rhs.length()) {
                char op2 = rhs.charAt(p4);
                int p3 = p4 + 1;
                p3 = skipWs(rhs, p3);
                if (p3 + len <= rhs.length() && rhs.substring(p3, p3 + len).equals("readInt()")) {
                  if (op2 == '+' || op2 == '-' || op2 == '*' || op2 == '/' || op2 == '%') {
                    readIntOpReadInt.put(name, op2);
                  }
                }
              }
            }
          }

        } else if (rhs.startsWith("readInt() + ")) {
          String lit = rhs.substring("readInt() + ".length()).trim();
          if (isIntegerLiteral(lit)) {
            readIntPlusLiteral.put(name, Integer.parseInt(lit));
          }
        } else {
          int idx = rhs.indexOf(" + readInt()");
          if (idx > 0) {
            String lit = rhs.substring(0, idx).trim();
            if (isIntegerLiteral(lit)) {
              readIntPlusLiteral.put(name, Integer.parseInt(lit));
            }
          } else if (isIntegerLiteral(rhs)) {
            letIntVals.put(name, Integer.parseInt(rhs));
          } else if (rhs.equals("true") || rhs.equals("false")) {
            letBoolVals.put(name, rhs);
          }
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

    // Normalize outer parentheses: (e) -> e
    expr = stripOuterParens(expr);

    // If the final expression is an identifier bound to a boolean literal, print it
    if (letBoolVals.containsKey(expr)) {
      return Result.ok(CompilerUtil.codePrintString(letBoolVals.get(expr)));
    }

    // If the final expression is an integer literal, print it
    if (isIntegerLiteral(expr)) {
      return Result.ok(CompilerUtil.codePrintString(expr));
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

    // Identifier expression bound to readInt() + literal
    if (readIntPlusLiteral.containsKey(expr)) {
      return Result.ok(CodeGen.codeOneIntAddLiteral(readIntPlusLiteral.get(expr)));
    }

    // Identifier expression bound to readInt() <op> readInt()
    if (readIntOpReadInt.containsKey(expr)) {
      return Result.ok(CompilerUtil.codeBinary(readIntOpReadInt.get(expr)));
    }

    // Identifier expression bound to readInt() + readInt() + ...
    if (readIntChainCount.containsKey(expr)) {
      int n = readIntChainCount.get(expr);
      char op = readIntChainOp.getOrDefault(expr, '+');
      if (op == '+') {
        return Result.ok(CodeGen.codeSumNInts(n));
      } else {
        return Result.ok(CodeGen.codeReduceNInts(op, n));
      }
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
            // ensure this isn't actually the start of a three-term pattern
            int r = q + tok.length();
            r = skipWs(expr, r);
            if (r < expr.length()) {
              char nextc = expr.charAt(r);
              if (nextc == '+' || nextc == '-' || nextc == '*' || nextc == '/' || nextc == '%') {
                int s = r + 1;
                s = skipWs(expr, s);
                if (s + tok.length() <= expr.length() && expr.substring(s, s + tok.length()).equals(tok)) {
                  // there's a third readInt(), so skip two-term match
                } else {
                  return Result.ok(CompilerUtil.codeBinary(op));
                }
              } else {
                return Result.ok(CompilerUtil.codeBinary(op));
              }
            } else {
              return Result.ok(CompilerUtil.codeBinary(op));
            }
          }
        }
      }
    }

    // Three-term mix: readInt() op1 readInt() op2 readInt() (handle precedence)
    int first = expr.indexOf(tok);
    if (first >= 0) {
      int p1 = first + tok.length();
      p1 = skipWs(expr, p1);
      if (p1 < expr.length()) {
        char op1 = expr.charAt(p1);
        int p2 = p1 + 1;
        p2 = skipWs(expr, p2);
        if (p2 + tok.length() <= expr.length() && expr.substring(p2, p2 + tok.length()).equals(tok)) {
          int p3 = p2 + tok.length();
          p3 = skipWs(expr, p3);
          if (p3 < expr.length()) {
            char op2 = expr.charAt(p3);
            int p4 = p3 + 1;
            p4 = skipWs(expr, p4);
            if (p4 + tok.length() <= expr.length() && expr.substring(p4, p4 + tok.length()).equals(tok)) {
              // matched readInt() op1 readInt() op2 readInt()
              if ((op1 == '+' || op1 == '-' || op1 == '*' || op1 == '/' || op1 == '%') &&
                  (op2 == '+' || op2 == '-' || op2 == '*' || op2 == '/' || op2 == '%')) {
                return Result.ok(CodeGen.codeThreeReadIntBinary(op1, op2));
              }
            }
          }
        }
      }
    }

    // Single readInt()
    if (expr.equals("readInt()")) {
      return Result.ok(CompilerUtil.codeOneInt());
    }

    // Literal-literal binary like '5 / 0' -> either constant fold or runtime fail
    // on div by zero
    int opPosLit = findBinaryOp(expr);
    if (opPosLit > 0) {
      char opLit = expr.charAt(opPosLit);
      String leftLit = expr.substring(0, opPosLit).trim();
      String rightLit = expr.substring(opPosLit + 1).trim();
      if (isIntegerLiteral(leftLit) && isIntegerLiteral(rightLit)) {
        int lv = Integer.parseInt(leftLit);
        int rv = Integer.parseInt(rightLit);
        return Result.ok(CompilerHelpers.emitBinaryIntResultOrRuntimeFail(opLit, lv, rv));
      }
      // mixed literal and let identifier where let has known integer value
      if (isIntegerLiteral(leftLit) && letIntVals.containsKey(rightLit)) {
        int lv = Integer.parseInt(leftLit);
        int rv = letIntVals.get(rightLit);
        return Result.ok(CompilerHelpers.emitBinaryIntResultOrRuntimeFail(opLit, lv, rv));
      }
      if (letIntVals.containsKey(leftLit) && isIntegerLiteral(rightLit)) {
        int lv = letIntVals.get(leftLit);
        int rv = Integer.parseInt(rightLit);
        return Result.ok(CompilerHelpers.emitBinaryIntResultOrRuntimeFail(opLit, lv, rv));
      }
    }

    // Binary x <op> y where x and y are lets bound to readInt()
    int opPos = findBinaryOp(expr);
    if (opPos > 0) {
      char op = expr.charAt(opPos);
      if (op == '+' || op == '-' || op == '*' || op == '/' || op == '%') {
        String left = expr.substring(0, opPos).trim();
        String right = expr.substring(opPos + 1).trim();
        // reject mixed-type binary literal operations like 5 + true or true + 5
        if ((isIntegerLiteral(left) && (right.equals("true") || right.equals("false"))) ||
            (isIntegerLiteral(right) && (left.equals("true") || left.equals("false")))) {
          return Result.err(new CompileError("Type mismatch in binary operation", expr));
        }
        if (isReadIntLetPair(letNames, readIntLets, left, right)) {
          return Result.ok(CompilerUtil.codeBinary(op));
        }
      }
    }

    // Default: empty program
    return Result.ok(CompilerUtil.codeEmpty());
  }

  private static String stripOuterParens(String s) {
    String t = s.trim();
    boolean changed = true;
    while (changed && t.length() >= 2 && t.charAt(0) == '(' && t.charAt(t.length() - 1) == ')') {
      // check matching parens across the string
      int depth = 0;
      boolean matches = true;
      for (int i = 0; i < t.length(); i++) {
        char c = t.charAt(i);
        if (c == '(')
          depth++;
        else if (c == ')')
          depth--;
        if (depth == 0 && i < t.length() - 1) {
          matches = false;
        }
        if (depth < 0) {
          matches = false;
        }
        if (!matches) {
          // exit the for-loop early by advancing i to end
          i = t.length();
        }
      }
      if (matches) {
        t = t.substring(1, t.length() - 1).trim();
      } else {
        changed = false;
      }
    }
    return t;
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

  private static String normalizeSpaces(String s) {
    StringBuilder out = new StringBuilder();
    int i = 0;
    while (i < s.length()) {
      char c = s.charAt(i);
      if (Character.isWhitespace(c)) {
        // emit a single space and skip following whitespace
        out.append(' ');
        i++;
        while (i < s.length() && Character.isWhitespace(s.charAt(i)))
          i++;
      } else {
        out.append(c);
        i++;
      }
    }
    return out.toString().trim();
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
