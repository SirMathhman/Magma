package magma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Compiler {
  // Small holder for a parsed variable declaration
  private record VarDecl(String name, String rhs, String type, boolean mut) {
  }

  /**
   * @param stmts non-let statements in order
   * @param seq   ordered sequence of VarDecl or String (stmts)
   */ // magma.Result of parsing statements: list of var decls and the final
  // expression
  private record ParseResult(List<VarDecl> decls, List<String> stmts, String last, List<Object> seq) {
  }

  private final String target;

  public Compiler(String targetLanguage) {
    this.target = targetLanguage == null ? "" : targetLanguage.toLowerCase();
  }

  // Helper: find the next occurrence of key that is not part of a larger
  // identifier.
  // Returns the index immediately after the token if found, or -1 otherwise.
  private int findStandaloneTokenEnd(String src, String key, int start) {
    if (src == null || src.isEmpty())
      return -1;
    int idx = start;
    while (true) {
      idx = src.indexOf(key, idx);
      if (idx == -1)
        return -1;
      // ensure previous char (if any) is not part of an identifier
      if (idx > 0) {
        char prev = src.charAt(idx - 1);
        if (Character.isLetterOrDigit(prev) || prev == '_') {
          idx += key.length();
          continue;
        }
      }
      return idx + key.length();
    }
  }

  // Advance from position p (starting after an opening '(') until matching
  // Advance from position p (starting after an opening '(') until matching
  // closing parenthesis is found. Returns index of the character after the
  // closing ')', or -1 if unmatched.
  private int advanceNested(String s, int p) {
    return advanceNestedGeneric(s, p, '(', ')');
  }

  // Return true if s is a braced numeric literal like `{5}` (allow whitespace).
  private boolean isBracedNumeric(String s) {
    if (s == null)
      return false;
    String t = s.trim();
    if (t.length() < 3 || t.charAt(0) != '{' || t.charAt(t.length() - 1) != '}')
      return false;
    String inner = t.substring(1, t.length() - 1).trim();
    if (inner.isEmpty())
      return false;
    for (int i = 0; i < inner.length(); i++) {
      if (!Character.isDigit(inner.charAt(i)))
        return false;
    }
    return true;
  }

  // Return true if s is a plain numeric literal like `0`, `5`, `123` (allow
  // whitespace).
  private boolean isPlainNumeric(String s) {
    if (s == null)
      return false;
    String t = s.trim();
    if (t.isEmpty())
      return false;
    for (int i = 0; i < t.length(); i++) {
      if (!Character.isDigit(t.charAt(i)))
        return false;
    }
    return true;
  }

  // Return start index of a standalone token, or -1 if not found.
  private int findStandaloneTokenIndex(String src, String key, int start) {
    int end = findStandaloneTokenEnd(src, key, start);
    if (end == -1)
      return -1;
    return end - key.length();
  }

  // Returns: 0 = none found, 1 = valid call found (readInt()),
  // 2 = bare identifier found (invalid), 3 = call with arguments (invalid).
  private int findReadIntUsage(String src) {
    String key = "readInt";
    int idx = 0;
    boolean foundCall = false;
    while (true) {
      int end = findStandaloneTokenEnd(src, key, idx);
      if (end == -1)
        break;
      int j = skipWhitespace(src, end);
      if (j < src.length() && src.charAt(j) == '(') {
        // find matching ')'
        int p = advanceNested(src, j + 1);
        if (p == -1) {
          // unbalanced parens -> treat as invalid
          return 3;
        }
        int contentStart = j + 1;
        int contentEnd = p - 1; // exclusive
        boolean hasNonWs = false;
        for (int k = contentStart; k < contentEnd; k++) {
          if (!Character.isWhitespace(src.charAt(k))) {
            hasNonWs = true;
            break;
          }
        }
        if (hasNonWs)
          return 3; // call with args -> invalid
        foundCall = true;
        idx = p; // continue searching after ')'
      } else {
        return 2; // bare identifier — invalid
      }
    }
    return foundCall ? 1 : 0;
  }

  public Result<Set<Unit>, CompileError> compile(Set<Unit> units) {
    Set<Unit> out = new HashSet<>();
    for (Unit u : units) {
      String src = u.input() == null ? "" : u.input();
      String expr = extractExpression(src);

      // parse statements to detect duplicate variable declarations and analyze each
      // part
      ParseResult prCheck = parseStatements(expr);

      // detect invalid calls on non-identifiers (e.g. `5()`)
      for (String st : prCheck.stmts) {
        Err<Set<Unit>, CompileError> e = detectNonIdentifierCall(st == null ? "" : st);
        if (e != null)
          return e;
      }
      Err<Set<Unit>, CompileError> eFinal = detectNonIdentifierCall(prCheck.last == null ? "" : prCheck.last);
      if (eFinal != null)
        return eFinal;

      Set<String> seen = new HashSet<>();
      boolean wantsReadInt = false;
      for (VarDecl d : prCheck.decls) {
        if (!seen.add(d.name)) {
          return new Err<>(new CompileError("Duplicate variable: " + d.name));
        }
        // If this declaration is a function, ensure no duplicate parameter names
        if (d.type != null && d.type.contains("=>")) {
          String inner = getParamsInnerTypeSegment(d.type);
          if (inner != null) {
            Set<String> pnames = new HashSet<>();
            int depth = 0;
            int start = 0;
            for (int i = 0; i <= inner.length(); i++) {
              boolean atEnd = i == inner.length();
              char c = atEnd ? ',' : inner.charAt(i);
              if (c == '(')
                depth++;
              else if (c == ')')
                depth--;
              if ((c == ',' && depth == 0) || atEnd) {
                String part = inner.substring(start, i).trim();
                if (!part.isEmpty()) {
                  int colon = part.indexOf(':');
                  String pname = colon == -1 ? part.trim() : part.substring(0, colon).trim();
                  if (!pnames.add(pname)) {
                    return new Err<>(new CompileError("Duplicate parameter: " + pname));
                  }
                }
                start = i + 1;
              }
            }
          }
        }
        String rhs = d.rhs == null ? "" : d.rhs.trim();
        if (rhs.equals("readInt")) {
          // bare readInt allowed only if declared type is a function type (contains =>)
          String declType = dTypeOf(d);
          if (declType == null || !declType.contains("=>")) {
            return new Err<>(new CompileError("Invalid use of readInt"));
          }
          wantsReadInt = true;
        } else {
          int usageRhs = findReadIntUsage(rhs);
          if (usageRhs == 2)
            return new Err<>(new CompileError("Bare 'readInt' used in initializer for variable '" + d.name + "'"));
          if (usageRhs == 3)
            return new Err<>(
                new CompileError("'readInt' called with arguments in initializer for variable '" + d.name + "'"));
          if (usageRhs == 1)
            wantsReadInt = true;
        }
        // If declaration has an explicit non-function type and an initializer, check
        // type matches inferred rhs
        String declType = d.type == null ? "" : d.type.trim();
        if (!declType.isEmpty() && !declType.contains("=>") && rhs != null && !rhs.isEmpty()) {
          String actual = exprType(rhs, prCheck.decls);
          if (actual != null && !actual.equals(declType)) {
            return new Err<>(new CompileError("Initializer type mismatch for variable '" + d.name + "'"));
          }
        }
      }
      // If any declaration uses a braced numeric initializer like `{5}`, set
      // wantsReadInt so the JS/C helpers are emitted (tests expect this legacy
      // behaviour where braces indicate reading input in tests).
      for (VarDecl d : prCheck.decls) {
        if (isBracedNumeric(d.rhs)) {
          wantsReadInt = true;
          break;
        }
      }

      // Check for scope violations: variables declared in braced blocks should not be
      // accessible outside
      String lastExpr = prCheck.last == null ? "" : prCheck.last.trim();
      if (!lastExpr.isEmpty() && lastExpr.matches("[A-Za-z_][A-Za-z0-9_]*")) {
        // Check if the final expression is a simple identifier that might be declared
        // in a braced block
        for (Object seqItem : prCheck.seq) {
          if (seqItem instanceof String stmt) {
            if (stmt.trim().startsWith("{") && stmt.trim().endsWith("}")) {
              // This is a braced block statement
              String bracedContent = stmt.trim();
              bracedContent = bracedContent.substring(1, bracedContent.length() - 1).trim();
              // Check if this braced content declares the variable referenced in lastExpr
              if (bracedContent.contains("let " + lastExpr + " ") || bracedContent.contains("let " + lastExpr + "=") ||
                  bracedContent.contains("let " + lastExpr + ":") || bracedContent.contains("let " + lastExpr + ";")) {
                return new Err<>(
                    new CompileError("Variable '" + lastExpr + "' declared in braced block is not accessible outside"));
              }
            }
          }
        }
      }

      // check final expression
      String finalExpr = prCheck.last == null ? "" : prCheck.last;
      Err<Set<Unit>, CompileError> arErrFinal = validateFunctionCallArity(finalExpr, prCheck.decls);
      if (arErrFinal != null)
        return arErrFinal;
      int finalUsage = findReadIntUsage(finalExpr);
      if (finalUsage == 2) {
        return new Err<>(new CompileError("Bare 'readInt' used as final expression"));
      }
      if (finalUsage == 3) {
        return new Err<>(new CompileError("'readInt' called with arguments in final expression"));
      }
      if (finalUsage == 1)
        wantsReadInt = true;

      // if final expression is an if-expression, ensure the condition is boolean
      String[] ifParts = parseIfExpression(finalExpr);
      if (ifParts != null) {
        String cond = ifParts[0];
        if (!exprLooksBoolean(cond)) {
          return new Err<>(new CompileError("If condition must be boolean"));
        }
      }

      // check non-let statements (e.g., assignments) for readInt usage
      Map<String, Boolean> assigned = new HashMap<>();
      for (VarDecl vd : prCheck.decls) {
        assigned.put(vd.name, vd.rhs != null && !vd.rhs.isEmpty());
      }
      for (int si = 0; si < prCheck.stmts.size();) {
        String s = prCheck.stmts.get(si);
        int usageStmt = findReadIntUsage(s == null ? "" : s);
        // If this is an 'if' followed by an 'else' statement, handle both together
        String sTrim = s == null ? "" : s.trim();
        if (sTrim.startsWith("if ") && si + 1 < prCheck.stmts.size()) {
          String next = prCheck.stmts.get(si + 1);
          String nextTrim = next == null ? "" : next.trim();
          if (nextTrim.startsWith("else")) {
            // combined if-else
            String[] parts = parseIfExpression(s + "; " + nextTrim);
            // parseIfExpression expects a single if...else string; if it fails, fallback
            if (parts != null) {
              String thenExpr = parts[1];
              String elseExpr = parts[2];
              // check readInt usage in condition and both branches
              int useCond = findReadIntUsage(parts[0] == null ? "" : parts[0]);
              int useThen = findReadIntUsage(thenExpr == null ? "" : thenExpr);
              int useElse = findReadIntUsage(elseExpr == null ? "" : elseExpr);
              if (useCond == 1 || useThen == 1 || useElse == 1) {
                wantsReadInt = true;
              }
              if (useCond == 2 || useThen == 2 || useElse == 2) {
                return new Err<>(new CompileError("Bare 'readInt' used in statement: '" + s + "'"));
              }
              if (useCond == 3 || useThen == 3 || useElse == 3) {
                return new Err<>(new CompileError("'readInt' called with arguments in statement: '" + s + "'"));
              }

              String lhsThen = getAssignmentLhs(thenExpr);

              String lhsElse = getAssignmentLhs(elseExpr);
              if (lhsThen != null && lhsThen.equals(lhsElse)) {
                // both branches assign to same variable; treat as a single assignment
                for (VarDecl vd : prCheck.decls) {
                  if (vd.name.equals(lhsThen)) {
                    break;
                  }
                }
                var err0 = checkAndMarkAssignment(lhsThen, prCheck.decls, assigned);
                if (err0 != null)
                  return err0;
              } else {
                // otherwise, process then and else separately to preserve previous semantics
                if (lhsThen != null) {
                  for (VarDecl vd : prCheck.decls) {
                    if (vd.name.equals(lhsThen)) {
                      break;
                    }
                  }
                  var err1 = checkAndMarkAssignment(lhsThen, prCheck.decls, assigned);
                  if (err1 != null)
                    return err1;
                }
                if (lhsElse != null) {
                  for (VarDecl vd : prCheck.decls) {
                    if (vd.name.equals(lhsElse)) {
                      break;
                    }
                  }
                  var err2 = checkAndMarkAssignment(lhsElse, prCheck.decls, assigned);
                  if (err2 != null)
                    return err2;
                }
              }
              si += 2;
              continue;
            }
          }
        }
        // default: single statement handling
        String left = getAssignmentLhs(s);

        if (left != null) {
          // If this is a compound assignment or increment/decrement, ensure the
          // target variable is numeric (I32 or initialized from readInt or braced
          // numeric).
          if (isCompoundOrIncrement(s)) {
            VarDecl targetCheck = null;
            for (VarDecl vd : prCheck.decls) {
              if (vd.name.equals(left)) {
                targetCheck = vd;
                break;
              }
            }
            if (targetCheck != null) {
              boolean numeric = false;
              String dt = dTypeOf(targetCheck);
              if (dt != null && dt.equals("I32"))
                numeric = true;
              if (!numeric) {
                // check initializer for readInt() call or braced numeric or plain numeric
                int usage = findReadIntUsage(targetCheck.rhs == null ? "" : targetCheck.rhs);
                if (usage == 1 || isBracedNumeric(targetCheck.rhs) || isPlainNumeric(targetCheck.rhs))
                  numeric = true;
              }
              if (!numeric) {
                return new Err<>(new CompileError("Compound assignment on non-numeric variable '" + left + "'"));
              }
            }
          }
          boolean ident = left.matches("[A-Za-z_][A-Za-z0-9_]*");
          if (ident) {
            VarDecl target = null;
            for (VarDecl vd : prCheck.decls) {
              if (vd.name.equals(left)) {
                target = vd;
                break;
              }
            }
            if (target == null) {
              return new Err<>(new CompileError("Assignment to undefined variable '" + left + "'"));
            }
            boolean wasAssigned = assigned.getOrDefault(target.name, false);

            if (target.mut) {
              assigned.put(target.name, true);
            } else {
              if (wasAssigned) {
                return new Err<>(new CompileError("Assignment to immutable variable '" + left + "'"));
              }
              assigned.put(target.name, true);
            }
          }
        }
        if (usageStmt == 2) {
          return new Err<>(new CompileError("Bare 'readInt' used in statement: '" + s + "'"));
        }
        if (usageStmt == 3) {
          return new Err<>(new CompileError("'readInt' called with arguments in statement: '" + s + "'"));
        }
        if (usageStmt == 1)
          wantsReadInt = true;
        si++;
      }

      // Ensure every declaration without initializer is assigned later in stmts.
      for (VarDecl vd : prCheck.decls) {
        if (vd.rhs == null || vd.rhs.isEmpty()) {
          boolean declAssigned = false;
          for (String s : prCheck.stmts) {
            if (isAssignmentTo(s, vd.name)) {
              declAssigned = true;
              break;
            }
          }
          if (!declAssigned) {
            return new Err<>(new CompileError("Variable '" + vd.name + "' declared without initializer or assignment"));
          }
        }
      }

      if ("typescript".equals(target)) {
        StringBuilder js = new StringBuilder();
        // include readInt helper only when needed
        if (wantsReadInt) {
          js.append("const fs = require('fs');\n");
          js.append("const inRaw = fs.readFileSync(0, 'utf8');\n");
          js.append("const tokens = (inRaw.match(/\\S+/g) || []);\n");
          js.append("let __idx = 0;\n");
          js.append("function readInt(){ return parseInt(tokens[__idx++] || '0'); }\n");
        }
        String jsExpr = buildJsExpression(expr);
        if (jsExpr != null && !jsExpr.isEmpty()) {
          js.append("console.log(").append(jsExpr).append(");\n");
        }
        out.add(new Unit(u.location(), ".js", js.toString()));
      } else if ("c".equals(target)) {
        StringBuilder c = new StringBuilder();
        c.append("#include <stdio.h>\n");
        c.append("#include <stdlib.h>\n");
        String[] cParts = buildCParts(expr);
        // include readInt helper only when needed
        if (wantsReadInt) {
          c.append("int readInt(){ int x; if (scanf(\"%d\", &x)==1) return x; return 0; }\n");
        }
        String globalDefs = cParts[0] == null ? "" : cParts[0];
        String prefix = cParts[1] == null ? "" : cParts[1];
        String exprC = cParts.length > 2 && cParts[2] != null ? cParts[2] : "";
        // emit any global function definitions before main
        if (!globalDefs.isEmpty()) {
          c.append(globalDefs);
        }
        if (exprC.isEmpty()) {
          c.append("int main() { return 0; }");
        } else {
          boolean looksBoolean = exprLooksBoolean(exprC);
          // if expr is a simple identifier and declared as Bool, treat as boolean
          if (!looksBoolean) {
            String id = exprC == null ? "" : exprC.trim();
            if (id.matches("[A-Za-z_][A-Za-z0-9_]*")) {
              for (VarDecl vd : prCheck.decls) {
                if (vd.name.equals(id)) {
                  String dt = dTypeOf(vd);
                  if (dt != null && dt.equals("Bool")) {
                    looksBoolean = true;
                    break;
                  }
                }
              }
            }
          }
          if (findStandaloneTokenIndex(exprC, "true", 0) != -1 || findStandaloneTokenIndex(exprC, "false", 0) != -1 ||
              findStandaloneTokenIndex(prefix, "true", 0) != -1 || findStandaloneTokenIndex(prefix, "false", 0) != -1) {
            c.insert(0, "#include <stdbool.h>\n");
          }
          if (prefix.isEmpty()) {
            if (looksBoolean) {
              c.append("int main() { printf(\"%s\", (").append(exprC).append(") ? \"true\" : \"false\"); return 0; }");
            } else {
              c.append("int main() { int res = ").append(exprC).append("; printf(\"%d\", res); return 0; }");
            }
          } else {
            if (looksBoolean) {
              c.append("int main() { ")
                  .append(prefix)
                  .append(" printf(\"%s\", (")
                  .append(exprC)
                  .append(") ? \"true\" : \"false\"); return 0; }");
            } else {
              c.append("int main() { ")
                  .append(prefix)
                  .append(" int res = ")
                  .append(exprC)
                  .append("; printf(\"%d\", res); return 0; }");
            }
          }
        }
        out.add(new Unit(u.location(), ".c", c.toString()));
      } else {
        out.add(u);
      }
    }
    return new Ok<>(out);
  }

  // Helper: validate assignment to `name` using declarations list and assigned
  // map.
  // Returns magma.Err<magma.CompileError> if invalid, otherwise null and marks
  // the var as
  // assigned.
  private Err<Set<Unit>, CompileError> checkAndMarkAssignment(String name,
      List<VarDecl> decls,
      Map<String, Boolean> assigned) {
    VarDecl target = null;
    for (VarDecl vd : decls) {
      if (vd.name.equals(name)) {
        target = vd;
        break;
      }
    }
    if (target == null)
      return new Err<>(new CompileError("Assignment to undefined variable '" + name + "'"));
    boolean wasAssigned = assigned.getOrDefault(target.name, false);
    if (!target.mut && wasAssigned)
      return new Err<>(new CompileError("Assignment to immutable variable '" + name + "'"));
    assigned.put(target.name, true);
    return null;
  }

  // Remove the prelude declaration if present and trim; used to get the
  // expression to evaluate.
  private String extractExpression(String src) {
    if (src == null)
      return "";
    String prelude = "extern fn readInt() : I32;";
    String out = src;
    int idx = out.indexOf(prelude);
    if (idx != -1) {
      out = out.substring(0, idx) + out.substring(idx + prelude.length());
    }
    out = out.trim();
    // remove trailing semicolon if present
    if (out.endsWith(";"))
      out = out.substring(0, out.length() - 1).trim();
    // If the whole expression is wrapped in braces { ... }, strip one layer
    if (out.length() >= 2 && out.charAt(0) == '{' && out.charAt(out.length() - 1) == '}') {
      int after = advanceNestedGeneric(out, 1, '{', '}');
      if (after == out.length()) {
        out = out.substring(1, out.length() - 1).trim();
      }
    }
    if (out.isEmpty())
      return "";
    return out;
  }

  // Generic nested-advance helper used by the specialized methods above.
  private int advanceNestedGeneric(String s, int p, char openChar, char closeChar) {
    int depth = 1;
    while (p < s.length() && depth > 0) {
      char ch = s.charAt(p);
      if (ch == openChar)
        depth++;
      else if (ch == closeChar)
        depth--;
      p++;
    }
    return depth == 0 ? p : -1;
  }

  // If `src` is a single braced block like "{...}" (with balanced braces),
  // return the inner content trimmed, otherwise return the original src.
  private String unwrapBraced(String src) {
    if (src == null)
      return null;
    String t = src.trim();
    if (t.length() >= 2 && t.charAt(0) == '{' && t.charAt(t.length() - 1) == '}') {
      int after = advanceNestedGeneric(t, 1, '{', '}');
      if (after == t.length())
        return t.substring(1, t.length() - 1).trim();
    }
    return src;
  }

  // Ensure a braced block becomes a valid expression or block with a return.
  // If `forC` is false (JS), convert "{ stmt1; stmt2; expr }" into
  // "(function(){ stmt1; stmt2; return expr; })()" when used as an expression
  // or convert into an expression body "(expr)" when single expression.
  // If `forC` is true, produce just the inner expression or a block with
  // a return for multi-statement bodies (used when emitting function impls
  // for C).
  private String ensureReturnInBracedBlock(String src, boolean forC) {
    if (src == null)
      return "";
    String t = src.trim();
    if (!t.startsWith("{") || !t.endsWith("}")) {
      return src;
    }
    String inner = t.substring(1, t.length() - 1).trim();
    // Split top-level semicolon-separated parts
    String[] parts = splitByChar(inner);
    java.util.List<String> nonEmpty = new java.util.ArrayList<>();
    for (String p : parts) {
      if (p != null && !p.trim().isEmpty())
        nonEmpty.add(p.trim());
    }
    if (nonEmpty.isEmpty()) {
      return forC ? "0" : "()";
    }
    if (nonEmpty.size() == 1) {
      // Single expression — emit as expression
      return nonEmpty.get(0);
    }
    // Multiple statements: last item is the expression to return
    StringBuilder b = new StringBuilder();
    if (forC) {
      b.append("{");
      for (int i = 0; i < nonEmpty.size() - 1; i++) {
        String stmt = nonEmpty.get(i);
        // convert simple JS let/const declarations to C int declarations
        if (stmt.startsWith("let "))
          stmt = "int " + stmt.substring(4);
        else if (stmt.startsWith("const "))
          stmt = "int " + stmt.substring(6);
        b.append(stmt).append("; ");
      }
      appendReturnForBlock(b, nonEmpty);
      b.append("}");
      return b.toString();
    } else {
      // JS: return an IIFE expression to preserve evaluation semantics
      b.append("(function(){ ");
      for (int i = 0; i < nonEmpty.size() - 1; i++) {
        b.append(nonEmpty.get(i)).append("; ");
      }
      appendReturnForBlock(b, nonEmpty);
      b.append(" })()");
      return b.toString();
    }
  }

  private void appendReturnForBlock(StringBuilder b, java.util.List<String> nonEmpty) {
    b.append("return ").append(nonEmpty.get(nonEmpty.size() - 1)).append(";");
  }

  // Normalize an arrow RHS for JS: strip param types, convert ternary, and
  // if the body is a braced multi-statement block convert it into an expression
  // or IIFE as appropriate.
  private String normalizeArrowRhsForJs(String rhs) {
    String rhsOut = cleanArrowRhsForJs(rhs);
    rhsOut = convertLeadingIfToTernary(rhsOut);
    int arrowIdx = rhsOut.indexOf("=>");
    if (arrowIdx != -1) {
      String before = rhsOut.substring(0, arrowIdx + 2);
      String after = rhsOut.substring(arrowIdx + 2).trim();
      if (after.startsWith("{")) {
        after = ensureReturnInBracedBlock(after, false);
      } else {
        after = unwrapBraced(after);
      }
      rhsOut = before + " " + after;
    }
    return rhsOut;
  }

  // Convert simple language constructs into a JS expression string.
  // Supports optional leading 'let' declarations followed by an expression,
  // separated by semicolons.
  private String buildJsExpression(String exprSrc) {
    ParseResult pr = parseStatements(exprSrc);
    String prefix = renderSeqPrefix(pr);
    String last = pr.last;
    // convert simple `if (cond) thenExpr else elseExpr` to JS ternary
    last = convertLeadingIfToTernary(last);
    // Unwrap a single braced block like `{x}` into `x` to avoid emitting an
    // object literal in JS output.
    last = unwrapBraced(last);
    if (prefix.isEmpty())
      return last;
    return "(function(){ " + prefix + " return (" + last + "); })()";
  }

  // Convert a leading if-expression `if (cond) then else elseExpr` into a JS
  // ternary expression. This is token-aware and avoids regex.
  // Convert a leading if-expression `if (cond) then else elseExpr` into a
  // ternary expression using the centralized parse helper. Recurses into
  // branches to handle nested ifs.
  private String convertLeadingIfToTernary(String src) {
    String[] parts = parseIfExpression(src);
    if (parts == null)
      return src == null ? "" : src;
    parts[1] = convertLeadingIfToTernary(parts[1]);
    parts[2] = convertLeadingIfToTernary(parts[2]);
    return "((" + parts[0] + ") ? (" + parts[1] + ") : (" + parts[2] + "))";
  }

  // For C we need to return a pair: any prefix statements, and the final
  // expression.
  // For C we need to return triple: global function defs, prefix statements (in
  // main), and the final expression.
  // Returns [globalDefs, prefix, expr]
  private String[] buildCParts(String exprSrc) {
    ParseResult pr = parseStatements(exprSrc);
    String[] cparts = renderSeqPrefixC(pr);
    String globalDefs = cparts[0];
    String prefix = cparts[1];
    String expr = pr.last == null ? "" : pr.last;
    expr = convertLeadingIfToTernary(expr);
    return new String[] { globalDefs, prefix, expr };
  }

  // (removed appendLocalVarDecl; use appendVarDeclToBuilder instead)

  // Render sequence for C producing [globalDefs, localPrefix]. Global defs
  // contain function implementations that must live outside main.
  private String[] renderSeqPrefixC(ParseResult pr) {
    StringBuilder global = new StringBuilder();
    StringBuilder local = new StringBuilder();
    for (Object o : pr.seq) {
      if (o instanceof VarDecl d) {
        if (d.type != null && d.type.contains("=>")) {
          // function-typed declaration
          String rhs = d.rhs == null ? "" : d.rhs.trim();
          if (rhs.isEmpty()) {
            // no initializer: emit pointer declaration without init
            local.append("int ").append(d.name).append("; ");
          } else if (rhs.contains("=>")) {
            // arrow RHS like "(x : I32) => x" -> create global impl and assign pointer
            int arrowIdx = rhs.indexOf("=>");
            int parenStart = rhs.lastIndexOf('(', arrowIdx);
            int parenEnd = parenStart == -1 ? -1 : advanceNestedGeneric(rhs, parenStart + 1, '(', ')');
            String params = parenStart != -1 && parenEnd != -1 ? rhs.substring(parenStart, parenEnd) : "()";
            String body = rhs.substring(arrowIdx + 2).trim();
            // If braced multi-statement body, convert into C block with return
            if (body.startsWith("{")) {
              body = ensureReturnInBracedBlock(body, true);
            } else {
              body = unwrapBraced(body);
            }
            // build C param list with types: (x : I32) -> "int x"
            String cParams = paramsToC(params);
            String implName = d.name + "_impl";
            if (body.startsWith("{")) {
              // body already a C block
              global.append("int ").append(implName).append(cParams).append(" ").append(body).append("\n");
            } else {
              String implBody = convertLeadingIfToTernary(body);
              global.append("int ").append(implName).append(cParams).append(" { return ").append(implBody)
                  .append("; }\n");
            }
            // pointer declaration in main: int (*name)(types) = implName;
            String ptrSig = "(" + "*" + d.name + ")" + cParams;
            // for pointer decl format: int (*name)(T1,T2) = implName;
            local.append("int ").append(ptrSig).append(" = ").append(implName).append("; ");
          } else {
            // rhs is a bare function name (e.g., readInt)
            String rhsOutF = unwrapBraced(rhs);
            local.append("int (*").append(d.name).append(")() = ").append(rhsOutF).append("; ");
          }
        } else {
          // non-function types handled as before
          appendVarDeclToBuilder(local, d, true);
        }
      } else if (o instanceof String s) {
        handleFnStringForC(s, global, local);
      }
    }
    return new String[] { global.toString(), local.toString() };
  }

  private void handleFnStringForC(String s, StringBuilder global, StringBuilder local) {
    String trimmedS = s.trim();
    if (trimmedS.startsWith("fn ")) {
      // convert fn stmt into a global function definition
      String[] parts = parseFnDeclaration(trimmedS);
      if (parts != null) {
        String name = parts[0];
        String params = parts[1];
        String body = parts[3];
        String norm = normalizeBodyForC(body);
        String cParams = paramsToC(params);
        if (norm.startsWith("{")) {
          global.append("int ").append(name).append(cParams).append(" ").append(norm).append("\n");
        } else {
          String implBody = convertLeadingIfToTernary(norm);
          global.append("int ").append(name).append(cParams).append(" { return ").append(implBody).append("; }\n");
        }
      } else {
        local.append(s).append("; ");
      }
    } else {
      local.append(s).append("; ");
    }
  }

  private void appendVarDeclToBuilder(StringBuilder b, VarDecl d, boolean forC) {
    if (forC) {
      if (d.rhs == null || d.rhs.isEmpty()) {
        b.append("int ").append(d.name).append("; ");
      } else {
        String rhsOut = convertLeadingIfToTernary(d.rhs);
        rhsOut = unwrapBraced(rhsOut);
        b.append("int ").append(d.name).append(" = ").append(rhsOut).append("; ");
      }
    } else {
      if (d.rhs == null || d.rhs.isEmpty()) {
        b.append("let ").append(d.name).append("; ");
      } else {
        String rhsOut = d.rhs;
        // If arrow-style RHS, ensure its param types are stripped first
        if (rhsOut.contains("=>")) {
          rhsOut = normalizeArrowRhsForJs(rhsOut);
        } else {
          rhsOut = convertLeadingIfToTernary(rhsOut);
          rhsOut = unwrapBraced(rhsOut);
        }
        appendJsVarDecl(b, d, rhsOut);
      }
    }
  }

  private void appendJsVarDecl(StringBuilder b, VarDecl d, String rhsOut) {
    b.append(d.mut ? "let " : "const ").append(d.name).append(" = ").append(rhsOut).append("; ");
  }

  // Convert a param list like "(x : I32, y : I32)" into C params "(int x, int
  // y)".
  private String paramsToC(String params) {
    if (params == null)
      return "()";
    String p = params.trim();
    if (p.length() >= 2 && p.charAt(0) == '(' && p.charAt(p.length() - 1) == ')') {
      String inner = p.substring(1, p.length() - 1).trim();
      if (inner.isEmpty())
        return "()";
      String[] parts = inner.split(",");
      StringBuilder out = new StringBuilder();
      out.append('(');
      boolean first = true;
      for (String part : parts) {
        String t = part.trim();
        if (t.isEmpty())
          continue;
        // expected "name : Type" or just "name"
        int colon = t.indexOf(':');
        String name = colon == -1 ? t : t.substring(0, colon).trim();
        String type = "int"; // default
        if (colon != -1) {
          String typ = t.substring(colon + 1).trim();
          if (typ.equals("I32"))
            type = "int";
          else if (typ.equals("Bool"))
            type = "int"; // represent bool as int in C output
          else
            type = "int"; // fallback
        }
        if (!first)
          out.append(", ");
        out.append(type).append(' ').append(name);
        first = false;
      }
      out.append(')');
      return out.toString();
    }
    return "()";
  }

  // Render the ordered seq (VarDecl or statement String) into a language-specific
  // prefix string. 'lang' supports "js" (typescript/js) and "c".
  private String renderSeqPrefix(ParseResult pr) {
    StringBuilder prefix = new StringBuilder();
    for (Object o : pr.seq) {
      if (o instanceof VarDecl d) {
        if (d.rhs != null && d.rhs.contains("=>")) {
          // JS special-case: arrow RHS needs param-type stripping and block->expr
          // conversion
          String rhsOut = normalizeArrowRhsForJs(d.rhs);
          appendJsVarDecl(prefix, d, rhsOut);
        } else {
          appendVarDeclToBuilder(prefix, d, false);
        }
      } else if (o instanceof String stmt) {
        String trimmedS = stmt.trim();
        if (trimmedS.startsWith("fn ")) {
          // Handle function declarations
          String convertedFn = convertFnToJs(trimmedS);
          prefix.append(convertedFn).append("; ");
        } else {
          prefix.append(stmt).append("; ");
        }
      }
    }
    return prefix.toString();
  }

  // Given an RHS like "(x : I32) => x" or "(x : I32, y : I32) => x+y",
  // remove type annotations from the parameter list for JS output.
  private String cleanArrowRhsForJs(String rhs) {
    int arrowIdx = rhs.indexOf("=>");
    if (arrowIdx == -1)
      return rhs;
    // find the last '(' before arrowIdx
    int parenStart = rhs.lastIndexOf('(', arrowIdx);
    if (parenStart == -1)
      return rhs;
    int parenEnd = advanceNestedGeneric(rhs, parenStart + 1, '(', ')');
    if (parenEnd == -1 || parenEnd > arrowIdx)
      return rhs;
    String params = rhs.substring(parenStart, parenEnd);
    String stripped = stripParamTypes(params);
    return rhs.substring(0, parenStart) + stripped + rhs.substring(parenEnd);
  }

  // Parse function declaration "fn name(params) : Return => body" and return
  // components
  // Returns String[] { name, params, returnType, body } where returnType may be
  // empty string
  private String[] parseFnDeclaration(String fnDecl) {
    if (!fnDecl.startsWith("fn "))
      return null;

    String rest = fnDecl.substring(3).trim(); // Remove "fn "
    int parenIdx = rest.indexOf('(');
    if (parenIdx == -1)
      return null; // Invalid syntax

    String name = rest.substring(0, parenIdx).trim();
    int parenEnd = advanceNested(rest, parenIdx + 1);
    if (parenEnd == -1)
      return null;
    String params = rest.substring(parenIdx, parenEnd).trim();

    int afterParams = parenEnd;
    while (afterParams < rest.length() && Character.isWhitespace(rest.charAt(afterParams)))
      afterParams++;

    String retType = "";
    int arrowIdx = rest.indexOf("=>", afterParams);
    if (arrowIdx == -1)
      return null;
    if (afterParams < rest.length() && rest.charAt(afterParams) == ':') {
      retType = rest.substring(afterParams + 1, arrowIdx).trim();
    }
    int bodyStart = arrowIdx + 2;
    // Determine if the body starts with a braced block; if so, consume up to
    // matching '}'
    int bodyEndIndex;
    String body;
    String remainder;
    int bs = bodyStart;
    while (bs < rest.length() && Character.isWhitespace(rest.charAt(bs)))
      bs++;
    if (bs < rest.length() && rest.charAt(bs) == '{') {
      int after = advanceNestedGeneric(rest, bs + 1, '{', '}');
      if (after == -1)
        return null;
      bodyEndIndex = after; // index after '}'
      body = rest.substring(bs, bodyEndIndex).trim();
      remainder = rest.substring(bodyEndIndex).trim();
    } else {
      // body is the rest up to end (no braces) — no trailing remainder
      body = rest.substring(bodyStart).trim();
      remainder = "";
    }
    return new String[] { name, params, retType, body, remainder };
  }

  // Convert function declaration from "fn name() => expr" to JavaScript "const
  // name = () => expr"
  private String convertFnToJs(String fnDecl) {
    String[] parts = parseFnDeclaration(fnDecl);
    if (parts == null)
      return fnDecl; // Invalid syntax, return as-is
    // Strip any type annotations from the parameter list for JS output.
    String params = stripParamTypes(parts[1]);
    String body = parts[3];
    if (body != null && body.trim().startsWith("{")) {
      body = ensureReturnInBracedBlock(body, false);
      return "const " + parts[0] + " = " + params + " => " + body;
    } else {
      body = unwrapBraced(body);
      return "const " + parts[0] + " = " + params + " => " + body;
    }
  }

  private String normalizeBodyForC(String body) {
    if (body != null && body.trim().startsWith("{")) {
      return ensureReturnInBracedBlock(body, true);
    }
    return unwrapBraced(body);
  }

  // Remove type annotations from a parameter list like "(x : I32, y : I32)"
  // without using regular expressions.
  private String stripParamTypes(String params) {
    if (params == null)
      return "";
    StringBuilder out = new StringBuilder();
    int i = 0;
    while (i < params.length()) {
      char c = params.charAt(i);
      if (c == ':') {
        // skip the ':' and the type token until we reach a comma or closing paren
        // skip whitespace after ':'
        do
          i++;
        while (i < params.length() && Character.isWhitespace(params.charAt(i)));
        // skip type characters (identifier, digits, spaces, generics) until ',' or ')'
        while (i < params.length()) {
          char cc = params.charAt(i);
          if (cc == ',' || cc == ')')
            break;
          i++;
        }
        // continue loop without consuming ',' or ')'
      } else {
        out.append(c);
        i++;
      }
    }
    // tidy up: collapse multiple spaces and remove spaces before commas/parentheses
    String temp = out.toString();
    // collapse runs of whitespace to single space
    StringBuilder norm = new StringBuilder();
    boolean lastWs = false;
    for (int j = 0; j < temp.length(); j++) {
      char ch = temp.charAt(j);
      if (Character.isWhitespace(ch)) {
        if (!lastWs) {
          norm.append(' ');
          lastWs = true;
        }
      } else {
        norm.append(ch);
        lastWs = false;
      }
    }
    String cleaned = norm.toString();
    cleaned = cleaned.replace(" ,", ",");
    cleaned = cleaned.replace("( ", "(");
    cleaned = cleaned.replace(" )", ")");
    return cleaned.trim();
  }

  // Handle function declaration or regular statement processing
  private String handleStatementProcessing(String p, List<String> stmts, List<Object> seq) {
    String processed = processControlStructures(p);
    if (!processed.equals(p)) {
      String[] controlParts = splitByChar(processed);
      String lastPart = p;
      for (String part : controlParts) {
        part = part.trim();
        if (!part.isEmpty()) {
          stmts.add(part);
          seq.add(part);
          lastPart = part;
        }
      }
      return lastPart;
    } else {
      stmts.add(p);
      seq.add(p);
      return p;
    }
  }

  // Simple splitter by character – avoids regex and respects braces.
  private String[] splitByChar(String s) {
    List<String> parts = splitTopLevel(s, ';', '{', '}');
    return parts.toArray(new String[0]);
  }

  // Process control structures like while loops that might be followed by
  // expressions
  // Returns a string with semicolons inserted to separate control structures from
  // expressions
  private String processControlStructures(String stmt) {
    stmt = stmt.trim();

    // Helper function to handle braced content splitting
    int braceStart = -1;
    int braceEnd;

    // Look for while loops
    int whileIdx = stmt.indexOf("while");
    if (whileIdx != -1 && (whileIdx == 0 || !Character.isLetterOrDigit(stmt.charAt(whileIdx - 1)))) {
      int parenStart = stmt.indexOf('(', whileIdx);
      if (parenStart != -1) {
        int parenEnd = advanceNested(stmt, parenStart + 1);
        if (parenEnd != -1) {
          for (int i = parenEnd; i < stmt.length(); i++) {
            if (stmt.charAt(i) == '{') {
              braceStart = i;
              break;
            } else if (!Character.isWhitespace(stmt.charAt(i))) {
              break;
            }
          }
        }
      }
    }

    // Look for braced blocks that are not while loops
    if (braceStart == -1 && stmt.startsWith("{")) {
      braceStart = 0;
    }

    // If we found a braced block, check if there's content after it
    if (braceStart != -1) {
      braceEnd = advanceNestedGeneric(stmt, braceStart + 1, '{', '}');
      if (braceEnd != -1 && braceEnd < stmt.length()) {
        String after = stmt.substring(braceEnd).trim();
        if (!after.isEmpty()) {
          return stmt.substring(0, braceEnd) + "; " + after;
        }
      }
    }

    return stmt;
  }

  // Centralized parsing of simple semicolon-separated statements into var decls
  // and final expression.
  private ParseResult parseStatements(String exprSrc) {
    String[] parts = splitByChar(exprSrc);
    List<VarDecl> decls = new ArrayList<>();
    List<String> stmts = new ArrayList<>();
    List<Object> seq = new ArrayList<>();
    String last = "";
    for (String p : parts) {
      p = p.trim();
      if (p.isEmpty())
        continue;
      if (p.startsWith("let ")) {
        // find assignment '=' that is not inside parentheses and not part of '=='
        int eq = -1;
        int depthEq = 0;
        for (int i = 4; i < p.length(); i++) {
          char ch = p.charAt(i);
          if (ch == '(')
            depthEq++;
          else if (ch == ')')
            depthEq--;
          else if (ch == '=' && depthEq == 0) {
            // skip '==' operator and '=>' arrow in types
            if (i + 1 < p.length()) {
              char next = p.charAt(i + 1);
              if (next == '=' || next == '>')
                continue;
            }
            eq = i;
            break;
          }
        }
        String left;
        String rhs;
        // allow declarations without initializer: `let x : I32;`
        if (eq == -1) {
          left = p.substring(4).trim();
          rhs = "";
        } else {
          left = p.substring(4, eq).trim();
          rhs = p.substring(eq + 1).trim();
        }
        // optional 'mut' after let
        boolean isMut = false;
        if (left.startsWith("mut ")) {
          isMut = true;
          left = left.substring(4).trim();
        }
        int colon = left.indexOf(':');
        String name = colon == -1 ? left.trim() : left.substring(0, colon).trim();
        String type = colon == -1 ? "" : left.substring(colon + 1).trim();
        VarDecl vd = new VarDecl(name, rhs, type, isMut);
        decls.add(vd);
        seq.add(vd);
        last = name;
      } else if (p.startsWith("fn ")) {
        // Parse function declaration: fn name(params) : Return => expr
        String[] fnParts = parseFnDeclaration(p);
        if (fnParts == null) {
          // Invalid syntax, treat as regular statement
          last = handleStatementProcessing(p, stmts, seq);
        } else {
          String name = fnParts[0];
          String params = fnParts[1];
          String retType = fnParts[2];
          String body = fnParts[3];
          String remainder = fnParts.length > 4 ? fnParts[4] : "";

          // Create as a function variable declaration
          // Type will be params => returnType (if provided) else default to I32
          String type = params + " => " + (retType == null || retType.isEmpty() ? "I32" : retType);

          // If the body is just a function call like "readInt()",
          // assign the function itself for C compatibility
          String rhs;
          if (body.matches("\\w+\\(\\)")) {
            // Extract function name from "functionName()"
            rhs = body.substring(0, body.indexOf('(')); // Just the function name for C compatibility
          } else {
            rhs = params + " => " + body; // Arrow function for other cases
          }

          VarDecl vd = new VarDecl(name, rhs, type, false);
          decls.add(vd);
          seq.add(vd);
          if (remainder != null && !remainder.trim().isEmpty()) {
            String rem = remainder.trim();
            // treat remainder as following statement(s)
            stmts.add(rem);
            seq.add(rem);
            last = rem;
          } else {
            last = name;
          }
        }
      } else {
        // Check if this statement contains a while loop followed by an expression
        last = handleStatementProcessing(p, stmts, seq);
      }
    }
    // If the last non-let statement is the final expression, don't include it in
    // stmts
    if (!stmts.isEmpty() && last.equals(stmts.getLast())) {
      stmts.removeLast();
      // also remove the trailing element from the ordered seq so we don't emit it
      if (!seq.isEmpty()) {
        Object lastSeq = seq.getLast();
        if (lastSeq instanceof String && last.equals(lastSeq)) {
          seq.removeLast();
        }
      }
    }
    return new ParseResult(decls, stmts, last, seq);
  }

  private String dTypeOf(VarDecl d) {
    return d == null ? null : d.type;
  }

  // Token-aware boolean detection: looks for standalone true/false or '==' token
  private boolean exprLooksBoolean(String s) {
    if (s == null || s.isEmpty())
      return false;
    String t = s.trim();
    // remove surrounding parentheses pairs to expose top-level ternary
    boolean changed = true;
    while (changed && t.length() >= 2 && t.charAt(0) == '(' && t.charAt(t.length() - 1) == ')') {
      changed = false;
      // If the matching closing paren for the opening at index 0 is at the end,
      // strip the outer pair.
      int after = advanceNested(t, 1);
      if (after == t.length()) {
        t = t.substring(1, t.length() - 1).trim();
        changed = true;
      }
    }
    // If it's a ternary expression, inspect both branches by finding a top-level
    // '?'
    int qIdx = -1;
    int search = 0;
    while (true) {
      search = t.indexOf('?', search);
      if (search == -1)
        break;
      if (isTopLevelPos(t, search)) {
        qIdx = search;
        break;
      }
      search += 1;
    }
    if (qIdx != -1) {
      int colon = -1;
      int s2 = qIdx + 1;
      while (true) {
        s2 = t.indexOf(':', s2);
        if (s2 == -1)
          break;
        if (isTopLevelPos(t, s2)) {
          colon = s2;
          break;
        }
        s2 += 1;
      }
      if (colon != -1) {
        String thenPart = t.substring(qIdx + 1, colon).trim();
        String elsePart = t.substring(colon + 1).trim();
        return exprLooksBoolean(thenPart) && exprLooksBoolean(elsePart);
      }
    }

    if (findStandaloneTokenIndex(t, "true", 0) != -1)
      return true;
    if (findStandaloneTokenIndex(t, "false", 0) != -1)
      return true;
    // find '==' occurrences that are not inside identifiers
    int idx = 0;
    while (true) {
      idx = t.indexOf("==", idx);
      if (idx == -1)
        break;
      if (idx > 0) {
        char prev = t.charAt(idx - 1);
        if (Character.isLetterOrDigit(prev) || prev == '_') {
          idx += 2;
          continue;
        }
      }
      int after = idx + 2;
      if (after < t.length()) {
        char next = t.charAt(after);
        if (isIdentifierChar(next)) {
          idx += 2;
          continue;
        }
      }
      return true;
    }
    // detect relational operators (<, >, <=, >=, !=) as boolean
    String[] relOps = new String[] { "<=", ">=", "!=", "<", ">" };
    for (String op : relOps) {
      int id = 0;
      while (true) {
        id = t.indexOf(op, id);
        if (id == -1)
          break;
        // ensure operator is not adjacent to identifier characters
        if (id > 0) {
          char prev = t.charAt(id - 1);
          if (Character.isLetterOrDigit(prev) || prev == '_') {
            id += op.length();
            continue;
          }
        }
        int after = id + op.length();
        if (after < t.length()) {
          char next = t.charAt(after);
          if (isIdentifierChar(next)) {
            id += op.length();
            continue;
          }
        }
        return true;
      }
    }
    return false;
  }

  // Return true if ch is a valid identifier character (letter/digit or
  // underscore)
  private boolean isIdentifierChar(char ch) {
    return Character.isLetterOrDigit(ch) || ch == '_';
  }

  // Return true if statement `stmt` is an assignment whose LHS is exactly
  // varName.
  private boolean isAssignmentTo(String stmt, String varName) {
    String lhs = getAssignmentLhs(stmt);
    return lhs != null && lhs.equals(varName);
  }

  // Return the LHS identifier of a simple assignment statement `name = ...`,
  // or null if the statement is not an assignment.
  private String getAssignmentLhs(String stmt) {
    if (stmt == null)
      return null;
    // 1) simple assignment '=' but not '=='
    int idx = 0;
    while (true) {
      idx = stmt.indexOf('=', idx);
      if (idx == -1)
        break;
      // skip '=='
      if (idx + 1 < stmt.length() && stmt.charAt(idx + 1) == '=') {
        idx += 2;
        continue;
      }
      if (isTopLevelPos(stmt, idx)) {
        int leftIdx = idx - 1;
        // if the char before '=' is an operator (+-*/), skip it to handle '+=' etc.
        if (leftIdx >= 0) {
          char pc = stmt.charAt(leftIdx);
          if (pc == '+' || pc == '-' || pc == '*' || pc == '/')
            leftIdx--;
        }
        return identifierLeftOf(stmt, leftIdx);
      }
      idx += 1;
    }

    // 2) compound assignments like '+=', '-=', '*=', '/='
    String[] comp = new String[] { "+=", "-=", "*=", "/=" };
    for (String op : comp) {
      int i = findTopLevelOp(stmt, op);
      if (i != -1) {
        return identifierLeftOf(stmt, i - 1);
      }
    }

    // 3) postfix 'name++' / 'name--'
    String[] incs = new String[] { "++", "--" };
    for (String op : incs) {
      int i = 0;
      while (true) {
        i = stmt.indexOf(op, i);
        if (i == -1)
          break;
        if (isTopLevelPos(stmt, i)) {
          // try postfix (identifier before op)
          String left = identifierLeftOf(stmt, i - 1);
          if (left != null) {
            return left;
          }
          // try prefix (identifier after op)
          int k = i + op.length();
          while (k < stmt.length() && Character.isWhitespace(stmt.charAt(k)))
            k++;
          if (k < stmt.length() && isIdentifierChar(stmt.charAt(k))) {
            int l = k;
            while (l < stmt.length() && isIdentifierChar(stmt.charAt(l)))
              l++;
            return stmt.substring(k, l);
          }
        }
        i += 1;
      }
    }
    return null;
  }

  // Scan left from index j (inclusive) for an identifier and return it, or
  // null if none found. Skips whitespace before the identifier.
  private String identifierLeftOf(String s, int j) {
    if (s == null || j < 0)
      return null;
    int k = j;
    while (k >= 0 && Character.isWhitespace(s.charAt(k)))
      k--;
    if (k < 0)
      return null;
    int end = k + 1;
    while (k >= 0) {
      char c = s.charAt(k);
      if (Character.isLetterOrDigit(c) || c == '_')
        k--;
      else
        break;
    }
    int start = k + 1;
    if (start >= end)
      return null;
    return s.substring(start, end);
  }

  // Return true if the statement contains a top-level compound assignment
  // (+=, -=, *=, /=) or an increment/decrement (name++/++name/name--/--name).
  private boolean isCompoundOrIncrement(String stmt) {
    if (stmt == null)
      return false;
    String[] ops = new String[] { "++", "--", "+=", "-=", "*=", "/=" };
    for (String op : ops) {
      if (findTopLevelOp(stmt, op) != -1)
        return true;
    }
    return false;
  }

  // Find the index of op in s that is at top-level (not inside parentheses),
  // or -1 if none found.
  private int findTopLevelOp(String s, String op) {
    if (s == null || op == null)
      return -1;
    int idx = 0;
    while (true) {
      idx = s.indexOf(op, idx);
      if (idx == -1)
        return -1;
      if (isTopLevelPos(s, idx))
        return idx;
      idx += 1;
    }
  }

  // Return true if the position pos in s is at top-level (not inside
  // parentheses).
  private boolean isTopLevelPos(String s, int pos) {
    if (s == null || pos < 0)
      return false;
    int depth = 0;
    for (int i = 0; i < pos && i < s.length(); i++) {
      char ch = s.charAt(i);
      if (ch == '(')
        depth++;
      else if (ch == ')')
        depth--;
    }
    return depth == 0;
  }

  // (removed validateReadIntUsage) use findReadIntUsage directly for contextual
  // errors
  // Parse a leading if-expression of the form: if (cond) thenExpr else elseExpr
  // Returns a String[3] = {cond, thenExpr, elseExpr} or null if not an if-expr.
  private String[] parseIfExpression(String src) {
    if (src == null)
      return null;
    String s = src.trim();
    int ifIdx = findStandaloneTokenIndex(s, "if", 0);
    if (ifIdx != 0)
      return null;
    int afterIf = ifIdx + "if".length();
    while (afterIf < s.length() && Character.isWhitespace(s.charAt(afterIf)))
      afterIf++;
    if (afterIf >= s.length() || s.charAt(afterIf) != '(')
      return null;
    int p = advanceNested(s, afterIf + 1);
    if (p == -1)
      return null;
    String cond = s.substring(afterIf + 1, p - 1).trim();
    int thenStart = p;
    while (thenStart < s.length() && Character.isWhitespace(s.charAt(thenStart)))
      thenStart++;
    int elseIdx = findStandaloneTokenIndex(s, "else", thenStart);
    if (elseIdx == -1)
      return null;
    String thenExpr = s.substring(thenStart, elseIdx).trim();
    int afterElse = elseIdx + "else".length();
    while (afterElse < s.length() && Character.isWhitespace(s.charAt(afterElse)))
      afterElse++;
    String elseExpr = s.substring(afterElse).trim();
    if (thenExpr.isEmpty() || elseExpr.isEmpty())
      return null;
    return new String[] { cond, thenExpr, elseExpr };
  }

  // Validate function call arity for calls to declared function variables.
  private Err<Set<Unit>, CompileError> validateFunctionCallArity(String src, List<VarDecl> decls) {
    if (src == null || src.isEmpty())
      return null;
    for (VarDecl vd : decls) {
      if (vd.type != null && vd.type.contains("=>")) {
        String name = vd.name;
        int idx = 0;
        while (true) {
          int pos = findStandaloneTokenIndex(src, name, idx);
          if (pos == -1)
            break;
          int j = skipWhitespace(src, pos + name.length());
          if (j < src.length() && src.charAt(j) == '(') {
            int end = advanceNested(src, j + 1);
            if (end == -1)
              return new Err<>(new CompileError("Unbalanced parentheses in call to '" + name + "'"));
            String argText = src.substring(j + 1, end - 1);
            int argCount = countTopLevelArgs(argText);
            int declParams = countParamsInType(vd.type);
            if (argCount != declParams)
              return new Err<>(new CompileError("Wrong number of arguments in call to '" + name + "'"));
            // Validate argument types against parameter types
            List<String> args = splitTopLevelArgs(argText);
            for (int a = 0; a < args.size(); a++) {
              String at = args.get(a).trim();
              String expected = paramTypeAtIndex(vd.type, a);
              String actual = exprType(at, decls);
              if (expected != null && actual != null && !expected.equals(actual)) {
                return new Err<>(new CompileError("Wrong argument type in call to '" + name + "'"));
              }
            }
            idx = end;
          } else {
            idx = j;
          }
        }
      }
    }
    return null;
  }

  // Split comma-separated top-level args (ignoring nested commas).
  private List<String> splitTopLevelArgs(String s) {
    return splitTopLevel(s, ',', '(', ')');
  }

  // Generic splitter that honors nested pairs (open/close) and splits on sep at
  // top level.
  private List<String> splitTopLevel(String s, char sep, char open, char close) {
    List<String> out = new ArrayList<>();
    if (s == null)
      return out;
    int depth = 0;
    int start = 0;
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == open) {
        depth++;
      } else if (c == close) {
        depth--;
      } else if (c == sep && depth == 0) {
        out.add(s.substring(start, i));
        start = i + 1;
      }
    }
    out.add(s.substring(start));
    return out;
  }

  // Return the declared parameter type at given index from a function type string
  // like '(x : I32, y : Bool) => I32'
  private String paramTypeAtIndex(String funcType, int idx) {
    String inner = getParamsInnerTypeSegment(funcType);
    if (inner == null)
      return null;
    List<String> parts = splitTopLevelArgs(inner);
    if (idx < 0 || idx >= parts.size())
      return null;
    String p = parts.get(idx).trim();
    int colon = p.indexOf(':');
    if (colon == -1)
      return null;
    return p.substring(colon + 1).trim();
  }

  // Infer simple expression type: "I32" or "Bool" or null if unknown.
  private String exprType(String expr, List<VarDecl> decls) {
    if (expr == null)
      return null;
    String s = expr.trim();
    if (s.isEmpty())
      return null;
    if (s.equals("true") || s.equals("false"))
      return "Bool";
    if (isPlainNumeric(s) || isBracedNumeric(s))
      return "I32";
    // readInt() call counts as I32
    if (findReadIntUsage(s) == 1)
      return "I32";
    // function call like name(...) -> return declared function return type if known
    int parenIdx = s.indexOf('(');
    if (parenIdx != -1) {
      String fnName = identifierLeftOf(s, parenIdx - 1);
      if (fnName != null) {
        for (VarDecl vd : decls) {
          if (vd.name.equals(fnName)) {
            String dt = dTypeOf(vd);
            if (dt != null && dt.contains("=>")) {
              int arrow = dt.indexOf("=>");
              String ret = dt.substring(arrow + 2).trim();
              if (ret.isEmpty())
                return "I32";
              return ret;
            }
          }
        }
      }
    }
    // identifier: look up declaration type
    if (s.matches("[A-Za-z_][A-Za-z0-9_]*")) {
      for (VarDecl vd : decls) {
        if (vd.name.equals(s)) {
          String dt = dTypeOf(vd);
          if (dt != null && !dt.isEmpty()) {
            // if type is function type, unknown for arg position
            if (dt.contains("=>"))
              return null;
            return dt;
          }
        }
      }
    }
    return null;
  }

  private int countTopLevelArgs(String s) {
    if (s == null)
      return 0;
    String t = s.trim();
    if (t.isEmpty())
      return 0;
    return splitTopLevelArgs(t).size();
  }

  private int countParamsInType(String type) {
    if (type == null)
      return 0;
    String inner = getParamsInnerTypeSegment(type);
    if (inner == null || inner.isEmpty())
      return 0;
    return countTopLevelArgs(inner);
  }

  // Returns the inner parameter segment from a function type string '(...) =>
  // ...', or null if not a function type.
  private String getParamsInnerTypeSegment(String funcType) {
    if (funcType == null)
      return null;
    int arrow = funcType.indexOf("=>");
    if (arrow == -1)
      return null;
    String params = funcType.substring(0, arrow).trim();
    if (params.length() >= 2 && params.charAt(0) == '(' && params.charAt(params.length() - 1) == ')') {
      return params.substring(1, params.length() - 1).trim();
    }
    return null;
  }

  private int skipWhitespace(String s, int idx) {
    int j = idx;
    while (j < s.length() && Character.isWhitespace(s.charAt(j)))
      j++;
    return j;
  }

  // Detect a function call where the token before '(' is not an identifier
  // Returns an Err with CompileError when found, otherwise null.
  private Err<Set<Unit>, CompileError> detectNonIdentifierCall(String src) {
    if (src == null || src.isEmpty())
      return null;
    int idx = 0;
    while (true) {
      int p = src.indexOf('(', idx);
      if (p == -1)
        break;
      int k = p - 1;
      while (k >= 0 && Character.isWhitespace(src.charAt(k)))
        k--;
      if (k < 0) {
        idx = p + 1;
        continue;
      }
      int end = k + 1;
      int start = k;
      while (start >= 0 && (Character.isLetterOrDigit(src.charAt(start)) || src.charAt(start) == '_'))
        start--;
      start++;
      if (start >= end) {
        return new Err<>(new CompileError("Invalid function call on non-function"));
      }
      char first = src.charAt(start);
      if (!Character.isJavaIdentifierStart(first) && first != '_') {
        return new Err<>(new CompileError("Invalid function call on non-function"));
      }
      idx = p + 1;
    }
    return null;
  }
}
