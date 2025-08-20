package com.example.magma;

import java.util.Optional;

/**
 * Simple Compiler utility.
 * Provides a pure function-like method to "compile" a string input to a string
 * output.
 */
public final class Compiler {
  private Compiler() {
    // utility class - prevent instantiation
  }

  private static final java.util.Map<String, String> MAGMA_TO_C = java.util.Map.ofEntries(
      java.util.Map.entry("I8", "int8_t"), java.util.Map.entry("I16", "int16_t"),
      java.util.Map.entry("I32", "int32_t"), java.util.Map.entry("I64", "int64_t"),
      java.util.Map.entry("U8", "uint8_t"), java.util.Map.entry("U16", "uint16_t"),
      java.util.Map.entry("U32", "uint32_t"), java.util.Map.entry("U64", "uint64_t"),
      java.util.Map.entry("*CStr", "CStr *"));

  /**
   * Compile the given source text to a result string.
   * 
   * @param source non-null source text
   * @return compiled representation
   */
  public static String compile(String source) {
    String src = prepareSource(source);
    String body = src.replace("readInt()", "read_int()")
        .replace("readChar()", "read_int()")
        .replace("readString()", "read_string()");

    String[] letsCollected = collectLets(body);
    String letDecls = letsCollected[0];
    String bodyNoLets = letsCollected[1];
    String letNamesCsv = letsCollected.length > 2 ? letsCollected[2] : "";
    String letFuncRefsCsv = letsCollected.length > 3 ? letsCollected[3] : "";

    StringBuilder sb = new StringBuilder();
    emitPrelude(sb);

    String afterStructs = processStructures(bodyNoLets, sb);
    String afterFns = processFunctions(afterStructs, sb);

    validateAfterFunctions(afterFns, letNamesCsv, letFuncRefsCsv, sb);

    // convert Magma if-then-else expressions into C ternary expressions
    String transformed = IfExpressions.transform(afterFns);
    // if any let-declared variables are pointer types (e.g. "CStr * x"),
    // member access should use '->' instead of '.' in C
    java.util.Set<String> pointerVars = extractPointerVarNames(letDecls);
    if (!pointerVars.isEmpty()) {
      transformed = replaceMemberAccessForPointers(transformed, pointerVars);
    }
    emitMain(sb, letDecls, transformed);
    return sb.toString();
  }

  private static java.util.Set<String> extractPointerVarNames(String decls) {
    java.util.Set<String> out = new java.util.HashSet<>();
    if (decls == null || decls.isEmpty())
      return out;
    String[] lines = decls.split("\\n");
    for (String l : lines) {
      String trimmed = l == null ? "" : l.trim();
      if (trimmed.isEmpty())
        continue;
      for (String name : Structs.parsePointerNamesFromLine(trimmed)) {
        if (name != null && !name.isEmpty())
          out.add(name);
      }
    }
    return out;
  }

  private static String replaceMemberAccessForPointers(String expr, java.util.Set<String> pointers) {
    if (expr == null || expr.isEmpty() || pointers == null || pointers.isEmpty())
      return expr == null ? "" : expr;
    String out = expr;
    for (String name : pointers) {
      String pattern = name + ".";
      int idx = out.indexOf(pattern);
      while (idx != -1) {
        boolean okBefore = (idx == 0) || !isIdentifierChar(out.charAt(idx - 1));
        if (okBefore) {
          StringBuilder sb = new StringBuilder();
          sb.append(out, 0, idx).append(name).append("->").append(out.substring(idx + pattern.length()));
          out = sb.toString();
          idx = out.indexOf(pattern, idx + name.length() + 2);
        } else {
          idx = out.indexOf(pattern, idx + pattern.length());
        }
      }
    }
    return out;
  }

  private static boolean isIdentifierChar(char c) {
    return Character.isLetterOrDigit(c) || c == '_';
  }

  private static String extractNamesCsv(String declsText) {
    // decls are lines like " int x = (...);\n"
    String[] lines = declsText.split("\\n");
    StringBuilder names = new StringBuilder();
    for (String line : lines) {
      String t = line.trim();
      if (t.isEmpty())
        continue;
      if (!t.startsWith("int "))
        continue;
      String rest = t.substring(4).trim();
      int sp = rest.indexOf(' ');
      int eq = rest.indexOf('=');
      int end = sp != -1 ? sp : (eq != -1 ? eq : rest.length());
      String name = rest.substring(0, end).trim();
      if (name.isEmpty())
        continue;
      if (names.length() > 0)
        names.append(',');
      names.append(name);
    }
    return names.toString();
  }

  private static void checkVarsUsedAsFunctions(String expr, String namesCsv, String funcRefCsv) {
    if (expr == null || expr.isEmpty() || namesCsv == null || namesCsv.isEmpty())
      return;
    java.util.Set<String> funcRefs = parseFuncRefsCsv(funcRefCsv);
    String[] names = namesCsv.split(",");
    for (String name : names) {
      if (name == null || name.isEmpty())
        continue;
      if (funcRefs.contains(name))
        continue;
      if (exprContainsCall(expr, name)) {
        throw new CompileException("Identifier '" + name + "' used like a function");
      }
    }
  }

  private static java.util.Set<String> parseFuncRefsCsv(String funcRefCsv) {
    java.util.Set<String> funcRefs = new java.util.HashSet<>();
    if (funcRefCsv == null || funcRefCsv.isEmpty())
      return funcRefs;
    for (String f : funcRefCsv.split(",")) {
      if (f == null)
        continue;
      String t = f.trim();
      if (!t.isEmpty())
        funcRefs.add(t);
    }
    return funcRefs;
  }

  private static boolean exprContainsCall(String expr, String name) {
    if (expr == null || name == null || name.isEmpty())
      return false;
    return expr.contains(name + "(");
  }

  private static String prepareSource(String source) {
    String src = stripPrelude(Optional.ofNullable(source).orElse(""));
    String trimmed = src.trim();
    if (isSingleIdentifier(trimmed)) {
      throw new CompileException("Undefined symbol: " + trimmed);
    }
    return src;
  }

  private static void emitPrelude(StringBuilder sb) {
    sb.append("#include <stdio.h>\n");
    sb.append("#include <stdint.h>\n");
    sb.append("#include <stdlib.h>\n");
    sb.append("#include <string.h>\n");
    sb.append("int read_int(void) { int x = 0; if (scanf(\"%d\", &x) == 1) return x; ");
    sb.append("int c = getchar(); while (c != EOF && (c==' ' || c=='\\n' || c=='\\r' || c=='\\t')) c = getchar(); ");
    sb.append("if (c == '\\'') { int ch = getchar(); int close = getchar(); (void)close; return ch; } ");
    sb.append("if (c == EOF) return 0; return c; }");
    sb.append("\n");
    sb.append("typedef struct { int length; char *data; } CStr;\n");
    sb.append(
        "CStr *read_string(void) { char buf[4096]; if (!fgets(buf, sizeof(buf), stdin)) { CStr *s = malloc(sizeof(CStr)); s->length = 0; s->data = NULL; return s; } size_t len = strlen(buf); if (len > 0 && (buf[len-1] == '\\n' || buf[len-1] == '\\r')) len--; char *d = malloc(len + 1); if (d) { memcpy(d, buf, len); d[len] = '\\0'; } CStr *s = malloc(sizeof(CStr)); if (s) { s->length = (int)len; s->data = d; } return s; }");
    sb.append("\n");
  }

  private static void validateAfterFunctions(String afterFns, String letNamesCsv, String letFuncRefsCsv,
      StringBuilder sb) {
    if (!letNamesCsv.isEmpty()) {
      checkVarsUsedAsFunctions(afterFns, letNamesCsv, letFuncRefsCsv);
    }
    String fnSigs = extractFunctionSignatures(sb.toString());
    if (!fnSigs.isEmpty()) {
      checkFunctionCallArities(afterFns, fnSigs);
    }
  }

  private static void emitMain(StringBuilder sb, String letDecls, String afterFns) {
    sb.append("int main(void) {\n");
    if (!letDecls.isEmpty())
      sb.append(letDecls);
    String finalExpr = afterFns.isEmpty() ? "0" : afterFns;
    sb.append("  int result = (").append(finalExpr).append(");\n");
    sb.append("  return result;\n");
    sb.append("}\n");
  }

  private static boolean isSingleIdentifier(String s) {
    if (s == null)
      return false;
    String t = s.trim();
    if (t.isEmpty())
      return false;
    char c = t.charAt(0);
    if (!(Character.isLetter(c) || c == '_'))
      return false;
    for (int i = 1; i < t.length(); i++) {
      char ch = t.charAt(i);
      if (!(Character.isLetterOrDigit(ch) || ch == '_'))
        return false;
    }
    return true;
  }

  private static String stripPrelude(String src) {
    if (src == null || src.isEmpty())
      return "";
    String out = src;
    // remove any explicit extern fn declarations like: "extern fn name() : T;"
    int idx = out.indexOf("extern fn ");
    while (idx != -1) {
      int semi = out.indexOf(';', idx);
      if (semi == -1)
        break;
      out = out.substring(0, idx) + out.substring(semi + 1);
      idx = out.indexOf("extern fn ");
    }
    return out.trim();
  }

  // processLets was removed; collectLets is used instead to extract declarations

  /**
   * Extract all top-level let declarations from the body. Returns a two-element
   * array where index 0 is the C declarations text (each ending with newline)
   * and index 1 is the remaining body with lets removed.
   */
  private static String[] collectLets(String body) {
    String remaining = body == null ? "" : body.trim();
    StringBuilder decls = new StringBuilder();
    int idx = 0;
    while (true) {
      idx = remaining.indexOf("let ", idx);
      if (idx == -1)
        break;
      if (!isTopLevelIndex(remaining, idx)) {
        idx = idx + 4; // skip this occurrence
        continue;
      }
      String newRemaining = handleLetOccurrence(remaining, idx, decls);
      if (newRemaining == null)
        break;
      remaining = newRemaining;
      idx = 0; // restart search from beginning after modification
    }

    // also return a comma-separated list of declared variable names for
    // later sanity checks (e.g., calling a variable as a function)
    String namesCsv = decls.length() == 0 ? "" : extractNamesCsv(decls.toString());
    String funcRefsCsv = extractFuncRefsCsv(body);
    return new String[] { decls.toString(), remaining, namesCsv, funcRefsCsv };
  }

  private static String handleLetOccurrence(String remaining, int idx, StringBuilder decls) {
    String[] extracted = extractNextLet(remaining, idx);
    if (extracted == null)
      return null;
    String decl = extracted[0];
    int removeEnd = Integer.parseInt(extracted[1]);
    java.util.Optional<String> built = buildLetDeclaration(decl, remaining);
    built.ifPresent(decls::append);
    return (remaining.substring(0, idx) + remaining.substring(removeEnd)).trim();
  }

  private static String extractFuncRefsCsv(String body) {
    if (body == null || body.isEmpty())
      return "";
    StringBuilder funcRefs = new StringBuilder();
    int sidx = 0;
    while ((sidx = body.indexOf("let ", sidx)) != -1) {
      int semi = body.indexOf(';', sidx);
      if (semi == -1)
        break;
      String decl = body.substring(sidx, semi + 1).trim();
      DeclParts dp = extractDeclParts(decl);
      String rhs = dp.rhs();
      if (rhs != null && !rhs.isEmpty() && isBareIdentifier(rhs)) {
        if (funcRefs.length() > 0)
          funcRefs.append(',');
        funcRefs.append(dp.name());
      }
      sidx = semi + 1;
    }
    return funcRefs.length() == 0 ? "" : funcRefs.toString();
  }

  private static boolean isBareIdentifier(String s) {
    // require the same rules as a single identifier (first char letter/_)
    if (s == null)
      return false;
    return isSingleIdentifier(s.trim());
  }

  private static String mapBareIdentifierToC(String id) {
    if (id == null)
      return "";
    if ("readInt".equals(id))
      return "read_int";
    if ("readChar".equals(id))
      return "read_int";
    if ("readString".equals(id))
      return "read_string";
    return id;
  }

  private static boolean isTopLevelIndex(String s, int pos) {
    if (s == null || pos <= 0)
      return true;
    return Structs.computeBraceDepthUpTo(s, pos) == 0;
  }

  private static int findMatchingBrace(String s, int openIdx) {
    return Structs.findMatchingBrace(s, openIdx);
  }

  /**
   * Extracts the next let declaration from text starting at index startIdx.
   * Returns a two-element array: [declarationText, removeEndIndexAsString]
   * or null if none found or malformed.
   */
  private static String[] extractNextLet(String remaining, int startIdx) {
    int semi = remaining.indexOf(';', startIdx);
    if (semi == -1)
      return null;
    String decl = remaining.substring(startIdx, semi + 1).trim();
    return new String[] { decl, String.valueOf(semi + 1) };
  }

  private static java.util.Optional<String> buildLetDeclaration(String decl, String fullBody) {
    if (decl == null || decl.isEmpty())
      return java.util.Optional.empty();
    DeclParts parts = extractDeclParts(decl);
    String varName = parts.name();
    String rhs = parts.rhs();
    java.util.Optional<String> declaredType = parts.type();

    rhs = convertBooleanLiteral(rhs);

    quickValidateStructInit(rhs, fullBody);

    java.util.Optional<String> structInit = tryBuildStructInit(rhs, varName, fullBody);
    if (structInit.isPresent())
      return structInit;

    if (isBareIdentifier(rhs) && declaredType.isEmpty()) {
      String cName = mapBareIdentifierToC(rhs);
      return java.util.Optional.of("  int (*" + varName + ")(void) = " + cName + ";\n");
    }

    String cType = declaredType.map(Compiler::mapMagmaTypeToC).orElse("int");
    return java.util.Optional.of("  " + cType + " " + varName + " = (" + rhs + ");\n");
  }

  private static record DeclParts(String name, String rhs, java.util.Optional<String> type) {
  }

  private static DeclParts extractDeclParts(String decl) {
    int eq = decl.indexOf('=');
    String varName = extractVarName(decl, eq);
    String rhs = "0";
    java.util.Optional<String> type = java.util.Optional.empty();
    // if there's a colon after the var name, parse type between ':' and '=' or ';'
    int colon = decl.indexOf(':', 4);
    if (colon != -1) {
      int typeStart = colon + 1;
      int typeEnd = (eq != -1) ? eq : decl.length() - 1;
      if (typeEnd > typeStart) {
        String t = decl.substring(typeStart, typeEnd).trim();
        if (!t.isEmpty())
          type = java.util.Optional.of(t);
      }
    }
    if (eq != -1) {
      rhs = decl.substring(eq + 1, decl.length() - 1).trim();
      if (rhs.isEmpty()) {
        rhs = "0";
      }
    }
    return new DeclParts(varName, rhs, type);
  }

  private static String mapMagmaTypeToC(String t) {
    if (t == null)
      return "int";
    String v = MAGMA_TO_C.get(t.trim());
    return v == null ? "int" : v;
  }

  private static String convertBooleanLiteral(String rhs) {
    if (rhs == null)
      return "0";
    if ("true".equals(rhs))
      return "1";
    if ("false".equals(rhs))
      return "0";
    return rhs;
  }

  private static void quickValidateStructInit(String rhs, String fullBody) {
    if (rhs == null || !rhs.contains("{"))
      return;
    StructParts p = getStructPartsOrNull(rhs);
    if (p == null)
      return;
    java.util.Map<String, String[]> defs = parseStructDefinitions(fullBody);
    String[] types = defs.get(p.name);
    if (types == null || types.length == 0 || p.inner == null || p.inner.trim().isEmpty())
      return;
    Structs.validateStructInitElements(p.name, p.inner, types);
  }

  private static String extractVarName(String decl, int eq) {
    int varStart = 4;
    int colon = decl.indexOf(':', varStart);
    if (colon != -1)
      return decl.substring(varStart, colon).trim();
    if (eq != -1)
      return decl.substring(varStart, eq).trim();
    return "_tmp";
  }

  private static java.util.Optional<String> tryBuildStructInit(String rhs, String varName, String fullBody) {
    StructParts p = getStructPartsOrNull(rhs);
    if (p == null)
      return java.util.Optional.empty();
    validateStructArity(p, fullBody);
    quickValidateStructInit(rhs, fullBody);
    return buildStructInit(p.name, varName, p.inner);
  }

  private static java.util.Map<String, Integer> parseStructFieldCounts(String fullBody) {
    return Structs.structFieldCounts(fullBody);
  }

  private static java.util.Optional<String> buildStructInit(String structName, String varName, String inner) {
    StringBuilder init = new StringBuilder();
    init.append('{');
    if (!inner.isEmpty()) {
      String[] elems = inner.split(",");
      for (int i = 0; i < elems.length; i++) {
        if (i > 0)
          init.append(',').append(' ');
        init.append('(').append(elems[i].trim()).append(')');
      }
    }
    init.append('}');
    return java.util.Optional.of("  " + structName + " " + varName + " = " + init.toString() + ";\n");
  }

  private static int countNonEmpty(String csv) {
    return Structs.countNonEmpty(csv);
  }

  private static java.util.Map<String, String[]> parseStructDefinitions(String fullBody) {
    return Structs.structDefinitions(fullBody);
  }

  private static record StructParts(String name, String inner) {
  }

  private static StructParts getStructPartsOrNull(String rhs) {
    if (rhs == null)
      return null;
    int brace = rhs.indexOf('{');
    if (brace == -1)
      return null;
    int endBrace = rhs.lastIndexOf('}');
    if (endBrace == -1 || endBrace <= brace)
      return null;
    String structName = rhs.substring(0, brace).trim();
    String inner = rhs.substring(brace + 1, endBrace).trim();
    return new StructParts(structName, inner);
  }

  private static void validateStructArity(StructParts p, String fullBody) {
    if (p == null)
      return;
    java.util.Map<String, Integer> counts = parseStructFieldCounts(fullBody);
    Integer expected = counts.get(p.name);
    if (expected == null)
      return;
    int provided = countNonEmpty(p.inner);
    if (provided < expected) {
      throw new CompileException(
          "Struct " + p.name + " constructed with insufficient arguments: expected " + expected + " got " + provided);
    }
  }

  private static String processFunctions(String body, StringBuilder sb) {
    String remaining = body;
    while (remaining.startsWith("fn ")) {
      FunctionParts fp = extractNextFunction(remaining);
      if (fp == null)
        break;
      processSingleFunction(fp, sb);
      remaining = remaining.substring(fp.removeEnd()).trim();
    }
    return remaining;
  }

  private static void processSingleFunction(FunctionParts fp, StringBuilder sb) {
    if (fp == null)
      return;
    sb.append("/*fnsig:").append(fp.name()).append(':').append(fp.arity()).append("*/\n");

    String expr = fp.expr();
    if (handleThisReturn(fp, sb)) {
      return;
    }
    if (expr != null && expr.startsWith("{")) {
      if (!handleBlockReturn(fp, expr, sb)) {
        emitSimpleReturn(fp, sb);
      }
    } else {
      emitSimpleReturn(fp, sb);
    }
  }

  private static boolean handleThisReturn(FunctionParts fp, StringBuilder sb) {
    if (fp == null)
      return false;
    String expr = fp.expr();
    if (!"this".equals(expr))
      return false;
    String typedefName = capitalize(fp.name()) + "_ret";
    String[] pnames = fp.paramNames();
    java.util.List<String> fieldsList = new java.util.ArrayList<>();
    for (String pn : pnames) {
      if (pn == null || pn.trim().isEmpty())
        continue;
      fieldsList.add(pn);
    }
    Structs.emitStructReturnFromFields(typedefName, java.util.Collections.emptyList(), fieldsList, fp.name(),
        fp.paramsDecl(), sb);
    return true;
  }

  private static boolean handleBlockReturn(FunctionParts fp, String expr, StringBuilder sb) {
    if (fp == null || expr == null)
      return false;
    String inner = extractInnerBlockFromExpr(expr);
    if (inner == null)
      return false;
    Structs.LocalParseResult parsed = parseLocalDeclarations(inner);
    if (parsed == null)
      return false;
    if (parsed.remaining.trim().equals("this") || parsed.remaining.contains(" this ")
        || parsed.remaining.startsWith("this ") || parsed.remaining.endsWith(" this")) {
      String typedefName = capitalize(fp.name()) + "_ret";
      Structs.emitStructReturnFromFields(typedefName, parsed.localDecls, parsed.fieldNames, fp.name(), fp.paramsDecl(),
          sb);
      return true;
    }
    return false;
  }

  // LocalParseResult moved to Structs to reduce duplication in this file

  private static Structs.LocalParseResult parseLocalDeclarations(String inner) {
    if (inner == null)
      return new Structs.LocalParseResult(java.util.Collections.emptyList(), java.util.Collections.emptyList(), "");
    java.util.List<String> localDecls = new java.util.ArrayList<>();
    java.util.List<String> fieldNames = new java.util.ArrayList<>();
    String localRemaining = inner;
    while (true) {
      int lidx = localRemaining.indexOf("let ");
      if (lidx == -1)
        break;
      String[] ext = extractNextLet(localRemaining, lidx);
      if (ext == null)
        break;
      String decl = ext[0];
      int remEnd = Integer.parseInt(ext[1]);
      DeclParts dp = extractDeclParts(decl);
      String vname = dp.name();
      String vrhs = convertBooleanLiteral(dp.rhs());
      localDecls.add("  int " + vname + " = (" + vrhs + ");\n");
      fieldNames.add(vname);
      localRemaining = (localRemaining.substring(0, lidx) + localRemaining.substring(remEnd)).trim();
    }
    return new Structs.LocalParseResult(localDecls, fieldNames, localRemaining);
  }

  private static record FunctionParts(String name, String paramsDecl, int arity, String expr, int removeEnd,
      String[] paramNames) {
  }

  private static FunctionParts extractNextFunction(String remaining) {
    int arrow = remaining.indexOf("=>");
    if (arrow == -1)
      return null;
    // determine the semicolon that ends the function: if the expression starts
    // with a block '{', find the matching '}' and the following semicolon
    int exprStart = arrow + 2;
    if (exprStart >= remaining.length())
      return null;
    int semi = findFunctionTerminator(remaining, exprStart);
    if (semi == -1 || arrow > semi)
      return null;
    String header = remaining.substring(0, arrow).trim();
    int nameStart = 3; // after "fn "
    int paren = header.indexOf('(', nameStart);
    String name = (paren != -1) ? header.substring(nameStart, paren).trim() : "_fn";
    String paramsDecl = buildParamsDeclaration(header, paren);
    int arity = countParams(header, paren);
    // extract parameter names for possible 'this' return struct
    String[] paramNames = extractParamNames(header, paren);
    String expr = remaining.substring(arrow + 2, semi).trim();
    return new FunctionParts(name, paramsDecl, arity, expr, semi + 1, paramNames);
  }

  private static int findFunctionTerminator(String remaining, int exprStart) {
    if (remaining == null || exprStart >= remaining.length())
      return -1;
    int look = exprStart;
    while (look < remaining.length() && Character.isWhitespace(remaining.charAt(look)))
      look++;
    if (look < remaining.length() && remaining.charAt(look) == '{') {
      return findSemicolonAfterMatchingBrace(remaining, look);
    }
    return remaining.indexOf(';', exprStart);
  }

  private static int findSemicolonAfterMatchingBrace(String s, int openIdx) {
    return Structs.findSemicolonAfterMatchingBrace(s, openIdx);
  }

  private static String extractInnerBlockFromExpr(String expr) {
    if (expr == null)
      return null;
    if (expr.startsWith("{") && expr.endsWith("}"))
      return expr.substring(1, expr.length() - 1).trim();
    int o = expr.indexOf('{');
    int c = findMatchingBrace(expr, o);
    if (o != -1 && c != -1)
      return expr.substring(o + 1, c).trim();
    return null;
  }

  private static int countParams(String header, int paren) {
    String params = extractParams(header, paren);
    int cnt = 0;
    if (params != null && params.length() > 0) {
      String[] parts = params.split(",");
      for (String p : parts)
        if (!p.trim().isEmpty())
          cnt++;
    }
    return cnt;
  }

  private static String[] extractParamNames(String header, int paren) {
    return Structs.extractParamNamesFromHeader(header, paren);
  }

  private static String extractNameFromPart(String part) {
    if (part == null)
      return "";
    String p = part.trim();
    if (p.isEmpty())
      return "";
    int colon = p.indexOf(':');
    String pName = colon != -1 ? p.substring(0, colon).trim() : p;
    return pName == null ? "" : pName.trim();
  }

  private static String capitalize(String s) {
    if (s == null || s.isEmpty())
      return "";
    if (s.length() == 1)
      return s.toUpperCase();
    return Character.toUpperCase(s.charAt(0)) + s.substring(1);
  }

  private static String buildParamsDeclaration(String header, int paren) {
    String params = extractParams(header, paren);
    if (params.isEmpty())
      return "void";
    String[] parts = params.split(",");
    StringBuilder pd = new StringBuilder();
    for (String part : parts) {
      String pName = extractNameFromPart(part);
      if (pName.isEmpty())
        continue;
      // attempt to parse a declared type after ':' and map to C type
      String cType = "int";
      int colon = part == null ? -1 : part.indexOf(':');
      if (colon != -1) {
        String t = part.substring(colon + 1).trim();
        if (!t.isEmpty())
          cType = mapMagmaTypeToC(t);
      }
      if (pd.length() > 0)
        pd.append(", ");
      pd.append(cType).append(" ").append(pName);
    }
    return pd.length() == 0 ? "void" : pd.toString();
  }

  private static void emitFunctionHeader(StringBuilder sb, String returnType, String name, String paramsDecl) {
    sb.append(returnType).append(" ").append(name).append("(").append(paramsDecl).append(") { ");
  }

  private static void emitSimpleReturn(FunctionParts fp, StringBuilder sb) {
    emitFunctionHeader(sb, "int", fp.name(), fp.paramsDecl());
    sb.append(" return (").append(fp.expr()).append("); }\n");
  }

  private static String extractParams(String header, int paren) {
    if (paren == -1)
      return "";
    int parenClose = header.indexOf(')', paren);
    if (parenClose == -1)
      return "";
    String params = header.substring(paren + 1, parenClose).trim();
    return params;
  }

  private static String processStructures(String body, StringBuilder sb) {
    String remaining = body;
    int idx;
    while ((idx = findStructureIndex(remaining)) != -1) {
      // move to the start of the structure declaration and parse it
      remaining = remaining.substring(idx).trim();
      remaining = parseAndEmitStructure(remaining, sb);
    }
    return remaining;
  }

  private static String extractFunctionSignatures(String emitted) {
    // find occurrences of /*fnsig:name:arity*/ in the emitted text
    if (emitted == null || emitted.isEmpty())
      return "";
    StringBuilder sb = new StringBuilder();
    String marker = "/*fnsig:";
    int idx = 0;
    while ((idx = emitted.indexOf(marker, idx)) != -1) {
      int end = emitted.indexOf("*/", idx + marker.length());
      if (end == -1)
        break;
      String inner = emitted.substring(idx + marker.length(), end).trim();
      if (!inner.isEmpty()) {
        if (sb.length() > 0)
          sb.append(',');
        sb.append(inner);
      }
      idx = end + 2;
    }
    return sb.toString();
  }

  private static void checkFunctionCallArities(String expr, String fnSigsCsv) {
    if (expr == null || expr.isEmpty() || fnSigsCsv == null || fnSigsCsv.isEmpty())
      return;
    String[] sigs = fnSigsCsv.split(",");
    for (String sig : sigs) {
      String[] parts = sig.split(":");
      if (parts.length != 2)
        continue;
      String name = parts[0].trim();
      int arity = 0;
      try {
        arity = Integer.parseInt(parts[1].trim());
      } catch (NumberFormatException e) {
        continue;
      }
      checkFunctionCallsForSig(expr, name, arity);
    }
  }

  private static void checkFunctionCallsForSig(String expr, String name, int arity) {
    if (expr == null || expr.isEmpty() || name == null || name.isEmpty())
      return;
    int idx = -1;
    while ((idx = expr.indexOf(name + "(", idx + 1)) != -1) {
      int start = idx + name.length() + 1;
      int[] res = countArgsAndEnd(expr, start);
      if (res[0] == -1) {
        // malformed call; ignore here
        break;
      }
      int argCount = res[0];
      int end = res[1];
      if (argCount != arity) {
        throw new CompileException(
            "Function '" + name + "' called with wrong arity: expected " + arity + " got " + argCount);
      }
      idx = end; // continue after this call
    }
  }

  private static int[] countArgsAndEnd(String expr, int start) {
    if (invalidRange(expr, start))
      return failedPair();
    int end = Structs.findClosingIndex(expr, start);
    if (end == -1)
      return failedPair();
    int argCount = countArgsInRange(expr, start, end);
    return new int[] { argCount, end };
  }

  private static int[] failedPair() {
    return new int[] { -1, -1 };
  }

  private static int countArgsInRange(String expr, int start, int end) {
    if (expr == null || start < 0 || end <= start)
      return 0;
    int commas = 0;
    boolean seenAny = false;
    for (int i = start; i < end; i++) {
      char c = expr.charAt(i);
      if (c == ',')
        commas++;
      else if (!Character.isWhitespace(c))
        seenAny = true;
    }
    return seenAny ? (commas + 1) : 0;
  }

  private static boolean invalidRange(String expr, int start) {
    return Structs.invalidRange(expr, start);
  }

  private static int findStructureIndex(String s) {
    return Structs.findStructureIndex(s);
  }

  private static String parseAndEmitStructure(String remaining, StringBuilder sb) {
    String[] nxt = Structs.extractNextStruct(remaining == null ? "" : remaining);
    if (nxt == null)
      return remaining;
    String name = nxt[0];
    String bodyContent = nxt[1];
    int removeEnd = Integer.parseInt(nxt[2]);
    // support multiple fields separated by commas: "f1 : I32, f2 : I32"
    String[] parts = Structs.splitElements(bodyContent);
    String[] types = Structs.structDefinitions(remaining).get(name);
    StringBuilder fieldsSb = new StringBuilder();
    int idx = 0;
    for (String p : parts) {
      String fieldName = extractNameFromPart(p);
      if (fieldName.isEmpty()) {
        idx++;
        continue;
      }
      String magType = (types != null && idx < types.length) ? types[idx] : "";
      String cType = magType == null || magType.isEmpty() ? "int" : mapMagmaTypeToC(magType);
      fieldsSb.append(" ").append(cType).append(" ").append(fieldName).append(";");
      idx++;
    }
    sb.append("typedef struct {").append(fieldsSb.toString()).append(" } ").append(name).append(";\n");
    return remaining.substring(removeEnd).trim();
  }
}
