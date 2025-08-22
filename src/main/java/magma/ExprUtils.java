package magma;

public class ExprUtils {
  // parse an '&' or '&mut' expression starting at idx, append to out and return
  // new idx
  public static int handleAmpersand(String s, int idx, StringBuilder out, java.util.Set<String> letNames)
      throws CompileException {
    int len = s.length();
    int save = idx;
    idx++; // consume '&'
    if (s.startsWith("mut", idx)) {
      idx += 3;
      while (idx < len && Character.isWhitespace(s.charAt(idx)))
        idx++;
    }
    if (idx < len && Character.isJavaIdentifierStart(s.charAt(idx))) {
      StringBuilder id = new StringBuilder();
      while (idx < len && Character.isJavaIdentifierPart(s.charAt(idx))) {
        id.append(s.charAt(idx));
        idx++;
      }
      String name = id.toString();
      if (letNames != null && letNames.contains(name)) {
        out.append("&let_").append(name);
      } else {
        out.append("&").append(name);
      }
      return idx;
    }
    // restore position for error message
    idx = save;
    throw new CompileException("Invalid reference expression at index " + idx + " in: '" + s + "'");
  }

  // parse '*' unary when appropriate, else treat as binary multiply; return new
  // idx
  public static int handleAsterisk(String s, int idx, StringBuilder out, java.util.Set<String> letNames,
      java.util.Map<String, String> types) throws CompileException {
    if (isUnaryAsterisk(s, idx)) {
      int lookahead = idx + 1;
      int len = s.length();
      while (lookahead < len && Character.isWhitespace(s.charAt(lookahead)))
        lookahead++;
      int t = lookahead;
      StringBuilder id = new StringBuilder();
      while (t < len && Character.isJavaIdentifierPart(s.charAt(t))) {
        id.append(s.charAt(t));
        t++;
      }
      String namePeek = id.toString();
      idx = lookahead + id.length();
      String name = namePeek;
      boolean isLet = letNames != null && letNames.contains(name);
      boolean isPointer = isLet && types != null && types.containsKey(name)
          && (types.get(name).startsWith("*") || types.get(name).startsWith("mut *") || types.get(name).contains("*"));
      if (isPointer) {
        out.append("*let_").append(name);
      } else if (isLet) {
        out.append("let_").append(name);
      } else {
        out.append("*").append(name);
      }
      return idx;
    }
    // otherwise binary multiply
    out.append('*');
    return idx + 1;
  }

  private static boolean isUnaryAsterisk(String s, int idx) {
    int len = s.length();
    int lookahead = idx + 1;
    while (lookahead < len && Character.isWhitespace(s.charAt(lookahead)))
      lookahead++;
    if (lookahead < len && Character.isJavaIdentifierStart(s.charAt(lookahead))) {
      int back = idx - 1;
      while (back >= 0 && Character.isWhitespace(s.charAt(back)))
        back--;
      return !(back >= 0 && (Character.isJavaIdentifierPart(s.charAt(back)) || Character.isDigit(s.charAt(back))
          || s.charAt(back) == ')'));
    }
    return false;
  }

  // handle identifier rewrite when it's a let name; returns new idx or -1 if not
  // handled
  public static int handleIdentifierWithLets(String s, int idx, StringBuilder out, java.util.Set<String> letNames) {
    int len = s.length();
    if (idx < len && Character.isJavaIdentifierStart(s.charAt(idx))) {
      StringBuilder id = new StringBuilder();
      while (idx < len && Character.isJavaIdentifierPart(s.charAt(idx))) {
        id.append(s.charAt(idx));
        idx++;
      }
      String name = id.toString();
      if (letNames != null && letNames.contains(name)) {
        out.append("let_").append(name);
        return idx;
      }
      // not a let name -> signal not handled so caller can interpret differently
      return -1;
    }
    return -1;
  }

  // return consumed length of alias call like name() if matches a funcAliases key
  public static int aliasCallConsumed(String s, int idx, java.util.Map<String, String> funcAliases) {
    if (funcAliases == null)
      return 0;
    int len = s.length();
    for (String alias : funcAliases.keySet()) {
      String call = alias + "()";
      if (idx + call.length() <= len && s.startsWith(call, idx)) {
        return call.length();
      }
    }
    return 0;
  }

  public static boolean tryHandleFunctionAlias(String name, String declExpr, String declType,
      java.util.Map<String, String> funcAliases) {
    if (declType == null)
      return false;
    if (!declType.contains("=>"))
      return false;
    if (declExpr == null || declExpr.isEmpty())
      return false;
    if (!Character.isJavaIdentifierStart(declExpr.charAt(0)))
      return false;
    for (int i = 1; i < declExpr.length(); i++) {
      if (!Character.isJavaIdentifierPart(declExpr.charAt(i)))
        return false;
    }
    // allow aliasing any bare identifier (intrinsic or a previously-declared
    // function)
    funcAliases.put(name, declExpr);
    return true;
  }

  public static boolean isBareIdentifier(String s) {
    if (s == null || s.isEmpty())
      return false;
    if (!Character.isJavaIdentifierStart(s.charAt(0)))
      return false;
    for (int i = 1; i < s.length(); i++) {
      if (!Character.isJavaIdentifierPart(s.charAt(i)))
        return false;
    }
    return true;
  }

  // collect an identifier starting at idx into ident and return the new index
  public static int collectIdentifier(String s, int idx, StringBuilder ident) {
    int len = s.length();
    while (idx < len && Character.isJavaIdentifierPart(s.charAt(idx))) {
      ident.append(s.charAt(idx));
      idx++;
    }
    return idx;
  }

  // find assignment '=' index in a declaration substring, skipping '=>' arrows
  public static int findAssignmentIndex(String cur, int start) throws CompileException {
    for (int j = start; j < cur.length(); j++) {
      char cj = cur.charAt(j);
      if (cj == '=') {
        if (j + 1 < cur.length() && cur.charAt(j + 1) == '>') {
          j++;
          continue;
        }
        return j;
      }
    }
    throw new CompileException("Invalid let declaration: missing '=' in source segment: '" + cur + "'");
  }

  public static int readIntConsumed(String s, int idx) {
    String token = "readInt()";
    if (idx + token.length() <= s.length() && s.startsWith(token, idx)) {
      return token.length();
    }
    return 0;
  }
}
