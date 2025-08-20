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
    String src = stripPrelude(Optional.ofNullable(source).orElse(""));
    String trimmed = src.trim();
    if (isSingleIdentifier(trimmed)) {
      throw new CompileException("Undefined symbol: " + trimmed);
    }
    String body = src.replace("readInt()", "read_int()");

    // collect let declarations early so we can emit them inside main after
    // structures (typedefs) are emitted
    String[] letsCollected = collectLets(body);
    String letDecls = letsCollected[0];
    String bodyNoLets = letsCollected[1];
    String letNamesCsv = letsCollected.length > 2 ? letsCollected[2] : "";

    StringBuilder sb = new StringBuilder();
    sb.append("#include <stdio.h>\n");
    sb.append("int read_int(void) { int x = 0; if (scanf(\"%d\", &x) == 1) return x; return 0; }\n");

    // emit any top-level structures and function definitions first
    String afterStructs = processStructures(bodyNoLets, sb);
    String afterFns = processFunctions(afterStructs, sb);

    sb.append("int main(void) {\n");

    // append collected let declarations inside main
    if (!letDecls.isEmpty()) {
      sb.append(letDecls);
    }

    // if any let-declared variable is invoked like a function (e.g. "x()"),
    // treat it as a compile-time error rather than letting C compilation fail.
    if (!letNamesCsv.isEmpty()) {
      checkVarsUsedAsFunctions(afterFns, letNamesCsv);
    }

    String finalExpr = afterFns.isEmpty() ? "0" : afterFns;
    sb.append("  int result = (").append(finalExpr).append(");\n");

    sb.append("  return result;\n");
    sb.append("}\n");
    return sb.toString();
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
      java.util.Optional<String> built = buildLetDeclaration(decl);
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

  private static java.util.Optional<String> buildLetDeclaration(String decl) {
    if (decl == null || decl.isEmpty())
      return java.util.Optional.empty();
    int eq = decl.indexOf('=');
    String varName = extractVarName(decl, eq);
    String rhs = "0";
    if (eq != -1) {
      rhs = decl.substring(eq + 1, decl.length() - 1).trim();
      if (rhs.isEmpty()) {
        rhs = "0";
      }
    }

    java.util.Optional<String> structInit = tryBuildStructInit(rhs, varName);
    if (structInit.isPresent())
      return structInit;

    return java.util.Optional.of("  int " + varName + " = (" + rhs + ");\n");
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

  private static java.util.Optional<String> tryBuildStructInit(String rhs, String varName) {
    int brace = rhs.indexOf('{');
    if (brace == -1)
      return java.util.Optional.empty();
    String structName = rhs.substring(0, brace).trim();
    int endBrace = rhs.lastIndexOf('}');
    String inner = endBrace != -1 ? rhs.substring(brace + 1, endBrace).trim() : "";
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

  private static String processFunctions(String body, StringBuilder sb) {
    String remaining = body;
    while (remaining.startsWith("fn ")) {
      int arrow = remaining.indexOf("=>");
      int semi = remaining.indexOf(';');
      if (arrow == -1 || semi == -1 || arrow > semi) {
        // malformed function, stop
        break;
      }

      // expected form: fn name() : I32 => expr;
      String header = remaining.substring(0, arrow).trim();
      // extract function name
      int nameStart = 3; // after "fn "
      int paren = header.indexOf('(', nameStart);
      String name = (paren != -1) ? header.substring(nameStart, paren).trim() : "_fn";

      String paramsDecl = buildParamsDeclaration(header, paren);

      String expr = remaining.substring(arrow + 2, semi).trim();

      sb.append("int ").append(name).append("(").append(paramsDecl).append(") { return (")
          .append(expr).append("); }\n");

      remaining = remaining.substring(semi + 1).trim();
    }
    return remaining;
  }

  private static String buildParamsDeclaration(String header, int paren) {
    if (paren == -1)
      return "void";
    int parenClose = header.indexOf(')', paren);
    if (parenClose == -1)
      return "void";
    String params = header.substring(paren + 1, parenClose).trim();
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

  private static int findStructureIndex(String s) {
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

  private static String parseAndEmitStructure(String remaining, StringBuilder sb) {
    int braceOpen = remaining.indexOf('{');
    int braceClose = remaining.indexOf('}', braceOpen);
    if (braceOpen == -1 || braceClose == -1) {
      return remaining;
    }

    int semi = remaining.indexOf(';', braceClose);
    String header = remaining.substring(0, braceOpen).trim();
    String prefix = header.startsWith("structure") ? "structure" : "struct";
    int nameStart = prefix.length();
    String name = header.substring(nameStart).trim();

    String bodyContent = remaining.substring(braceOpen + 1, braceClose).trim();
    // support multiple fields separated by commas: "f1 : I32, f2 : I32"
    String[] parts = bodyContent.split(",");
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

    int removeEnd = (semi == -1) ? (braceClose + 1) : (semi + 1);
    return remaining.substring(removeEnd).trim();
  }
}
