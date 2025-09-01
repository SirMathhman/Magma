package magma;

import java.util.ArrayList;
import java.util.List;

public final class ParserUtils {
  private ParserUtils() {}

  public static int advanceNested(String s, int p, char openChar, char closeChar) {
    int depth = 1;
    while (p < s.length() && depth > 0) {
      char ch = s.charAt(p);
      if (ch == openChar)
        depth++;
      else if (ch == closeChar)
        depth--;
      p++;
    }
    return depth == 0 ? p : -1;
  }

  public static List<String> splitTopLevel(String s, char sep, char open, char close) {
    List<String> out = new ArrayList<>();
    if (s == null) return out;
    int depth = 0;
    int start = 0;
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == open) depth++;
      else if (c == close) depth--;
      else if (c == sep && depth == 0) {
        out.add(s.substring(start, i));
        start = i + 1;
      }
    }
    out.add(s.substring(start));
    return out;
  }
}
