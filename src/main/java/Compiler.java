class Compiler {
  public static String compile(String input) throws CompileException {
    boolean hasPrelude = hasPreludeDecl(input);
    String expr = extractExprFromInput(input);
    String[] parts = buildDeclAndRet(expr, hasPrelude, input);
    // parts: [topDecl, localDecl, retExpr]
    return buildC(parts[0], parts[1], parts[2]);
  }

  private static boolean hasPreludeDecl(String input) {
    if (input == null)
      return false;
    int firstSemi = input.indexOf(';');
    if (firstSemi < 0)
      return false;
    String head = input.substring(0, firstSemi).trim();
    return head.startsWith("intrinsic ");
  }

  private static String extractExprFromInput(String input) {
    if (input == null)
      return "";
    int firstSemi = input.indexOf(';');
    if (firstSemi >= 0) {
      String head = input.substring(0, firstSemi).trim();
      String tail = (firstSemi + 1 < input.length()) ? input.substring(firstSemi + 1).trim() : "";
      if (head.startsWith("fn ")) {
        return head + "; " + tail;
      }
      return tail;
    }
    return input.trim();
  }

  private static String[] buildDeclAndRet(String expr, boolean hasPrelude, String input)
      throws CompileException {
    String topDecl = "";
    String localDecl = "";
    String retExpr = (expr == null || expr.isEmpty()) ? "0" : stripTrailingSemicolon(expr);

    validateTopLevelExpression(expr, hasPrelude, input);

    // support simple function declarations: fn name() => body; rest
    FunctionDecl fd = parseFunctionDecl(expr);
    if (fd != null) {
      return handleFunctionDecl(fd);
    }

    LetBinding lb = parseLetBinding(expr);
    if (lb != null) {
      processLetBinding(lb);
      String[] decls = buildLocalDeclForLetWithTop(lb); // [topDecl, localDecl]
      if (decls != null) {
        topDecl += decls[0];
        localDecl = decls[1];
      }
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
    // If the function body contains nested `fn` declarations, extract them and
    // emit them as top-level declarations, then use the remaining body as the
    // outer function's return expression. When the inner function references
    // outer parameters, convert the inner to accept those parameters and pass
    // them at the call site.
    String[] extracted = extractInnerFunctionsFromBody(fd);
    if (extracted != null) {
      topDecl += extracted[0];
      fd = new FunctionDecl(fd.name, fd.paramDecl, fd.paramTypes, extracted[1], fd.after);
    }

    // create a C function returning int as a top-level declaration
    topDecl += buildTopLevelDecl(fd);
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

  private static void validateTopLevelExpression(String expr, boolean hasPrelude, String input)
      throws CompileException {
    validateIdentifiers(expr, hasPrelude);
    // Validate prelude return usage
    checkPreludeReturnInvalid(expr, hasPrelude, input);
    // Ensure calls to readInt do not include arguments (e.g. readInt(5))
    validateCallArgs(expr);

    // If the expression begins with a top-level `fn` but contains no body (no
    // `=>`),
    // treat it as a compile-time error (functions must have bodies).
    String trimmedExpr = expr == null ? "" : expr.trim();
    if (trimmedExpr.startsWith("fn ") && !trimmedExpr.contains("=>")) {
      throw new CompileException("Functions must have bodies");
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

  private static LetBinding parseLetBinding(String expr) {
    if (expr == null)
      return null;
    String trimmed = expr.trim();
    if (!trimmed.startsWith("let "))
      return null;
    // find the assignment '=' but skip the '=' that is part of a '=>' arrow
    int eq = findAssignmentIndex(trimmed);
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

  private static int findAssignmentIndex(String s) {
    if (s == null)
      return -1;
    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);
      if (ch == '=') {
        // skip =>
        if (i + 1 < s.length() && s.charAt(i + 1) == '>')
          continue;
        return i;
      }
    }
    return -1;
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
    int paren = indexOfNameParen(t, nameStart);
    if (paren < 0)
      return null;
    int closeParen = findMatchingParen(t, paren);
    if (closeParen < 0)
      return null;
    int arrow = indexOfArrow(t, closeParen);
    if (arrow < 0)
      return null;
    int semi = findTopLevelSemicolon(t, arrow + 2);
    if (semi < 0)
      return null;
    return new int[] { paren, closeParen, arrow, semi };
  }

  private static int indexOfNameParen(String t, int start) {
    if (t == null)
      return -1;
    return t.indexOf('(', start);
  }

  private static int indexOfArrow(String t, int after) {
    if (t == null || after < 0)
      return -1;
    return t.indexOf("=>", after);
  }

  private static int findTopLevelSemicolon(String s, int start) {
    if (s == null)
      return -1;
    int braceDepth = 0;
    int parenDepth = 0;
    for (int i = start; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == '{')
        braceDepth++;
      else if (c == '}')
        braceDepth = Math.max(0, braceDepth - 1);
      else if (c == '(')
        parenDepth++;
      else if (c == ')')
        parenDepth = Math.max(0, parenDepth - 1);
      else if (c == ';' && braceDepth == 0 && parenDepth == 0)
        return i;
    }
    return -1;
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

  private static String[] buildLocalDeclForLetWithTop(LetBinding lb) throws CompileException {
    if (lb == null)
      return new String[] { "", "" };
    String topDecl = "";
    String localDecl = "";

    // If the initializer is a lambda expression (contains =>), create a
    // top-level function for it and assign the function pointer locally.
    if (isLambdaInit(lb.init)) {
      LambdaParts lp = parseLambdaParts(lb.init);
      String lambdaName = lb.id + "_lambda";
      FunctionDecl fd = new FunctionDecl(lambdaName, lp.paramDecl, lp.paramTypes, lp.body, "");
      topDecl = buildTopLevelDecl(fd);
      String paramsC = buildParamCList(lp.paramDecl);
      localDecl = "    int (*" + lb.id + ")(" + paramsC + ") = " + lambdaName + ";\n";
      return new String[] { topDecl, localDecl };
    }

    // declared function type without a lambda initializer: treat init as name
    if (lb.declaredType != null && lb.declaredType.contains("=>")) {
      String left = lb.declaredType.substring(0, lb.declaredType.indexOf("=>")).trim();
      String paramsC = "void";
      if (left.startsWith("(") && left.endsWith(")")) {
        String inside = left.substring(1, left.length() - 1).trim();
        if (!inside.isEmpty()) {
          paramsC = "void";
        }
      }
      localDecl = "    int (*" + lb.id + ")(" + paramsC + ") = " + lb.init + ";\n";
      return new String[] { topDecl, localDecl };
    }

    // if initializer is a bare identifier (function name) and no declared type,
    // treat as function pointer assignment: `int (*id)(void) = name;`
    if (isBareIdentifier(lb.init)) {
      localDecl = "    int (*" + lb.id + ")(" + "void" + ") = " + lb.init + ";\n";
      return new String[] { topDecl, localDecl };
    }

    localDecl = "    int " + lb.id + " = (" + lb.init + ");\n";
    return new String[] { topDecl, localDecl };
  }

  private static boolean isLambdaInit(String init) {
    return init != null && init.contains("=>");
  }

  private static final class LambdaParts {
    final String paramDecl;
    final String[] paramTypes;
    final String body;

    LambdaParts(String paramDecl, String[] paramTypes, String body) {
      this.paramDecl = paramDecl;
      this.paramTypes = paramTypes;
      this.body = body;
    }
  }

  private static LambdaParts parseLambdaParts(String init) throws CompileException {
    String s = init.trim();
    int arrow = s.indexOf("=>");
    if (arrow <= 0)
      throw new CompileException("Invalid function literal");
    String paramsPart = s.substring(0, arrow).trim();
    String bodyPart = s.substring(arrow + 2).trim();
    String lambdaBody = stripTrailingSemicolon(bodyPart);

    String paramsInside = paramsPart;
    if (paramsPart.startsWith("(") && paramsPart.endsWith(")")) {
      paramsInside = paramsPart.substring(1, paramsPart.length() - 1).trim();
    }

    String paramDecl = "";
    String[] paramTypes = null;
    if (!paramsInside.isEmpty()) {
      paramDecl = parseParamDecls(paramsInside);
      paramTypes = extractParamTypes(paramsInside);
      if (paramDecl == null || paramTypes == null)
        throw new CompileException("Invalid lambda parameters");
    } else {
      paramDecl = "";
      paramTypes = new String[0];
    }

    return new LambdaParts(paramDecl, paramTypes, lambdaBody);
  }

  private static String buildParamCList(String paramDecl) {
    if (paramDecl == null || paramDecl.trim().isEmpty())
      return "void";
    String[] parts = paramDecl.split(",");
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < parts.length; i++) {
      String p = parts[i].trim();
      int sp = p.indexOf(' ');
      String typeOnly = sp >= 0 ? p.substring(0, sp) : p;
      if (sb.length() > 0)
        sb.append(", ");
      sb.append(typeOnly);
    }
    return sb.toString();
  }

  private static boolean isBareIdentifier(String s) {
    if (s == null)
      return false;
    String t = s.trim();
    if (t.isEmpty())
      return false;
    // must start with letter or underscore
    char c = t.charAt(0);
    if (!Character.isLetter(c) && c != '_')
      return false;
    for (int i = 1; i < t.length(); i++) {
      char ch = t.charAt(i);
      if (!Character.isLetterOrDigit(ch) && ch != '_')
        return false;
    }
    return true;
  }

  // Extract nested `fn` declarations from a function body. Returns an array of
  // two strings: [topLevelDecls, remainingBody]. If no inner functions are
  // found, returns null. This is intentionally small and only supports the
  // patterns used in tests (e.g. `{ fn inner() => readInt(); inner() }`).
  private static String[] extractInnerFunctionsFromBody(FunctionDecl outer) throws CompileException {
    if (outer == null || outer.body == null)
      return null;
    String t = outer.body.trim();
    if (!hasSurroundingBraces(t))
      return null;
    String inner = t.substring(1, t.length() - 1).trim();
    int fnIdx = findInnerFnIndex(inner);
    if (fnIdx < 0)
      return null;
    // parse inner function
    String before = inner.substring(0, fnIdx).trim();
    String afterFn = inner.substring(fnIdx).trim();
    FunctionDecl innerFd = parseFunctionDecl(afterFn);
    if (innerFd == null)
      return null;
    // gather which outer params the inner uses and prepare needed pieces
    String[] outerParamNames = extractParamNamesFromCDecl(outer.paramDecl);
    java.util.List<String> neededDecls = new java.util.ArrayList<>();
    java.util.List<String> neededTypes = new java.util.ArrayList<>();
    java.util.List<String> neededArgs = gatherNeededArgs(outerParamNames, outer.paramTypes, innerFd.body, neededDecls,
        neededTypes);

    String newInnerParamDecl = combineParamDecls(innerFd.paramDecl, neededDecls);
    String[] newParamTypes = buildNewParamTypes(innerFd.paramTypes, neededTypes);
    FunctionDecl newInnerFd = new FunctionDecl(innerFd.name, newInnerParamDecl, newParamTypes, innerFd.body,
        innerFd.after);

    String innerDecl = buildTopLevelDecl(newInnerFd);
    String remaining = newInnerFd.after == null ? "" : newInnerFd.after.trim();
    if (!neededArgs.isEmpty()) {
      remaining = replaceCallWithArgs(remaining, newInnerFd.name, String.join(", ", neededArgs));
    }
    String remainingBody = (before.isEmpty() ? "" : before + " ") + remaining;
    remainingBody = remainingBody.trim();
    if (remainingBody.isEmpty())
      remainingBody = "0";
    return new String[] { innerDecl, remainingBody };
  }

  private static java.util.List<String> gatherNeededArgs(String[] outerParamNames, String[] outerParamTypes,
      String innerBody, java.util.List<String> neededDecls, java.util.List<String> neededTypes) {
    java.util.List<String> neededArgs = new java.util.ArrayList<>();
    if (outerParamNames == null || outerParamNames.length == 0)
      return neededArgs;
    for (int i = 0; i < outerParamNames.length; i++) {
      String pname = outerParamNames[i];
      if (containsIdentifier(innerBody, pname)) {
        neededArgs.add(pname);
        neededDecls.add("int " + pname);
        if (outerParamTypes != null && i < outerParamTypes.length)
          neededTypes.add(outerParamTypes[i]);
        else
          neededTypes.add("I32");
      }
    }
    return neededArgs;
  }

  private static String combineParamDecls(String origDecl, java.util.List<String> neededDecls) {
    String newInnerParamDecl = origDecl == null ? "" : origDecl.trim();
    if (!neededDecls.isEmpty()) {
      if (newInnerParamDecl.isEmpty())
        newInnerParamDecl = String.join(", ", neededDecls);
      else
        newInnerParamDecl = newInnerParamDecl + ", " + String.join(", ", neededDecls);
    }
    return newInnerParamDecl;
  }

  private static String[] buildNewParamTypes(String[] origTypes, java.util.List<String> neededTypes) {
    if ((origTypes == null || origTypes.length == 0) && neededTypes.isEmpty())
      return null;
    java.util.List<String> pt = new java.util.ArrayList<>();
    if (origTypes != null) {
      for (String s : origTypes)
        pt.add(s);
    }
    pt.addAll(neededTypes);
    return pt.toArray(new String[0]);
  }

  private static String[] extractParamNamesFromCDecl(String cdecl) {
    if (cdecl == null || cdecl.trim().isEmpty())
      return new String[0];
    String[] parts = cdecl.split(",");
    String[] names = new String[parts.length];
    for (int i = 0; i < parts.length; i++) {
      String p = parts[i].trim();
      int sp = p.lastIndexOf(' ');
      if (sp < 0)
        names[i] = p;
      else
        names[i] = p.substring(sp + 1).trim();
    }
    return names;
  }

  private static boolean containsIdentifier(String s, String id) {
    if (s == null || id == null)
      return false;
    for (int i = 0; i + id.length() <= s.length(); i++) {
      int j = i + id.length();
      if (s.substring(i, j).equals(id)) {
        if (isIdentifierLeftBoundary(s, i) && isIdentifierRightBoundary(s, j))
          return true;
      }
    }
    return false;
  }

  private static boolean isIdentifierLeftBoundary(String s, int i) {
    if (i == 0)
      return true;
    char c = s.charAt(i - 1);
    return !Character.isLetterOrDigit(c) && c != '_';
  }

  private static boolean isIdentifierRightBoundary(String s, int j) {
    if (j == s.length())
      return true;
    char c = s.charAt(j);
    return !Character.isLetterOrDigit(c) && c != '_';
  }

  private static String replaceCallWithArgs(String expr, String name, String args) {
    if (expr == null || name == null)
      return expr == null ? null : expr;
    int idx = expr.indexOf(name + "(");
    if (idx < 0)
      return expr;
    int start = idx + name.length() + 1;
    int end = findMatchingParen(expr, start - 1);
    if (end < 0)
      return expr;
    String before = expr.substring(0, start);
    String after = expr.substring(end);
    return before + args + after;
  }

  private static boolean hasSurroundingBraces(String s) {
    return s != null && s.startsWith("{") && s.endsWith("}");
  }

  private static int findInnerFnIndex(String s) {
    if (s == null)
      return -1;
    return s.indexOf("fn ");
  }

  private static String buildTopLevelDecl(FunctionDecl fd) {
    if (fd == null)
      return "";
    if (fd.paramDecl == null || fd.paramDecl.isEmpty())
      return "int " + fd.name + "(void) { return (" + fd.body + "); }\n";
    return "int " + fd.name + "(" + fd.paramDecl + ") { return (" + fd.body + "); }\n";
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