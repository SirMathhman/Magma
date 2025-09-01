public class CompilerUtil {
  public static String stripExterns(String src) {
    String body = src.replace("\r", "\n");
    String[] lines = body.split("\n");
    StringBuilder filtered = new StringBuilder();
    for (String line : lines) {
      String cleaned = line.replaceAll("^\\s*extern\\b.*?;\\s*", "");
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
      // Translate Magma 'let' mutability to JS/TS: 'let mut' -> 'let', 'let' -> 'const'
      String translated = translateLetForJs(filteredBody);
      String[] headTail = splitHeadTail(translated);
      String head = headTail[0];
      String tail = headTail[1];
      if (!head.isBlank()) sb.append(head).append("\n");
      if (!tail.isBlank()) sb.append("console.log(").append(tail).append(");\n");
      else sb.append("// no top-level expression to evaluate\n");
    }
    return sb.toString();
  }

  public static String translateLetForJs(String body) {
    if (body == null || body.isBlank()) return body;
    // Protect mutable lets
    String tmp = body.replaceAll("\\blet\\s+mut\\s+", "__LET_MUT__");
    // Non-mutable lets become const
    tmp = tmp.replaceAll("\\blet\\s+", "const ");
    // Restore mutable lets as 'let '
    tmp = tmp.replace("__LET_MUT__", "let ");
    return tmp;
  }

  public static String unwrapBracesIfSingleExpression(String s) {
    if (s == null) return s;
    String t = s.trim();
    if (!t.startsWith("{") || !t.endsWith("}")) return s;
    // quick checks: single pair of braces and no semicolons inside -> likely an expression block
    int open = 0;
    int pairs = 0;
    boolean hasSemicolon = false;
    for (int i = 0; i < t.length(); i++) {
      char c = t.charAt(i);
      if (c == '{') { open++; pairs++; }
      else if (c == '}') open--;
      else if (c == ';') hasSemicolon = true;
      if (open < 0) return s; // malformed
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
