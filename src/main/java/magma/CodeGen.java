package magma;

public final class CodeGen {
  private CodeGen() {
  }

  public static String codeOneIntAddLiteral(int literal) {
    return "#include <stdio.h>\nint main(void) {\n  int x = 0;\n  if (scanf(\"%d\", &x) != 1) return 1;\n  printf(\"%d\", x + "
        + literal + ");\n  return 0;\n}\n";
  }

  public static String codeSumNInts(int n) {
    StringBuilder sb = new StringBuilder();
    sb.append(codeReadNScanf(n));
    sb.append("  int res = 0;\n");
    for (int i = 0; i < n; i++) {
      sb.append("  res += a" + i + ";\n");
    }
    sb.append(codePrintResAndClose());
    return sb.toString();
  }

  private static String codeReadNScanf(int n) {
    StringBuilder sb = new StringBuilder();
    sb.append("#include <stdio.h>\n");
    sb.append("int main(void) {\n");
    for (int i = 0; i < n; i++) {
      sb.append("  int a" + i + " = 0;\n");
      sb.append("  if (scanf(\"%d\", &a" + i + ") != 1) return 1;\n");
    }
    return sb.toString();
  }

  public static String codeReduceNInts(char op, int n) {
    StringBuilder sb = new StringBuilder();
    sb.append(codeReadNScanf(n));
    sb.append("  int res = a0;\n");
    for (int i = 1; i < n; i++) {
      if (op == '/') {
        sb.append("  if (a" + i + " == 0) return 1;\n");
      }
      sb.append("  res = res " + op + " a" + i + ";\n");
    }
    sb.append(codePrintResAndClose());
    return sb.toString();
  }

  private static String codePrintResAndClose() {
    return "  printf(\"%d\", res);\n" +
        "  return 0;\n" +
        "}\n";
  }
}
