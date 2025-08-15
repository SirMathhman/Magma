package magma;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple application class for the Magma project.
 */
public class Application {
  /**
   * Compile Magma source (string) to C source (string).
   * Minimal compiler: handles empty input and a single `let <name> : I32 =
   * <number>;`.
   *
   * Examples:
   * - "" -> ""
   * - "let x : I32 = 0;" -> "#include <stdint.h>\r\nint32_t x = 0;"
   */
  public static String compile(String source) {
    if (source == null)
      return "";
    String s = source.trim();
    if (s.isEmpty())
      return "";

    // match: let <ident> : I32 = <integer>;
    Pattern p = Pattern.compile("^let\\s+([A-Za-z_]\\w*)\\s*:\\s*I32\\s*=\\s*([-+]?\\d+)\\s*;\\s*$");
    Matcher m = p.matcher(s);
    if (m.matches()) {
      String name = m.group(1);
      String value = m.group(2);
      StringBuilder sb = new StringBuilder();
      sb.append("#include <stdint.h>").append("\r\n");
      sb.append("int32_t ").append(name).append(" = ").append(value).append(";");
      return sb.toString();
    }

    // Fallback: return source unchanged for now
    return source;
  }
}
