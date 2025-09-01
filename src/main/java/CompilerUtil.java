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
      // translate struct declarations and constructors into JS object literals
      translated = translateStructsForJs(translated);
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
  // JS use prefix="function " and suffix="; "; for C use prefix="int " and
  // suffix="\n".
  private static String translateFnArrowGeneric(String s, String declPrefix, String declSuffix) {
    if (s == null || s.isEmpty())
      return s;
    StringBuilder out = new StringBuilder();
    int pos = 0;
    int n = s.length();
    while (pos < n) {
      int idx = findNextFnIndex(s, pos);
      if (idx == -1) {
        out.append(s.substring(pos));
        break;
      }
      // ensure token boundary
      boolean leftOk = idx == 0 || !Character.isLetterOrDigit(s.charAt(idx - 1));
      if (!leftOk) {
        out.append(s.substring(pos, idx + 2));
        pos = idx + 2;
        continue;
      }
      int j = idx + 2;
      // skip whitespace
      while (j < n && Character.isWhitespace(s.charAt(j)))
        j++;
      // read name
      int nameStart = j;
      j = readIdentifierEnd(s, j);
      if (j == nameStart) {
        out.append(s.substring(pos, idx + 2));
        pos = idx + 2;
        continue;
      }
      String name = s.substring(nameStart, j);
      // skip whitespace
      while (j < n && Character.isWhitespace(s.charAt(j)))
        j++;
      if (j >= n || s.charAt(j) != '(') {
        out.append(s.substring(pos, idx + 2));
        pos = idx + 2;
        continue;
      }
      // find matching ')'
      int paramsEnd = findMatchingClose(s, j, '(', ')');
      if (paramsEnd == -1) {
        out.append(s.substring(pos, idx + 2));
        pos = idx + 2;
        continue;
      }
      int k = paramsEnd + 1;
      // skip whitespace
      while (k < n && Character.isWhitespace(s.charAt(k)))
        k++;
      if (k + 1 >= n || s.charAt(k) != '=' || s.charAt(k + 1) != '>') {
        out.append(s.substring(pos, idx + 2));
        pos = idx + 2;
        continue;
      }
      k += 2;
      // skip whitespace
      while (k < n && Character.isWhitespace(s.charAt(k)))
        k++;
      // read expression until semicolon at top-level
      int exprStart = k;
      int exprEnd = findSemicolonAtTopLevel(s, k);
      if (exprEnd == -1) {
        out.append(s.substring(pos));
        break;
      }
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
      if (idx == 0 || !Character.isLetterOrDigit(s.charAt(idx - 1)))
        return idx;
      idx = s.indexOf("fn", idx + 2);
    }
    return -1;
  }

  private static int findMatchingClose(String s, int startPos, char openChar, char closeChar) {
    int depth = 0;
    for (int i = startPos; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == openChar)
        depth++;
      else if (c == closeChar) {
        depth--;
        if (depth == 0)
          return i;
      }
    }
    return -1;
  }

  private static int findSemicolonAtTopLevel(String s, int start) {
    int depth = 0;
    for (int i = start; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == '(')
        depth++;
      else if (c == ')')
        depth = Math.max(0, depth - 1);
      else if (c == ';' && depth == 0)
        return i;
    }
    return -1;
  }

  // Extract top-level C-style function definitions that start with 'int <name>()
  // {'
  // Returns a pair: [functions, remainder]. Functions are concatenated and
  // kept in the same order they were found. This is conservative and only
  // extracts definitions that appear at top-level (not nested inside other
  // constructs).
  public static String[] extractTopLevelIntFunctions(String s) {
    if (s == null || s.isEmpty())
      return new String[] { "", s };
    StringBuilder funcs = new StringBuilder();
    StringBuilder rest = new StringBuilder();
    int depthParen = 0, depthBrace = 0, depthBracket = 0;
    int i = 0;
    while (i < s.length()) {
      char c = s.charAt(i);
      // update nesting for all chars up to potential 'int '
      if (c == '(')
        depthParen++;
      else if (c == ')')
        depthParen = Math.max(0, depthParen - 1);
      else if (c == '{')
        depthBrace++;
      else if (c == '}')
        depthBrace = Math.max(0, depthBrace - 1);
      else if (c == '[')
        depthBracket++;
      else if (c == ']')
        depthBracket = Math.max(0, depthBracket - 1);

      if (depthParen == 0 && depthBrace == 0 && depthBracket == 0 && s.startsWith("int ", i)) {
        // attempt to parse 'int name() {'
        int j = i + 4;
        while (j < s.length() && Character.isWhitespace(s.charAt(j)))
          j++;
        int nameStart = j;
        j = readIdentifierEnd(s, j);
        if (j == nameStart) { // not a function
          rest.append(s.charAt(i));
          i++;
          continue;
        }
        while (j < s.length() && Character.isWhitespace(s.charAt(j)))
          j++;
        if (j >= s.length() || s.charAt(j) != '(') {
          rest.append(s.charAt(i));
          i++;
          continue;
        }
        int paramsEnd = findMatchingClose(s, j, '(', ')');
        if (paramsEnd == -1) {
          rest.append(s.charAt(i));
          i++;
          continue;
        }
        int k = paramsEnd + 1;
        while (k < s.length() && Character.isWhitespace(s.charAt(k)))
          k++;
        if (k >= s.length() || s.charAt(k) != '{') {
          rest.append(s.charAt(i));
          i++;
          continue;
        }
        int bodyEnd = findMatchingClose(s, k, '{', '}');
        if (bodyEnd == -1) {
          rest.append(s.charAt(i));
          i++;
          continue;
        }
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

  public static String translateStructsForJs(String s) {
    if (s == null || s.isEmpty())
      return s;
    StringBuilder prefix = new StringBuilder();
    StringBuilder src = new StringBuilder(s);
    java.util.Map<String, String> methodsByType = new java.util.HashMap<>();

    // extract enums -> const Object via helper
    java.util.List<String[]> enums = extractTopLevelEnumBlocks(src);
    for (String[] en : enums) {
      String name = en[0];
      String body = en[1];
      String[] items = body.split(",");
      StringBuilder map = new StringBuilder();
      map.append("const ").append(name).append(" = {");
      int c = 0;
      for (String it0 : items) {
        String it = it0.trim();
        if (it.isEmpty())
          continue;
        if (c > 0)
          map.append(", ");
        map.append(it).append(": ").append(c);
        c++;
      }
      map.append("};\n");
      prefix.append(map.toString());
    }

    // collect impl methods and remove impl blocks via helper
    java.util.List<String[]> impls = extractTopLevelImplBlocks(src);
    for (String[] im : impls) {
      String target = im[1];
      String body = im[2];
      String methods = collectMethodsFromBody(body);
      if (!methods.isEmpty())
        methodsByType.put(target, methods);
    }

    // remove trait declarations
    int idx = 0;
    while (true) {
      int tr = indexOfToken(src.toString(), "trait", idx);
      if (tr == -1)
        break;
      int j = tr + 5;
      while (j < src.length() && Character.isWhitespace(src.charAt(j)))
        j++;
      while (j < src.length() && Character.isJavaIdentifierPart(src.charAt(j)))
        j++;
      while (j < src.length() && Character.isWhitespace(src.charAt(j)))
        j++;
      if (j < src.length() && src.charAt(j) == '{') {
        int bodyEnd = findMatchingClose(src.toString(), j, '{', '}');
        if (bodyEnd == -1)
          break;
        src.delete(tr, bodyEnd + 1);
        continue;
      }
      idx = tr + 5;
    }

    // remove struct declarations and convert constructors, injecting methods
    String base = removeTopLevelStructDecls(src.toString());
    StringBuilder out = new StringBuilder();
    int i = 0;
    while (i < base.length()) {
      int b = base.indexOf('{', i);
      if (b == -1) {
        out.append(base.substring(i));
        break;
      }
      int k = b - 1;
      while (k >= 0 && Character.isWhitespace(base.charAt(k)))
        k--;
      int nameEnd = k;
      while (k >= 0 && Character.isJavaIdentifierPart(base.charAt(k)))
        k--;
      int nameStart = k + 1;
      if (nameStart <= nameEnd) {
        int bodyEnd = findMatchingClose(base, b, '{', '}');
        if (bodyEnd == -1) {
          out.append(base.substring(i));
          break;
        }
        String inner = base.substring(b + 1, bodyEnd).trim();
        String typeName = base.substring(nameStart, nameEnd + 1);
        String meths = methodsByType.get(typeName);
        out.append(base, i, nameStart);
        // build object literal. If inner doesn't contain ':', it's a single-field
        out.append('{');
        if (!inner.isEmpty()) {
          if (!inner.contains(":")) {
            out.append("field: ").append(inner);
            if (meths != null && !meths.isEmpty()) {
              out.append(", ").append(meths);
            }
          } else {
            out.append(inner);
            if (meths != null && !meths.isEmpty()) {
              out.append(", ").append(meths);
            }
          }
        } else {
          if (meths != null && !meths.isEmpty()) {
            out.append(meths);
          }
        }
        out.append('}');
        i = bodyEnd + 1;
        continue;
      }
      out.append(base.substring(i, b + 1));
      i = b + 1;
    }

    return prefix.append(out.toString()).toString();
  }

  // Conservative C-side struct handling: remove top-level 'struct' decls,
  // convert single-field constructors 'Name { expr }' -> '(expr)', and
  // remove '.field' accesses so C code compiles.
  public static String translateStructsForC(String s) {
    if (s == null || s.isEmpty())
      return s;
    // first extract top-level enums to C #defines and remove impl/trait/enum blocks
    StringBuilder src = new StringBuilder(s);
    StringBuilder defines = new StringBuilder();
    int idx = 0;
    while (true) {
      int e = indexOfToken(src.toString(), "enum", idx);
      if (e == -1)
        break;
      int j = e + 4;
      while (j < src.length() && Character.isWhitespace(src.charAt(j)))
        j++;
      int nameStart = j;
      while (j < src.length() && Character.isJavaIdentifierPart(src.charAt(j)))
        j++;
      if (j == nameStart) {
        idx = e + 4;
        continue;
      }
      String name = src.substring(nameStart, j);
      while (j < src.length() && Character.isWhitespace(src.charAt(j)))
        j++;
      if (j >= src.length() || src.charAt(j) != '{') {
        idx = e + 4;
        continue;
      }
      int bodyEnd = findMatchingClose(src.toString(), j, '{', '}');
      if (bodyEnd == -1)
        break;
      String body = src.substring(j + 1, bodyEnd).trim();
      String[] items = body.split(",");
      int c = 0;
      for (String it0 : items) {
        String it = it0.trim();
        if (it.isEmpty())
          continue;
        defines.append("#define ").append(name).append("_").append(it).append(" ").append(c).append('\n');
        c++;
      }
      src.delete(e, bodyEnd + 1);
      // ensure enum removal doesn't leave adjacent tokens glued in C output
      src.insert(e, ";");
      // replace occurrences like Name.Item -> Name_Item
      String repl = src.toString().replace(name + ".", name + "_");
      src = new StringBuilder(repl);
      idx = e + 1;
    }
    // extract impl methods as top-level C functions and remove impl blocks
    idx = 0;
    while (true) {
      int im = indexOfToken(src.toString(), "impl", idx);
      if (im == -1)
        break;
      int j = im + 4;
      while (j < src.length() && Character.isWhitespace(src.charAt(j)))
        j++;
      // read possible trait/type and optional 'for' target
      // skip possible identifier after 'impl'
      while (j < src.length() && Character.isJavaIdentifierPart(src.charAt(j)))
        j++;
      while (j < src.length() && Character.isWhitespace(src.charAt(j)))
        j++;
      if (src.toString().startsWith("for", j)) {
        j += 3;
        while (j < src.length() && Character.isWhitespace(src.charAt(j)))
          j++;
        j = readIdentifierEnd(src.toString(), j);
      }
      while (j < src.length() && src.charAt(j) != '{')
        j++;
      if (j >= src.length() || src.charAt(j) != '{') {
        idx = im + 4;
        continue;
      }
      int bodyEnd = findMatchingClose(src.toString(), j, '{', '}');
      if (bodyEnd == -1)
        break;
      String body = src.substring(j + 1, bodyEnd);
      // parse 'fn name() => expr;' patterns inside impl body and inline
      // method calls by replacing '<ident>.name()' with '(expr)'
      int p = 0;
      while (p < body.length()) {
        int f = body.indexOf("fn", p);
        if (f == -1)
          break;
        // ensure token boundary
        if (f > 0 && Character.isLetterOrDigit(body.charAt(f - 1))) {
          p = f + 2;
          continue;
        }
        int q = f + 2;
        while (q < body.length() && Character.isWhitespace(body.charAt(q)))
          q++;
        int nmStart = q;
        while (q < body.length() && Character.isJavaIdentifierPart(body.charAt(q)))
          q++;
        if (q == nmStart) {
          p = f + 2;
          continue;
        }
        String mname = body.substring(nmStart, q);
        while (q < body.length() && body.charAt(q) != '=')
          q++;
        if (q + 1 >= body.length() || body.charAt(q) != '=' || body.charAt(q + 1) != '>') {
          p = f + 2;
          continue;
        }
        q += 2;
        // read expression until semicolon
        int exprStart = q;
        int exprEnd = findSemicolonAtTopLevel(body, q);
        if (exprEnd == -1)
          break;
        String expr = body.substring(exprStart, exprEnd).trim();
        // inline replace '<ident>.mname()' with the method expression
        String callPat = "." + mname + "()";
        int repIdx = 0;
        while (true) {
          int occ = src.indexOf(callPat, repIdx);
          if (occ == -1)
            break;
          // find identifier start before the dot
          int idEnd = occ - 1;
          int idScan = idEnd;
          while (idScan >= 0 && Character.isJavaIdentifierPart(src.charAt(idScan)))
            idScan--;
          idScan++;
          if (idScan <= idEnd) {
            src.replace(idScan, occ + callPat.length(), "(" + expr + ")");
            repIdx = idScan + expr.length() + 2; // move past replacement
          } else {
            repIdx = occ + callPat.length();
          }
        }
        p = exprEnd + 1;
      }
      // remove the impl block entirely
      src.delete(im, bodyEnd + 1);
      idx = im;
    }
    // remove trait blocks
    idx = 0;
    while (true) {
      int tr = indexOfToken(src.toString(), "trait", idx);
      if (tr == -1)
        break;
      int j = tr + 5;
      while (j < src.length() && src.charAt(j) != '{')
        j++;
      if (j >= src.length() || src.charAt(j) != '{') {
        idx = tr + 5;
        continue;
      }
      int bodyEnd = findMatchingClose(src.toString(), j, '{', '}');
      if (bodyEnd == -1)
        break;
      src.delete(tr, bodyEnd + 1);
      idx = tr;
    }

    String base = removeTopLevelStructDecls(src.toString());
    String conv = convertSingleFieldConstructors(base, "(%s)", true);
    // prepend defines and emitted functions
    if (defines.length() > 0) {
      conv = defines.toString() + "\n" + conv;
    }
    // convert empty constructor '()' (produced for empty structs) to 0 for C
    conv = conv.replaceAll("=\\s*\\(\\s*\\)", "= 0");
    // convertSingleFieldConstructors with removeFieldAccess=true already strips
    // '.field'
    // mangle standalone identifier 'struct' -> '__struct' to avoid C keyword
    StringBuilder mangled = new StringBuilder();
    int pos = 0;
    while (pos < conv.length()) {
      int sidx = indexOfToken(conv, "struct", pos);
      if (sidx == -1) {
        mangled.append(conv.substring(pos));
        break;
      }
      mangled.append(conv, pos, sidx);
      mangled.append("__struct");
      pos = sidx + "struct".length();
    }
    String result = mangled.length() == 0 ? conv : mangled.toString();
    // Remove any remaining enum blocks conservatively (safety net)
    int scan = 0;
    while (true) {
      int ei = indexOfToken(result, "enum", scan);
      if (ei == -1)
        break;
      int j = ei + 4;
      while (j < result.length() && Character.isWhitespace(result.charAt(j)))
        j++;
      // skip identifier
      j = readIdentifierEnd(result, j);
      while (j < result.length() && Character.isWhitespace(result.charAt(j)))
        j++;
      if (j < result.length() && result.charAt(j) == '{') {
        int bodyEnd = findMatchingClose(result, j, '{', '}');
        if (bodyEnd == -1)
          break;
        result = result.substring(0, ei) + ";" + result.substring(bodyEnd + 1);
        scan = ei + 1;
      } else {
        scan = j + 1;
      }
    }
    // Ensure dot-accesses like Name.Item are converted to Name_Item for all defined
    // enums
    if (defines.length() > 0) {
      for (String L : defines.toString().split("\n")) {
        if (!L.startsWith("#define "))
          continue;
        String rem = L.substring(8);
        int us = rem.indexOf('_');
        if (us > 0) {
          String ename = rem.substring(0, us);
          result = result.replace(ename + ".", ename + "_");
        }
      }
    }
    return result;
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

  private static int readIdentifierEnd(String s, int pos) {
    int j = pos;
    while (j < s.length() && Character.isJavaIdentifierPart(s.charAt(j)))
      j++;
    return j;
  }

  // Extract top-level enum blocks. Returns list of [name, body]. Consumes nothing
  // from the input string builder; callers may choose to remove ranges
  // separately.
  private static java.util.List<String[]> extractTopLevelEnumBlocks(StringBuilder src) {
    java.util.List<String[]> out = new java.util.ArrayList<>();
    int idx = 0;
    String s = src.toString();
    while (true) {
      int e = indexOfToken(s, "enum", idx);
      if (e == -1)
        break;
      int j = e + 4;
      while (j < s.length() && Character.isWhitespace(s.charAt(j)))
        j++;
      int nameStart = j;
      j = readIdentifierEnd(s, j);
      if (j == nameStart) {
        idx = e + 4;
        continue;
      }
      String name = s.substring(nameStart, j);
      while (j < s.length() && Character.isWhitespace(s.charAt(j)))
        j++;
      if (j >= s.length() || s.charAt(j) != '{') {
        idx = e + 4;
        continue;
      }
      int bodyEnd = findMatchingClose(s, j, '{', '}');
      if (bodyEnd == -1)
        break;
      String body = s.substring(j + 1, bodyEnd);
      out.add(new String[] { name, body });
      // remove the block from source so callers don't see it
      src.delete(e, bodyEnd + 1);
      s = src.toString();
      idx = e;
    }
    return out;
  }

  // Extract top-level impl blocks. Returns list of [firstIdent, targetIdent,
  // body].
  private static java.util.List<String[]> extractTopLevelImplBlocks(StringBuilder src) {
    java.util.List<String[]> out = new java.util.ArrayList<>();
    String s = src.toString();
    int idx = 0;
    while (true) {
      int im = indexOfToken(s, "impl", idx);
      if (im == -1)
        break;
      int j = im + 4;
      while (j < s.length() && Character.isWhitespace(s.charAt(j)))
        j++;
      int idStart = j;
      j = readIdentifierEnd(s, j);
      if (j == idStart) {
        idx = im + 4;
        continue;
      }
      String first = s.substring(idStart, j);
      while (j < s.length() && Character.isWhitespace(s.charAt(j)))
        j++;
      String target = first;
      if (s.startsWith("for", j)) {
        j += 3;
        while (j < s.length() && Character.isWhitespace(s.charAt(j)))
          j++;
        int id2 = j;
        j = readIdentifierEnd(s, j);
        if (j > id2)
          target = s.substring(id2, j);
      }
      while (j < s.length() && s.charAt(j) != '{')
        j++;
      if (j >= s.length() || s.charAt(j) != '{') {
        idx = im + 4;
        continue;
      }
      int bodyEnd = findMatchingClose(s, j, '{', '}');
      if (bodyEnd == -1)
        break;
      String body = s.substring(j + 1, bodyEnd);
      out.add(new String[] { target, target, body });
      // remove impl block from source
      src.delete(im, bodyEnd + 1);
      s = src.toString();
      idx = im;
    }
    return out;
  }

  // Split out lines starting with '#define' from a string. Returns [defines,
  // rest]
  public static String[] splitOutDefines(String s) {
    if (s == null)
      return new String[] { "", "" };
    if (!s.contains("#define"))
      return new String[] { "", s };
    String[] lines = s.split("\n", -1);
    StringBuilder rest = new StringBuilder();
    StringBuilder dacc = new StringBuilder();
    for (String L : lines) {
      if (L.trim().startsWith("#define")) {
        dacc.append(L.trim()).append('\n');
      } else {
        rest.append(L).append('\n');
      }
    }
    if (rest.length() > 0 && rest.charAt(rest.length() - 1) == '\n')
      rest.setLength(rest.length() - 1);
    return new String[] { dacc.toString(), rest.toString() };
  }

  // Split out top-level enum blocks into #define lines and return [defines, rest]
  public static String[] splitOutEnumDefines(String s) {
    if (s == null)
      return new String[] { "", "" };
    StringBuilder defs = new StringBuilder();
    StringBuilder rest = new StringBuilder(s);
    int idx = 0;
    while (true) {
      int e = indexOfToken(rest.toString(), "enum", idx);
      if (e == -1)
        break;
      int j = e + 4;
      while (j < rest.length() && Character.isWhitespace(rest.charAt(j)))
        j++;
      int nameStart = j;
      j = readIdentifierEnd(rest.toString(), j);
      if (j == nameStart) {
        idx = e + 4;
        continue;
      }
      String name = rest.substring(nameStart, j);
      while (j < rest.length() && Character.isWhitespace(rest.charAt(j)))
        j++;
      if (j >= rest.length() || rest.charAt(j) != '{') {
        idx = e + 4;
        continue;
      }
      int bodyEnd = findMatchingClose(rest.toString(), j, '{', '}');
      if (bodyEnd == -1)
        break;
      String body = rest.substring(j + 1, bodyEnd).trim();
      String[] items = body.split(",");
      int c = 0;
      for (String it0 : items) {
        String it = it0.trim();
        if (it.isEmpty())
          continue;
        defs.append("#define ").append(name).append("_").append(it).append(" ").append(c).append('\n');
        c++;
      }
      // remove enum block and replace occurrences Name.Item -> Name_Item
      rest.delete(e, bodyEnd + 1);
      rest.insert(e, ";");
      String repl = rest.toString().replace(name + ".", name + "_");
      rest = new StringBuilder(repl);
      idx = e + 1;
    }
    return new String[] { defs.toString(), rest.toString() };
  }

  // Run common normalization steps for C code generation: arrow->C, let handling
  // and simple const replacement. Returns normalized code.
  public static String normalizeForC(String s) {
    if (s == null)
      return s;
    s = translateFnArrowToC(s);
    s = protectLetMut(s);
    s = replaceLetWithConst(s);
    s = addIntAfterConst(s);
    s = s.replace("__LET_MUT__", "int ");
    return s;
  }

  private static String addIntAfterConst(String s) {
    if (s == null || !s.contains("const"))
      return s;
    StringBuilder out = new StringBuilder();
    int pos = 0;
    while (pos < s.length()) {
      int idx = indexOfToken(s, "const", pos);
      if (idx == -1) {
        out.append(s.substring(pos));
        break;
      }
      out.append(s, pos, idx + 5); // include 'const'
      int j = idx + 5;
      // skip whitespace
      while (j < s.length() && Character.isWhitespace(s.charAt(j)))
        j++;
      // if next token already 'int', don't add
      if (!s.startsWith("int", j)) {
        out.append(" int ");
      } else {
        out.append(s, idx + 5, j);
      }
      pos = j;
    }
    return out.toString();
  }

  // Collect 'function' declarations inside an impl body and return as
  // 'name: function(...) { ... }, ...' string (no surrounding braces).
  private static String collectMethodsFromBody(String body) {
    if (body == null || body.isEmpty())
      return "";
    StringBuilder methods = new StringBuilder();
    int p = 0;
    while (p < body.length()) {
      int f = body.indexOf("function", p);
      if (f == -1)
        break;
      int q = f + "function".length();
      while (q < body.length() && Character.isWhitespace(body.charAt(q)))
        q++;
      int nmStart = q;
      while (q < body.length() && Character.isJavaIdentifierPart(body.charAt(q)))
        q++;
      if (q == nmStart) {
        p = f + 8;
        continue;
      }
      String mname = body.substring(nmStart, q);
      while (q < body.length() && body.charAt(q) != '(')
        q++;
      int paramsEnd = findMatchingClose(body, q, '(', ')');
      if (paramsEnd == -1)
        break;
      int funcBodyStart = paramsEnd + 1;
      while (funcBodyStart < body.length() && Character.isWhitespace(body.charAt(funcBodyStart)))
        funcBodyStart++;
      if (funcBodyStart >= body.length() || body.charAt(funcBodyStart) != '{')
        break;
      int funcBodyEnd = findMatchingClose(body, funcBodyStart, '{', '}');
      if (funcBodyEnd == -1)
        break;
      String funcText = body.substring(q, funcBodyEnd + 1);
      if (methods.length() > 0)
        methods.append(", ");
      methods.append(mname).append(": ").append("function").append(funcText);
      p = funcBodyEnd + 1;
    }
    return methods.toString();
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

  private static String removeTopLevelStructDecls(String s) {
    if (s == null || s.isEmpty())
      return s;
    StringBuilder without = new StringBuilder();
    int p = 0;
    int L = s.length();
    while (p < L) {
      int idx = s.indexOf("struct", p);
      if (idx == -1) {
        without.append(s.substring(p));
        break;
      }
      without.append(s, p, idx);
      if (idx > 0 && Character.isLetterOrDigit(s.charAt(idx - 1))) {
        without.append("struct");
        p = idx + 6;
        continue;
      }
      int j = idx + 6;
      while (j < L && Character.isWhitespace(s.charAt(j)))
        j++;
      int nameStart = j;
      while (j < L && Character.isJavaIdentifierPart(s.charAt(j)))
        j++;
      if (j == nameStart) {
        without.append("struct");
        p = idx + 6;
        continue;
      }
      while (j < L && Character.isWhitespace(s.charAt(j)))
        j++;
      if (j >= L || s.charAt(j) != '{') {
        without.append(s.substring(idx, j));
        p = j;
        continue;
      }
      int bodyEnd = findMatchingClose(s, j, '{', '}');
      if (bodyEnd == -1) {
        without.append(s.substring(idx));
        break;
      }
      p = bodyEnd + 1;
    }
    return without.toString();
  }

  private static String convertSingleFieldConstructors(String base, String format, boolean removeFieldAccess) {
    if (base == null || base.isEmpty())
      return base;
    StringBuilder out = new StringBuilder();
    int i = 0;
    int N = base.length();
    while (i < N) {
      int idx = base.indexOf('{', i);
      if (idx == -1) {
        out.append(base.substring(i));
        break;
      }
      int k = idx - 1;
      while (k >= 0 && Character.isWhitespace(base.charAt(k)))
        k--;
      int nameEnd = k;
      while (k >= 0 && Character.isJavaIdentifierPart(base.charAt(k)))
        k--;
      int nameStart2 = k + 1;
      if (nameStart2 <= nameEnd) {
        int bodyEnd2 = findMatchingClose(base, idx, '{', '}');
        if (bodyEnd2 == -1) {
          out.append(base.substring(i));
          break;
        }
        String inner = base.substring(idx + 1, bodyEnd2).trim();
        if (!inner.contains(":")) {
          out.append(base, i, nameStart2);
          out.append(String.format(format, inner));
          i = bodyEnd2 + 1;
          continue;
        }
      }
      out.append(base.substring(i, idx + 1));
      i = idx + 1;
    }
    String result = out.length() == 0 ? base : out.toString();
    if (removeFieldAccess)
      result = result.replace(".field", "");
    return result;
  }

}
