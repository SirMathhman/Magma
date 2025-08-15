package magma;

import java.util.Map;

/**
 * Simple application class for the Magma project.
 */
public class Application {

  /**
   * Compile Magma source (string) to C source (string).
   * On success returns the C code; on failure returns null.
   */
  // Parse a program consisting of one or more `let` declarations
  private static String parseProgram(String source) {
    Parser p = new Parser(source);
    p.skipWs();

    java.util.List<String> decls = new java.util.ArrayList<>();
    java.util.Map<String, String> env = new java.util.HashMap<>();
    boolean anyBool = false;

    while (!p.atEnd()) {
      String decl = parseOneDeclaration(p, env);
      if (decl == null) {
        return null;
      }
      decls.add(decl);
      // track if any declaration used bool for header selection
      if (decl.startsWith("bool ") || decl.contains(" bool ")) {
        anyBool = true;
      }
      // after a declaration we expect either end or more content
      p.skipWs();
      // allow successive declarations; loop will check atEnd
    }

    String header = anyBool ? "#include <stdbool.h>" : "#include <stdint.h>";
    StringBuilder sb = new StringBuilder();
    sb.append(header);
    sb.append("\r\n");
    for (int i = 0; i < decls.size(); i++) {
      sb.append(decls.get(i));
      if (i + 1 < decls.size()) {
        sb.append("\r\n");
      }
    }
    return sb.toString();
  }

  private static String parseOneDeclaration(Parser p, java.util.Map<String, String> env) {
    p.skipWs();
    String name = parseLetName(p);
    if (name == null) {
      return null;
    }

    TypeInfo typeInfo = parseOptionalType(p);
    if (typeInfo == null) {
      return null;
    }

    String value = parseValueAndOptionalSuffix(p, typeInfo, env);
    if (value == null) {
      return null;
    }

    if (!p.consumeChar(';')) {
      return null;
    }

    // record declared variable type for subsequent references
    env.put(name, typeInfo.cType);

    return buildDecl(typeInfo.cType, name, value);
  }

  private static String buildDecl(String cType, String name, String value) {
    StringBuilder sb = new StringBuilder();
    sb.append(cType);
    sb.append(' ');
    sb.append(name);
    sb.append(" = ");
    sb.append(value);
    sb.append(';');
    return sb.toString();
  }

  private static String buildOutput(String cType, String name, String value) {
    StringBuilder sb = new StringBuilder();
    String header = "#include <stdint.h>";
    if ("bool".equals(cType)) {
      header = "#include <stdbool.h>";
    }
    sb.append(header);
    sb.append("\r\n");
    sb.append(cType);
    sb.append(' ');
    sb.append(name);
    sb.append(" = ");
    sb.append(value);
    sb.append(';');
    return sb.toString();
  }

  private static String parseLetName(Parser p) {
    if (!p.consume("let")) {
      return null;
    }
    String name = p.parseIdent();
    if (name == null) {
      return null;
    }
    return name;
  }

  // original-compatible signature delegates to the env-aware form
  private static String parseValueAndOptionalSuffix(Parser p, TypeInfo typeInfo) {
    return parseValueAndOptionalSuffix(p, typeInfo, null);
  }

  // New: allow resolution of identifier RHS using the provided env map
  private static String parseValueAndOptionalSuffix(Parser p, TypeInfo typeInfo, java.util.Map<String, String> env) {
    if (!p.consumeChar('=')) {
      return null;
    }

    LiteralParseResult lit = parseLiteral(p);
    if (lit == null) {
      // maybe RHS is an identifier referring to another variable
      p.skipWs();
      int save = p.getPos();
      String rhsIdent = p.parseIdent();
      if (rhsIdent != null && env != null && env.containsKey(rhsIdent)) {
        // adopt the referenced variable's C type
        typeInfo.cType = env.get(rhsIdent);
        return rhsIdent;
      }
      p.setPos(save);
      return null;
    }

    if (!applySuffixIfAny(lit, typeInfo)) {
      return null;
    }

    if (!checkBoolCompatibility(lit, typeInfo)) {
      return null;
    }

    return lit.value;
  }

  private static boolean applySuffixIfAny(LiteralParseResult lit, TypeInfo typeInfo) {
    if (lit.suffixToken == null) {
      return true;
    }
    if (typeInfo.explicitToken != null && !typeInfo.explicitToken.equals(lit.suffixToken)) {
      return false;
    }
    typeInfo.cType = TYPE_MAP.get(lit.suffixToken);
    return true;
  }

  private static boolean checkBoolCompatibility(LiteralParseResult lit, TypeInfo typeInfo) {
    if (lit.isBool) {
      if (typeInfo.explicitToken != null && !"Bool".equals(typeInfo.explicitToken)) {
        return false;
      }
      if (typeInfo.explicitToken == null && lit.suffixToken == null) {
        typeInfo.cType = "bool";
      }
      return true;
    }
    // numeric literal: cannot initialize Bool
    return !"Bool".equals(typeInfo.explicitToken);
  }

  private static class LiteralParseResult {
    final String value;
    final boolean isBool;
    final String suffixToken;

    LiteralParseResult(String value, boolean isBool, String suffixToken) {
      this.value = value;
      this.isBool = isBool;
      this.suffixToken = suffixToken;
    }
  }

  private static LiteralParseResult parseLiteral(Parser p) {
    p.skipWs();
    int save = p.getPos();

    String intVal = p.parseInteger();
    if (intVal != null) {
      p.skipWs();
      int save2 = p.getPos();
      String litTok = p.parseIdent();
      if (litTok != null) {
        String mapped = mapTypeToken(litTok);
        if (mapped != null) {
          return new LiteralParseResult(intVal, false, litTok);
        }
        // not a suffix token we know; roll back
        p.setPos(save2);
      }
      return new LiteralParseResult(intVal, false, null);
    }

    String b = p.parseBool();
    if (b != null) {
      return new LiteralParseResult(b, true, null);
    }

    p.setPos(save);
    return null;
  }

  /**
   * Public-facing compile API. Returns "" for null/empty input, the C code
   * for successful compilation, or the original source on failure.
   */
  public static String compile(String source) {
    if (source == null) {
      return "";
    }

    String trimmed = source.trim();
    if (trimmed.isEmpty()) {
      return "";
    }

  String out = parseProgram(trimmed);
    if (out == null) {
      return source;
    }
    return out;
  }

  private static class TypeInfo {
    final String explicitToken;
    String cType;

    TypeInfo(String explicitToken, String cType) {
      this.explicitToken = explicitToken;
      this.cType = cType;
    }
  }

  private static TypeInfo parseOptionalType(Parser p) {
    p.skipWs();
    String cType = "int32_t";
    String explicit = null;
    if (p.consumeChar(':')) {
      p.skipWs();
      explicit = p.parseIdent();
      if (explicit == null) {
        return null;
      }
      String mapped = mapTypeToken(explicit);
      if (mapped == null) {
        return null;
      }
      cType = mapped;
    }
    return new TypeInfo(explicit, cType);
  }

  private static class Parser {
    private final String src;
    private final int n;
    private int pos;

    Parser(String s) {
      this.src = s;
      this.n = s.length();
      this.pos = 0;
    }

    int getPos() {
      return pos;
    }

    void setPos(int p) {
      this.pos = p;
    }

    void skipWs() {
      while (pos < n && Character.isWhitespace(src.charAt(pos))) {
        pos++;
      }
    }

    boolean consume(String token) {
      skipWs();
      if (src.startsWith(token, pos)) {
        pos += token.length();
        return true;
      }
      return false;
    }

    String parseIdent() {
      skipWs();
      if (pos >= n) {
        return null;
      }
      char c = src.charAt(pos);
      if (!Character.isLetter(c) && c != '_') {
        return null;
      }
      int start = pos++;
      while (pos < n) {
        char ch = src.charAt(pos);
        if (!Character.isLetterOrDigit(ch) && ch != '_') {
          break;
        }
        pos++;
      }
      return src.substring(start, pos);
    }

    String parseInteger() {
      skipWs();
      if (pos >= n) {
        return null;
      }
      int start = pos;
      char c = src.charAt(pos);
      if (c == '+' || c == '-') {
        pos++;
      }
      boolean hasDigits = false;
      while (pos < n && Character.isDigit(src.charAt(pos))) {
        pos++;
        hasDigits = true;
      }
      return hasDigits ? src.substring(start, pos) : null;
    }

    String parseBool() {
      skipWs();
      int save = pos;
      if (src.startsWith("true", pos)) {
        pos += 4;
        return "true";
      }
      if (src.startsWith("false", pos)) {
        pos += 5;
        return "false";
      }
      pos = save;
      return null;
    }

    boolean consumeChar(char c) {
      skipWs();
      if (pos < n && src.charAt(pos) == c) {
        pos++;
        return true;
      }
      return false;
    }

    boolean atEnd() {
      skipWs();
      return pos >= n;
    }
  }

  private static String mapTypeToken(String token) {
    if (token == null) {
      return null;
    }
    return TYPE_MAP.get(token);
  }

  private static final Map<String, String> TYPE_MAP = Map.of(
      "Bool", "bool",
      "I8", "int8_t",
      "I16", "int16_t",
      "I32", "int32_t",
      "I64", "int64_t",
      "U8", "uint8_t",
      "U16", "uint16_t",
      "U32", "uint32_t",
      "U64", "uint64_t"
  );
}
