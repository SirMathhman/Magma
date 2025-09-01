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
    int lastSemicolon = expr.lastIndexOf(';');
    if (lastSemicolon >= 0) {
      String head = expr.substring(0, lastSemicolon + 1).trim();
      String tail = expr.substring(lastSemicolon + 1).trim();
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
      String[] headTail = splitHeadTail(translated);
      String head = headTail[0];
      String tail = headTail[1];
      if (!head.isBlank())
        sb.append(head).append("\n");
      if (!tail.isBlank())
        sb.append("console.log(").append(tail).append(");\n");
      else
        sb.append("// no top-level expression to evaluate\n");
    }
    return sb.toString();
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
      int idx = s.indexOf("let", i);
      if (idx == -1) {
        out.append(s.substring(i));
        break;
      }
      out.append(s, i, idx);
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
}
