class Compiler {
  public static String compile(String input) {
    String expr = extractTrailingExpression(input);

    // If the *input* mentions readInt anywhere (even in a function body or
    // declaration), generate a program that includes the readInt helper and
    // converts declarations (let/fn) to C before returning the final expr.
    if (input != null && input.contains("readInt")) {
      return generateReadIntProgram(input);
    }

    int ret = expr.isEmpty() ? 0 : parseOperation(expr);

    return "#include <stdio.h>\n" +
        "int main(void) {\n" +
        "  return " + ret + ";\n" +
        "}\n";
  }

  // Helper container for parsed program parts.
  private static final class ProgramParts {
    final StringBuilder decls = new StringBuilder();
    final StringBuilder fnDecls = new StringBuilder();
    final StringBuilder globalDecls = new StringBuilder();
    final StringBuilder typeDecls = new StringBuilder();
    String lastExpr = "";
    String lastDeclName = "";
  }

  // Pure: parse semicolon-separated segments extracting let/fn declarations
  // and the final expression. Keeps logic small and testable.
  private static ProgramParts parseSegments(String t) {
    return parseSegments(t, false);
  }

  private static ProgramParts parseSegments(String t, boolean inFunction) {
    ProgramParts parts = new ProgramParts();
    if (t == null || t.isEmpty())
      return parts;

    String[] segs = splitTopLevel(t);
    for (String part : segs) {
      String p = part.trim();
      if (p.isEmpty())
        continue;
      if (tryParseClass(parts, p, inFunction))
        continue;
      if (tryParseStruct(parts, p, inFunction))
        continue;
      if (tryParseLet(parts, p, inFunction))
        continue;
      if (tryParseFn(parts, p, inFunction))
        continue;
      if (tryIgnoreExtern(p))
        continue;

      parts.lastExpr = p;
    }
    return parts;
  }

  // Split on semicolons that are at top-level (not inside braces {}).
  private static String[] splitTopLevel(String s) {
    java.util.List<String> parts = new java.util.ArrayList<>();
    if (s == null || s.isEmpty())
      return new String[0];
    StringBuilder cur = new StringBuilder();
    int depth = 0;
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == '{') {
        depth++;
        cur.append(c);
      } else if (c == '}') {
        depth = Math.max(0, depth - 1);
        cur.append(c);
      } else if (c == ';' && depth == 0) {
        parts.add(cur.toString());
        cur.setLength(0);
      } else {
        cur.append(c);
      }
    }
    if (cur.length() > 0)
      parts.add(cur.toString());
    return parts.toArray(new String[0]);
  }

  private static boolean tryParseLet(ProgramParts parts, String p, boolean inFunction) {
    if (!p.startsWith("let "))
      return false;
    int eq = p.indexOf('=');
    if (eq < 0)
      return true; // treated as handled but malformed
    String name = p.substring(4, eq).trim();
    String init = p.substring(eq + 1).trim();
    int braceIdx = init.indexOf('{');
    if (braceIdx > 0) {
      String typeName = init.substring(0, braceIdx).trim();
      String inner = init.substring(braceIdx + 1).trim();
      if (inner.endsWith("}"))
        inner = inner.substring(0, inner.length() - 1).trim();
      return handleStructConstructor(parts, name, typeName, inner, inFunction);
    }

    // If init is a bare function name that was declared earlier, emit a
    // function-pointer variable with matching signature instead of an int.
    if (!init.contains("(") && !init.contains(" ")) {
      String[] sig = extractFunctionSignature(parts.fnDecls.toString(), init);
      if (sig != null) {
        String returnType = sig[0];
        String paramsC = sig[1];
        if (inFunction) {
          parts.decls.append("  ").append(returnType).append(" (*").append(name).append(")(")
              .append(paramsC).append(") = ").append(init).append(";\n");
        } else {
          parts.globalDecls.append(returnType).append(" (*").append(name).append(")(")
              .append(paramsC).append(");\n");
          parts.decls.append("  ").append(name).append(" = ").append(init).append(";\n");
        }
        parts.lastDeclName = name;
        return true;
      }
    }

    if (handleConstructorCall(parts, name, init, inFunction))
      return true;

    handleSimpleLet(parts, name, init, inFunction);
    parts.lastDeclName = name;
    return true;
  }

  // Extract return type and C parameter list for a function declared in fnDecls
  // Returns {returnType, params} or null if not found.
  private static String[] extractFunctionSignature(String fnDecls, String fnName) {
    if (fnDecls == null || fnName == null || fnName.isEmpty())
      return null;
    int idx = fnDecls.indexOf(fnName + "(");
    if (idx < 0)
      return null;
    int open = fnDecls.indexOf('(', idx);
    if (open < 0)
      return null;
    int close = findMatchingParen(fnDecls, open);
    if (close < 0)
      return null;
    // find return type start: scan backwards from fnName start to previous newline
    int nameStart = fnDecls.lastIndexOf('\n', idx);
    int rtStart = (nameStart < 0) ? 0 : nameStart + 1;
    String beforeName = fnDecls.substring(rtStart, idx).trim();
    if (beforeName.isEmpty())
      return null;
    String returnType = beforeName;
    String params = fnDecls.substring(open + 1, close).trim();
    if (params.isEmpty())
      params = "void";
    return new String[] { returnType, params };
  }

  private static boolean handleStructConstructor(ProgramParts parts, String name, String typeName, String inner,
      boolean inFunction) {
    ProgramParts innerParts = parseSegments(inner, inFunction);
    String value;
    if (!innerParts.lastExpr.isEmpty())
      value = innerParts.lastExpr;
    else if (!innerParts.lastDeclName.isEmpty())
      value = innerParts.lastDeclName;
    else
      value = "0";
    if (inFunction) {
      parts.decls.append("  struct ").append(typeName).append(" ").append(name).append(" = { ").append(value)
          .append(" };\n");
    } else {
      parts.globalDecls.append("struct ").append(typeName).append(" ").append(name).append(";\n");
      parts.decls.append("  ").append(name).append(" = (struct ").append(typeName).append("){ ").append(value)
          .append(" };\n");
    }
    parts.lastDeclName = name;
    return true;
  }

  private static boolean handleConstructorCall(ProgramParts parts, String name, String init, boolean inFunction) {
    int parenIdx = init.indexOf('(');
    if (parenIdx <= 0)
      return false;
    int closing = findMatchingParen(init, parenIdx);
    if (closing != init.length() - 1)
      return false;
    String typeName = init.substring(0, parenIdx).trim();
    if (parts.typeDecls.indexOf("struct " + typeName) < 0)
      return false;
    String inner = init.substring(parenIdx + 1, closing).trim();
    ProgramParts innerParts = parseSegments(inner, inFunction);
    String value;
    if (!innerParts.lastExpr.isEmpty())
      value = innerParts.lastExpr;
    else if (!innerParts.lastDeclName.isEmpty())
      value = innerParts.lastDeclName;
    else
      value = "0";
    if (inFunction) {
      parts.decls.append("  struct ").append(typeName).append(" ").append(name).append(" = { ").append(value)
          .append(" };\n");
    } else {
      parts.globalDecls.append("struct ").append(typeName).append(" ").append(name).append(";\n");
      parts.decls.append("  ").append(name).append(" = (struct ").append(typeName).append("){ ").append(value)
          .append(" };\n");
    }
    parts.lastDeclName = name;
    return true;
  }

  private static void handleSimpleLet(ProgramParts parts, String name, String init, boolean inFunction) {
    if (inFunction) {
      parts.decls.append("  int ").append(name).append(" = ").append(init).append(";\n");
    } else {
      parts.globalDecls.append("int ").append(name).append(";\n");
      parts.decls.append("  ").append(name).append(" = ").append(init).append(";\n");
    }
  }

  private static boolean tryParseStruct(ProgramParts parts, String p, boolean inFunction) {
    if (!p.startsWith("struct "))
      return false;
    int nameStart = 7; // after "struct "
    int braceIdx = p.indexOf('{', nameStart);
    if (braceIdx < 0)
      return true; // malformed but handled
    String name = p.substring(nameStart, braceIdx).trim();
    // strip any generic type parameters like '<T>' from the struct name
    int genericStart = name.indexOf('<');
    if (genericStart >= 0) {
      name = name.substring(0, genericStart).trim();
    }
    int endBrace = findMatchingBrace(p, braceIdx);
    if (endBrace < 0)
      return true; // malformed

    String inner = p.substring(braceIdx + 1, endBrace).trim();
    buildStructDef(parts, name, inner);

    // merge any trailing text after the struct declaration
    if (endBrace + 1 < p.length()) {
      String rest = p.substring(endBrace + 1).trim();
      if (!rest.isEmpty())
        mergePartsFromString(parts, rest, inFunction);
    }
    return true;
  }

  private static int findMatchingBrace(String s, int openIndex) {
    int depth = 0;
    for (int i = openIndex; i < s.length(); i++) {
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

  private static void buildStructDef(ProgramParts parts, String name, String inner) {
    String[] fields = inner.split(",");
    StringBuilder sb = new StringBuilder();
    sb.append("struct ").append(name).append(" {\n");
    for (String f : fields) {
      String ft = f.trim();
      if (ft.isEmpty())
        continue;
      int colon = ft.indexOf(":");
      String fieldName = colon >= 0 ? ft.substring(0, colon).trim() : ft;
      sb.append("  int ").append(fieldName).append(";\n");
    }
    sb.append("};\n");
    parts.typeDecls.append(sb.toString());
  }

  private static void mergePartsFromString(ProgramParts parts, String rest, boolean inFunction) {
    // Parse the trailing text directly into the existing ProgramParts so
    // later segments see types/decls declared earlier in the same segment.
    parseSegmentsInto(parts, rest, inFunction);
  }

  private static void parseSegmentsInto(ProgramParts parts, String t, boolean inFunction) {
    if (t == null || t.isEmpty())
      return;
    String[] segs = splitTopLevel(t);
    for (String part : segs) {
      String p = part.trim();
      if (p.isEmpty())
        continue;
      if (tryParseClass(parts, p, inFunction))
        continue;
      if (tryParseStruct(parts, p, inFunction))
        continue;
      if (tryParseLet(parts, p, inFunction))
        continue;
      if (tryParseFn(parts, p, inFunction))
        continue;
      if (tryIgnoreExtern(p))
        continue;
      parts.lastExpr = p;
    }
  }

  private static boolean tryParseFn(ProgramParts parts, String p, boolean inFunction) {
    if (!p.startsWith("fn "))
      return false;
    int nameStart = 3; // after "fn "
    int parenIdx = p.indexOf('(', nameStart);
    if (parenIdx < 0)
      return true; // handled but malformed
    String name = p.substring(nameStart, parenIdx).trim();
    int closeParen = findMatchingParen(p, parenIdx);
    if (closeParen < 0)
      return true; // malformed
    String params = p.substring(parenIdx + 1, closeParen).trim();
    int arrow = p.indexOf("=>", closeParen);
    if (arrow < 0)
      return true; // handled but malformed
    String body = p.substring(arrow + 2).trim();
    // strip trailing semicolon if present
    if (body.endsWith(";"))
      body = body.substring(0, body.length() - 1).trim();

    // support block bodies: { let x = readInt(); x }
    if (body.startsWith("{") && body.endsWith("}")) {
      String inner = body.substring(1, body.length() - 1).trim();
      ProgramParts innerParts = parseSegments(inner, true);
      return handleFunctionBlockBody(parts, name, params, inner, innerParts);
    }

    return emitSimpleFn(parts, name, params, body);
  }

  // Emit a simple function (non-block body). Supports integer returns and
  // struct-constructor style returns like "TypeName { ... }" mapping to
  // "struct TypeName" return type and a C compound literal.
  private static boolean emitSimpleFn(ProgramParts parts, String name, String params, String body) {
    if (tryEmitFunctionPointerReturn(parts, name, params, body))
      return true;
    if (tryEmitStructReturn(parts, name, params, body))
      return true;
    // default to integer return
    parts.fnDecls.append("int ").append(name).append("(").append(buildCParams(params)).append(") { return ")
        .append(body).append("; }\n");
    return true;
  }

  private static boolean tryEmitFunctionPointerReturn(ProgramParts parts, String name, String params, String body) {
    if (body == null || body.contains("(") || body.contains(" "))
      return false;
    String[] sig = extractFunctionSignature(parts.fnDecls.toString(), body);
    if (sig == null)
      return false;
    // sig[0] = returnType of inner, sig[1] = params of inner
    String innerRet = sig[0];
    String innerParams = sig[1];
    String cParams = buildCParams(params);
    StringBuilder decl = new StringBuilder();
    // function that returns function pointer: innerRet
    // (*name(cParams))(innerParams) { return inner; }
    decl.append(innerRet).append(" (*").append(name).append("(").append(cParams).append("))(").append(innerParams)
        .append(") {");
    decl.append(" return ").append(body).append("; }");
    parts.fnDecls.append(decl.toString()).append("\n");
    return true;
  }

  private static boolean tryEmitStructReturn(ProgramParts parts, String name, String params, String body) {
    if (body == null)
      return false;
    int braceIdx = body.indexOf('{');
    if (braceIdx <= 0)
      return false;
    int j = braceIdx - 1;
    while (j >= 0 && Character.isWhitespace(body.charAt(j)))
      j--;
    int tnEnd = j + 1;
    int tnStart = j;
    while (tnStart >= 0 && Character.isJavaIdentifierPart(body.charAt(tnStart)))
      tnStart--;
    tnStart++;
    if (tnStart >= tnEnd)
      return false;
    String typeName = body.substring(tnStart, tnEnd);
    if (!parts.typeDecls.toString().contains("struct " + typeName + " {"))
      return false;
    int lastBrace = body.lastIndexOf('}');
    String innerInit = body.substring(braceIdx + 1, lastBrace).trim();
    String retExpr = "(struct " + typeName + "){ " + innerInit + " }";
    parts.fnDecls.append("struct ").append(typeName).append(" ").append(name).append("(")
        .append(buildCParams(params)).append(") { return ").append(retExpr).append("; }\n");
    return true;
  }

  // Convert source parameter list like "x : I32, y : I32" to C parameters
  // like "int x, int y". Empty or whitespace-only params map to "void".
  private static String buildCParams(String params) {
    if (params == null || params.trim().isEmpty())
      return "void";
    String[] parts = params.split(",");
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (String part : parts) {
      String p = part.trim();
      if (p.isEmpty())
        continue;
      int colon = p.indexOf(':');
      String name = colon >= 0 ? p.substring(0, colon).trim() : p;
      if (!first)
        sb.append(", ");
      sb.append("int ").append(name);
      first = false;
    }
    if (sb.length() == 0)
      return "void";
    return sb.toString();
  }

  // Extract parameter names from a source param list like "x : I32, y : I32".
  private static String[] extractParamNames(String params) {
    if (params == null || params.trim().isEmpty())
      return new String[0];
    String[] parts = params.split(",");
    java.util.List<String> names = new java.util.ArrayList<>();
    for (String p : parts) {
      String t = p.trim();
      if (t.isEmpty())
        continue;
      int colon = t.indexOf(':');
      String name = colon >= 0 ? t.substring(0, colon).trim() : t;
      if (!name.isEmpty())
        names.add(name);
    }
    return names.toArray(new String[0]);
  }

  // Result of adjusting inner function declarations: rewritten decls and
  // a map from inner-fn name to argument list to pass at call sites.
  private static final class FnAdjustResult {
    final String decls;
    final java.util.Map<String, String> callArgs = new java.util.HashMap<>();

    FnAdjustResult(String decls) {
      this.decls = decls == null ? "" : decls;
    }
  }

  // Adjust inner function declarations so that any reference to outer
  // parameters becomes explicit parameters on the inner functions. Returns
  // rewritten declarations and a map of call argument strings for each inner fn.
  private static FnAdjustResult adjustInnerFnDecls(String decls, String[] outerParamNames) {
    if (decls == null || decls.trim().isEmpty() || outerParamNames.length == 0)
      return new FnAdjustResult(decls);

    StringBuilder out = new StringBuilder();
    java.util.Map<String, String> callArgsMap = new java.util.HashMap<>();
    int idx = 0;
    while (idx < decls.length()) {
      OneAdjust oa = adjustOneInner(decls, idx, outerParamNames);
      out.append(oa.snippet);
      callArgsMap.putAll(oa.callArgs);
      idx = oa.nextIndex;
      if (oa.nextIndex <= idx)
        break; // safety to avoid infinite loop
    }

    FnAdjustResult res = new FnAdjustResult(out.toString());
    res.callArgs.putAll(callArgsMap);
    return res;
  }

  // Helper result for processing a single inner function declaration
  private static final class OneAdjust {
    final String snippet;
    final java.util.Map<String, String> callArgs;
    final int nextIndex;

    OneAdjust(String snippet, java.util.Map<String, String> callArgs, int nextIndex) {
      this.snippet = snippet == null ? "" : snippet;
      this.callArgs = callArgs == null ? new java.util.HashMap<>() : callArgs;
      this.nextIndex = nextIndex;
    }
  }

  // Process one inner function declaration starting at index 'start' and
  // return the rewritten snippet, any call-arg mappings, and the index to
  // continue from. Keeps the complexity of adjustInnerFnDecls small.
  private static OneAdjust adjustOneInner(String decls, int start, String[] outerParamNames) {
    int sigStart = findSigStart(decls, start);
    if (sigStart < 0)
      return new OneAdjust(decls.substring(start), null, decls.length());
    return processInnerFrom(decls, sigStart, outerParamNames);
  }

  private static int findSigStart(String decls, int start) {
    return decls.indexOf("int ", start);
  }

  // Collect which outer parameter names are referenced in the given body.
  private static java.util.List<String> collectUsedOuterParams(String body, String[] outerParamNames) {
    java.util.List<String> used = new java.util.ArrayList<>();
    for (String p : outerParamNames) {
      if (p != null && !p.isEmpty() && body.contains(p))
        used.add(p);
    }
    return used;
  }

  // Build a C-style parameter list like "int x, int y" from the used names.
  private static String buildParamList(java.util.List<String> used) {
    if (used == null || used.isEmpty())
      return "";
    StringBuilder paramList = new StringBuilder();
    boolean first = true;
    for (String u : used) {
      if (!first)
        paramList.append(", ");
      paramList.append("int ").append(u);
      first = false;
    }
    return paramList.toString();
  }

  // Build an argument list like "x, y" from the used names.
  private static String buildArgsList(java.util.List<String> used) {
    if (used == null || used.isEmpty())
      return "";
    StringBuilder args = new StringBuilder();
    boolean first = true;
    for (String u : used) {
      if (!first)
        args.append(", ");
      args.append(u);
      first = false;
    }
    return args.toString();
  }

  private static OneAdjust processInnerFrom(String decls, int sigStart, String[] outerParamNames) {
    StringBuilder snippet = new StringBuilder();
    snippet.append(decls.substring(0, sigStart));
    int nameStart = sigStart + 4;
    int paren = decls.indexOf('(', nameStart);
    if (paren < 0) {
      return new OneAdjust(decls.substring(sigStart), null, decls.length());
    }
    String name = decls.substring(nameStart, paren).trim();
    int closeParen = findMatchingParen(decls, paren);
    if (closeParen < 0) {
      return new OneAdjust(decls.substring(sigStart), null, decls.length());
    }
    int bodyStart = decls.indexOf('{', closeParen);
    if (bodyStart < 0) {
      return new OneAdjust(decls.substring(sigStart), null, decls.length());
    }
    int bodyEnd = findMatchingBrace(decls, bodyStart);
    if (bodyEnd < 0) {
      return new OneAdjust(decls.substring(sigStart), null, decls.length());
    }
    String body = decls.substring(bodyStart + 1, bodyEnd);

    java.util.List<String> used = collectUsedOuterParams(body, outerParamNames);
    java.util.Map<String, String> callArgs = new java.util.HashMap<>();
    if (used.isEmpty()) {
      snippet.append(decls.substring(sigStart, bodyEnd + 1));
    } else {
      String paramList = buildParamList(used);
      snippet.append("int ").append(name).append("(").append(paramList).append(") ")
          .append(decls.substring(bodyStart, bodyEnd + 1));
      String args = buildArgsList(used);
      callArgs.put(name, args);
    }
    return new OneAdjust(snippet.toString(), callArgs, bodyEnd + 1);
  }

  // Emit the outer function body for a block-style function while
  // incorporating any adjustments for inner functions (parameters & call sites).
  private static boolean handleFunctionBlockBody(ProgramParts parts, String name, String params, String inner,
      ProgramParts innerParts) {
    // compute which outer params exist
    String[] outerParamNames = extractParamNames(params);
    FnAdjustResult adj = adjustInnerFnDecls(innerParts.fnDecls.toString(), outerParamNames);
    String adjustedFnDecls = adj.decls;

    String innerDecls = rewriteInnerCallsInDecls(innerParts.decls.toString(), adj.callArgs);
    String ret = determineReturnExpression(innerParts);
    ret = rewriteCallsInReturn(ret, adj.callArgs);

    if (tryHandleClosureReturn(parts, name, params, adjustedFnDecls, adj.callArgs, ret))
      return true;
    if (tryHandleFunctionPointerReturn(parts, name, params, adjustedFnDecls, ret))
      return true;

    // default: append any adjusted inner function decls and emit normal outer
    if (adjustedFnDecls != null && !adjustedFnDecls.isEmpty())
      parts.fnDecls.append(adjustedFnDecls);

    StringBuilder f = new StringBuilder();
    f.append("int ").append(name).append("(").append(buildCParams(params)).append(") {\n");
    f.append(innerDecls);
    f.append("  return ").append(ret).append(";\n");
    f.append("}\n");
    parts.fnDecls.append(f.toString());
    return true;
  }

  private static boolean tryHandleClosureReturn(ProgramParts parts, String name, String params,
      String adjustedFnDecls, java.util.Map<String, String> callArgs, String ret) {
    if (ret == null || ret.contains("(") || ret.contains(" "))
      return false;
    String[] sig = extractFunctionSignature(adjustedFnDecls, ret);
    if (sig == null)
      return false;
    if (!callArgs.containsKey(ret))
      return false;
    String args = callArgs.get(ret);
    java.util.List<String> used = parseUsedArgs(args);
    if (used.isEmpty())
      return false;

    java.util.List<String> globals = buildClosureGlobals(parts, ret, used);

    adjustedFnDecls = rewriteClosureInnerDecls(adjustedFnDecls, ret, used, globals);

    parts.fnDecls.append(adjustedFnDecls);

    emitClosureOuterFunction(parts, name, params, used, globals, sig, ret);
    return true;
  }

  private static java.util.List<String> parseUsedArgs(String args) {
    java.util.List<String> used = new java.util.ArrayList<>();
    if (args == null || args.trim().isEmpty())
      return used;
    String[] parts = args.split(",");
    for (String p : parts) {
      String t = p.trim();
      if (!t.isEmpty())
        used.add(t);
    }
    return used;
  }

  private static java.util.List<String> buildClosureGlobals(ProgramParts parts, String ret,
      java.util.List<String> used) {
    java.util.List<String> globals = new java.util.ArrayList<>();
    for (String up : used) {
      String gname = "__" + ret + "_" + up;
      globals.add(gname);
      parts.globalDecls.append("int ").append(gname).append(";\n");
    }
    return globals;
  }

  private static String rewriteClosureInnerDecls(String adjustedFnDecls, String ret,
      java.util.List<String> used, java.util.List<String> globals) {
    int declIdx = adjustedFnDecls.indexOf("int " + ret + "(");
    if (declIdx >= 0) {
      int pOpen = adjustedFnDecls.indexOf('(', declIdx);
      int pClose = findMatchingParen(adjustedFnDecls, pOpen);
      int bStart = adjustedFnDecls.indexOf('{', pClose);
      int bEnd = findMatchingBrace(adjustedFnDecls, bStart);
      if (pOpen >= 0 && pClose >= 0 && bStart >= 0 && bEnd >= 0) {
        String body = adjustedFnDecls.substring(bStart + 1, bEnd);
        for (int i = 0; i < used.size(); i++) {
          String up = used.get(i);
          if (up == null || up.isEmpty())
            continue;
          String gname = globals.get(i);
          body = body.replace(up, gname);
        }
        String newDecl = "int " + ret + "(void) {" + body + " }";
        adjustedFnDecls = adjustedFnDecls.substring(0, declIdx) + newDecl
            + adjustedFnDecls.substring(bEnd + 1);
      }
    }
    return adjustedFnDecls;
  }

  private static void emitClosureOuterFunction(ProgramParts parts, String name, String params,
      java.util.List<String> used, java.util.List<String> globals, String[] sig, String ret) {
    StringBuilder fptr = new StringBuilder();
    String cParams = buildCParams(params);
    fptr.append(sig[0]).append(" (*").append(name).append("(").append(cParams).append(") )")
        .append("(void) {\n");
    for (int i = 0; i < used.size(); i++) {
      String up = used.get(i);
      if (up == null || up.isEmpty())
        continue;
      String gname = globals.get(i);
      fptr.append("  ").append(gname).append(" = ").append(up).append(";\n");
    }
    fptr.append("  return ").append(ret).append(";\n");
    fptr.append("}\n");
    parts.fnDecls.append(fptr.toString());
  }

  private static boolean tryHandleFunctionPointerReturn(ProgramParts parts, String name, String params,
      String adjustedFnDecls, String ret) {
    if (ret == null || ret.contains("(") || ret.contains(" "))
      return false;
    String[] sig = extractFunctionSignature(adjustedFnDecls, ret);
    if (sig == null)
      return false;
    String innerRet = sig[0];
    String innerParams = sig[1];
    String cParams = buildCParams(params);
    StringBuilder fptr = new StringBuilder();
    fptr.append(innerRet).append(" (*").append(name).append("(").append(cParams).append(") )(")
        .append(innerParams).append(") {\n");
    fptr.append("  return ").append(ret).append(";\n");
    fptr.append("}\n");
    parts.fnDecls.append(adjustedFnDecls);
    parts.fnDecls.append(fptr.toString());
    return true;
  }

  // Replace calls to inner functions in the function declarations with
  // argument lists when needed. Small pure helper to keep complexity low.
  private static String rewriteInnerCallsInDecls(String innerDecls, java.util.Map<String, String> callArgs) {
    if (innerDecls == null || innerDecls.isEmpty() || callArgs == null || callArgs.isEmpty())
      return innerDecls == null ? "" : innerDecls;
    String s = innerDecls;
    for (java.util.Map.Entry<String, String> e : callArgs.entrySet()) {
      String callNoArgs = e.getKey() + "()";
      String callWithArgs = e.getKey() + "(" + e.getValue() + ")";
      s = s.replace(callNoArgs, callWithArgs);
    }
    return s;
  }

  // Determine the return expression for a block-style function.
  private static String determineReturnExpression(ProgramParts innerParts) {
    if (innerParts == null)
      return "0";
    if (innerParts.lastExpr.isEmpty())
      return innerParts.lastDeclName.isEmpty() ? "0" : innerParts.lastDeclName;
    return innerParts.lastExpr;
  }

  // Rewrite any calls in the return expression to include arguments when needed.
  private static String rewriteCallsInReturn(String ret, java.util.Map<String, String> callArgs) {
    if (ret == null || ret.isEmpty() || callArgs == null || callArgs.isEmpty())
      return ret == null ? "" : ret;
    String s = ret;
    for (java.util.Map.Entry<String, String> e : callArgs.entrySet()) {
      String callNoArgs = e.getKey() + "()";
      String callWithArgs = e.getKey() + "(" + e.getValue() + ")";
      s = s.replace(callNoArgs, callWithArgs);
    }
    return s;
  }

  private static boolean tryParseClass(ProgramParts parts, String p, boolean inFunction) {
    if (!p.startsWith("class ") && !p.startsWith("class\t"))
      return false;
    ClassHeader h = parseClassHeader(p);
    if (h == null)
      return true;
    emitClassStruct(parts, h.name, h.params);
    if (h.afterIndex < p.length()) {
      String rest2 = p.substring(h.afterIndex).trim();
      if (!rest2.isEmpty())
        mergePartsFromString(parts, rest2, inFunction);
    }
    return true;
  }

  private static final class ClassHeader {
    final String name;
    final String params;
    final int afterIndex;

    ClassHeader(String name, String params, int afterIndex) {
      this.name = name;
      this.params = params;
      this.afterIndex = afterIndex;
    }
  }

  private static ClassHeader parseClassHeader(String p) {
    int i = skipWhitespace(p, 5);
    i = skipOptionalFn(p, i);
    if (i >= p.length())
      return null;
    int nameStart = i;
    int nameEnd = findNameEnd(p, nameStart);
    if (nameEnd <= nameStart)
      return null;
    String name = p.substring(nameStart, nameEnd).trim();
    int openIndex = p.indexOf('(', nameStart);
    if (openIndex < 0)
      return null;
    int closeIndex = findMatchingParen(p, openIndex);
    if (closeIndex < 0)
      return null;
    String params = p.substring(openIndex + 1, closeIndex).trim();
    int after = skipWhitespace(p, closeIndex + 1);
    after = skipOptionalArrowBody(p, after);
    return new ClassHeader(name, params, after);
  }

  private static int skipWhitespace(String s, int i) {
    while (i < s.length() && Character.isWhitespace(s.charAt(i)))
      i++;
    return i;
  }

  private static int skipOptionalFn(String s, int i) {
    if (s.startsWith("fn", i) && (i + 2 == s.length() || Character.isWhitespace(s.charAt(i + 2)))) {
      i += 2;
      i = skipWhitespace(s, i);
    }
    return i;
  }

  private static int findNameEnd(String s, int i) {
    while (i < s.length() && !Character.isWhitespace(s.charAt(i)) && s.charAt(i) != '(')
      i++;
    return i;
  }

  private static int skipOptionalArrowBody(String s, int i) {
    i = skipWhitespace(s, i);
    if (s.startsWith("=>", i)) {
      i += 2;
      i = skipWhitespace(s, i);
      if (i < s.length() && s.charAt(i) == '{') {
        int endBrace = findMatchingBrace(s, i);
        if (endBrace > 0)
          i = endBrace + 1;
      }
    }
    return i;
  }

  private static void emitClassStruct(ProgramParts parts, String name, String params) {
    buildStructDef(parts, name, params);
  }

  private static int findMatchingParen(String s, int openIndex) {
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

  private static boolean tryIgnoreExtern(String p) {
    return p.startsWith("extern ");
  }

  // Pure: generate C program that implements readInt() and returns the
  // provided expression (which may contain one or more readInt() calls).
  private static String generateReadIntProgram(String expr) {
    String header = "#include <stdio.h>\n" +
        "int readInt(void) {\n" +
        "  int x = 0;\n" +
        "  if (scanf(\"%d\", &x) == 1) return x;\n" +
        "  return 0;\n" +
        "}\n";

    String t = expr.trim();

    // Parse semicolon-separated segments into parts used to build the C program.
    ProgramParts parts = parseSegments(t);

    StringBuilder sb = new StringBuilder();
    sb.append(header);
    // append any generated type declarations (structs)
    if (parts.typeDecls.length() > 0) {
      sb.append(parts.typeDecls.toString());
    }
    // append any global variable declarations so functions can reference them
    if (parts.globalDecls.length() > 0) {
      sb.append(parts.globalDecls.toString());
    }
    // append any generated functions
    if (parts.fnDecls.length() > 0) {
      sb.append(parts.fnDecls.toString());
    }
    sb.append("int main(void) {\n");
    sb.append(parts.decls.toString());

    if (parts.lastExpr.isEmpty()) {
      if (parts.lastDeclName.isEmpty()) {
        sb.append("  return 0;\n");
      } else {
        sb.append("  return ").append(parts.lastDeclName).append(";\n");
      }
    } else {
      sb.append("  return ").append(parts.lastExpr).append(";\n");
    }

    sb.append("}\n");
    return sb.toString();
  }

  // Pure: extract the trailing expression after any leading declarations or
  // earlier semicolons. Avoid regex; handle trailing semicolons and spaces.
  private static String extractTrailingExpression(String input) {
    if (input == null)
      return "";
    String s = input.trim();
    if (s.isEmpty())
      return "";

    String[] parts = s.split(";");
    String letExpr = reconstructLetExpression(parts);
    if (letExpr != null)
      return letExpr;

    String candidate = lastNonEmptySegment(parts);
    if (candidate.isEmpty())
      return "";

    if (!looksLikeDeclaration(candidate))
      return candidate;

    // find last non-declaration segment
    for (int i = parts.length - 1; i >= 0; i--) {
      String t = parts[i].trim();
      if (t.isEmpty())
        continue;
      if (!looksLikeDeclaration(t))
        return t;
    }

    return "";
  }

  private static String lastNonEmptySegment(String[] parts) {
    for (int i = parts.length - 1; i >= 0; i--) {
      String t = parts[i].trim();
      if (!t.isEmpty())
        return t;
    }
    return "";
  }

  private static String reconstructLetExpression(String[] parts) {
    for (int i = 0; i < parts.length; i++) {
      String t = parts[i].trim();
      if (t.startsWith("let ")) {
        StringBuilder sb = new StringBuilder();
        for (int j = i; j < parts.length; j++) {
          if (sb.length() > 0)
            sb.append("; ");
          sb.append(parts[j].trim());
        }
        return sb.toString().trim();
      }
    }
    return null;
  }

  private static boolean looksLikeDeclaration(String s) {
    return s.contains("extern") || s.contains("fn");
  }

  // Pure: determine whether the expression is binary (+, -, *) or a lone integer.
  private static int parseOperation(String s) {
    int plusIdx = s.indexOf('+');
    if (plusIdx >= 0)
      return parseBinary(s, plusIdx, '+');

    int minusIdx = s.indexOf('-');
    if (minusIdx >= 0)
      return parseBinary(s, minusIdx, '-');

    int mulIdx = s.indexOf('*');
    if (mulIdx >= 0)
      return parseBinary(s, mulIdx, '*');

    return parseIntOrZero(s);
  }

  // Pure: parse a binary operation around the given operator index.
  private static int parseBinary(String s, int opIdx, char op) {
    String left = s.substring(0, opIdx).trim();
    String right = s.substring(opIdx + 1).trim();
    try {
      int l = Integer.parseInt(left);
      int r = Integer.parseInt(right);
      switch (op) {
        case '+':
          return l + r;
        case '-':
          return l - r;
        case '*':
          return l * r;
        default:
          return 0;
      }
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  // Pure: parse integer or return 0 on failure.
  private static int parseIntOrZero(String s) {
    try {
      return Integer.parseInt(s);
    } catch (NumberFormatException e) {
      return 0;
    }
  }
}