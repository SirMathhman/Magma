package magma;

/**
 * Simple application class for the Magma project.
 */
public class Application {

  /**
   * Compile Magma source (string) to C source (string).
   * Minimal hand-written parser: handles empty input and a single
   * `let <name> : I32 = <number>;` statement.
   */
  public static String compile(String source) {
    if (source == null)
      return "";
    int i = 0;
    int n = source.length();

    // helper lambdas as local methods via anonymous inner class mimic
    class Cursor {
      int pos = i;

      void skipWs() {
        while (pos < n && Character.isWhitespace(source.charAt(pos)))
          pos++;
      }

      boolean consume(String token) {
        skipWs();
        if (source.startsWith(token, pos)) {
          pos += token.length();
          return true;
        }
        return false;
      }

      String parseIdent() {
        skipWs();
        if (pos >= n)
          return null;
        char c = source.charAt(pos);
        if (!Character.isLetter(c) && c != '_')
          return null;
        int start = pos++;
        while (pos < n) {
          char ch = source.charAt(pos);
          if (!Character.isLetterOrDigit(ch) && ch != '_')
            break;
          pos++;
        }
        return source.substring(start, pos);
      }

      String parseInteger() {
        skipWs();
        if (pos >= n)
          return null;
        int start = pos;
        if (source.charAt(pos) == '+' || source.charAt(pos) == '-')
          pos++;
        boolean hasDigits = false;
        while (pos < n && Character.isDigit(source.charAt(pos))) {
          pos++;
          hasDigits = true;
        }
        return hasDigits ? source.substring(start, pos) : null;
      }

      boolean consumeChar(char c) {
        skipWs();
        if (pos < n && source.charAt(pos) == c) {
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

    Cursor cur = new Cursor();

    // empty input
    cur.skipWs();
    if (cur.atEnd())
      return "";

    // parse 'let'
    if (!cur.consume("let"))
      return source; // fallback

    // identifier
    String name = cur.parseIdent();
    if (name == null)
      return source;

    // optional colon and type. If omitted, default to I32.
    cur.skipWs();
    String cType = "int32_t"; // default
    if (cur.consumeChar(':')) {
      cur.skipWs();
      String typeTok = cur.parseIdent();
      if (typeTok == null)
        return source;
      switch (typeTok) {
        case "I8":
          cType = "int8_t";
          break;
        case "I16":
          cType = "int16_t";
          break;
        case "I32":
          cType = "int32_t";
          break;
        case "I64":
          cType = "int64_t";
          break;
        case "U8":
          cType = "uint8_t";
          break;
        case "U16":
          cType = "uint16_t";
          break;
        case "U32":
          cType = "uint32_t";
          break;
        case "U64":
          cType = "uint64_t";
          break;
        default:
          return source; // unsupported type
      }
    }

    // equals
    if (!cur.consumeChar('='))
      return source;

    // integer literal
    String value = cur.parseInteger();
    if (value == null)
      return source;

    // semicolon
    if (!cur.consumeChar(';'))
      return source;

    // must be end
    if (!cur.atEnd())
      return source;

    StringBuilder sb = new StringBuilder();
    sb.append("#include <stdint.h>").append("\r\n");
    sb.append(cType).append(" ").append(name).append(" = ").append(value).append(";");
    return sb.toString();
  }

  // No entry point or greeting; this class only exposes the compile() API.
}
