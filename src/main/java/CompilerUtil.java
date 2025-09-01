public class CompilerUtil {
  public static String stripExterns(String src) {
    String body = src.replace("\r", "\n");
    String[] lines = body.split("\n");
    StringBuilder filtered = new StringBuilder();
    for (String line : lines) {
      String cleaned = removeExternDeclaration(line);
      if (!cleaned.isBlank()) {
        filtered.append(cleaned).append('\n');
      }
    }
    return filtered.toString().trim();
  }

  public static String[] splitHeadTail(String filteredBody) {
    String expr = filteredBody.replace("\n", " ").trim();
    int lastTopLevelSemicolon = -1;
    int paren = 0, brace = 0, bracket = 0;
    for (int i = 0; i < expr.length(); i++) {
      char c = expr.charAt(i);
      if (c == '(')
        paren++;
      else if (c == ')')
        paren = Math.max(0, paren - 1);
      else if (c == '{')
        brace++;
      else if (c == '}')
        brace = Math.max(0, brace - 1);
      else if (c == '[')
        bracket++;
      else if (c == ']')
        bracket = Math.max(0, bracket - 1);
      else if (c == ';' && paren == 0 && brace == 0 && bracket == 0) {
        lastTopLevelSemicolon = i;
      }
    }
    if (lastTopLevelSemicolon >= 0) {
      String head = expr.substring(0, lastTopLevelSemicolon + 1).trim();
      String tail = expr.substring(lastTopLevelSemicolon + 1).trim();
      return new String[] { head, tail };
    }
    return new String[] { "", expr };
  }

  public static String combineUnitsInput(java.util.Set<Unit> units) {
    StringBuilder combined = new StringBuilder();
    for (Unit u : units) {
      if (u.input() != null && !u.input().isBlank()) {
        combined.append(u.input()).append('\n');
      }
    }
    return combined.toString();
  }

  public static java.util.Set<Unit> outSetSingle(Location loc, String ext, String content) {
    java.util.Set<Unit> out = new java.util.HashSet<>();
    out.add(new Unit(loc, ext, content));
    return out;
  }

  public static String readIntPrologTs() {
    StringBuilder sb = new StringBuilder();
    sb.append("import fs from 'fs';\n");
    sb.append("const input = fs.readFileSync(0, 'utf8');\n");
    sb.append("const tokens = input.trim() ? input.trim().split(/\\s+/) : [];\n");
    sb.append("let _i = 0;\n");
    sb.append("function readInt(): number { return parseInt(tokens[_i++] || '0'); }\n");
    return sb.toString();
  }

  public static String readIntPrologJs() {
    StringBuilder sb = new StringBuilder();
    sb.append("const fs = require('fs');\n");
    sb.append("const input = fs.readFileSync(0, 'utf8');\n");
    sb.append("const tokens = input.trim() ? input.trim().split(/\\s+/) : [];\n");
    sb.append("let _i = 0;\n");
    sb.append("function readInt() { return parseInt(tokens[_i++] || '0'); }\n");
    return sb.toString();
  }

  public static java.util.Map<String, String> emitTsAndJs(String src) {
    java.util.Map<String, String> out = new java.util.HashMap<>();
    out.put(".ts", emitForProlog(readIntPrologTs(), src));
    out.put(".js", emitForProlog(readIntPrologJs(), src));
    return out;
  }

  private static String emitForProlog(String prolog, String src) {
    StringBuilder sb = new StringBuilder();
    sb.append(prolog);
    if (src.contains("readInt()")) {
      String filteredBody = unwrapBracesIfSingleExpression(stripExterns(src));
      // Translate Magma 'let' mutability to JS/TS: 'let mut' -> 'let', 'let' ->
      // 'const'
  String translated = translateLetForJs(filteredBody);
  // convert simple arrow-style function definitions to JS functions
  translated = translateFnArrowToJs(translated);
  translated = translateIfElseToTernary(translated);
      String[] headTail = splitHeadTail(translated);
      String head = headTail[0];
      String tail = headTail[1];
      if (!tail.isBlank() && tail.trim().startsWith("if")) {
        tail = translateIfElseToTernary(tail);
      }
      if (!head.isBlank()) {
        String hoisted = hoistReadIntInFor(head);
        sb.append(hoisted).append("\n");
      }
      if (!tail.isBlank())
        sb.append("console.log(").append(tail).append(");\n");
      else
        sb.append("// no top-level expression to evaluate\n");
    }
    return sb.toString();
  }

  // Convert simple top-level 'fn name() => expr;' into
  // 'function name() { return expr; }'. This only handles the single-line
  // arrow form and does token-aware, top-level scanning (no braces nesting).
  public static String translateFnArrowToJs(String s) {
  return translateFnArrowGeneric(s, "function ", "; ");
  }

  // Convert simple top-level 'fn name() => expr;' into a C function
  // 'int name() { return expr; }'. This is conservative and only handles
  // the single-line arrow form.
  public static String translateFnArrowToC(String s) {
    return translateFnArrowGeneric(s, "int ", "\n");
  }

  // Generic helper to translate simple top-level 'fn name() => expr;' forms
  // into a declaration using the given prefix and suffix. For example, for
  // JS use prefix="function " and suffix="; "; for C use prefix="int " and suffix="\n".
  private static String translateFnArrowGeneric(String s, String declPrefix, String declSuffix) {
    if (s == null || s.isEmpty()) return s;
    StringBuilder out = new StringBuilder();
    int pos = 0;
    int n = s.length();
    while (pos < n) {
      int idx = findNextFnIndex(s, pos);
      if (idx == -1) { out.append(s.substring(pos)); break; }
      // ensure token boundary
      boolean leftOk = idx == 0 || !Character.isLetterOrDigit(s.charAt(idx - 1));
    if (!leftOk) { out.append(s.substring(pos, idx + 2)); pos = idx + 2; continue; }
      int j = idx + 2;
      // skip whitespace
    while (j < n && Character.isWhitespace(s.charAt(j))) j++;
      // read name
      int nameStart = j;
    while (j < n && Character.isJavaIdentifierPart(s.charAt(j))) j++;
    if (j == nameStart) { out.append(s.substring(pos, idx + 2)); pos = idx + 2; continue; }
  String name = s.substring(nameStart, j);
      // skip whitespace
    while (j < n && Character.isWhitespace(s.charAt(j))) j++;
    if (j >= n || s.charAt(j) != '(') { out.append(s.substring(pos, idx + 2)); pos = idx + 2; continue; }
  // find matching ')'
  int paramsEnd = findMatchingClose(s, j, '(', ')');
  if (paramsEnd == -1) { out.append(s.substring(pos, idx + 2)); pos = idx + 2; continue; }
  int k = paramsEnd + 1;
      // skip whitespace
    while (k < n && Character.isWhitespace(s.charAt(k))) k++;
    if (k + 1 >= n || s.charAt(k) != '=' || s.charAt(k + 1) != '>') { out.append(s.substring(pos, idx + 2)); pos = idx + 2; continue; }
  k += 2;
    // skip whitespace
    while (k < n && Character.isWhitespace(s.charAt(k))) k++;
      // read expression until semicolon at top-level
  int exprStart = k;
  int exprEnd = findSemicolonAtTopLevel(s, k);
  if (exprEnd == -1) { out.append(s.substring(pos)); break; }
  String expr = s.substring(exprStart, exprEnd).trim();
      // emit declaration and continue after the semicolon
  out.append(s.substring(pos, idx));
  out.append(declPrefix).append(name).append("() { return ").append(expr).append("; }").append(declSuffix);
  pos = exprEnd + 1;
    }
    return out.toString();
  }

  private static int findNextFnIndex(String s, int start) {
    int idx = s.indexOf("fn", start);
    while (idx != -1) {
      if (idx == 0 || !Character.isLetterOrDigit(s.charAt(idx - 1))) return idx;
      idx = s.indexOf("fn", idx + 2);
    }
    return -1;
  }
  
  private static int findMatchingClose(String s, int startPos, char openChar, char closeChar) {
    int depth = 0;
    for (int i = startPos; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == openChar) depth++;
      else if (c == closeChar) {
        depth--;
        if (depth == 0) return i;
      }
    }
    return -1;
  }

  private static int findSemicolonAtTopLevel(String s, int start) {
    int depth = 0;
    for (int i = start; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == '(') depth++;
      else if (c == ')') depth = Math.max(0, depth - 1);
      else if (c == ';' && depth == 0) return i;
    }
    return -1;
  }

  // Extract top-level C-style function definitions that start with 'int <name>() {'
  // Returns a pair: [functions, remainder]. Functions are concatenated and
  // kept in the same order they were found. This is conservative and only
  // extracts definitions that appear at top-level (not nested inside other
  // constructs).
  public static String[] extractTopLevelIntFunctions(String s) {
    if (s == null || s.isEmpty()) return new String[] { "", s };
    StringBuilder funcs = new StringBuilder();
    StringBuilder rest = new StringBuilder();
    int depthParen = 0, depthBrace = 0, depthBracket = 0;
    int i = 0;
    while (i < s.length()) {
      char c = s.charAt(i);
      // update nesting for all chars up to potential 'int '
      if (c == '(') depthParen++;
      else if (c == ')') depthParen = Math.max(0, depthParen - 1);
      else if (c == '{') depthBrace++;
      else if (c == '}') depthBrace = Math.max(0, depthBrace - 1);
      else if (c == '[') depthBracket++;
      else if (c == ']') depthBracket = Math.max(0, depthBracket - 1);

      if (depthParen == 0 && depthBrace == 0 && depthBracket == 0 && s.startsWith("int ", i)) {
        // attempt to parse 'int name() {'
        int j = i + 4;
        while (j < s.length() && Character.isWhitespace(s.charAt(j))) j++;
        int nameStart = j;
        while (j < s.length() && Character.isJavaIdentifierPart(s.charAt(j))) j++;
        if (j == nameStart) { // not a function
          rest.append(s.charAt(i));
          i++;
          continue;
        }
        while (j < s.length() && Character.isWhitespace(s.charAt(j))) j++;
        if (j >= s.length() || s.charAt(j) != '(') { rest.append(s.charAt(i)); i++; continue; }
        int paramsEnd = findMatchingClose(s, j, '(', ')');
        if (paramsEnd == -1) { rest.append(s.charAt(i)); i++; continue; }
        int k = paramsEnd + 1;
        while (k < s.length() && Character.isWhitespace(s.charAt(k))) k++;
        if (k >= s.length() || s.charAt(k) != '{') { rest.append(s.charAt(i)); i++; continue; }
        int bodyEnd = findMatchingClose(s, k, '{', '}');
        if (bodyEnd == -1) { rest.append(s.charAt(i)); i++; continue; }
        // extract function definition
        String fn = s.substring(i, bodyEnd + 1);
        funcs.append(fn).append('\n');
        i = bodyEnd + 1;
        continue;
      }
      // otherwise copy char to rest
      rest.append(c);
      i++;
    }
    return new String[] { funcs.toString(), rest.toString() };
  }

  // Hoist readInt() calls that appear inside for(...) parentheses by
  // evaluating them once before the loop and replacing occurrences with a
  // generated temp name. This avoids consuming input on every loop
  // iteration when the loop condition calls readInt(). It's conservative
  // and only affects readInt() calls found inside for(...) groups.
  public static String hoistReadIntInFor(String head) {
    return hoistReadIntInForWithPrefix(head, "const ");
  }

  public static String hoistReadIntInForWithPrefix(String head, String declPrefix) {
    StringBuilder out = new StringBuilder();
    StringBuilder pre = new StringBuilder();
    int len = head.length();
    int i = 0;
    int tempCounter = 0;
    while (i < len) {
      int idx = head.indexOf("for", i);
      if (idx == -1) {
        out.append(head.substring(i));
        break;
      }
      // append up to 'for'
      out.append(head, i, idx);
      int j = idx + 3;
      // skip whitespace
      while (j < len && Character.isWhitespace(head.charAt(j)))
        j++;
      if (j >= len || head.charAt(j) != '(') {
        // not a for(, copy 'for' and continue
        out.append("for");
        i = idx + 3;
        continue;
      }
      // find matching ')'
      int depth = 0;
      int startParen = j;
      int endParen = -1;
      for (int k = j; k < len; k++) {
        char c = head.charAt(k);
        if (c == '(')
          depth++;
        else if (c == ')') {
          depth--;
          if (depth == 0) {
            endParen = k;
            break;
          }
        }
      }
      if (endParen == -1) {
        // malformed, copy rest
        out.append(head.substring(idx));
        break;
      }
      String inside = head.substring(startParen + 1, endParen);
      if (inside.contains("readInt()")) {
        String temp = "__RINT" + (tempCounter++) + "__";
        pre.append(declPrefix).append(temp).append(" = readInt();\n");
        // replace all occurrences of readInt() inside this paren with temp
        inside = inside.replace("readInt()", temp);
      }
      // append 'for' and the parenthesis content (with replacements)
      out.append("for");
      out.append(head, startParen, startParen + 1); // append '('
      out.append(inside);
      out.append(')');
      i = endParen + 1;
    }
    if (pre.length() > 0) {
      return pre.toString() + out.toString();
    }
    return out.toString();
  }

  public static String translateLetForJs(String body) {
    if (body == null || body.isBlank())
      return body;
    // Use non-regex transformations for predictable behavior
    String tmp = protectLetMut(body);
    tmp = replaceLetWithConst(tmp);
    tmp = tmp.replace("__LET_MUT__", "let ");
    return tmp;
  }

  // Remove an extern declaration from a line (non-regex). If a line begins
  // with 'extern' and contains a semicolon, drop the extern declaration and
  // return the remainder of the line after the semicolon. Otherwise return
  // the original line.
  public static String removeExternDeclaration(String line) {
    if (line == null)
      return null;
    String t = line;
    int i = 0;
    // skip leading whitespace
    while (i < t.length() && Character.isWhitespace(t.charAt(i)))
      i++;
    if (i >= t.length())
      return "";
    if (!t.startsWith("extern", i))
      return line;
    int semi = t.indexOf(';', i);
    if (semi == -1)
      return line;
    String after = t.substring(semi + 1).trim();
    return after;
  }

  // Replace occurrences of 'let mut' with a placeholder '__LET_MUT__' so we
  // can later transform non-mutable 'let' to 'const' safely, without regex.
  public static String protectLetMut(String s) {
    return processLets(s, true, false);
  }

  // Replace non-mutable 'let ' occurrences (token) with 'const ' using simple
  // scanning instead of regex.
  public static String replaceLetWithConst(String s) {
    return processLets(s, false, true);
  }

  // Shared scanner for 'let' tokens. If protectMut is true, occurrences of
  // 'let mut' become the placeholder '__LET_MUT__'. If toConst is true,
  // non-mutable 'let ' tokens become 'const '. The two flags are mutually
  // exclusive in our usage patterns but are both supported.
  private static String processLets(String s, boolean protectMut, boolean toConst) {
    if (s == null || s.isEmpty())
      return s;
    StringBuilder out = new StringBuilder();
    int i = 0;
    int len = s.length();
    while (i < len) {
      int idx = getLetIndex(s, i);
      if (appendRestIfIdxNotFound(out, s, idx, i))
        break;
      // ensure 'let' is not part of an identifier
      if (idx > 0 && Character.isLetterOrDigit(s.charAt(idx - 1))) {
        out.append("let");
        i = idx + 3;
        continue;
      }
      int j = idx + 3; // index after 'let'
      if (j < len && Character.isWhitespace(s.charAt(j))) {
        int k = j;
        while (k < len && Character.isWhitespace(s.charAt(k)))
          k++;
        // handle 'let mut'
        if (protectMut && k + 3 <= len && s.startsWith("mut", k)
            && (k + 3 == len || !Character.isLetterOrDigit(s.charAt(k + 3)))) {
          out.append("__LET_MUT__");
          i = k + 3;
          continue;
        }
        // handle 'let ' -> 'const '
        if (toConst) {
          out.append("const ");
          i = k; // continue from variable name
          continue;
        }
      }
      // default: copy 'let' and continue
      out.append("let");
      i = j;
    }
    return out.toString();
  }

  public static String unwrapBracesIfSingleExpression(String s) {
    if (s == null)
      return s;
    String t = s.trim();
    if (!t.startsWith("{") || !t.endsWith("}"))
      return s;
    // quick checks: single pair of braces and no semicolons inside -> likely an
    // expression block
    int open = 0;
    int pairs = 0;
    boolean hasSemicolon = false;
    for (int i = 0; i < t.length(); i++) {
      char c = t.charAt(i);
      if (c == '{') {
        open++;
        pairs++;
      } else if (c == '}')
        open--;
      else if (c == ';')
        hasSemicolon = true;
      if (open < 0)
        return s; // malformed
    }
    if (pairs == 1 && !hasSemicolon) {
      return t.substring(1, t.length() - 1).trim();
    }
    return s;
  }

  public static Location locOrDefault(java.util.Set<Unit> units) {
    return units.stream().findFirst().map(Unit::location)
        .orElse(new Location(java.util.Collections.emptyList(), "main"));
  }

  // Convert a top-level 'if (cond) thenExpr else elseExpr' into a ternary
  // '(cond) ? thenExpr : elseExpr'. This is a shallow transformation that
  // only rewrites the outermost if-else if it exactly matches the pattern.
  // It does simple parenthesis/bracket matching to find the condition and
  // splits on the 'else' token at the same nesting level.
  public static String translateIfElseToTernary(String s) {
    if (s == null)
      return s;
    String t = s.trim();
    if (!t.startsWith("if"))
      return s;
    int i = 2;
    int len = t.length();
    // skip whitespace
    while (i < len && Character.isWhitespace(t.charAt(i)))
      i++;
    if (i >= len || t.charAt(i) != '(')
      return s;
    // find matching ')'
    int depth = 0;
    int condStart = i + 1;
    int j = i;
    for (; j < len; j++) {
      char c = t.charAt(j);
      if (c == '(')
        depth++;
      else if (c == ')') {
        depth--;
        if (depth == 0)
          break;
      }
    }
    if (j >= len)
      return s; // malformed
    String cond = t.substring(condStart, j).trim();
    int k = j + 1;
    // skip whitespace
    while (k < len && Character.isWhitespace(t.charAt(k)))
      k++;
    // parse thenExpr until an 'else' token at depth 0
    int thenStart = k;
    depth = 0;
    int elseIdx = -1;
    for (int p = k; p < len; p++) {
      char c = t.charAt(p);
      if (c == '(')
        depth++;
      else if (c == ')')
        depth--;
      // detect 'else' token when depth==0 and it's a standalone token
      if (depth == 0 && p + 4 <= len && t.startsWith("else", p)) {
        // ensure token boundary
        int before = p - 1;
        int after = p + 4;
        boolean okBefore = before < thenStart || !Character.isLetterOrDigit(t.charAt(before));
        boolean okAfter = after >= len || !Character.isLetterOrDigit(t.charAt(after));
        if (okBefore && okAfter) {
          elseIdx = p;
          break;
        }
      }
    }
    if (elseIdx == -1)
      return s;
    String thenExpr = t.substring(thenStart, elseIdx).trim();
    int altStart = elseIdx + 4;
    while (altStart < len && Character.isWhitespace(t.charAt(altStart)))
      altStart++;
    String elseExpr = t.substring(altStart).trim();
    if (cond.isEmpty() || thenExpr.isEmpty() || elseExpr.isEmpty())
      return s;
    return "(" + cond + ") ? (" + thenExpr + ") : (" + elseExpr + ")";
  }

  // Replace standalone 'true'/'false' tokens with '1'/'0' for C codegen.
  public static String translateBoolForC(String s) {
    if (s == null || s.isEmpty())
      return s;
    StringBuilder result = new StringBuilder();
    int pos = 0;
    final int total = s.length();
    while (pos < total) {
      int idxTrue = indexOfToken(s, "true", pos);
      int idxFalse = indexOfToken(s, "false", pos);
      int found = -1;
      String replacement = null;
      if (idxTrue != -1 && (idxFalse == -1 || idxTrue < idxFalse)) {
        found = idxTrue;
        replacement = "1";
      } else if (idxFalse != -1) {
        found = idxFalse;
        replacement = "0";
      }
      if (found == -1) {
        result.append(s.substring(pos));
        break;
      }
      result.append(s, pos, found);
      result.append(replacement);
      pos = found + (replacement.equals("1") ? 4 : 5);
    }
    return result.toString();
  }

  private static int indexOfToken(String s, String token, int start) {
    int len = s.length();
    int tlen = token.length();
    for (int i = start; i + tlen <= len; i++) {
      int idx = s.indexOf(token, i);
      if (idx == -1)
        return -1;
      boolean leftOk = (idx == 0) || !Character.isLetterOrDigit(s.charAt(idx - 1));
      int after = idx + tlen;
      boolean rightOk = (after >= len) || !Character.isLetterOrDigit(s.charAt(after));
      if (leftOk && rightOk)
        return idx;
      i = idx + 1;
    }
    return -1;
  }

  // If idx == -1 append the remainder of s starting at 'start' to out and
  // return true; otherwise append s[start:idx] and return false.
  private static boolean appendRestIfIdxNotFound(StringBuilder out, String s, int idx, int start) {
    if (idx == -1) {
      out.append(s.substring(start));
      return true;
    }
    out.append(s, start, idx);
    return false;
  }

  private static int getLetIndex(String s, int start) {
    if (s == null)
      return -1;
    return s.indexOf("let", start);
  }

}
