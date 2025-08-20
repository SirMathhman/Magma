package com.example.magma;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public final class Structs {
  private Structs() {
  }

  public static record StructDef(String name, String body, int removeEnd) {
  }

  public static String[] extractNextStruct(String remaining) {
    if (remaining == null)
      return null;
    int idx = findStructureIndex(remaining);
    if (idx == -1)
      return null;
    remaining = remaining.substring(idx).trim();
    int bOpen = remaining.indexOf('{');
    int bClose = remaining.indexOf('}', bOpen);
    if (bOpen == -1 || bClose == -1)
      return null;
    int semi = remaining.indexOf(';', bClose);
    String header = remaining.substring(0, bOpen).trim();
    String prefix = header.startsWith("structure") ? "structure" : "struct";
    String name = header.substring(prefix.length()).trim();
    String bodyContent = remaining.substring(bOpen + 1, bClose).trim();
    int removeEnd = (semi == -1) ? (bClose + 1) : (semi + 1);
    return new String[] { name, bodyContent, String.valueOf(removeEnd) };
  }

  public static List<StructDef> getStructDefs(String fullBody) {
    List<StructDef> list = new ArrayList<>();
    if (fullBody == null || fullBody.isEmpty())
      return list;
    String remaining = fullBody;
    while (true) {
      String[] nxt = extractNextStruct(remaining);
      if (nxt == null)
        break;
      list.add(new StructDef(nxt[0], nxt[1], Integer.parseInt(nxt[2])));
      remaining = remaining.substring(Integer.parseInt(nxt[2]));
    }
    return list;
  }

  public static int countNonEmpty(String csv) {
    if (isBlank(csv))
      return 0;
    int cnt = 0;
    for (String s : csv.split(",")) {
      if (!s.trim().isEmpty())
        cnt++;
    }
    return cnt;
  }

  public static int findStructureIndex(String s) {
    if (s == null)
      return -1;
    int a = s.indexOf("structure ");
    int b = s.indexOf("struct ");
    if (a == -1)
      return b;
    if (b == -1)
      return a;
    return Math.min(a, b);
  }

  public static String[] splitElements(String csv) {
    if (csv == null || csv.trim().isEmpty())
      return new String[0];
    String[] parts = csv.split(",");
    for (int i = 0; i < parts.length; i++)
      parts[i] = parts[i].trim();
    return parts;
  }

  public static void validateStructInitElements(String sname, String inner, String[] types) {
    String[] elems = splitElements(inner);
    for (int i = 0; i < elems.length && i < types.length; i++) {
      String el = elems[i];
      String exp = types[i] == null ? "" : types[i].trim();
      if ("I32".equals(exp) && ("true".equals(el) || "false".equals(el))) {
        throw new CompileException("Struct initializer for " + sname
            + " has mismatched type for field " + i + ": expected " + exp);
      }
    }
  }

  public static java.util.Map<String, Integer> structFieldCounts(String fullBody) {
    java.util.Map<String, Integer> structFields = new java.util.HashMap<>();
    if (isBlank(fullBody))
      return structFields;
    for (StructDef def : getStructDefs(fullBody)) {
      structFields.put(def.name(), countNonEmpty(def.body()));
    }
    return structFields;
  }

  public static java.util.Map<String, String[]> structDefinitions(String fullBody) {
    java.util.Map<String, String[]> map = new java.util.HashMap<>();
    if (isBlank(fullBody))
      return map;
    for (StructDef def : getStructDefs(fullBody)) {
      String name = def.name();
      String bodyContent = def.body();
      if (bodyContent.isEmpty()) {
        map.put(name, new String[0]);
        continue;
      }
      String[] parts = splitElements(bodyContent);
      String[] types = new String[parts.length];
      for (int i = 0; i < parts.length; i++) {
        String p = parts[i];
        if (p == null || p.isEmpty()) {
          types[i] = "";
          continue;
        }
        int colon = p.indexOf(':');
        String type = colon != -1 ? p.substring(colon + 1).trim() : "";
        types[i] = type;
      }
      map.put(name, types);
    }
    return map;
  }

  public static String[] extractParamNamesFromHeader(String header, int paren) {
    String params = "";
    if (paren != -1) {
      int parenClose = header.indexOf(')', paren);
      if (parenClose != -1)
        params = header.substring(paren + 1, parenClose).trim();
    }
    if (params.isEmpty())
      return new String[0];
    String[] parts = splitElements(params);
    List<String> names = new ArrayList<>();
    for (String p : parts) {
      String name = p == null ? "" : p.trim();
      if (name.isEmpty())
        continue;
      int colon = name.indexOf(':');
      String pName = colon != -1 ? name.substring(0, colon).trim() : name;
      if (!pName.isEmpty())
        names.add(pName);
    }
    return names.toArray(new String[0]);
  }

  public static int findMatchingBrace(String s, int openIdx) {
    if (s == null || openIdx < 0 || openIdx >= s.length() || s.charAt(openIdx) != '{')
      return -1;
    int depth = 0;
    for (int i = openIdx; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == '{')
        depth++;
      else if (c == '}') {
        depth--;
        if (depth == 0)
          return i;
      }
    }
    return -1;
  }

  public static int computeBraceDepthUpTo(String s, int pos) {
    if (s == null || pos <= 0)
      return 0;
    int depth = 0;
    char[] a = s.toCharArray();
    int limit = Math.min(pos, a.length);
    for (int i = 0; i < limit; i++) {
      char ch = a[i];
      if (ch == '{') {
        depth = depth + 1;
      } else if (ch == '}') {
        if (depth > 0)
          depth = depth - 1;
      }
    }
    return depth;
  }

  public static int findSemicolonAfterMatchingBrace(String s, int openIdx) {
    int close = findMatchingBrace(s, openIdx);
    if (close == -1)
      return -1;
    return s.indexOf(';', close + 1);
  }

  public static final class LocalParseResult {
    public final List<String> localDecls;
    public final List<String> fieldNames;
    public final String remaining;

    public LocalParseResult(List<String> localDecls, List<String> fieldNames, String remaining) {
      this.localDecls = localDecls == null ? Collections.emptyList() : localDecls;
      this.fieldNames = fieldNames == null ? Collections.emptyList() : fieldNames;
      this.remaining = remaining == null ? "" : remaining;
    }
  }

  public static void emitStructReturnFromFields(String typedefName, java.util.List<String> localDecls,
      java.util.List<String> fieldNames, String fnName, String paramsDecl, StringBuilder sb) {
    sb.append("typedef struct {");
    for (String fn : fieldNames) {
      sb.append(" int ").append(fn).append(";");
    }
    sb.append(" } ").append(typedefName).append(";\n");
    sb.append(typedefName).append(" ").append(fnName).append("(").append(paramsDecl).append(") { ");
    for (String d : localDecls)
      sb.append(d);
    sb.append("  return (").append(typedefName).append("){ ");
    for (int i = 0; i < fieldNames.size(); i++) {
      if (i > 0)
        sb.append(',').append(' ');
      sb.append(fieldNames.get(i));
    }
    sb.append(" }; }\n");
  }

  private static boolean isBlank(String s) {
    return s == null || s.trim().isEmpty();
  }
}
