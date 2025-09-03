package magma;

public final class CompilerHelpers {
  private CompilerHelpers() {
  }

  public static String emitBinaryIntResultOrRuntimeFail(char op, int lv, int rv) {
    if ((op == '/' || op == '%') && rv == 0) {
      return CompilerUtil.codeRuntimeFail();
    }
    int res = 0;
    if (op == '+')
      res = lv + rv;
    else if (op == '-')
      res = lv - rv;
    else if (op == '*')
      res = lv * rv;
    else if (op == '/')
      res = lv / rv;
    else if (op == '%')
      res = lv % rv;
    return CompilerUtil.codePrintString(String.valueOf(res));
  }

  public static int findOpAfter(String s, int pos) {
    pos = skipWs(s, pos);
    if (pos < s.length()) {
      char c = s.charAt(pos);
      if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%')
        return pos;
    }
    return -1;
  }

  // find op at or after pos and return the position after the op (skipping whitespace), or -1
  public static int findOpNext(String s, int pos) {
    int opPos = findOpAfter(s, pos);
    if (opPos < 0) return -1;
    int next = opPos + 1;
    next = skipWs(s, next);
    return next;
  }

  public static boolean matchesReadIntAt(String s, int pos) {
    String tok = "readInt()";
    if (pos + tok.length() <= s.length() && s.substring(pos, pos + tok.length()).equals(tok)) return true;
    if (pos < s.length() && s.charAt(pos) == '(') {
      int inner = pos + 1;
      if (inner + tok.length() <= s.length() && s.substring(inner, inner + tok.length()).equals(tok)) {
        int after = inner + tok.length();
        if (after < s.length() && s.charAt(after) == ')') return true;
      }
    }
    return false;
  }

  public static int findOpAndRhsReadIntPos(String s, int pos) {
    int q = findOpNext(s, pos);
    if (q <= 0) return -1;
    if (matchesReadIntAt(s, q)) return q;
    return -1;
  }

  private static int skipWs(String s, int pos) {
    while (pos < s.length() && Character.isWhitespace(s.charAt(pos)))
      pos++;
    return pos;
  }
}
