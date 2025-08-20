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

  private static void checkVarsUsedAsFunctions(String expr, String namesCsv) {
    if (expr == null || expr.isEmpty() || namesCsv == null || namesCsv.isEmpty())
      return;
    String[] names = namesCsv.split(",");
    for (String name : names) {
      if (name.isEmpty())
        continue;
      String pattern = name + "(";
      if (expr.contains(pattern)) {
        throw new CompileException("Identifier '" + name + "' used like a function");
      }
    }
  }

  /**
   * Compile the given source text to a result string.
   * This implementation is intentionally trivial: it returns the input wrapped to
   * indicate compilation.
   *
   * @param source non-null source text
   * @return compiled representation
   */
  public static String compile(String source) {
    String src = prepareSource(source);
    String body = src.replace("readInt()", "read_int()");

    String[] letsCollected = collectLets(body);
    String letDecls = letsCollected[0];
    String bodyNoLets = letsCollected[1];
    String letNamesCsv = letsCollected.length > 2 ? letsCollected[2] : "";

    StringBuilder sb = new StringBuilder();
    emitPrelude(sb);

    String afterStructs = processStructures(bodyNoLets, sb);
    String afterFns = processFunctions(afterStructs, sb);

    validateAfterFunctions(afterFns, letNamesCsv, sb);

    emitMain(sb, letDecls, afterFns);
    return sb.toString();
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
    sb.append("int read_int(void) { int x = 0; if (scanf(\"%d\", &x) == 1) return x; return 0; }\n");
  }

  private static void validateAfterFunctions(String afterFns, String letNamesCsv, StringBuilder sb) {
    if (!letNamesCsv.isEmpty()) {
      checkVarsUsedAsFunctions(afterFns, letNamesCsv);
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
    String prelude = "extern fn readInt() : I32;";
    int pIdx = src.indexOf(prelude);
    if (pIdx != -1) {
      src = src.substring(0, pIdx) + src.substring(pIdx + prelude.length());
    }
    return src.trim();
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

    int idx;
    while ((idx = remaining.indexOf("let ")) != -1) {
      String[] extracted = extractNextLet(remaining, idx);
      if (extracted == null)
        break;
      String decl = extracted[0];
      int removeEnd = Integer.parseInt(extracted[1]);
      java.util.Optional<String> built = buildLetDeclaration(decl, remaining);
      built.ifPresent(decls::append);
      remaining = (remaining.substring(0, idx) + remaining.substring(removeEnd)).trim();
    }

    // also return a comma-separated list of declared variable names for
    // later sanity checks (e.g., calling a variable as a function)
    String namesCsv = decls.length() == 0 ? "" : extractNamesCsv(decls.toString());
    return new String[] { decls.toString(), remaining, namesCsv };
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

    rhs = convertBooleanLiteral(rhs);

    quickValidateStructInit(rhs, fullBody);

    java.util.Optional<String> structInit = tryBuildStructInit(rhs, varName, fullBody);
    if (structInit.isPresent())
      return structInit;

    return java.util.Optional.of("  int " + varName + " = (" + rhs + ");\n");
  }

  private static record DeclParts(String name, String rhs) {
  }

  private static DeclParts extractDeclParts(String decl) {
    int eq = decl.indexOf('=');
    String varName = extractVarName(decl, eq);
    String rhs = "0";
    if (eq != -1) {
      rhs = decl.substring(eq + 1, decl.length() - 1).trim();
      if (rhs.isEmpty()) {
        rhs = "0";
      }
    }
    return new DeclParts(varName, rhs);
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
      sb.append("/*fnsig:").append(fp.name()).append(':').append(fp.arity()).append("*/\n");
      sb.append("int ").append(fp.name()).append("(").append(fp.paramsDecl()).append(") { return (")
          .append(fp.expr()).append("); }\n");
      remaining = remaining.substring(fp.removeEnd()).trim();
    }
    return remaining;
  }

  private static record FunctionParts(String name, String paramsDecl, int arity, String expr, int removeEnd) {
  }

  private static FunctionParts extractNextFunction(String remaining) {
    int arrow = remaining.indexOf("=>");
    int semi = remaining.indexOf(';');
    if (arrow == -1 || semi == -1 || arrow > semi)
      return null;
    String header = remaining.substring(0, arrow).trim();
    int nameStart = 3; // after "fn "
    int paren = header.indexOf('(', nameStart);
    String name = (paren != -1) ? header.substring(nameStart, paren).trim() : "_fn";
    String paramsDecl = buildParamsDeclaration(header, paren);
    int arity = countParams(header, paren);
    String expr = remaining.substring(arrow + 2, semi).trim();
    return new FunctionParts(name, paramsDecl, arity, expr, semi + 1);
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

  private static String buildParamsDeclaration(String header, int paren) {
    String params = extractParams(header, paren);
    if (params.isEmpty())
      return "void";
    String[] parts = params.split(",");
    StringBuilder pd = new StringBuilder();
    for (String part : parts) {
      String p = part.trim();
      if (p.isEmpty())
        continue;
      int colon = p.indexOf(':');
      String pName = colon != -1 ? p.substring(0, colon).trim() : p;
      // default to I32 -> int
      String cType = "int";
      if (pd.length() > 0)
        pd.append(", ");
      pd.append(cType).append(" ").append(pName);
    }
    return pd.length() == 0 ? "void" : pd.toString();
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
    int end = findClosingIndex(expr, start);
    if (end == -1)
      return failedPair();
    int argCount = countArgsInRange(expr, start, end);
    return new int[] { argCount, end };
  }

  private static int[] failedPair() {
    return new int[] { -1, -1 };
  }

  private static int findClosingIndex(String expr, int start) {
    if (invalidRange(expr, start))
      return -1;
    int depth = 1;
    for (int i = start; i < expr.length(); i++) {
      char c = expr.charAt(i);
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
    return expr == null || start < 0 || start >= expr.length();
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
    StringBuilder fieldsSb = new StringBuilder();
    for (String p : parts) {
      String part = p.trim();
      if (part.isEmpty())
        continue;
      int colon = part.indexOf(':');
      String fieldName = colon != -1 ? part.substring(0, colon).trim() : part;
      fieldsSb.append(" int ").append(fieldName).append(";");
    }
    sb.append("typedef struct {").append(fieldsSb.toString()).append(" } ").append(name).append(";\n");
    return remaining.substring(removeEnd).trim();
  }
}
