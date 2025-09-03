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
    sb.append("  printf(\"%d\", res);\n");
    sb.append("  return 0;\n");
    sb.append("}\n");
    return sb.toString();
  }

  public static String codeBinaryCompound(char op1, int literal, char op2) {
  StringBuilder sb = new StringBuilder(startTwoInt());
    // check division/mod for literal if used as divisor
    if (op1 == '/' || op1 == '%') {
      if (literal == 0) return codeRuntimeFail();
    }
    if (op2 == '/' || op2 == '%') {
      sb.append("  if (b == 0) return 1;\n");
    }
    sb.append("  int res = 0;\n");
    sb.append("  res = a " + op1 + " " + literal + " " + op2 + " b;\n");
    sb.append("  printf(\"%d\", res);\n");
    sb.append("  return 0;\n");
    sb.append("}\n");
    return sb.toString();
  }

  public static String emitIfProgram(String thenLit, String elseLit) {
    StringBuilder sb = new StringBuilder(startTwoInt());
    sb.append("  if (a == b) printf(\"%s\", \"" + thenLit + "\"); else printf(\"%s\", \"" + elseLit + "\");\n");
    sb.append("  return 0;\n");
    sb.append("}\n");
    return sb.toString();
  }

  public static String codeRuntimeFail() {
    return "#include <stdio.h>\nint main(void) { return 1; }\n";
  }
}
