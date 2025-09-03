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
}
