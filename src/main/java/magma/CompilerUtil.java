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
}
