package magma;

public final class CompilerUtil {
  private CompilerUtil() {
  }

  public static boolean isBracedNumeric(String s) {
    if (s == null)
      return false;
    String t = s.trim();
    if (t.length() < 3 || t.charAt(0) != '{' || t.charAt(t.length() - 1) != '}')
      return false;
    String inner = t.substring(1, t.length() - 1).trim();
    if (inner.isEmpty())
      return false;
    for (int i = 0; i < inner.length(); i++) {
      if (!Character.isDigit(inner.charAt(i)))
        return false;
    }
    return true;
  }

  public static boolean isPlainNumeric(String s) {
    if (s == null)
      return false;
    String t = s.trim();
    if (t.isEmpty())
      return false;
    for (int i = 0; i < t.length(); i++) {
      if (!Character.isDigit(t.charAt(i)))
        return false;
    }
    return true;
  }

  public static boolean isIdentifierChar(char ch) {
    return Character.isLetterOrDigit(ch) || ch == '_';
  }

  public static int findStandaloneTokenEnd(String src, String key, int start) {
    if (src == null || src.isEmpty())
      return -1;
    int idx = start;
    while (true) {
      idx = src.indexOf(key, idx);
      if (idx == -1)
        return -1;
      if (idx > 0) {
        char prev = src.charAt(idx - 1);
        if (Character.isLetterOrDigit(prev) || prev == '_') {
          idx += key.length();
          continue;
        }
      }
      return idx + key.length();
    }
  }

  public static int findStandaloneTokenIndex(String src, String key, int start) {
    int end = findStandaloneTokenEnd(src, key, start);
    if (end == -1)
      return -1;
    return end - key.length();
  }

  public static int skipWhitespace(String s, int idx) {
    int j = idx;
    while (j < s.length() && Character.isWhitespace(s.charAt(j)))
      j++;
    return j;
  }

  public static boolean isTopLevelPos(String s, int pos) {
    if (s == null || pos < 0)
      return false;
    int depth = 0;
    for (int i = 0; i < pos && i < s.length(); i++) {
      char ch = s.charAt(i);
      if (ch == '(')
        depth++;
      else if (ch == ')')
        depth--;
    }
    return depth == 0;
  }

  public static int findTopLevelOp(String s, String op) {
    if (s == null || op == null)
      return -1;
    int idx = 0;
    while (true) {
      idx = s.indexOf(op, idx);
      if (idx == -1)
        return -1;
      if (isTopLevelPos(s, idx))
        return idx;
      idx += 1;
    }
  }
  public static int countTopLevelArgs(String s) {
    if (s == null)
      return 0;
    String t = s.trim();
    if (t.isEmpty())
      return 0;
    // reuse ParserUtils
    return ParserUtils.splitTopLevel(t, ',', '(', ')').size();
  }

  public static int countParamsInType(String type) {
    if (type == null)
      return 0;
    String inner = getParamsInnerTypeSegment(type);
    if (inner == null || inner.isEmpty())
      return 0;
    return countTopLevelArgs(inner);
  }

  public static String getParamsInnerTypeSegment(String funcType) {
    if (funcType == null)
      return null;
    int arrow = funcType.indexOf("=>");
    if (arrow == -1)
      return null;
    String params = funcType.substring(0, arrow).trim();
    if (params.length() >= 2 && params.charAt(0) == '(' && params.charAt(params.length() - 1) == ')') {
      return params.substring(1, params.length() - 1).trim();
    }
    return null;
  }
}
