package magma;

public final class CompilerUtil {
  private CompilerUtil() {
  }

  public static String codeEmpty() {
    return "#include <stdio.h>\nint main(void) {\n  return 0;\n}\n";
  }

  public static String codePrintString(String s) {
    return "#include <stdio.h>\nint main(void) {\n  printf(\"%s\", \"" + s + "\");\n  return 0;\n}\n";
  }

  public static String startTwoInt() {
    StringBuilder sb = new StringBuilder();
    sb.append("#include <stdio.h>\n");
    sb.append("int main(void) {\n");
    sb.append("  int a = 0, b = 0;\n");
    sb.append("  if (scanf(\"%d\", &a) != 1) return 1;\n");
    sb.append("  if (scanf(\"%d\", &b) != 1) return 1;\n");
    return sb.toString();
  }

  public static String codeCompare() {
    StringBuilder sb = new StringBuilder(startTwoInt());
    sb.append("  if (a == b) printf(\"%s\", \"true\"); else printf(\"%s\", \"false\");\n");
    sb.append("  return 0;\n");
    sb.append("}\n");
    return sb.toString();
  }

  public static String codeOneInt() {
    return "#include <stdio.h>\nint main(void) {\n  int x = 0;\n  if (scanf(\"%d\", &x) != 1) return 1;\n  printf(\"%d\", x);\n  return 0;\n}\n";
  }

  public static String codeOneIntAddLiteral(int literal) {
    return "#include <stdio.h>\nint main(void) {\n  int x = 0;\n  if (scanf(\"%d\", &x) != 1) return 1;\n  printf(\"%d\", x + "
        + literal + ");\n  return 0;\n}\n";
  }

  public static String codeSumNInts(int n) {
    StringBuilder sb = new StringBuilder();
    sb.append("#include <stdio.h>\n");
    sb.append("int main(void) {\n");
    for (int i = 0; i < n; i++) {
      sb.append("  int a" + i + " = 0;\n");
      sb.append("  if (scanf(\"%d\", &a" + i + ") != 1) return 1;\n");
    }
    sb.append("  int res = 0;\n");
    for (int i = 0; i < n; i++) {
      sb.append("  res += a" + i + ";\n");
    }
    sb.append(appendPrintfReturnClose("res"));
    return sb.toString();
  }

  public static String codeBinary(char op) {
    StringBuilder sb = new StringBuilder(startTwoInt());
    sb.append("  int res = 0;\n");
    if (op == '+') {
      sb.append("  res = a + b;\n");
    } else if (op == '-') {
      sb.append("  res = a - b;\n");
    } else if (op == '*') {
      sb.append("  res = a * b;\n");
    } else if (op == '/') {
      sb.append("  if (b == 0) return 1;\n");
      sb.append("  res = a / b;\n");
    } else if (op == '%') {
      sb.append("  if (b == 0) return 1;\n");
      sb.append("  res = a % b;\n");
    }
    sb.append(appendPrintfReturnClose("res"));
    return sb.toString();
  }

  private static String appendPrintfReturnClose(String expr) {
    return "  printf(\"%d\", " + expr + ");\n" +
        "  return 0;\n" +
        "}\n";
  }

  public static String emitIfProgram(String thenLit, String elseLit) {
    StringBuilder sb = new StringBuilder(startTwoInt());
    sb.append("  if (a == b) printf(\"%s\", \"" + thenLit + "\"); else printf(\"%s\", \"" + elseLit + "\");\n");
    sb.append("  return 0;\n");
    sb.append("}\n");
    return sb.toString();
  }
}
