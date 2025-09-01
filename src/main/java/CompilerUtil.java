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

  public static Set<Unit> outSetSingle(Location loc, String ext, String content) {
    Set<Unit> out = new java.util.HashSet<>();
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
    StringBuilder sbts = new StringBuilder();
    sbts.append(readIntPrologTs());
    if (src.contains("readInt()")) {
      String filteredBody = stripExterns(src);
      String[] headTail = splitHeadTail(filteredBody);
      String head = headTail[0];
      String tail = headTail[1];
      if (!head.isBlank()) sbts.append(head).append("\n");
      if (!tail.isBlank()) sbts.append("console.log(").append(tail).append(");\n");
      else sbts.append("// no top-level expression to evaluate\n");
    } else {
      sbts.append("// no entry points\n");
    }

    StringBuilder sbjs = new StringBuilder();
    sbjs.append(readIntPrologJs());
    if (src.contains("readInt()")) {
      String filteredBody = stripExterns(src);
      String[] headTail = splitHeadTail(filteredBody);
      String headJs = headTail[0];
      String tailJs = headTail[1];
      if (!headJs.isBlank()) sbjs.append(headJs).append("\n");
      if (!tailJs.isBlank()) sbjs.append("console.log(").append(tailJs).append(");\n");
      else sbjs.append("// no top-level expression to evaluate\n");
    }
    out.put(".ts", sbts.toString());
    out.put(".js", sbjs.toString());
    return out;
  }

  public static Location locOrDefault(java.util.Set<Unit> units) {
    return units.stream().findFirst().map(Unit::location)
        .orElse(new Location(java.util.Collections.emptyList(), "main"));
  }
}
