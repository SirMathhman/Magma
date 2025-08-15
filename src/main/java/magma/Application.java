package magma;

/**
 * Simple application class for the Magma project.
 */
public class Application {

  /**
   * Compile Magma source (string) to C source (string).
   * On success returns the C code; on failure returns null.
   */
  private static String parseDeclaration(String source) {
    Parser p = new Parser(source);
    p.skipWs();

    String name = parseLetName(p);
    if (name == null) {
      return null;
    }

    TypeInfo typeInfo = parseOptionalType(p);
    if (typeInfo == null) {
      return null;
    }

    String value = parseValueAndOptionalSuffix(p, typeInfo);
    if (value == null) {
      return null;
    }

    if (!p.consumeChar(';')) {
      return null;
    }

    if (!p.atEnd()) {
      return null;
    }

    StringBuilder sb = new StringBuilder();
    sb.append("#include <stdint.h>");
    sb.append("\r\n");
    sb.append(typeInfo.cType);
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

  private static String parseValueAndOptionalSuffix(Parser p, TypeInfo typeInfo) {
    if (!p.consumeChar('=')) {
      return null;
    }
    String value = p.parseInteger();
    if (value == null) {
      return null;
    }
    p.skipWs();
    int save = p.getPos();
    String litTok = p.parseIdent();
    if (litTok != null) {
      if (typeInfo.explicitToken != null && !typeInfo.explicitToken.equals(litTok)) {
        return null;
      }
      String m = mapTypeToken(litTok);
      if (m == null) {
        p.setPos(save);
      } else {
        typeInfo.cType = m;
      }
    }
    return value;
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

    String out = parseDeclaration(trimmed);
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
    switch (token) {
      case "I8":
        return "int8_t";
      case "I16":
        return "int16_t";
      case "I32":
        return "int32_t";
      case "I64":
        return "int64_t";
      case "U8":
        return "uint8_t";
      case "U16":
        return "uint16_t";
      case "U32":
        return "uint32_t";
      case "U64":
        return "uint64_t";
      default:
        return null;
    }
  }
}
