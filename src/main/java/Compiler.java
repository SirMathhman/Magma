import java.util.Set;
import java.util.HashSet;

public class Compiler {
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
  // closing
  // parenthesis is found. Returns index of the character after the closing ')',
  // or -1 if unmatched.
  private int advanceNested(String s, int p) {
    int depth = 1;
    while (p < s.length() && depth > 0) {
      char ch = s.charAt(p);
      if (ch == '(')
        depth++;
      else if (ch == ')')
        depth--;
      p++;
    }
    return depth == 0 ? p : -1;
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
      int j = end;
      while (j < src.length() && Character.isWhitespace(src.charAt(j)))
        j++;
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

  public Result<java.util.Set<Unit>, CompileError> compile(java.util.Set<Unit> units) {
    Set<Unit> out = new HashSet<>();
    for (Unit u : units) {
      String src = u.input() == null ? "" : u.input();
      String expr = extractExpression(src);

      // parse statements to detect duplicate variable declarations and analyze each
      // part
      ParseResult prCheck = parseStatements(expr);
      java.util.Set<String> seen = new java.util.HashSet<>();
      boolean wantsReadInt = false;
      for (VarDecl d : prCheck.decls) {
        if (!seen.add(d.name)) {
          return new Err<>(new CompileError("Duplicate variable: " + d.name));
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
      }
      // check final expression
      String finalExpr = prCheck.last == null ? "" : prCheck.last;
      int finalUsage = findReadIntUsage(finalExpr);
      if (finalUsage == 2) {
        return new Err<>(new CompileError("Bare 'readInt' used as final expression"));
      }
      if (finalUsage == 3) {
        return new Err<>(new CompileError("'readInt' called with arguments in final expression"));
      }
      if (finalUsage == 1)
        wantsReadInt = true;

      // check non-let statements (e.g., assignments) for readInt usage
      for (String s : prCheck.stmts) {
        int usageStmt = findReadIntUsage(s == null ? "" : s);
        // detect simple assignment to a declared variable: "name = ..."
        String stmtTrim = s == null ? "" : s.trim();
        int eqIdx = stmtTrim.indexOf('=');
        if (eqIdx > 0) {
          String left = stmtTrim.substring(0, eqIdx).trim();
          // if left is a single identifier, check mutability
          boolean ident = left.matches("[A-Za-z_][A-Za-z0-9_]*");
          if (ident) {
            boolean found = false;
            for (VarDecl vd : prCheck.decls) {
              if (vd.name.equals(left)) {
                found = true;
                if (!vd.mut) {
                  return new Err<>(new CompileError("Assignment to immutable variable '" + left + "'"));
                }
                break;
              }
            }
            if (!found) {
              return new Err<>(new CompileError("Assignment to undefined variable '" + left + "'"));
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
          js.append("console.log(" + jsExpr + ");\n");
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
        String prefix = cParts[0];
        String exprC = cParts[1] == null ? "" : cParts[1];
        if (exprC.isEmpty()) {
          c.append("int main() { return 0; }");
        } else {
          boolean looksBoolean = exprLooksBoolean(exprC);
          if (findStandaloneTokenIndex(exprC, "true", 0) != -1 || findStandaloneTokenIndex(exprC, "false", 0) != -1) {
            c.insert(0, "#include <stdbool.h>\n");
          }
          if (prefix.isEmpty()) {
            if (looksBoolean) {
              c.append("int main() { printf(\"%s\", (" + exprC + ") ? \"true\" : \"false\"); return 0; }");
            } else {
              c.append("int main() { int res = " + exprC + "; printf(\"%d\", res); return 0; }");
            }
          } else {
            if (looksBoolean) {
              c.append(
                  "int main() { " + prefix + " printf(\"%s\", (" + exprC + ") ? \"true\" : \"false\"); return 0; }");
            } else {
              c.append("int main() { " + prefix + " int res = " + exprC + "; printf(\"%d\", res); return 0; }");
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
    if (out.isEmpty())
      return "";
    return out;
  }

  // Convert simple language constructs into a JS expression string.
  // Supports optional leading 'let' declarations followed by an expression,
  // separated by semicolons.
  private String buildJsExpression(String exprSrc) {
    ParseResult pr = parseStatements(exprSrc);
    String prefix = renderSeqPrefix(pr, "js");
    String last = pr.last;
    // convert simple `if (cond) thenExpr else elseExpr` to JS ternary
    last = convertIfExpression(last);
    if (prefix.length() == 0)
      return last;
    return "(function(){ " + prefix.toString() + " return (" + last + "); })()";
  }

  // Convert a leading if-expression `if (cond) then else elseExpr` into a JS
  // ternary expression. This is token-aware and avoids regex.
  private String convertIfExpression(String src) {
    if (src == null)
      return "";
    String s = src.trim();
    if (s.isEmpty())
      return s;
    // must start with standalone 'if'
    int ifIdx = findStandaloneTokenIndex(s, "if", 0);
    if (ifIdx != 0)
      return src;
    int afterIf = ifIdx + "if".length();
    // skip whitespace
    while (afterIf < s.length() && Character.isWhitespace(s.charAt(afterIf)))
      afterIf++;
    if (afterIf >= s.length() || s.charAt(afterIf) != '(')
      return src;
    // find matching ')'
    int p = advanceNested(s, afterIf + 1);
    if (p == -1)
      return src; // unbalanced
    String cond = s.substring(afterIf + 1, p - 1).trim();
    // then expression starts at p
    int thenStart = p;
    while (thenStart < s.length() && Character.isWhitespace(s.charAt(thenStart)))
      thenStart++;
    // find standalone 'else' token
    int elseIdx = findStandaloneTokenIndex(s, "else", thenStart);
    if (elseIdx == -1)
      return src;
    // elseIdx is start index of 'else'
    String thenExpr = s.substring(thenStart, elseIdx).trim();
    int afterElse = elseIdx + "else".length();
    while (afterElse < s.length() && Character.isWhitespace(s.charAt(afterElse)))
      afterElse++;
    String elseExpr = s.substring(afterElse).trim();
    if (thenExpr.isEmpty() || elseExpr.isEmpty())
      return src;
    return "((" + cond + ") ? (" + thenExpr + ") : (" + elseExpr + "))";
  }

  // For C we need to return a pair: any prefix statements, and the final
  // expression.
  // Returns [prefix, expr]
  private String[] buildCParts(String exprSrc) {
    ParseResult pr = parseStatements(exprSrc);
    String prefix = renderSeqPrefix(pr, "c");
    String expr = pr.last == null ? "" : pr.last;
    expr = convertIfExpression(expr);
    return new String[] { prefix, expr };
  }

  // Render the ordered seq (VarDecl or statement String) into a language-specific
  // prefix string. 'lang' supports "js" (typescript/js) and "c".
  private String renderSeqPrefix(ParseResult pr, String lang) {
    StringBuilder prefix = new StringBuilder();
    for (Object o : pr.seq) {
      if (o instanceof VarDecl d) {
        if ("c".equals(lang)) {
          if (d.type != null && d.type.contains("=>")) {
            prefix.append("int (*").append(d.name).append(")() = ").append(d.rhs).append("; ");
          } else {
            prefix.append("int ").append(d.name).append(" = ").append(d.rhs).append("; ");
          }
        } else {
          prefix.append(d.mut ? "let " : "const ").append(d.name).append(" = ").append(d.rhs).append("; ");
        }
      } else if (o instanceof String s) {
        prefix.append(s).append("; ");
      }
    }
    return prefix.toString();
  }

  // Simple splitter by character – avoids regex.
  private String[] splitByChar(String s, char ch) {
    java.util.List<String> out = new java.util.ArrayList<>();
    if (s == null)
      return new String[0];
    int start = 0;
    for (int i = 0; i < s.length(); i++) {
      if (s.charAt(i) == ch) {
        out.add(s.substring(start, i));
        start = i + 1;
      }
    }
    out.add(s.substring(start));
    return out.toArray(new String[0]);
  }

  // Small holder for a parsed variable declaration
  private static class VarDecl {
    final String name;
    final String rhs;
    final String type;
    final boolean mut;

    VarDecl(String name, String rhs, String type, boolean mut) {
      this.name = name;
      this.rhs = rhs;
      this.type = type;
      this.mut = mut;
    }
  }

  // Result of parsing statements: list of var decls and the final expression
  private static class ParseResult {
    final java.util.List<VarDecl> decls;
    final String last;
    final java.util.List<String> stmts; // non-let statements in order
    final java.util.List<Object> seq; // ordered sequence of VarDecl or String (stmts)

    ParseResult(java.util.List<VarDecl> decls, java.util.List<String> stmts, String last, java.util.List<Object> seq) {
      this.decls = decls;
      this.stmts = stmts;
      this.last = last;
      this.seq = seq;
    }
  }

  // Centralized parsing of simple semicolon-separated statements into var decls
  // and final expression.
  private ParseResult parseStatements(String exprSrc) {
    String[] parts = splitByChar(exprSrc, ';');
    java.util.List<VarDecl> decls = new java.util.ArrayList<>();
    java.util.List<String> stmts = new java.util.ArrayList<>();
    java.util.List<Object> seq = new java.util.ArrayList<>();
    String last = "";
    for (String p : parts) {
      p = p.trim();
      if (p.isEmpty())
        continue;
      if (p.startsWith("let ")) {
        int eq = p.lastIndexOf('=');
        if (eq == -1)
          continue;
        String left = p.substring(4, eq).trim();
        // optional 'mut' after let
        boolean isMut = false;
        if (left.startsWith("mut ")) {
          isMut = true;
          left = left.substring(4).trim();
        }
        int colon = left.indexOf(':');
        String name = colon == -1 ? left.trim() : left.substring(0, colon).trim();
        String type = colon == -1 ? "" : left.substring(colon + 1).trim();
        String rhs = p.substring(eq + 1).trim();
        VarDecl vd = new VarDecl(name, rhs, type, isMut);
        decls.add(vd);
        seq.add(vd);
        last = name;
      } else {
        stmts.add(p);
        seq.add(p);
        last = p;
      }
    }
    // If the last non-let statement is the final expression, don't include it in
    // stmts
    if (!stmts.isEmpty() && last.equals(stmts.get(stmts.size() - 1))) {
      stmts.remove(stmts.size() - 1);
      // also remove the trailing element from the ordered seq so we don't emit it
      if (!seq.isEmpty()) {
        Object lastSeq = seq.get(seq.size() - 1);
        if (lastSeq instanceof String && last.equals((String) lastSeq)) {
          seq.remove(seq.size() - 1);
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
    // If it's a ternary expression, inspect both branches
    int depth = 0;
    int qIdx = -1;
    for (int i = 0; i < t.length(); i++) {
      char ch = t.charAt(i);
      if (ch == '(')
        depth++;
      else if (ch == ')')
        depth--;
      else if (ch == '?' && depth == 0) {
        qIdx = i;
        break;
      }
    }
    if (qIdx != -1) {
      // find matching ':' at same nesting
      int depth2 = 0;
      int colon = -1;
      for (int i = qIdx + 1; i < t.length(); i++) {
        char ch = t.charAt(i);
        if (ch == '(')
          depth2++;
        else if (ch == ')')
          depth2--;
        else if (ch == ':' && depth2 == 0) {
          colon = i;
          break;
        }
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
        if (Character.isLetterOrDigit(next) || next == '_') {
          idx += 2;
          continue;
        }
      }
      return true;
    }
    return false;
  }

  // (removed validateReadIntUsage) use findReadIntUsage directly for contextual
  // errors
}
