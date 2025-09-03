package magma;

public final class CodeGen {
  private CodeGen() {}

  public static String header() {
    return "#include <stdio.h>\n\nint main(void) {\n";
  }

  public static String footer() {
    return "  return 0;\n}\n";
  }

  public static String declareInt(String name) {
    return "  int " + name + ";\n";
  }

  public static String declareStr(String name) {
    return "  const char* " + name + ";\n";
  }

  public static String assign(String name, String expr) {
    return "  " + name + " = " + expr + ";\n";
  }

  public static String scanInt(String name) {
    return "  if (scanf(\"%d\", &" + name + ") != 1) return 0;\n";
  }

  public static String printfIntExpr(String expr) {
    return "  printf(\"%d\", " + expr + ");\n";
  }

  public static String printfStrExpr(String expr) {
    return "  printf(\"%s\", " + expr + ");\n";
  }
}
