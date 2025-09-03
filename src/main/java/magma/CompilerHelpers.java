package magma;

public final class CompilerHelpers {
  private CompilerHelpers() {
  }

  public static String declForInt(String name) {
    return CodeGen.declareInt(name);
  }

  public static String codeForScanInt(String name) {
    return CodeGen.scanInt(name);
  }

  public static String declForAssignInt(String name) {
    return CodeGen.declareInt(name);
  }

  public static String codeForAssign(String name, String expr) {
    return CodeGen.assign(name, expr);
  }

  public static String declForAssignBool(String name) {
    return CodeGen.declareStr(name);
  }

  public static String codeForAssignBool(String name, String val) {
    return CodeGen.assign(name, "\"" + val + "\"");
  }

  public static String emitOperand(String op, StringBuilder out, int[] tempCounter) {
    op = op.trim();
    if ("readInt()".equals(op)) {
      String tmp = "r" + (tempCounter[0]++);
      out.append(CodeGen.declareInt(tmp));
      out.append(CodeGen.scanInt(tmp));
      return tmp;
    }
    if ("true".equals(op) || "false".equals(op)) {
      return "\"" + op + "\"";
    }
    try {
      Integer.parseInt(op);
      return op;
    } catch (NumberFormatException nfe) {
      return op;
    }
  }

  private static String[] evalTwoOperands(String left, String right, int[] tempCounter, StringBuilder out) {
    // emitOperand appends any required decls/code for readInt() temporaries
    String l = emitOperand(left, out, tempCounter);
    String r = emitOperand(right, out, tempCounter);
    return new String[] { l, r };
  }

  public static String emitBinaryPrint(String left, String right, String operator, int[] tempCounter,
      StringBuilder out) {
    String[] lr = evalTwoOperands(left, right, tempCounter, out);
    return CodeGen.printfIntExpr(lr[0] + " " + operator + " " + lr[1]);
  }

  public static String emitEqPrint(String left, String right, int[] tempCounter, StringBuilder out) {
    String[] lr = evalTwoOperands(left, right, tempCounter, out);
    String expr = "((" + lr[0] + ") == (" + lr[1] + ")) ? \"true\" : \"false\"";
    return CodeGen.printfStrExpr(expr);
  }
}
