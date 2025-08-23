package magma.util;

import java.util.ArrayList;
import java.util.List;

public final class BlockUtils {
  private BlockUtils() {
  }

  // Find index after matching closing brace for '{' at start (returns index of
  // char after '}') or -1
  public static int findClosingBrace(String s, int start) {
    int len = s.length();
    if (start < 0 || start >= len || s.charAt(start) != '{')
      return -1;
    int depth = 1;
    int i = start + 1;
    while (i < len && depth > 0) {
      char c = s.charAt(i);
      if (c == '{')
        depth++;
      else if (c == '}')
        depth--;
      i++;
    }
    return depth == 0 ? i : -1;
  }

  // Return index of next top-level semicolon (ignoring braces and parentheses),
  // or -1
  public static int findTopLevelSemicolon(String s) {
    int depthB = 0;
    int depthP = 0;
    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);
      if (ch == '{')
        depthB++;
      else if (ch == '}')
        depthB--;
      else if (ch == '(')
        depthP++;
      else if (ch == ')')
        depthP--;
      else if (ch == ';' && depthB == 0 && depthP == 0)
        return i;
    }
    return -1;
  }

  // Split the string into top-level statements by semicolon, ignoring semicolons
  // inside braces/parentheses
  public static List<String> splitTopLevelStatements(String s) {
    List<String> out = new ArrayList<>();
    String remaining = s == null ? "" : s.trim();
    while (!remaining.isEmpty()) {
      int semi = findTopLevelSemicolon(remaining);
      if (semi == -1)
        break;
      String stmt = remaining.substring(0, semi).trim();
      if (!stmt.isEmpty())
        out.add(stmt);
      remaining = remaining.substring(semi + 1).trim();
    }
    return out;
  }
}
