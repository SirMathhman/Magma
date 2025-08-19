class Compiler {
  public static String compile(String input) throws CompileException {
    boolean hasPrelude = false;
    if (input != null) {
      int firstSemi = input.indexOf(';');
      if (firstSemi >= 0) {
        String head = input.substring(0, firstSemi).trim();
        if (head.startsWith("intrinsic ")) {
          hasPrelude = true;
        }
      }
    }
    String expr;
    if (input != null) {
      int firstSemi = input.indexOf(';');
      if (firstSemi >= 0) {
        String head = input.substring(0, firstSemi).trim();
        String tail = (firstSemi + 1 < input.length()) ? input.substring(firstSemi + 1).trim() : "";
        if (head.startsWith("fn ")) {
          // preserve top-level fn declarations placed before the semicolon
          expr = head + "; " + tail;
        } else {
          expr = tail;
        }
      } else {
        expr = input.trim();
      }
    } else {
      expr = "";
    }
    String[] parts = buildDeclAndRet(expr, hasPrelude, input);
    // parts: [topDecl, localDecl, retExpr]
    return buildC(parts[0], parts[1], parts[2]);
  }

  private static String[] buildDeclAndRet(String expr, boolean hasPrelude, String input) throws CompileException {
    String topDecl = "";
    String localDecl = "";
    String retExpr = (expr == null || expr.isEmpty()) ? "0" : stripTrailingSemicolon(expr);

    validateIdentifiers(expr, hasPrelude);

    // Validate prelude return usage (extracted helper to reduce complexity)
    checkPreludeReturnInvalid(expr, hasPrelude, input);
    // Ensure calls to readInt do not include arguments (e.g. readInt(5))
    validateCallArgs(expr);

    // support simple function declarations: fn name() => body; rest
    FunctionDecl fd = parseFunctionDecl(expr);
    if (fd != null) {
      return handleFunctionDecl(fd);
    }

    LetBinding lb = parseLetBinding(expr);
    if (lb != null) {
      processLetBinding(lb);
      localDecl = "    int " + lb.id + " = (" + lb.init + ");\n";
      retExpr = (lb.after == null || lb.after.isEmpty()) ? "0" : stripTrailingSemicolon(lb.after);
    }

    return new String[] { topDecl, localDecl, retExpr };
  }

  private static String[] handleFunctionDecl(FunctionDecl fd) throws CompileException {
    String topDecl = "";
    String localDecl = "";
    String retExpr = "0";
    // validate that any call to this function in the 'after' expression matches the
    // declared params
    validateFunctionCallArgs(fd);
    // if the 'after' expression begins with another top-level fn, treat as invalid
    String aTrim = fd.after == null ? "" : fd.after.trim();
    if (aTrim.startsWith("fn ")) {
      throw new CompileException("Duplicate function declaration");
    }
    // create a C function returning int as a top-level declaration
    if (fd.paramDecl == null || fd.paramDecl.isEmpty()) {
      topDecl = "int " + fd.name + "(void) { return (" + fd.body + "); }\n";
    } else {
      topDecl = "int " + fd.name + "(" + fd.paramDecl + ") { return (" + fd.body + "); }\n";
    }
    // if the after expression calls the function with an argument, keep it (strip
    // trailing semicolon)
    retExpr = (fd.after == null || fd.after.isEmpty()) ? "0" : stripTrailingSemicolon(fd.after);
    return new String[] { topDecl, localDecl, retExpr };
  }

  private static void validateIdentifiers(String expr, boolean hasPrelude) throws CompileException {
    if (expr == null)
      return;
    String t = expr.trim();
    if (t.isEmpty() || t.startsWith("let ") || t.startsWith("fn "))
      return;
    // if there's no prelude, identifiers should be considered undefined
    String cleaned = expr;
    if (hasPrelude) {
      // remove allowed identifiers and then fail if any letter remains
      cleaned = expr.replace("readInt", "").replace("true", "").replace("false", "");
    }
    String tok = firstUnknownIdentifier(cleaned);
    if (tok != null) {
      throw new CompileException("Unknown identifier: " + tok);
    }
  }

  private static void checkPreludeReturnInvalid(String expr, boolean hasPrelude, String input)
      throws CompileException {
    if (!hasPrelude)
      return;
    String preludeType = getPreludeReturnType(input);
    if (!"Bool".equals(preludeType))
      return;
    String t = (expr == null) ? "" : expr.trim();
    if (t.equals("readInt()")) {
      throw new CompileException("Invalid return: readInt declared as Bool");
    }
  }

  private static String firstUnknownIdentifier(String cleaned) {
    if (cleaned == null)
      return null;
    for (int i = 0; i < cleaned.length(); i++) {
      if (Character.isLetter(cleaned.charAt(i))) {
        int j = i;
        while (j < cleaned.length() && (Character.isLetterOrDigit(cleaned.charAt(j)) || cleaned.charAt(j) == '_'))
          j++;
        return cleaned.substring(i, j);
      }
    }
    return null;
  }

  private static String stripTrailingSemicolon(String s) {
    if (s == null)
      return null;
    String t = s.trim();
    if (t.endsWith(";"))
      return t.substring(0, t.length() - 1).trim();
    return t;
  }

  private static void processLetBinding(LetBinding lb) throws CompileException {
    if (lb.declaredType != null) {
      if (lb.declaredType.equals("Bool") && !isBooleanInit(lb.init)) {
        throw new CompileException("Type mismatch: cannot assign non-bool to Bool");
      }
      if (lb.declaredType.equals("I32") && isBooleanInit(lb.init)) {
        throw new CompileException("Type mismatch: cannot assign bool to I32");
      }
    }
  }

  private static String buildC(String topDecl, String localDecl, String retExpr) {
    StringBuilder out = new StringBuilder();
    out.append("#include <stdio.h>\n");
    out.append("#include <stdlib.h>\n\n");
    // provide a readInt helper that reads an integer from stdin unless the user
    // defines one
    if (topDecl == null || !topDecl.contains("int readInt(")) {
      out.append("int readInt() {\n");
      out.append("    int v = 0;\n");
      out.append("    if (scanf(\"%d\", &v) != 1) return 0;\n");
      out.append("    return v;\n");
      out.append("}\n\n");
    }

    if (topDecl != null && !topDecl.isEmpty()) {
      out.append(topDecl);
      out.append("\n");
    }

    out.append("int main(void) {\n");
    if (localDecl != null && !localDecl.isEmpty()) {
      out.append(localDecl);
    }
    out.append("    return (");
    out.append(retExpr);
    out.append(");\n");
    out.append("}\n");

    return out.toString();
  }

  private static String extractExpr(String input) {
    if (input == null)
      return "";
    int semicolon = input.indexOf(';');
    if (semicolon >= 0 && semicolon + 1 < input.length()) {
      return input.substring(semicolon + 1).trim();
    }
    return input.trim();
  }

  private static LetBinding parseLetBinding(String expr) {
    if (expr == null)
      return null;
    String trimmed = expr.trim();
    if (!trimmed.startsWith("let "))
      return null;
    int eq = trimmed.indexOf('=');
    int semi = trimmed.indexOf(';', eq >= 0 ? eq : 0);
    if (eq <= 0 || semi <= eq)
      return null;
    String idPart = trimmed.substring(4, eq).trim();
    // support optional type annotation after identifier, e.g. 'x: I32'
    String declaredType = null;
    int colon = idPart.indexOf(':');
    if (colon >= 0) {
      declaredType = idPart.substring(colon + 1).trim();
      idPart = idPart.substring(0, colon).trim();
    }
    String initPart = trimmed.substring(eq + 1, semi).trim();
    String after = trimmed.substring(semi + 1).trim();
    if (idPart.isEmpty() || initPart.isEmpty())
      return null;
    // allow empty 'after' (e.g. `let x: Bool = 5;`) — caller will handle default
    return new LetBinding(idPart, initPart, after, declaredType);
  }

  private static final class FunctionDecl {
    final String name;
    final String paramDecl; // C-style parameter declaration like "int x, int y"
    final String[] paramTypes; // declared param types like {"I32","I32"}
    final String body;
    final String after;

    FunctionDecl(String name, String paramDecl, String[] paramTypes, String body, String after) {
      this.name = name;
      this.paramDecl = paramDecl;
      this.paramTypes = paramTypes;
      this.body = body;
      this.after = after;
    }
  }

  private static FunctionDecl parseFunctionDecl(String expr) {
    if (expr == null)
      return null;
    String t = expr.trim();
    if (!t.startsWith("fn "))
      return null;
    // expect: fn name(param: Type) => body; rest (param optional)
    int nameStart = 3;
    String name;
    int[] parts = findFunctionParts(t, nameStart);
    if (parts == null)
      return null;
    int paren = parts[0];
    int closeParen = parts[1];
    int arrow = parts[2];
    int semi = parts[3];
    name = t.substring(nameStart, paren).trim();
    String params = t.substring(paren + 1, closeParen).trim();
    String paramDecl = parseParamDecls(params);
    if (params.length() > 0 && paramDecl == null)
      return null;
    String[] paramTypes = extractParamTypes(params);
    if (params.length() > 0 && paramTypes == null)
      return null;
    String body = t.substring(arrow + 2, semi).trim();
    String after = t.substring(semi + 1).trim();
    if (name.isEmpty() || body.isEmpty())
      return null;
    return new FunctionDecl(name, paramDecl, paramTypes, body, after);
  }

  private static String parseParamDecls(String params) {
    if (params == null || params.isEmpty())
      return "";
    String[] parts = params.split(",");
    StringBuilder decl = new StringBuilder();
    for (int i = 0; i < parts.length; i++) {
      String p = parts[i].trim();
      int colon = p.indexOf(':');
      if (colon <= 0)
        return null;
      String name = p.substring(0, colon).trim();
      if (name.isEmpty())
        return null;
      if (decl.length() > 0)
        decl.append(", ");
      decl.append("int ").append(name);
    }
    return decl.toString();
  }

  private static String[] extractParamTypes(String params) {
    if (params == null || params.isEmpty())
      return new String[0];
    String[] parts = params.split(",");
    String[] types = new String[parts.length];
    for (int i = 0; i < parts.length; i++) {
      String p = parts[i].trim();
      int colon = p.indexOf(':');
      if (colon <= 0)
        return null;
      String type = p.substring(colon + 1).trim();
      if (type.isEmpty())
        return null;
      types[i] = type;
    }
    return types;
  }

  private static int[] findFunctionParts(String t, int nameStart) {
    int paren = t.indexOf('(', nameStart);
    if (paren < 0)
      return null;
    int closeParen = t.indexOf(')', paren);
    if (closeParen < 0)
      return null;
    int arrow = t.indexOf("=>", closeParen);
    if (arrow < 0)
      return null;
    int semi = t.indexOf(';', arrow);
    if (semi < 0)
      return null;
    return new int[] { paren, closeParen, arrow, semi };
  }

  private static void validateFunctionCallArgs(FunctionDecl fd) throws CompileException {
    if (fd == null || fd.after == null || fd.after.isEmpty())
      return;
    String call = fd.name + "(";
    int idx = fd.after.indexOf(call);
    if (idx < 0)
      return;
    int start = idx + call.length();
    int end = findMatchingParen(fd.after, start - 1);
    if (end < 0)
      return;
    String inside = fd.after.substring(start, end).trim();
    String[] args = splitTopLevelArgs(inside);
    if (args.length != fd.paramTypes.length) {
      throw new CompileException("Argument count mismatch for function: " + fd.name);
    }
    validateArgTypes(args, fd.paramTypes);
  }

  private static void validateArgTypes(String[] args, String[] paramTypes) throws CompileException {
    if (args == null || paramTypes == null)
      return;
    for (int i = 0; i < args.length; i++) {
      String a = args[i].trim();
      String declared = paramTypes[i];
      boolean isBool = isBooleanInit(a);
      if ("Bool".equals(declared) && !isBool) {
        throw new CompileException("Type mismatch: cannot pass non-bool to Bool parameter");
      }
      if ("I32".equals(declared) && isBool) {
        throw new CompileException("Type mismatch: cannot pass bool to I32 parameter");
      }
    }
  }

  private static int findMatchingParen(String s, int openIndex) {
    if (s == null || openIndex < 0 || openIndex >= s.length() || s.charAt(openIndex) != '(')
      return -1;
    int depth = 0;
    for (int i = openIndex; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == '(')
        depth++;
      else if (c == ')') {
        depth--;
        if (depth == 0)
          return i;
      }
    }
    return -1;
  }

  private static String[] splitTopLevelArgs(String s) {
    if (s == null || s.trim().isEmpty())
      return new String[0];
    java.util.List<String> parts = new java.util.ArrayList<>();
    StringBuilder cur = new StringBuilder();
    int depth = 0;
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == ',' && depth == 0) {
        parts.add(cur.toString());
        cur.setLength(0);
        continue;
      }
      cur.append(c);
      if (c == '(')
        depth++;
      else if (c == ')')
        depth = Math.max(0, depth - 1);
    }
    parts.add(cur.toString());
    return parts.toArray(new String[0]);
  }

  // ...existing code...

  private static final class LetBinding {
    final String id;
    final String init;
    final String after;
    final String declaredType;

    LetBinding(String id, String init, String after, String declaredType) {
      this.id = id;
      this.init = init;
      this.after = after;
      this.declaredType = declaredType;
    }
  }

  private static boolean isBooleanInit(String init) {
    if (init == null)
      return false;
    String t = init.trim();
    if (t.equals("true") || t.equals("false"))
      return true;
    // simple heuristic: presence of comparison operators yields boolean
    return t.contains("==") || t.contains("!=") || t.contains("<") || t.contains(">") || t.contains("<=")
        || t.contains(">=");
  }

  private static String getPreludeReturnType(String input) {
    if (input == null)
      return null;
    int semicolon = input.indexOf(';');
    if (semicolon <= 0)
      return null;
    String pre = input.substring(0, semicolon).trim();
    // expected form: extern fn readInt() : Type
    int colon = pre.indexOf(':');
    if (colon < 0)
      return null;
    String type = pre.substring(colon + 1).trim();
    return type.isEmpty() ? null : type;
  }

  private static void validateCallArgs(String expr) throws CompileException {
    if (expr == null)
      return;
    String s = expr;
    int idx = s.indexOf("readInt(");
    while (idx >= 0) {
      int start = idx + "readInt(".length();
      // find matching closing paren or next ')' position
      int end = s.indexOf(')', start);
      if (end < 0)
        break;
      String inside = s.substring(start, end).trim();
      if (!inside.isEmpty()) {
        throw new CompileException("Invalid argument to readInt");
      }
      idx = s.indexOf("readInt(", end);
    }
  }
}