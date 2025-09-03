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

  private static int skipWs(String s, int pos) {
    while (pos < s.length() && Character.isWhitespace(s.charAt(pos)))
      pos++;
    return pos;
  }
}
